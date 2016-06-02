package com.uluee.window;

import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapObject;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.uluee.util.CallSOAPAction;
import com.uluee.util.CallSOAPAction.ISOAPResultCallBack;
import com.uluee.util.Utils;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

public class SearchMapWindow extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8721411974967872837L;
	private TextField text;
	private LMap leafletMap;
	
	private String city;
	private String country;
	private String latLang;
	private String postalCode;
	private String province;
	private String subCity;
	private String route;
	private String streetNr;
	
	private LMarker marker;
	
	private ClickListener selectListener = new ClickListener(){

		@Override
		public void buttonClick(ClickEvent event) {
			if(latLang == null){
				UI.getCurrent().removeWindow(SearchMapWindow.this);
				Notification.show("You haven't select any point. Window will be closed", Type.HUMANIZED_MESSAGE);
				return;
			}
			UI.getCurrent().removeWindow(SearchMapWindow.this);
			
			String value = route+" "+streetNr+" "+city+", "+country;
			text.setEnabled(true);
			text.setValue(value);
			text.setEnabled(false);
			text.setData(latLang);
		}
		
	};
	
	LeafletClickListener listener = new LeafletClickListener() {

		@Override
		public void onClick(LeafletClickEvent event) {
			Point p = event.getPoint();
			if (p != null) {
				final Double lat = p.getLat();
				final Double lon = p.getLon();
				
				String value = lat+","+lon;
				
				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("area", value);
				new CallSOAPAction(map, "getAreasGeoCode", new ISOAPResultCallBack() {
					
					@Override
					public void handleResult(SoapObject data) {
						if(data.getPropertyCount() > 0){
							SoapObject obj = (SoapObject) data.getProperty(0);
							city = Utils.checkNull(obj.getProperty("city").toString());
							country = Utils.checkNull(obj.getProperty("country").toString());
							latLang = Utils.checkNull(obj.getProperty("latlang").toString());
							postalCode = Utils.checkNull(obj.getProperty("postalCode").toString());
							province = Utils.checkNull(obj.getProperty("province").toString());
							subCity = Utils.checkNull(obj.getProperty("subcity").toString());
							route = Utils.checkNull(obj.getProperty("route").toString());
							streetNr = Utils.checkNull(obj.getProperty("streetNr"));
						}
						
						if(marker != null)leafletMap.removeComponent(marker);
						
						String popUp = "<div><b>"+route+" "+streetNr+" "+subCity+" "+city+"</b><br>"
									+ "<i>"+country+"</i>"
									+ "</div>";
						marker = new LMarker(lat,lon);
						marker.setPopup(popUp);
						leafletMap.addComponent(marker);
					}
					
					@Override
					public void handleError() {
						
						
					}
				});
			}
		}
	};
	
	
	public SearchMapWindow(TextField text){
		this.text = text;
		setDraggable(true);
		setClosable(true);
		setResizable(false);
		createContents();
		center();
	}
	


	private void createContents() {
		VerticalLayout layout = new VerticalLayout();
		layout.setCaption("Please select a point");
		layout.setHeight(400, Unit.PIXELS);
		layout.setWidth(400, Unit.PIXELS);
		layout.setMargin(true);
		
		layout.setSpacing(true);
		Component map = createMap();
		
		Button selectButton = new Button("Select");
		selectButton.addStyleName(ValoTheme.BUTTON_SMALL);
		selectButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		selectButton.addClickListener(selectListener);
		layout.addComponent(map);
		layout.addComponent(selectButton);
		
		layout.setComponentAlignment(map, Alignment.TOP_CENTER);
		layout.setComponentAlignment(selectButton, Alignment.BOTTOM_CENTER);
		
		layout.setExpandRatio(map, 1.0f);
		layout.setExpandRatio(selectButton, 0.0f);
		
		setContent(layout);
	}
	private Component createMap() {
	leafletMap = new LMap();
	leafletMap.addClickListener(listener);
//	leafletMap.setCenter(-6.159218, 106.824628);
	leafletMap.setZoomLevel(1);
	leafletMap.addBaseLayer(new LOpenStreetMapLayer(),"OSM");

//	LMarker marker = new LMarker(-0.789275,113.921327);
//	marker.setPopup("Indonesia");
//	leafletMap.addComponent(marker);
	leafletMap.setHeight(100, Unit.PERCENTAGE);
	
	return leafletMap;
}
}
