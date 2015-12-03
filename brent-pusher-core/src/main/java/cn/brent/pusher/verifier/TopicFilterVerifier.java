package cn.brent.pusher.verifier;

import java.util.ArrayList;
import java.util.List;

import cn.brent.pusher.IVerifier;
import cn.brent.pusher.core.PathDesc;
import cn.brent.pusher.session.Session;

public class TopicFilterVerifier implements IVerifier {

	protected List<String> topics = new ArrayList<String>();
	
	public TopicFilterVerifier(String... topic) {
		for(String b:topic){
			topics.add(b);
		}
	}
	
	@Override
	public boolean verify(PathDesc path,Session session) {
		return topics.contains(path.getTopic());
	}

	public void addTopic(String topic){
		topics.add(topic);
	}

	@Override
	public String failMsg(PathDesc path) {
		return "Unsupported topic";
	}
}
