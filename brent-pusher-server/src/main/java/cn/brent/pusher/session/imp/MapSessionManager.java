package cn.brent.pusher.session.imp;

import java.util.HashMap;
import java.util.Map;

import cn.brent.pusher.server.PusherWebSocket;
import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.Session;

public class MapSessionManager implements ISessionManager {

	protected Map<String, Session> sessionMap = new HashMap<String, Session>();

	@Override
	public Session getSession(String biz, String key) {
		String name = Session.getName(biz, key);
		return sessionMap.get(name);
	}


	@Override
	public void removeSession(String biz, String key) {
		String name = Session.getName(biz, key);
		sessionMap.remove(name);
	}


	@Override
	public void saveConnect(PusherWebSocket socket) {
		String name = Session.getName(socket.getBiz(), socket.getKey());
		Session session=sessionMap.get(name);
		if(session==null){
			sessionMap.put(name, new Session(socket.getBiz(), socket.getKey(), socket));
			return;
		}
		session.addSockets(socket);
	}


	@Override
	public void removeSession(Session session) {
		this.removeSession(session.getBiz(), session.getKey());
	}


	@Override
	public void removeConnect(PusherWebSocket socket) {
		Session session=getSession(socket.getBiz(), socket.getKey());
		if(session!=null){
			session.removeSocket(socket);
		}
	}

}
