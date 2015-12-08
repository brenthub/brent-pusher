package cn.brent.pusher.session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.brent.pusher.core.IPusherClient;

/**
 * 会话管理器
 */
public interface ISessionManager {

	/** 连接队列 */
	final List<IPusherClient> clientQueue = new CopyOnWriteArrayList<IPusherClient>();
	
	/**
	 * 保存client
	 * @param client
	 */
	void saveConnect(IPusherClient client);
	
	/**
	 * 移除client
	 * @param client
	 */
	void removeConnect(IPusherClient client);
	
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
