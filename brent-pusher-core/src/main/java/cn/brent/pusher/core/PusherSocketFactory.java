package cn.brent.pusher.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.server.WebSocketServer.WebSocketServerFactory;

/**
 * 重写Factory为了引入自定义的WebSocket
 */
public class PusherSocketFactory implements WebSocketServerFactory{

	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d, Socket s) {
		return new PusherWebSocket( a, d );
	}

	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> drafts, Socket s) {
		return new PusherWebSocket( a, drafts );
	}

	@Override
	public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
		return (SocketChannel) channel;
	}
	
}
