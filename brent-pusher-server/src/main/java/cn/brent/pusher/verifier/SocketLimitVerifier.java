package cn.brent.pusher.verifier;

import cn.brent.pusher.IVerifier;
import cn.brent.pusher.server.PathDesc;
import cn.brent.pusher.session.Session;

public class SocketLimitVerifier implements IVerifier {

	private final int num;
	
	public SocketLimitVerifier(int num) {
		this.num=num;
	}
	
	@Override
	public boolean verify(PathDesc path,Session session) {
		if(session==null){
			return true;
		}
		if(session.getSockets().size()>=num){
			return false;
		}
		return true;
	}

	@Override
	public String failMsg(PathDesc path) {
		return "connect for ["+path.getBiz()+"/"+path.getKey()+"] reach limit";
	}
}
