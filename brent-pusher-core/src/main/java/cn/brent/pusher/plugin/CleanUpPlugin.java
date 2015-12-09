package cn.brent.pusher.plugin;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.IPlugin;
import cn.brent.pusher.core.IPusherClient;
import cn.brent.pusher.session.ISessionManager;

public class CleanUpPlugin implements IPlugin {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Thread cleanUpThead;

	/** 超时时间 毫秒 为空时永不超时*/
	protected final Long timeout;
	
	/** 检查间隔 毫秒 */
	protected final long checkInterval;

	public CleanUpPlugin(Long timeout, long checkInterval) {
		this.timeout = timeout;
		this.checkInterval = checkInterval;
	}

	/**
	 * 清扫任务
	 * 
	 * @param timeout
	 * @param checkInterval
	 * @param handler
	 */
	protected void cleanUp() {
		logger.debug("start cleanUp...");
		long startTime = System.currentTimeMillis();
		
		Iterator<IPusherClient> it = ISessionManager.clientQueue.iterator();
		
		while (it.hasNext()) {
			IPusherClient conn = (IPusherClient) it.next();
			if(this.timeout==null&&conn.getTimeOut()==null){
				continue;
			}
			
			long timeout=conn.getTimeOut()==null?this.timeout:conn.getTimeOut();
			
			if (System.currentTimeMillis() - conn.getCreateTime() >= timeout) {
				logger.debug("cleanUp remove connect:" + conn);
				conn.close(IPusherClient.NORMAL, "connect timeout");
			}
		}
			
		long reflushCostTime = System.currentTimeMillis() - startTime;
		logger.debug("end cleanUp Task cost time:" + reflushCostTime);
	}

	@Override
	public void start() {
		cleanUpThead = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						cleanUp();
						Thread.sleep(checkInterval);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("CleanUpTask error", e);
					}
				}
			}
		});
		cleanUpThead.start();
	}

	@Override
	public void stop() {
	}

}
