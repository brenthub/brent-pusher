package cn.brent.pusher.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.IPlugin;
import cn.brent.pusher.IVerifier;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.Session;

public abstract class PusherServer {
	
	protected static Logger logger = LoggerFactory.getLogger(PusherServer.class);

	/** 个性化配置 */
	protected PusherConfig pushConfig;
	
	/** 会话管理 */
	protected ISessionManager sessionManager;
	
	/** 认证器 */
	protected IVerifier[] verifiers;
	
	/** 插件 */
	protected IPlugin[] plugins;
	
	/** 端口 */
	protected int port;
	
	public PusherServer(PusherConfig pushConfig) {
		this.pushConfig=pushConfig;
		Config.configPusher(pushConfig);
		port=Config.getConstants().getPort();
		sessionManager=Config.getConstants().getSessionManager();
		verifiers=Config.getVerifiers().getAll();
		plugins=Config.getPlugins().getAll();
	}
	
	protected void onOpen(IPusherClient conn, String uri) {
		PathDesc path;
		try {
			try {
				path = PathDesc.parse(uri);
			} catch (Exception e) {
				throw new RuntimeException("Unsupported msg format");
			}
			
			Session session=sessionManager.getSession(path.getTopic(), path.getKey());
			
			for(IVerifier verifier:verifiers){
				if(!verifier.verify(path,session)){
					throw new RuntimeException(verifier.failMsg(path));
				}
			}

			//保存会话信息
			sessionManager.saveConnect(conn);
			
			//连接成功后的回调
			pushConfig.afterConnectSuccess(conn);
			
			logger.debug("new connection: " + uri + "," + conn + " connected!");
		} catch (Exception e) {
			conn.close(IPusherClient.REFUSE, "rejected connection, "+e.getMessage());
		}
	}
	
	protected void onClose(IPusherClient conn, int code, String reason){
		if (code == IPusherClient.REFUSE) {// 被服务器拒绝
			// do nothing
		} else {
			logger.debug("connect disconnect...");
			sessionManager.removeConnect(conn);
		}
	}
	
	public void start() {
		startServer();
		for(IPlugin p:plugins){
			p.start();
		}
		pushConfig.afterStart();
		logger.info("PushServer started on port:" + this.getPort());
	}
	
	public void stop() {
		pushConfig.beforeStop();
		for(IPlugin p:plugins){
			p.stop();
		}
		this.stopServer();
	}
	
	public int getPort() {
		return port;
	}
	
	/**
	 * 停止服务
	 */
	protected abstract void stopServer();

	/**
	 * 开启服务
	 */
	protected abstract void startServer();

	
	
}
