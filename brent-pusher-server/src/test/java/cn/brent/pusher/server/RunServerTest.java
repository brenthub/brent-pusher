package cn.brent.pusher.server;

import cn.brent.pusher.config.Constants;
import cn.brent.pusher.config.Plugins;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.config.Verifiers;
import cn.brent.pusher.plugin.CleanUpPlugin;
import cn.brent.pusher.plugin.MonitorPlugin;
import cn.brent.pusher.session.Session;
import cn.brent.pusher.verifier.BizFilterVerifier;
import cn.brent.pusher.verifier.SignVerifier;
import cn.brent.pusher.verifier.SocketLimitVerifier;

public class RunServerTest {
	
	public static void main(String[] args) {
		PusherServer server=new PusherServer(8887, new PusherConfig() {
			@Override
			public void configVerifier(Verifiers me) {
				me.add(new BizFilterVerifier("order","mic","card"));
				me.add(new SocketLimitVerifier(3));
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
			}
			
			@Override
			public void configPlugin(Plugins me) {
				me.add(new CleanUpPlugin(60, 10));
				me.add(new MonitorPlugin(3));
			}
			
			@Override
			public void configConstant(Constants me) {
			}
		});
		server.start();
	}

	
}
