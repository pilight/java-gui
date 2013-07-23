package gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.json.JSONObject;

import classes.NSocket;

public class SwitchActionListener implements ActionListener {
	private String location;
	private String device;
	private boolean status;
	
	public SwitchActionListener(String location, String device, boolean status) {
		this.location = location;
		this.device = device;
		this.status = status;
	}
	
	public void actionPerformed(ActionEvent e) {
		JToggleButton button = (JToggleButton)e.getSource();
		this.status = button.isSelected();
		if(this.status) {
			button.setText("On");
		} else {
			button.setText("Off");
		}
		JSONObject json = new JSONObject();
		JSONObject code = new JSONObject();
		json.put("message", "send");
		code.put("location", this.location);
		code.put("device", this.device);
		code.put("state", this.status);
		json.put("code", code);
		NSocket.write(json.toString());
	}
}