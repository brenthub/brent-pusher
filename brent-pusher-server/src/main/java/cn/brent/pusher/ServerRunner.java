package cn.brent.pusher;

import java.io.IOException;
import java.util.Properties;

import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.server.PusherServer;

public class ServerRunner {
	
	public static final String configName="/pusher.conf";

	public static void main(String[] args) {

		Properties p=new Properties();
		try {
			p.load(ServerRunner.class.getResourceAsStream(configName));
		} catch (IOException e1) {
			System.out.println("can't find config file "+configName);
			return;
		}
		
		String port=p.getProperty("port", "8887");
		String configClass=p.getProperty("configClass", "cn.brent.pusher.config.BlankPusherConfig");
		
		Object obj;
		try {
			obj = Class.forName(configClass).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
		if(obj instanceof PusherConfig){
			PusherConfig config=(PusherConfig)obj;
			PusherServer server=new PusherServer(Integer.parseInt(port),config);
			server.start();
		}else{
			System.out.println("");
			return;
		}
		
	}

}
