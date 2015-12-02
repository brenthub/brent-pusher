package cn.brent.pusher.plugin;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import cn.brent.pusher.IPlugin;
import cn.brent.pusher.PushMsg;
import cn.brent.pusher.server.PusherServer;

import com.alibaba.fastjson.JSONObject;

/**
 *
 */
public class JettyServicePlugin implements IPlugin {
	
	protected int port;
	
	public JettyServicePlugin(int port) {
		this.port=port;
	}
	

	@Override
	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				startJetty();
			}
		}).start();
	}
	
	protected void startJetty(){
		try {
			Server server = new Server(port);
			ServletContextHandler sch=new ServletContextHandler();
			sch.addServlet(new ServletHolder(new Servlet() {

				@Override
				public void init(ServletConfig config) throws ServletException {
					
				}

				@Override
				public ServletConfig getServletConfig() {
					return null;
				}

				@Override
				public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
					String biz=req.getParameter("biz");
					String key=req.getParameter("key");
					JSONObject json=new JSONObject();
					json.put("success", true);
					PusherServer.push(new PushMsg(biz, key, true,json));
				}

				@Override
				public String getServletInfo() {
					return null;
				}

				@Override
				public void destroy() {
					
				}
				
			}), "/push");
			server.setHandler(sch);
			server.start();
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {

	}

}
