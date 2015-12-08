package cn.brent.pusher.core;

public interface IPusherClient {
	
	/**
	 * 正常关闭
	 */
	public static final int NORMAL = 1000;
	
	/**
	 * 服务器拒绝
	 */
	public static final int REFUSE = 1003;
	
	
	/**
	 * 关闭连接
	 * @param code 状态码
	 * @param reason 原因
	 */
	void close(int code,String reason);

	/**
	 * 获取属性值
	 * @param key
	 * @return
	 */
	Object getAttr(String key);

	/**
	 * 新增属性值
	 * @param key
	 * @param val
	 */
	void addAttr(String key, Object val);

	/**
	 * 移除属性值
	 * @param key
	 */
	void removeAttr(String key);

	/**
	 * 获取key
	 * @return
	 */
	String getKey();

	/**
	 * 获取Topic
	 * @return
	 */
	String getTopic();

	/**
	 * 保存key
	 * @param key
	 */
	void setKey(String key);

	/**
	 * 保存Topic
	 * @param topic
	 */
	void setTopic(String topic);

	/**
	 * 获取创建时间
	 * @return
	 */
	long getCreateTime();

	/**
	 * 发送消息
	 * @param message
	 */
	void send(String message);
	
	/**
	 * 发送消息
	 * @param message
	 * @param close 发送成功后是否关闭连接
	 */
	void send(String message,boolean close);
	

	
}