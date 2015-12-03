package cn.brent.pusher.core;

import cn.brent.pusher.config.Constants;
import cn.brent.pusher.config.Plugins;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.config.Verifiers;

public class Config {

	private static  Constants constants = new Constants();
	private static  Verifiers verifiers = new Verifiers();
	private static  Plugins plugins = new Plugins();
	
	protected static void configPusher(PusherConfig pushConfig) {
		pushConfig.configConstant(constants);		
		pushConfig.configVerifier(verifiers);
		pushConfig.configPlugin(plugins);
	}

	// prevent new Config();
	private Config() {
	}

	public static Constants getConstants() {
		return constants;
	}

	public static Verifiers getVerifiers() {
		return verifiers;
	}

	public static Plugins getPlugins() {
		return plugins;
	}

}
