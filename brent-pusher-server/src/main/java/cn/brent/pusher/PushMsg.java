package cn.brent.pusher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class PushMsg implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private String biz;

	private String key;

	private boolean sucessClose = false;

	private Map<String, Object> data = new HashMap<String, Object>();

	public PushMsg(String biz, String key, boolean sucessClose,
			Map<String, Object> data) {
		super();
		this.biz = biz;
		this.key = key;
		this.sucessClose = sucessClose;
		this.data = data;
	}

	public PushMsg(String biz, String key, boolean sucessClose) {
		super();
		this.biz = biz;
		this.key = key;
		this.sucessClose = sucessClose;
	}
	
	public PushMsg(String biz, String key) {
		super();
		this.biz = biz;
		this.key = key;
	}

	public void addData(String key, Object o) {
		this.data.put(key, o);
	}

	public String getBiz() {
		return biz;
	}

	public void setBiz(String biz) {
		this.biz = biz;
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
