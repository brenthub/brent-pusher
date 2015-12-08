package cn.brent.pusher.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.config.Constants;
import cn.brent.pusher.config.Plugins;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.config.Verifiers;
import cn.brent.pusher.plugin.CleanUpPlugin;
import cn.brent.pusher.plugin.MonitorPlugin;
import cn.brent.pusher.verifier.SignVerifier;
import cn.brent.pusher.verifier.SocketLimitVerifier;
import cn.brent.pusher.verifier.TopicFilterVerifier;

/**
 * 业务配置，不同业务可以重新定制此类-后期考虑移出此工程
 * 不同业务可能有区别
 */
public class CommonConfig extends PusherConfig {
	
	static Logger logger=LoggerFactory.getLogger(CommonConfig.class);
	
	public static final String ProName = "/pusher_common.properties";
	
	boolean topicFilterStart = Boolean.parseBoolean(getConfig("topicFilter.start", "false"));
	String topicFilterTopics = getConfig("topicFilter.topics", "");
	
	boolean signStart = Boolean.parseBoolean(getConfig("sign.start", "false"));
	String signClass = getConfig("sign.class", "");
	
	boolean keyMaxConnectStart = Boolean.parseBoolean(getConfig("keyMaxConnect.start", "false"));
	String keyMaxConnectLimit = getConfig("keyMaxConnect.limit", "5");
	
	boolean monitorStart = Boolean.parseBoolean(getConfig("monitor.start", "false"));
	String monitorInterval = getConfig("monitor.interval", "30");
	
	boolean cleanStart = Boolean.parseBoolean(getConfig("clean.start", "false"));
	String cleanCheckInterval = getConfig("clean.checkInterval", "30");
	String cleanTimeout = getConfig("clean.timeout", "1200");
	
	int port=Integer.parseInt(getConfig("port", "8887"));
	
	protected static Properties p=new Properties();
	static{
		try {
			p.load(CommonConfig.class.getResourceAsStream(ProName));
		} catch (IOException e) {
			System.out.println("can't find config file "+ProName);
		}
	}

	private static String getConfig(String key,String def){
		return p.getProperty(key, def);
	}
	
	@Override
	public void configVerifier(Verifiers me) {//顺序很重要
		if (topicFilterStart&&StringUtils.isNotEmpty(topicFilterTopics)) {
			TopicFilterVerifier bv=new TopicFilterVerifier();
			for (String topic : topicFilterTopics.split(",")) {
				bv.addTopic(topic);
			}
			me.add(bv);
		}
		
		if(signStart&&StringUtils.isNotEmpty(signClass)){
			SignVerifier sv;
			try {
				sv = (SignVerifier)Class.forName(signClass).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("init signClass failed");
			}
			me.add(sv);
		}
		
		if (keyMaxConnectStart){
			me.add(new SocketLimitVerifier(Integer.parseInt(keyMaxConnectLimit)));
		}
	}
	
	@Override
	public void configPlugin(Plugins me) {
		if(cleanStart){
			me.add(new CleanUpPlugin(Long.parseLong(cleanTimeout), Long.parseLong(cleanCheckInterval)));
		}
		if (monitorStart){
			me.add(new MonitorPlugin(Integer.parseInt(monitorInterval)));
		}
	}
	
	@Override
	public void configConstant(Constants me) {
		me.setPort(port);
	}

}
