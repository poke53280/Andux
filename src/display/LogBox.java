package display;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;
import java.util.Observable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Label;

public class LogBox extends Panel implements Observer {

	TextField userField;
	TextField passField;
	Label message;
	Button loginButton;
	Button newButton;


	public LogBox() {
		super();

		add(new Label("Log in now") );

		userField = new TextField(15);
		add(userField);

		passField = new TextField(15);
		passField.setEchoChar('*');

		add(passField);

		loginButton = new Button("Login");
		add(loginButton);

		newButton = new Button("new user");
		add(newButton);

		message = new Label("status messages here");
		add(message);

	}

	public void addLoginListener(ActionListener l) {
		loginButton.addActionListener(l);
	}

	public void addNewListener(ActionListener l) {
		newButton.addActionListener(l);
	}

	public String getUsername() {
		return userField.getText();
	}

	public String getPassword() {
		return passField.getText();
	}

	public void setData(String username, String password) {
		userField.setText(username);
		passField.setText(password);
	}

	public void setMessage(String msg) {
		message.setText(msg);
	}

	public void update(Observable o, Object arg) {
		if (arg != null) {
			Graphics g = getGraphics();
			g.drawString(arg.toString(), 10,10 );
		} else {
			System.out.println("LogBox: arg is null");
		}
	}



}

