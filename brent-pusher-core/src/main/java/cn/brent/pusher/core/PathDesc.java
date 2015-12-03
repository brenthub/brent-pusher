package cn.brent.pusher.core;

import com.alibaba.fastjson.JSON;

public class PathDesc {

	private String topic;

	private String key;

	private String sign;
	
	private PathDesc(){
	}
	
	public static PathDesc parse(String pathDesc){
		try {
			PathDesc desc=new PathDesc();
			String t[]=pathDesc.split("\\?");
			String path=t[0];
			String params=t[1];
			desc.key=path.substring(path.lastIndexOf("/")+1);
			desc.topic=path.substring(1, path.lastIndexOf("/"));
			
			String param[]=params.split("&");
			for(String pa:param){
				if(pa.startsWith("sign=")){
					desc.sign=pa.substring(pa.indexOf("=")+1);
					break;
				}
			}
			return desc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("pathDesc Illegal");
		}
	}
	
	public static void main(String[] args) {
		PathDesc pd=PathDesc.parse("/order/2343423234?sign=order-2343423234");
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
	
	@Override
	public String toString() {
		return JSON.toJSON(this).toString();
	}

}
