package cn.brent.pusher.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.IPlugin;
import cn.brent.pusher.session.ISessionManager;

public class MonitorPlugin implements IPlugin {
	
	protected Logger logger = LoggerFactory.getLogger("monitor");
	
	protected Thread monitorThread;
	
	/** 监控扫描间隔 单位秒   默认5分钟 */
	protected long monitorInterval = 5 * 60 * 1000;
	
	public MonitorPlugin(long monitorInterval) {
		this.monitorInterval = monitorInterval*1000;
	}
	
	protected void log() {
		logger.info(ISessionManager.clientQueue.size()+"");
	}

	@Override
	public void start() {
		monitorThread = new Thread() {
			public void run() {
				while (true) {
					try {
						log();
						Thread.sleep(monitorInterval);
					} catch (Exception e) {
						logger.error("Monitor run error: ", e);
					}
				}
			}

		};
		monitorThread.start();
	}

	@Override
	public void stop() {

	}

}
