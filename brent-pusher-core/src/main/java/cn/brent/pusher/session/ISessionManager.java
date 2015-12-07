package cn.brent.pusher.session;

import java.util.LinkedList;
import java.util.Queue;

import cn.brent.pusher.core.IPusherClient;

/**
 * 会话管理器
 */
public interface ISessionManager {

	/** 连接队列 */
	final static Queue<IPusherClient> clientQueue = new LinkedList<IPusherClient>();
	
	/**
	 * 保存socket
	 * @param socket
	 */
	void saveConnect(IPusherClient socket);
	
	/**
	 * 移除socket
	 * @param socket
	 */
	void removeConnect(IPusherClient socket);
	
	/**
	 * 根据业务编码和key获取Session
	 * @param topic
	 * @param key
	 * @return
	 */
	Session getSession(String topic, String key);
	
	/**
	 * 移除Session
	 * @param topic
	 * @param key
	 */
	void removeSession(String topic, String key);
	
	/**
	 * 移除Session
	 * @param Session
	 */
	void removeSession(Session session);
	
}
