package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class LoaderDlg {
	
	private JFrame frame;
	private JProgressBar progressBar;
	private JLabel lblMessage;
	
	private int posX = 0;
	private int posY = 0;
	
	private Runnable funcSettings = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	}
	
	public void show() {
		frame.setVisible(true);
		frame.toFront();
		frame.repaint();
	}
	
	public void close() {
		frame.dispose();
	}
	
	public void hide() {
		frame.setVisible(false);
	}
	
	public void disable() {
		frame.setEnabled(false);
	}
	
	public void enable() {
		frame.setEnabled(true);
		frame.toFront();
		frame.repaint();
	}
	
	public boolean enabled() {
		return (frame != null && frame.isEnabled() && frame.isVisible());
	}
	
	public void continous() {
		progressBar.setIndeterminate(true); 
	}
	
	public void progress() {
		progressBar.setIndeterminate(false); 
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	public void create() {
		frame = new JFrame();
		frame.setBounds(0, 0, 300, 100);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("433-controller :: Loader");
		frame.setLocationRelativeTo(null);
		//frame.setUndecorated(true);
		frame.addMouseListener(new MouseAdapter() {
		   public void mousePressed(MouseEvent e) {
		      posX=e.getX();
		      posY=e.getY();
		   }
		});
		frame.addMouseMotionListener(new MouseAdapter() {
		     public void mouseDragged(MouseEvent evt) {		
				frame.setLocation(evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);
							
		     }
		});
		
//		JMenuBar jmb = new JMenuBar();
//		JMenu jmSettings = new JMenu("Settings");
//		jmSettings.addMenuListener(settingsAction);
//		jmb.add(jmSettings);
//		frame.setJMenuBar(jmb);		
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipady = 20;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		lblMessage = new JLabel("Connecting to server...");
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setHorizontalTextPosition(JLabel.CENTER);
		panel.add(lblMessage, gbc);		
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 1;
		gbc1.ipady = 0;
		gbc1.fill = GridBagConstraints.HORIZONTAL;
		gbc1.anchor = GridBagConstraints.CENTER;
		gbc1.weightx = 1;
		gbc1.weighty = 1;
		
		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);

		panel.add(progressBar, gbc1);
		frame.getContentPane().add(panel);
	}
	
//	private MenuListener settingsAction = new MenuListener() {
//		public void menuSelected(MenuEvent e) {
//			onSettings();
//		}
//		public void menuDeselected(MenuEvent e) { };
//		public void menuCanceled(MenuEvent e) { };
//	};
	
//	public void setOnSettings(Runnable func) {
//		this.funcSettings = func;
//	}
//	
//	public void onSettings() {
//		this.funcSettings.run();
//	}		
	
	public void update(int percent, String message) {
		lblMessage.setText(message);
		progressBar.setValue(percent);		
	}
}
