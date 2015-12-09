package cn.brent.pusher.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.brent.pusher.core.IPusherClient;

public class Session {
	
	/** sessionID */
	protected String name;

	/** 创建时间  */
	protected long createTime;

	/** 业务ID */
	protected String key;
	
	/** 业务类型 */
	protected String topic;

	/** WebSockets */
	protected Collection<IPusherClient> clients=new CopyOnWriteArrayList<IPusherClient>();
	
	/** 属性 */
	protected Map<String,Object> attrs;

	public Session(String topic, String key) {
		this.name = getName(topic, key);
		this.key=key;
		this.topic=topic;
		this.createTime = System.currentTimeMillis();
	}
	
	public Session(String topic, String key, IPusherClient socket) {
		this(topic, key);
		this.addClient(socket);
	}
	
	/**
	 * 获取topic和key生成的ID
	 * @param topic
	 * @param key
	 * @return
	 */
	public static String getName(String topic,String key){
		return topic+"/"+key;
	}

	public long getCreateTime() {
		return createTime;
	}

	public Collection<IPusherClient> getClients() {
		return clients;
	}
	
	public void addClient(IPusherClient socket) {
		clients.add(socket);
	}
	
	public void removeClient(IPusherClient socket) {
		clients.remove(socket);
	}
	
	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getTopic() {
		return topic;
	}

	public Object getAttr(String key) {
		if(attrs==null){
			return null;
		}
		return attrs.get(key);
	}
	
	public void addAttr(String key,Object val) {
		if(attrs==null){
			attrs=new HashMap<String, Object>();
		}
		attrs.put(key,val);
	}
}
