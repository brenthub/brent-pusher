package cn.brent.pusher.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.core.IPusherClient;
import cn.brent.pusher.core.PusherServer;

public class NettyPusherServer extends PusherServer {

	protected EventLoopGroup parentGroup = new NioEventLoopGroup();
	protected EventLoopGroup workGroup = new NioEventLoopGroup();

	public NettyPusherServer(PusherConfig pushConfig) {
		super(pushConfig);
	}

	@Override
	protected void stopServer() {
		workGroup.shutdownGracefully();
		parentGroup.shutdownGracefully();
	}

	@Override
	protected void startServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerBootstrap b = new ServerBootstrap();
					b.group(parentGroup, workGroup);
					b.channel(PusherChannel.class);
					b.childHandler(new ChildChannelHandler());
					Channel ch = b.bind(getPort()).sync().channel();
					ch.closeFuture().sync();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					parentGroup.shutdownGracefully();
					workGroup.shutdownGracefully();
				}
			}
		}).start();
	}

	public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast("http-codec", new HttpServerCodec());
			ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
			ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
			ch.pipeline().addLast("handler", new WebsocketHandler());
		}
	}

	public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {

		private WebSocketServerHandshaker handshaker;

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			IPusherClient conn=(IPusherClient)ctx.channel();
			onOpen(conn, "/order/1?sign=1");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			IPusherClient conn=(IPusherClient)ctx.channel();
			onClose(conn, IPusherClient.NORMAL, "");
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof FullHttpRequest) {
				handleHttpRequest(ctx, ((FullHttpRequest) msg));
			} else if (msg instanceof WebSocketFrame) {
				handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
			}
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
			// 判断是否关闭链路的指令
			if (frame instanceof CloseWebSocketFrame) {
				handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
				return;
			}
			// 判断是否ping消息
			if (frame instanceof PingWebSocketFrame) {
				ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
				return;
			}
			// 仅支持文本消息，不支持二进制消息
			if (!(frame instanceof TextWebSocketFrame)) {
				ctx.close();//(String.format("%s frame types not supported", frame.getClass().getName()));
				return;
			}

		}

		private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
			if (!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
				return;
			}
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8887", null, false);
			handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				handshaker.handshake(ctx.channel(), req);
			}
		}

		private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
			// 返回应答给客户端
			if (res.getStatus().code() != 200) {
				ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
				res.content().writeBytes(buf);
				buf.release();
			}
			// 如果是非Keep-Alive，关闭连接
			ChannelFuture f = ctx.channel().writeAndFlush(res);
			if (!isKeepAlive(req) || res.getStatus().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		}

		private boolean isKeepAlive(FullHttpRequest req) {
			return false;
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}
}
