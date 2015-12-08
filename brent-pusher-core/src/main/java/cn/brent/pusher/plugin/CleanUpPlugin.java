package cn.brent.pusher.plugin;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.pusher.IPlugin;
import cn.brent.pusher.core.IPusherClient;
import cn.brent.pusher.session.ISessionManager;

public class CleanUpPlugin implements IPlugin {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/** 上一次清除时间 */
	protected long lastcleanUpTime;

	protected final BlockingQueue<Runnable> processQueue = new LinkedBlockingQueue<Runnable>(50); // 处理队列(全局公用)

	protected final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 50, 30, TimeUnit.SECONDS, processQueue,
			new ThreadPoolExecutor.DiscardOldestPolicy());

	protected Thread cleanUpThead;

	/** 超时时间 秒 */
	protected final long timeout;
	
	/** 检查间隔 秒 */
	protected final long checkInterval;

	public CleanUpPlugin(long timeout, long checkInterval) {
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
		if (System.currentTimeMillis() - this.lastcleanUpTime < checkInterval * 1000) {
			return;
		}
		logger.debug("start cleanUp...");
		long startTime = System.currentTimeMillis();
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				Iterator<IPusherClient> it = ISessionManager.clientQueue.iterator();
				while (it.hasNext()) {
					IPusherClient conn = (IPusherClient) it.next();
					if (System.currentTimeMillis() - conn.getCreateTime() >= timeout * 1000) {
						logger.debug("cleanUp remove connect:" + conn);
						conn.close(IPusherClient.NORMAL, "connect timeout");
					} else {// 第一次出现不超时，后面也不会超时
						return;
					}
				}
			}
		});
		this.lastcleanUpTime = System.currentTimeMillis();
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
						Thread.sleep(5000);// 暂停五秒
					} catch (Exception e) {
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
