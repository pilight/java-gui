package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import classes.RegexFormatter;

public class SettingsDlg {
	
	private JFrame frame;
	private JProgressBar progressBar;
	private JLabel lblServer;
	private JLabel lblPort;
	private JFormattedTextField txtServer;
	private JFormattedTextField txtPort;
	
	private Runnable funcSave = null;
	
	private int posX = 0;
	private int posY = 0;
	
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
		frame.setBounds(0, 0, 300, 150);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("433-controller :: Settings");
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
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
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 20;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 5, 5);
		gbc.weightx = 0;
		gbc.weighty = 1;
		
		lblServer = new JLabel("Server:");
		lblServer.setHorizontalAlignment(SwingConstants.LEFT);
		lblServer.setHorizontalTextPosition(JLabel.LEFT);
		panel.add(lblServer, gbc);		
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(5, 5, 5, 0);
		gbc.weightx = 1;
		gbc.weighty = 1;

		String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		final Pattern p1 = Pattern.compile( "^(?:" + _255 + "\\.){3}" + _255 + "$");
		RegexFormatter ipFormatter = new RegexFormatter(p1);
		txtServer = new JFormattedTextField(ipFormatter);
		txtServer.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
		        String text = ((JTextField) input).getText();
		        if(p1.matcher(text).find()) {
		        	input.setForeground(Color.BLACK);
		        	return true;
		        } else {
		        	input.setForeground(Color.RED);
		        	return false;
		        }
		    }
		});
		panel.add(txtServer, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 20;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 5, 5);
		gbc.weightx = 0;
		gbc.weighty = 1;
		
		lblPort = new JLabel("Port:");
		lblPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblPort.setHorizontalTextPosition(JLabel.LEFT);
		panel.add(lblPort, gbc);	
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.ipadx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 0);
		gbc.weightx = 1;
		gbc.weighty = 1;		
		
		final Pattern p2 = Pattern.compile("^[0-9]{1,5}$");
		RegexFormatter portFormatter = new RegexFormatter(p2);
		txtPort = new JFormattedTextField(portFormatter);
		txtPort.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
		        String text = ((JTextField) input).getText();
		        if(p2.matcher(text).find()) {
		        	input.setForeground(Color.BLACK);
		        	return true;
		        } else {
		        	input.setForeground(Color.RED);
		        	return false;
		        }
		    }
		});
		panel.add(txtPort, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.ipadx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 0);
		gbc.weightx = 0;
		gbc.weighty = 1;		
		
		JButton button = new JButton("Save");
		button.addActionListener(buttonAction);
		panel.add(button, gbc);
		
		frame.getContentPane().add(panel);
	}
	
	private ActionListener buttonAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			onSave();
		}
	};
	
	public void setServer(String server) {
		txtServer.setText(server);
	}
	
	public void setPort(int port) {
		txtPort.setText(new String().valueOf(port));
	}
	
	public String getServer() {
		return txtServer.getText();
	}
	
	public String getPort() {
		return txtPort.getText();
	}	
	
	public void setOnSave(Runnable func) {
		this.funcSave = func;
	}
	
	public void onSave() {
		this.funcSave.run();
	}
};
