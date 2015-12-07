package cn.brent.pusher.config;

import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.imp.MapSessionManager;

/**
 * 变量配置
 */
final public class Constants {

	/** 连接保存者 */
	protected ISessionManager sessionManager=new MapSessionManager();

	protected int port=8887;

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
