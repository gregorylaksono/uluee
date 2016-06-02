package com.uluee;

import javax.servlet.annotation.WebServlet;

import com.uluee.page.LoginPage;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("uluee")
public class UlueeUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = UlueeUI.class, widgetset = "com.uluee.widgetset.UlueeWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	
	private String sessionId;
	private VerticalLayout mainLayout;

	@Override
	protected void init(VaadinRequest request) {
		createAndSetMainLayout();

	}

	private void createAndSetMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setHeight(100, Unit.PERCENTAGE);
		setContent(mainLayout);
		setPage(new LoginPage());
	}
	
	public void setPage(Layout layout){
		mainLayout.removeAllComponents();
		mainLayout.addComponent(layout);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	

}