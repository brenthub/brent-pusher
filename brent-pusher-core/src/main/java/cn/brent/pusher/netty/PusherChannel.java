package cn.brent.pusher.netty;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Map;

import cn.brent.pusher.core.IPusherClient;

public class PusherChannel extends NioServerSocketChannel implements IPusherClient {

	protected final AttributeKey<String> TOPIC = AttributeKey.newInstance("topic");

	protected final AttributeKey<String> KEY = AttributeKey.newInstance("key");

	protected final AttributeKey<Map<String, Object>> ATTRS = AttributeKey.newInstance("attrs");
	
	protected final long createTime;
	
	public PusherChannel() {
		createTime=System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#getAttr(java.lang.String)
	 */
	@Override
	public Object getAttr(String key) {
		Attribute<Map<String, Object>> attribute = attr(ATTRS);
		Map<String, Object> attrs = attribute.get();
		if (attrs == null) {
			return null;
		}
		return attrs.get(key);
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#addAttr(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addAttr(String key, Object val) {
		Attribute<Map<String, Object>> attribute = attr(ATTRS);
		Map<String, Object> attrs = attribute.get();
		if (attrs == null) {
			attrs = new HashMap<String, Object>();
		}
		attrs.put(key, val);
	}
	
	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#removeAttr(java.lang.String)
	 */
	@Override
	public void removeAttr(String key) {
		Attribute<Map<String, Object>> attribute = attr(ATTRS);
		Map<String, Object> attrs = attribute.get();
		if (attrs == null) {
			attrs = new HashMap<String, Object>();
		}
		attrs.remove(key);
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#getKey()
	 */
	@Override
	public String getKey() {
		Attribute<String> attribute = attr(KEY);
		return attribute.get();
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#getTopic()
	 */
	@Override
	public String getTopic() {
		Attribute<String> attribute = attr(TOPIC);
		return attribute.get();
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		Attribute<String> attribute = attr(TOPIC);
		attribute.set(key);
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#setTopic(java.lang.String)
	 */
	@Override
	public void setTopic(String topic) {
		Attribute<String> attribute = attr(TOPIC);
		attribute.set(topic);
	}

	/* (non-Javadoc)
	 * @see cn.brent.pusher.core.IClientChannel#getCreateTime()
	 */
	@Override
	public long getCreateTime() {
		return this.createTime;
	}

	@Override
	public void close(int code, String reason) {
		this.close();
	}

	@Override
	public void send(String message) {
		this.writeAndFlush(message);
	}

}
