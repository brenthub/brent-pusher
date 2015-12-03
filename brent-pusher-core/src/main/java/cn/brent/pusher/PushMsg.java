package cn.brent.pusher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class PushMsg implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private String topic;

	private String key;

	private boolean sucessClose = false;

	private Map<String, Object> data = new HashMap<String, Object>();

	public PushMsg(String topic, String key, boolean sucessClose,
			Map<String, Object> data) {
		super();
		this.topic = topic;
		this.key = key;
		this.sucessClose = sucessClose;
		this.data = data;
	}

	public PushMsg(String topic, String key, boolean sucessClose) {
		super();
		this.topic = topic;
		this.key = key;
		this.sucessClose = sucessClose;
	}
	
	public PushMsg(String topic, String key) {
		super();
		this.topic = topic;
		this.key = key;
	}

	public void addData(String key, Object o) {
		this.data.put(key, o);
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public boolean isSucessClose() {
		return sucessClose;
	}

	public void setSucessClose(boolean sucessClose) {
		this.sucessClose = sucessClose;
	}

	@Override
	public String toString() {
		return JSON.toJSON(this).toString();
	}

}
