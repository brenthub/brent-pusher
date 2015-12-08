package cn.brent.pusher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.core.Config;
import cn.brent.pusher.core.IPusherClient;
import cn.brent.pusher.core.PusherServer;
import cn.brent.pusher.session.Session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class PushHelper {

	private static Logger logger = LoggerFactory.getLogger(PusherServer.class);

	/**
	 * 推送消息
	 * 
	 * @param msg
	 */
	public static void push(PushMsg msg) {

		logger.info("push msg " + msg);

		if (msg.getTopic() == null) {
			logger.error(msg + "topic is null");
			return;
		}
		if (msg.getKey() == null) {
			logger.error(msg + "key is null");
			return;
		}
		if (msg.getData() == null || msg.getData().size() == 0) {
			logger.error(msg + "data is null");
			return;
		}

		Session session = Config.getConstants().getSessionManager().getSession(msg.getTopic(), msg.getKey());
		if (session == null) {
			return;
		}
		JSON ret = new JSONObject(msg.getData());

		for (IPusherClient webc : session.getClients()) {
			try {
				webc.send(ret.toJSONString(),msg.isSucessClose());
			} catch (Exception e) {
				logger.error("push failed:", e);
			}
		}
		
	}

}
