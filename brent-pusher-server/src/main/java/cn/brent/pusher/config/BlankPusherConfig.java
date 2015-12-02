package cn.brent.pusher.config;

import cn.brent.pusher.plugin.CleanUpPlugin;
import cn.brent.pusher.plugin.JettyServicePlugin;
import cn.brent.pusher.plugin.MonitorPlugin;
import cn.brent.pusher.server.PathDesc;
import cn.brent.pusher.session.Session;
import cn.brent.pusher.verifier.BizFilterVerifier;
import cn.brent.pusher.verifier.SignVerifier;
import cn.brent.pusher.verifier.SocketLimitVerifier;

public class BlankPusherConfig extends PusherConfig {

	@Override
	public void configVerifier(Verifiers me) {
		me.add(new BizFilterVerifier("order"));
		me.add(new SignVerifier() {
			@Override
			public boolean verify(PathDesc path, Session session) {
				if(path.getSign().equals("000000")){
					return true;
				}else{
					return false;
				}
			}
		});
		me.add(new SocketLimitVerifier(10));
	}
	
	@Override
	public void configPlugin(Plugins me) {
		me.add(new CleanUpPlugin(60*20, 10));
		me.add(new MonitorPlugin(3));
		me.add(new JettyServicePlugin(8888));
	}
	
	@Override
	public void configConstant(Constants me) {
	}

}
