package cn.brent.pusher.config;

import java.util.ArrayList;
import java.util.List;

import cn.brent.pusher.IVerifier;

/**
 * 认证管理
 */
final public class Verifiers {

	private final List<IVerifier> list = new ArrayList<IVerifier>();
	
	/**
	 * 新增认证器
	 * @param verifier
	 * @return
	 */
	public Verifiers add(IVerifier verifier) {
		if (verifier != null)
			this.list.add(verifier);
		return this;
	}
	
	public IVerifier[] getAll() {
		return list.toArray(new IVerifier[list.size()]);
	}
	
}
