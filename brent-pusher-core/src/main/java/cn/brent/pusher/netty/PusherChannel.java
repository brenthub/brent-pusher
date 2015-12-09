package cn.brent.pusher.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import cn.brent.pusher.core.IPusherClient;

public class PusherChannel extends NioSocketChannel implements IPusherClient {

	protected static final AttributeKey<String> TOPIC = AttributeKey.newInstance("topic");

	protected static final AttributeKey<String> KEY = AttributeKey.newInstance("key");

	protected static final AttributeKey<Map<String, Object>> ATTRS = AttributeKey.newInstance("attrs");
	
	protected static final AttributeKey<Long> TIME_OUT = AttributeKey.newInstance("timeout");

	protected final long createTime;

	public PusherChannel(Channel parent, SocketChannel socket) {
		super(parent, socket);
		createTime = System.currentTimeMillis();
	}

	@Override
	public Object getAttr(String key) {
		Attribute<Map<String, Object>> attribute = attr(ATTRS);
		Map<String, Object> attrs = attribute.get();
		if (attrs == null) {
			return null;
		}
		return attrs.get(key);
	}

	@Override
	public void addAttr(String key, Object val) {
		Attribute<Map<String, Object>> attribute = attr(ATTRS);
		Map<String, Object> attrs = attribute.get();
		if (attrs == null) {
			attrs = new HashMap<String, Object>();
		}
		attrs.put(key, val);
	}

	@Override
	public void removeAttr(String key) {
		Attribute<Map<String, Object>> attribute = attr(ATTRS);
		Map<String, Object> attrs = attribute.get();
		if (attrs == null) {
			attrs = new HashMap<String, Object>();
		}
		attrs.remove(key);
	}

	@Override
	public String getKey() {
		Attribute<String> attribute = attr(KEY);
		return attribute.get();
	}

	@Override
	public String getTopic() {
		Attribute<String> attribute = attr(TOPIC);
		return attribute.get();
	}

	@Override
	public void setKey(String key) {
		Attribute<String> attribute = attr(KEY);
		attribute.set(key);
	}

	@Override
	public void setTopic(String topic) {
		Attribute<String> attribute = attr(TOPIC);
		attribute.set(topic);
	}
	
	@Override
	public Long getTimeOut() {
		Attribute<Long> attribute = attr(TIME_OUT);
		return attribute.get();
	}

	@Override
	public void setTimeOut(Long timeout) {
		if(timeout==null){
			return;
		}
		Attribute<Long> attribute = attr(TIME_OUT);
		attribute.set(timeout);
	}


	@Override
	public long getCreateTime() {
		return this.createTime;
	}

	@Override
	public void close(int code, String reason) {
		CloseWebSocketFrame clf=new CloseWebSocketFrame(code, reason);
        writeAndFlush(clf, newPromise()).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void send(String message) {
		TextWebSocketFrame msg=new TextWebSocketFrame(message);
		this.writeAndFlush(msg);
	}

	@Override
	public void send(String message, boolean close) {
		if (close) {
			TextWebSocketFrame msg=new TextWebSocketFrame(message);
			this.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                   CloseWebSocketFrame clf=new CloseWebSocketFrame(NORMAL, "push seccess");
                   writeAndFlush(clf, newPromise()).addListener(ChannelFutureListener.CLOSE);
                }
            });
		} else {
			this.send(message);
		}
	}


}
