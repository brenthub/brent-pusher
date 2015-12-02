package cn.brent.pusher.verifier;

import java.util.ArrayList;
import java.util.List;

import cn.brent.pusher.IVerifier;
import cn.brent.pusher.server.PathDesc;
import cn.brent.pusher.session.Session;

public class BizFilterVerifier implements IVerifier {

	protected List<String> allowBiz = new ArrayList<String>();
	
	public BizFilterVerifier(String... biz) {
		for(String b:biz){
			allowBiz.add(b);
		}
	}
	
	@Override
	public boolean verify(PathDesc path,Session session) {
		return allowBiz.contains(path.getBiz());
	}

	public void addBizType(String biz){
		allowBiz.add(biz);
	}

	@Override
	public String failMsg(PathDesc path) {
		return "Unsupported biz type";
	}
}
