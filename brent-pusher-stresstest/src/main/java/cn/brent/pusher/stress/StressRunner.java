package cn.brent.pusher.stress;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.java_websocket.client.WebSocketClient;

public class StressRunner extends JFrame {
	/** */
	private static final long serialVersionUID = 1L;
	
	private JSlider clients;
	private JSlider joinrate;
	private JButton start, stop, reset;
	private JLabel joinratelabel = new JLabel();
	private JLabel clientslabel = new JLabel();
	private JLabel intervallabel = new JLabel();
	private JTextField uriinput = new JTextField("ws://127.0.0.1:8887/");
	private JTextField bizType = new JTextField("order");
	private Thread adjustthread;

	public StressRunner() {
		setTitle( "StressRunner" );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		start = new JButton( "Start" );
		start.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				start.setEnabled( false );
				stop.setEnabled( true );
				reset.setEnabled( false );
				clients.setEnabled( false );

				stopAdjust();
				adjustthread = new Thread( new Runnable() {
					@Override
					public void run() {
						try {
							adjust();
						} catch ( InterruptedException e ) {
							System.out.println( "adjust chanced" );
						}
					}
				} );
				adjustthread.start();

			}
		} );
		stop = new JButton( "Stop" );
		stop.setEnabled( false );
		stop.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				stopAdjust();
				start.setEnabled( true );
				stop.setEnabled( false );
				reset.setEnabled( true );
				joinrate.setEnabled( true );
				clients.setEnabled( true );
			}
		} );
		reset = new JButton( "reset" );
		reset.setEnabled( true );
		reset.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				while ( !websockets.isEmpty() )
					websockets.remove( 0 ).close();

			}
		} );
		joinrate = new JSlider( 0, 5000 );
		joinrate.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged( ChangeEvent e ) {
				joinratelabel.setText( "Joinrate: " + joinrate.getValue() + " ms " );
			}
		} );
		clients = new JSlider( 0, 10000 );
		clients.addChangeListener( new ChangeListener() {

			@Override
			public void stateChanged( ChangeEvent e ) {
				clientslabel.setText( "Clients: " + clients.getValue() );

			}
		} );

		setSize( 300, 400 );
		setLocationRelativeTo(null); 
		setLayout( new GridLayout( 10, 1, 10, 10 ) );
		add( new JLabel( "Base URI" ) );
		add( uriinput );
		add( new JLabel( "Biz Type" ) );
		add( bizType );
		add( joinratelabel );
		add( joinrate );
		add( clientslabel );
		add( clients );
		add( intervallabel );
		JPanel south = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		add( south );

		south.add( start );
		south.add( stop );
		south.add( reset );

		joinrate.setValue( 20 );
		clients.setValue( 100 );

	}

	List<WebSocketClient> websockets = Collections.synchronizedList( new LinkedList<WebSocketClient>() );
	public void adjust() throws InterruptedException {
		System.out.println( "Adjust" );
		int totalclients = clients.getValue();
		while ( websockets.size() < totalclients ) {
			URI uri;
			try {
				String sign=AddressHelper.generatePath(bizType.getText(), websockets.size()+"");
				uri = new URI( uriinput.getText()+sign );
			} catch ( URISyntaxException e ) {
				e.printStackTrace();
				continue;
			}
			WebSocketClient cl = new Client( uri ) {
				@Override
				public void onClose( int code, String reason, boolean remote ) {
					System.out.println( "Closed duo " + code + " " + reason );
					clients.setValue( websockets.size() );
					websockets.remove( this );
				}
			};

			cl.connect();
			clients.setValue( websockets.size() );
			websockets.add( cl );
			Thread.sleep( joinrate.getValue() );
		}
		while ( websockets.size() > clients.getValue() ) {
			websockets.remove( 0 ).close();
		}

	}

	public void stopAdjust() {
		if( adjustthread != null ) {
			adjustthread.interrupt();
			try {
				adjustthread.join();
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		new StressRunner().setVisible( true );
	}

}
