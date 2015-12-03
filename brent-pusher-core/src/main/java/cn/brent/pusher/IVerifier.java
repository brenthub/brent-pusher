package cn.brent.pusher;

import cn.brent.pusher.core.PathDesc;
import cn.brent.pusher.session.Session;

/**
 * 连接认证
 */
public interface IVerifier {

	/**
	 * 认证 成功true
	 * @param path
	 * @return
	 */
	boolean verify(PathDesc path,Session session);
	
	/**
	 * 当认证失败返回的异常信息
	 * @return
	 */
	String failMsg(PathDesc path);
}
