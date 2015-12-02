package cn.brent.pusher.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.IPlugin;
import cn.brent.pusher.IVerifier;
import cn.brent.pusher.PushMsg;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.Session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class PusherServer extends WebSocketServer {

	protected static Logger log = LoggerFactory.getLogger(PusherServer.class);
	
	/** 个性化配置 */
	private PusherConfig pushConfig;
	
	/** 会话管理 */
	protected ISessionManager sessionManager;
	
	/** 认证器 */
	protected IVerifier[] verifiers;
	
	/** 插件 */
	protected IPlugin[] plugins;
	
	public PusherServer(int port,PusherConfig pushConfig) {
		this(new InetSocketAddress(port), pushConfig);
	}
	
	public PusherServer(String hostName,int port,PusherConfig pushConfig) {
		this(new InetSocketAddress(hostName,port), pushConfig);
	}
	
	public PusherServer(InetSocketAddress address,PusherConfig pushConfig) {
		super(address,DECODERS,null,ISessionManager.socketQueue);
		this.pushConfig = pushConfig;
		init();
	}
	
	protected void init(){
		Config.configPusher(pushConfig);
		setWebSocketFactory(Config.getConstants().getFactory());
		sessionManager=Config.getConstants().getSessionManager();
		verifiers=Config.getVerifiers().getAll();
		plugins=Config.getPlugins().getAll();
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		String desc = handshake.getResourceDescriptor();
		PathDesc path;
		try {
			try {
				path = PathDesc.parse(desc);
			} catch (Exception e) {
				throw new RuntimeException("Unsupported msg format");
			}
			
			Session session=sessionManager.getSession(path.getBiz(), path.getKey());
			
			for(IVerifier verifier:verifiers){
				if(!verifier.verify(path,session)){
					throw new RuntimeException(verifier.failMsg(path));
				}
			}
			
			PusherWebSocket pconn=(PusherWebSocket)conn;
			
			pconn.setBiz(path.getBiz());
			pconn.setKey(path.getKey());

			//保存会话信息
			sessionManager.saveConnect(pconn);
			
			//连接成功后的回调
			pushConfig.afterConnectSuccess(pconn);
			
			log.debug("new connection: " + handshake.getResourceDescriptor() + "," + conn.getRemoteSocketAddress() + " connected!");
		} catch (Exception e) {
			conn.close(CloseFrame.REFUSE, "rejected connection, "+e.getMessage());
		}

	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		log.debug("PushServer " + conn + " disconnected! reason:" + reason);
		if (code == CloseFrame.REFUSE) {// 被服务器拒绝
			// do nothing
		} else {
			this.disconnectHandler(conn);
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// do nothing
	}

	/**
	 * 消息推送处理
	 * 
	 * @param conn
	 * @param k
	 */
	public static void push(PushMsg msg) {

		log.info("push msg " + msg);

		if (msg.getBiz() == null) {
			log.error(msg + "biz is null");
			return;
		}
		if (msg.getKey() == null) {
			log.error(msg + "key is null");
			return;
		}
		if (msg.getData() == null || msg.getData().size() == 0) {
			log.error(msg + "data is null");
			return;
		}

		Session session = Config.getConstants().getSessionManager().getSession(msg.getBiz(), msg.getKey());
		if (session == null) {
			return;
		}
		JSON ret = new JSONObject(msg.getData());

		for (WebSocket webc : session.getSockets()) {
			try {
				sendMsg(webc, ret.toJSONString());
			} catch (Exception e) {
				log.error("push failed:", e);
			}
		}
		if (!msg.isSucessClose()) {
			return;
		}
		try {
			Thread.sleep(500);// 延迟半秒钟，防止客户端未收到消息就关闭连接了
		} catch (InterruptedException e1) {
		}
		for (WebSocket webc : session.getSockets()) {
			try {
				if (webc.isOpen()) {
					webc.close(CloseFrame.NORMAL, "business completion");
				}
			} catch (Exception e) {
				log.error("push failed:", e);
			}
		}
		Config.getConstants().getSessionManager().removeSession(session);
	}

	/**
	 * 发送消息
	 * 
	 * @param conn
	 * @param message
	 */
	protected static void sendMsg(WebSocket conn, String message) {
		synchronized (conn) {
			if (conn.isOpen()) {
				conn.send(message);
			} else {
				throw new RuntimeException("target webSocket not open");
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		log.error("PushServer onError:", ex);
		disconnectHandler(conn);
	}

	/**
	 * 客户端断开连接处理
	 * 
	 * @param conn
	 */
	protected void disconnectHandler(WebSocket conn) {
		log.debug("connect disconnect...");
		PusherWebSocket pconn=(PusherWebSocket)conn;
		sessionManager.removeConnect(pconn);
	}

	@Override
	public void start() {
		super.start();
		for(IPlugin p:plugins){
			p.start();
		}
		pushConfig.afterStart();
		log.info("PushServer started on port:" + this.getPort());
	}

	@Override
	public void stop() throws IOException, InterruptedException {
		pushConfig.beforeStop();
		for(IPlugin p:plugins){
			p.stop();
		}
		super.stop();
	}

}
