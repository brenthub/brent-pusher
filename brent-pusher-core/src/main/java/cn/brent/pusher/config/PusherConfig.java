package cn.brent.pusher.config;

import cn.brent.pusher.core.PusherWebSocket;

/**
 * 配置管理
 */
public abstract class PusherConfig {

	/**
	 * Config constant
	 */
	public abstract void configConstant(Constants me);
	
	/**
	 * Config IVerifier
	 */
	public abstract void configVerifier(Verifiers me);
	
	/**
	 * Config Plugin
	 */
	public abstract void configPlugin(Plugins me);

	/**
	 * Call back after start
	 */
	public void afterStart(){};
	
	/**
	 * Call back before stop
	 */
	public void beforeStop(){};
	
	/**
	 * Call back after Connect Success
	 */
	public void afterConnectSuccess(PusherWebSocket socket){};
}
