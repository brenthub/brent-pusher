package cn.brent.pusher.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;

public class PusherWebSocket extends WebSocketImpl {
	
	/** 创建时间  */
	protected long createTime;
	
	/** 业务ID */
	protected String key;
	
	/** 业务类型 */
	protected String biz;
	
	/** 属性 */
	protected Map<String,Object> attrs;

	public PusherWebSocket(WebSocketListener listener, Draft draft) {
		super(listener, draft);
		this.createTime=System.currentTimeMillis();
	}
	
	public PusherWebSocket( WebSocketListener listener , List<Draft> drafts) {
		super(listener, drafts);
		this.createTime=System.currentTimeMillis();
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
	
	public String getKey() {
		return key;
	}

	public String getBiz() {
		return biz;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setBiz(String biz) {
		this.biz = biz;
	}

	public long getCreateTime() {
		return createTime;
	}
	
}
