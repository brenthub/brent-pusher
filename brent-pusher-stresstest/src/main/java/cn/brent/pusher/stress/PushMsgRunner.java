package cn.brent.pusher.stress;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.brent.pusher.PushHelper;
import cn.brent.pusher.PushMsg;
import cn.brent.pusher.config.Constants;
import cn.brent.pusher.config.Plugins;
import cn.brent.pusher.config.PusherConfig;
import cn.brent.pusher.config.Verifiers;
import cn.brent.pusher.core.PathDesc;
import cn.brent.pusher.netty.NettyPusherServer;
import cn.brent.pusher.plugin.CleanUpPlugin;
import cn.brent.pusher.plugin.MonitorPlugin;
import cn.brent.pusher.session.Session;
import cn.brent.pusher.verifier.SignVerifier;
import cn.brent.pusher.verifier.SocketLimitVerifier;
import cn.brent.pusher.verifier.TopicFilterVerifier;

import com.alibaba.fastjson.JSONObject;

public class PushMsgRunner extends JFrame {
	/** */
	private static final long serialVersionUID = 1L;
	
	private JButton start;
	private JTextField bizType = new JTextField("order");
	private JTextField key = new JTextField("1000000");
	private JTextField content = new JTextField("{\"success\":true}");
	private JTextField result = new JTextField("");
	private JComboBox<String> sucessClose = new JComboBox<String>(new String[]{"true","false"});

	public PushMsgRunner() {
		setTitle( "Send Msg" );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		start = new JButton( "Send" );
		start.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				result.setText("waiting...");
				start.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						send();
						start.setEnabled(true);
					}
				}).start();

			}

		} );

		setSize( 300, 430 );
		setLocationRelativeTo(null); 
		setLayout( new GridLayout( 11,1, 10, 10 ) );
		add( new JLabel( "Biz Type" ) );
		add( bizType );
		add( new JLabel( "Key" ) );
		add( key );
		add( new JLabel( "Content(JSON)" ) );
		add( content );
		add( new JLabel( "SucessClose" ) );
		add( sucessClose );
		add( new JLabel( "Result" ) );
		result.setEditable(false);
		add( result );
		
		JPanel south = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		add( south );

		south.add( start );
	}

	private void send() {
		
		String biz=bizType.getText();
		String k=key.getText();
		String json=content.getText();
		boolean isclose=Boolean.parseBoolean((String)sucessClose.getSelectedItem());
		JSONObject j=null;
		try {
			j=(JSONObject)JSONObject.parse(json);
		} catch (Exception e) {
			result.setText("content is not a json");
			return;
		}
		PushMsg msg=new PushMsg(biz, k, isclose,j);
		try {
			PushHelper.push(msg);
			result.setText("send sucess");
		} catch (Exception e) {
			result.setText("send failed,reason is "+e.getMessage());
			return;
		}
	}

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		new NettyPusherServer(new PusherConfig() {
			@Override
			public void configVerifier(Verifiers me) {
				me.add(new TopicFilterVerifier("order"));
				me.add(new SignVerifier() {
					@Override
					public boolean verify(PathDesc path, Session session) {
						if(path.getSign().equals("000000")){
							return true;
						}else{
							return false;
						}
					}
				});
				me.add(new SocketLimitVerifier(10));
			}
			
			@Override
			public void configPlugin(Plugins me) {
				me.add(new CleanUpPlugin(10L, 10));
				me.add(new MonitorPlugin(3));
			}
			
			@Override
			public void configConstant(Constants me) {
				
			}
		}).start();
		new PushMsgRunner().setVisible( true );
	}

}
