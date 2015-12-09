package cn.brent.pusher.session.imp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.brent.pusher.core.IPusherClient;
import cn.brent.pusher.session.ISessionManager;
import cn.brent.pusher.session.Session;

public class MapSessionManager implements ISessionManager {

	protected Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

	@Override
	public Session getSession(String topic, String key) {
		String name = Session.getName(topic, key);
		return sessionMap.get(name);
	}


	@Override
	public void removeSession(String topic, String key) {
		this.removeSession(getSession(topic, key));
	}


	@Override
	public void saveConnect(IPusherClient client) {
		String name = Session.getName(client.getTopic(), client.getKey());
		Session session=sessionMap.get(name);
		if(session==null){
			session=new Session(client.getTopic(), client.getKey());
			sessionMap.put(name, session);
		}
		session.addClient(client);
		clientQueue.add(client);
	}


	@Override
	public void removeSession(Session session) {
		this.removeSession(session.getTopic(), session.getKey());
		for(IPusherClient client:session.getClients()){
			clientQueue.remove(client);
		}
	}


	@Override
	public void removeConnect(IPusherClient client) {
		Session session=getSession(client.getTopic(), client.getKey());
		if(session!=null){
			session.removeClient(client);
		}
		clientQueue.remove(client);
	}

}
