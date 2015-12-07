package cn.brent.pusher.session.imp;

import java.util.HashMap;
import java.util.Map;

import cn.brent.pusher.core.IPusherClient;
import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.Session;

public class MapSessionManager implements ISessionManager {

	protected Map<String, Session> sessionMap = new HashMap<String, Session>();

	@Override
	public Session getSession(String topic, String key) {
		String name = Session.getName(topic, key);
		return sessionMap.get(name);
	}


	@Override
	public void removeSession(String topic, String key) {
		String name = Session.getName(topic, key);
		sessionMap.remove(name);
	}


	@Override
	public void saveConnect(IPusherClient socket) {
		String name = Session.getName(socket.getTopic(), socket.getKey());
		Session session=sessionMap.get(name);
		if(session==null){
			sessionMap.put(name, new Session(socket.getTopic(), socket.getKey(), socket));
			return;
		}
		session.addClient(socket);
	}


	@Override
	public void removeSession(Session session) {
		this.removeSession(session.getTopic(), session.getKey());
	}


	@Override
	public void removeConnect(IPusherClient socket) {
		Session session=getSession(socket.getTopic(), socket.getKey());
		if(session!=null){
			session.removeClient(socket);
		}
	}

}
