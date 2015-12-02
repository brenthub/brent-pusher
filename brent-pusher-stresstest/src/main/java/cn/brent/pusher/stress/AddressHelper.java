package cn.brent.pusher.stress;

import org.apache.commons.codec.digest.DigestUtils;

public class AddressHelper {
	

	/**
	 * 生成path后面一截
	 * @param bizType
	 * @param key
	 * @return
	 */
	public static String generatePath(String bizType,String key){
		String sign;
		String src=bizType+"/"+key;
		try {
			sign="000000";
		} catch (Exception e) {
			throw new RuntimeException("sign failed");
		}
		return src+"?sign="+sign;
	}
	
	/**
	 * 验证签名
	 * @param bizType
	 * @param key
	 * @param sign
	 * @return
	 */
	public static boolean vidationSign(String bizType,String key,String sign){
		String src=bizType+"/"+key;
		try {
			String sginRet=DigestUtils.sha1Hex(src).toString();
			if(sign.equals(sginRet)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
