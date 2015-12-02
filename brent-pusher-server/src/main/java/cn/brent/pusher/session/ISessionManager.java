package cn.brent.pusher.session;

import java.util.LinkedList;
import java.util.Queue;

import org.java_websocket.WebSocket;

import cn.brent.pusher.server.PusherWebSocket;

/**
 * 会话管理器
 */
public interface ISessionManager {

	/** 连接队列 */
	final static Queue<WebSocket> socketQueue = new LinkedList<WebSocket>();
	
	/**
	 * 保存socket
	 * @param socket
	 */
	void saveConnect(PusherWebSocket socket);
	
	/**
	 * 移除socket
	 * @param socket
	 */
	void removeConnect(PusherWebSocket socket);
	
	/**
	 * 根据业务编码和key获取Session
	 * @param biz
	 * @param key
	 * @return
	 */
	Session getSession(String biz, String key);
	
	/**
	 * 移除Session
	 * @param biz
	 * @param key
	 */
	void removeSession(String biz, String key);
	
	/**
	 * 移除Session
	 * @param Session
	 */
	void removeSession(Session session);
	
}
