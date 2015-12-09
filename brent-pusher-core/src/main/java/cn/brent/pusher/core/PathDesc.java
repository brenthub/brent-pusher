package cn.brent.pusher.core;

import com.alibaba.fastjson.JSON;

public class PathDesc {

	private String topic;

	private String key;

	private String sign;

	/** 超时时间，超时间为空时，为默认超时时间 */
	private Long timeOut;

	private PathDesc() {
	}

	public static PathDesc parse(String pathDesc) {
		try {
			PathDesc desc = new PathDesc();
			String t[] = pathDesc.split("\\?");
			String path = t[0];
			desc.key = path.substring(path.lastIndexOf("/") + 1);
			desc.topic = path.substring(1, path.lastIndexOf("/"));

			if(t.length==1){
				return desc;
			}
			String params = t[1];
			String param[] = params.split("&");
			for (String pa : param) {
				if (pa.startsWith("sign=")) {
					desc.sign = pa.substring(pa.indexOf("=") + 1);
					continue;
				}
				if (pa.startsWith("timeout=")) {
					desc.timeOut = Long.parseLong(pa.substring(pa.indexOf("=") + 1));
					continue;
				}
			}
			return desc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("request path illegal");
		}
	}

	public static void main(String[] args) {
		PathDesc pd = PathDesc.parse("/order/2343423234?timeout=23&sign=34");
		System.out.println(pd.toString());
	}

	public String getTopic() {
		return topic;
	}

	public String getKey() {
		return key;
	}

	public String getSign() {
		return sign;
	}

	public Long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public String toString() {
		return JSON.toJSON(this).toString();
	}

}
