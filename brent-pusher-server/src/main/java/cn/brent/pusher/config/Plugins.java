package cn.brent.pusher.config;

import java.util.ArrayList;
import java.util.List;

import cn.brent.pusher.IPlugin;

final public class Plugins { 

private final List<IPlugin> list = new ArrayList<IPlugin>();
	
	/**
	 * 新增插件
	 * @param verifier
	 * @return
	 */
	public Plugins add(IPlugin verifier) {
		if (verifier != null)
			this.list.add(verifier);
		return this;
	}
	
	public IPlugin[] getAll() {
		return list.toArray(new IPlugin[list.size()]);
	}
}
