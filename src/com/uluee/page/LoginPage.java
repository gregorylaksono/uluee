package com.uluee.page;

import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapObject;

import com.uluee.UlueeUI;
import com.uluee.util.CallSOAPAction;
import com.uluee.util.CallSOAPAction.ISOAPResultCallBack;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class LoginPage extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7399435971607660382L;
	private PasswordField password;
	private TextField username;
	private ClickListener loginListener = new ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			String uname = username.getValue();
			String pwd = password.getValue();
			if(uname.isEmpty() || pwd.isEmpty()) return;
			
			ISOAPResultCallBack callback = new ISOAPResultCallBack() {
				
				@Override
				public void handleResult(SoapObject data) {
					String sessionId = data.getProperty("sessionId").toString();
					((UlueeUI)UI.getCurrent()).setSessionId(sessionId);
					((UlueeUI)UI.getCurrent()).setPage(new BookingPage());
					
				}
				
				@Override
				public void handleError() {
					
				}
			};
			
			LinkedHashMap<String, Object>param = new LinkedHashMap<String, Object>();
			param.put("username", uname);
			param.put("password", pwd);
			new CallSOAPAction(param, "login",callback);
			
		}
	};
	public LoginPage(){
		createContents();
		setHeight(100, Unit.PERCENTAGE);
		setWidth(100, Unit.PERCENTAGE);
	}

	private void createContents() {
		VerticalLayout root = new VerticalLayout();
		root.setHeight(185, Unit.PIXELS);
		root.setWidth(250, Unit.PIXELS);
		root.setSpacing(true);
		
		FormLayout form = new FormLayout();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		buttonLayout.setSpacing(true);
		
		buttonLayout.setWidth(100, Unit.PERCENTAGE);
		form.setHeight(100, Unit.PERCENTAGE);
		
		username = new TextField("Username");
		password = new PasswordField("Password");
		form.addComponent(username);
		form.addComponent(password);
		
		Button login = new Button("Login");
		Button registerUser = new Button("Register user");
		Button registerCourier = new Button("Register courier");
		
		login.addClickListener(loginListener);
		
		registerUser.setWidth(null);
		
		login.setWidth(100, Unit.PERCENTAGE);
		
		login.addStyleName(ValoTheme.BUTTON_PRIMARY);
		registerUser.addStyleName(ValoTheme.BUTTON_SMALL);
		registerCourier.addStyleName(ValoTheme.BUTTON_SMALL);
		
		buttonLayout.addComponent(registerUser);
		buttonLayout.addComponent(registerCourier);

		buttonLayout.setExpandRatio(registerUser, 0.5f);
		buttonLayout.setExpandRatio(registerCourier, 0.5f);
		
		root.addComponent(form);
		root.addComponent(login);
		root.addComponent(buttonLayout);
		
		root.setExpandRatio(form, 1.0f);
		root.setExpandRatio(login, 0.0f);
		root.setExpandRatio(buttonLayout, 0.0f);
		
		addComponent(root);
		setComponentAlignment(root, Alignment.MIDDLE_CENTER);
		
	}

}

