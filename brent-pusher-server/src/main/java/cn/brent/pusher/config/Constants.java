package cn.brent.pusher.config;

import cn.brent.pusher.server.PusherSocketFactory;
import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.imp.MapSessionManager;

/**
 * 变量配置
 */
final public class Constants {

	/** 连接保存者 */
	protected ISessionManager sessionManager=new MapSessionManager();
	
	/** 连接工厂*/
	private PusherSocketFactory factory = new PusherSocketFactory();

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public PusherSocketFactory getFactory() {
		return factory;
	}

	public void setFactory(PusherSocketFactory factory) {
		this.factory = factory;
	}
	
}
