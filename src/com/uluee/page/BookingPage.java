package com.uluee.page;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;

import com.uluee.UlueeUI;
import com.uluee.util.CallSOAPAction;
import com.uluee.util.Utils;
import com.uluee.util.CallSOAPAction.ISOAPResultCallBack;
import com.uluee.window.SearchMapWindow;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class BookingPage extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4000695485102518121L;
	private static final String DELETE = "delete";
	private static final String INSURANCE = "insurance";
	private static final String WEIGHT = "weight";
	private static final String VOLUME = "volume";
	private static final String COMMODITY = "commodity";
	private static final Object PIECES = "pieces";
	private TextField deliveryTextField;
	private TextField originTextField;
	private LMap leafletMap;
	private TextField insuranceTf;
	private TextField weightTf;
	private TextField volumeTf;
	private TextField piecesTf;
	private MyComboBox commodityCb;
	private Table table;
	
	private ClickListener openMapPickup = new ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			UI.getCurrent().addWindow(new SearchMapWindow(originTextField));
			
		}
	};
	
	private ClickListener openMapDelivery = new ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			UI.getCurrent().addWindow(new SearchMapWindow(deliveryTextField));
			
		}
	};
	
	private ClickListener pickupSearchListener = new ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			
			String value = originTextField.getValue();
			if(value.isEmpty())return;
			
			value = value.replace(" ", "+");
			

		}
	};

	private TextChangeListener combocommodityListener = new TextChangeListener() {

		@Override
		public void textChange(TextChangeEvent event) {
			
			final MyComboBox cb = (MyComboBox) event.getComponent();
			String value = event.getText();
			System.out.println(value);
			if(value == null || value.length() < 2) return;
			
			String sessionId = ((UlueeUI)UI.getCurrent()).getSessionId();
			
			LinkedHashMap<String, Object> param = new LinkedHashMap<String, Object>();
			param.put("sessionId", sessionId);
			param.put("match", value);
			new CallSOAPAction(param, "getCommodityByMatch", new ISOAPResultCallBack() {
				
				@Override
				public void handleResult(SoapObject data) {
					cb.removeAllItems();
					for (int i = 0; i < data.getPropertyCount(); i++) {
						String temp = data.getProperty(i).toString();
						String[] temps = temp.split("\\|");
						String name = temps[2];
						String comId = temps[1];
						String annId = temps[0];
						
						Item item = cb.addItem(temps);				
						cb.setItemCaption(temps, name);
					}
					cb.markAsDirty();
				}
				
				@Override
				public void handleError() {
					
				}
			});
			
			
		}
		
		
	};
	private ClickListener insertToTableListener = new ClickListener(){

		@Override
		public void buttonClick(ClickEvent event) {
			String[] comArgs = (String[] ) commodityCb.getValue();
			
			String volume = volumeTf.getValue();
			String weight = weightTf.getValue();
			String itemValue = insuranceTf.getValue();
			String pieces = piecesTf.getValue();
			
			String name = comArgs[2];
			String comId = comArgs[1];
			String annId = comArgs[0];
			String sccCode = null;
			String sccName = null;
			if (comArgs.length > 3) {
				sccCode = comArgs[3];
				sccName = comArgs[4];
			}
			
			String id = comId+":"+annId+":"+name+"|"+Utils.checkNull(sccCode)+"|"+pieces+"|Each|"+weight+"||||"+volume+"|"+Utils.checkNull(itemValue)+"||||||";
			
			Button b = new Button("Delete");
			b.addStyleName(ValoTheme.BUTTON_DANGER);
			b.addStyleName(ValoTheme.BUTTON_TINY);
			b.addClickListener(new TableButtonDelete(id, table));
			Item i = table.addItem(id);
			i.getItemProperty(COMMODITY).setValue(name);
			i.getItemProperty(PIECES).setValue(pieces);
			i.getItemProperty(WEIGHT).setValue(weight);
			i.getItemProperty(VOLUME).setValue(volume);
			i.getItemProperty(INSURANCE).setValue(itemValue);
			i.getItemProperty(DELETE).setValue(b);
//			stringCommodities = stringCommodities + "&&"
//					+ txtCommodity.getValue() + ":"
//					+ "0" +":"+tempList.get(i).getCommodity()+ "|"
//					+ tempList.get(i).getScc() + "|"
//					+ tempList.get(i).getPieces() + "|" + "Each"
//					+ "|" + tempList.get(i).getWeight() + "|"
//					+ tempLength + "|" + tempWidth + "|"
//					+ tempHeight + "|"
//					+ tempList.get(i).getVolume()
//					+ "|"+sInsurance+"| | | | | | ";
		}
		
	};

	public BookingPage(){
		createContents();
		setHeight(100, Unit.PERCENTAGE);
		setWidth(100, Unit.PERCENTAGE);
	}

	private void createContents() {
		VerticalLayout root = new VerticalLayout();
		root.setHeight(100, Unit.PERCENTAGE);
		root.setWidth(100, Unit.PERCENTAGE);
		root.setSpacing(true);
		
		CssLayout originLayout = new CssLayout();
		originLayout.setCaption("Pickup");
		originLayout.addStyleName("v-component-group");
		
		CssLayout deliveryLayout = new CssLayout();
		deliveryLayout.setCaption("Delivery");
		deliveryLayout.addStyleName("v-component-group");
		
		MarginInfo marginBookingLayout = new MarginInfo(true);
		marginBookingLayout.setMargins(false, true, false, true);
		
		HorizontalLayout bookingLayout = new HorizontalLayout();
		bookingLayout.setMargin(marginBookingLayout);
		bookingLayout.setSpacing(true);
		
		bookingLayout.setHeight(180, Unit.PIXELS);
		bookingLayout.setWidth(null);
		
		FormLayout form = new FormLayout();
		form.setWidth(null);
		
		VerticalLayout commodityLayout = createCommodityLayout();
		
		bookingLayout.addComponent(form);
		
		bookingLayout.setExpandRatio(form, 0.0f);

		Button searchOriginButton = new Button("Open map");
		Button searchDeliveryButton = new Button("Open map");
		
		searchOriginButton.addClickListener(openMapPickup);
		searchDeliveryButton.addClickListener(openMapDelivery);
		
		searchOriginButton.addStyleName(ValoTheme.BUTTON_TINY);
		searchDeliveryButton.addStyleName(ValoTheme.BUTTON_TINY);
		
		form.setHeight(null);
		form.setWidth(null);
		
		Button searchScheduleButton = new Button("Search schedule");
		searchScheduleButton.addStyleName(ValoTheme.BUTTON_SMALL);
		searchScheduleButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		searchScheduleButton.setHeight(null);
		
		Label dummyLabel = new Label(" ");
		dummyLabel.setHeight(100, Unit.PERCENTAGE);
		
		originTextField = new TextField();
		deliveryTextField = new TextField();
		
		originTextField.setEnabled(false);
		deliveryTextField.setEnabled(false);
		
		originTextField.setRequired(true);
		deliveryTextField.setRequired(true);
		
		originTextField.setWidth(300, Unit.PIXELS);
		deliveryTextField.setWidth(300, Unit.PIXELS);
		
		originTextField.addStyleName(ValoTheme.TEXTAREA_TINY);
		deliveryTextField.addStyleName(ValoTheme.TEXTAREA_TINY);

		originLayout.addComponent(originTextField);
		originLayout.addComponent(searchOriginButton);
		
		deliveryLayout.addComponent(deliveryTextField);
		deliveryLayout.addComponent(searchDeliveryButton);
		
		form.addComponent(originLayout);
		form.addComponent(deliveryLayout);
	
		root.addComponent(bookingLayout);
		root.addComponent(searchScheduleButton);
		root.addComponent(commodityLayout);
		root.addComponent(dummyLabel);
		
		root.setExpandRatio(bookingLayout, 0.0f);
		root.setExpandRatio(commodityLayout, 0.0f);
		root.setExpandRatio(searchScheduleButton, 0.0f);
		root.setExpandRatio(dummyLabel, 1.0f);
		
		root.setComponentAlignment(bookingLayout, Alignment.TOP_CENTER);
		root.setComponentAlignment(commodityLayout, Alignment.BOTTOM_CENTER);
		root.setComponentAlignment(searchScheduleButton, Alignment.BOTTOM_CENTER);

		addComponent(root);
		
	}
	
	private VerticalLayout createCommodityLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(null);
		layout.setHeight(null);
		layout.setSpacing(true);
		
		HorizontalLayout commodityForm = new HorizontalLayout();
		commodityForm.setSpacing(true);
		commodityForm.setHeight(null);
		
		commodityCb = new MyComboBox();
		piecesTf = new TextField();
		volumeTf = new TextField();
		weightTf = new TextField();
		insuranceTf = new TextField();
		
		commodityCb.setImmediate(false);
//		commodityCb.addValueChangeListener(commodityInputListener);
		commodityCb.addListener(combocommodityListener);
		
		commodityCb.setInputPrompt("Commodity");
		piecesTf.setInputPrompt("Pieces");
		volumeTf.setInputPrompt("Volume");
		weightTf.setInputPrompt("Weight");
		insuranceTf.setInputPrompt("Item value");
		
		commodityCb.setWidth(80, Unit.PERCENTAGE);
		piecesTf.setWidth(50, Unit.PIXELS);
		volumeTf.setWidth(50, Unit.PIXELS);
		weightTf.setWidth(50, Unit.PIXELS);
		insuranceTf.setWidth(70, Unit.PIXELS);
		
		insuranceTf.setDescription("Commodity value if this should be insured");
		
		commodityCb.addStyleName(ValoTheme.COMBOBOX_TINY);
		volumeTf.addStyleName(ValoTheme.TEXTAREA_TINY);
		weightTf.addStyleName(ValoTheme.TEXTAREA_TINY);
		insuranceTf.addStyleName(ValoTheme.TEXTAREA_TINY);
		insuranceTf.addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
		piecesTf.addStyleName(ValoTheme.TEXTAREA_ALIGN_RIGHT);
		piecesTf.addStyleName(ValoTheme.TEXTAREA_TINY);
		
		commodityCb.setRequired(true);
		volumeTf.setRequired(true);
		weightTf.setRequired(true);
		piecesTf.setRequired(true);
		
		Button insertButton = new Button("Insert");
		insertButton.addClickListener(insertToTableListener );
		insertButton.addStyleName(ValoTheme.BUTTON_TINY);
		insertButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		commodityForm.addComponent(commodityCb);
		commodityForm.addComponent(piecesTf);
		commodityForm.addComponent(volumeTf);
		commodityForm.addComponent(weightTf);
		commodityForm.addComponent(insuranceTf);
		commodityForm.addComponent(insertButton);
		
		commodityForm.setExpandRatio(commodityCb, 1.0f);
		commodityForm.setExpandRatio(volumeTf, 0.0f);
		commodityForm.setExpandRatio(weightTf, 0.0f);
		commodityForm.setExpandRatio(insuranceTf, 0.0f);
		
		table = commodityTable();
		
		layout.addComponent(table);
		layout.addComponent(commodityForm);
		
		layout.setExpandRatio(table, 0.0f);
		layout.setExpandRatio(commodityForm, 1.0f);
		
		return layout;
	}


	private Table commodityTable() {
		table = new Table();
		table.addContainerProperty(COMMODITY, String.class, null);
		table.addContainerProperty(PIECES, String.class, null);
		table.addContainerProperty(VOLUME, String.class, null);
		table.addContainerProperty(WEIGHT, String.class, null);
		table.addContainerProperty(INSURANCE, String.class, null);
		table.addContainerProperty(DELETE, Component.class, null);
		
		table.setHeight(200, Unit.PIXELS);
		table.setWidth(500, Unit.PIXELS);
		
		table.setFooterVisible(true);
//		table.setColumnFooter(PIECES, "");
//		table.setColumnFooter(VOLUME, "");
//		table.setColumnFooter(WEIGHT, "");
//		table.setColumnFooter(INSURANCE, "");
		
		table.setColumnHeader(COMMODITY, "Commodity");
		table.setColumnHeader(VOLUME, "Volume");
		table.setColumnHeader(PIECES, "Pieces");
		table.setColumnHeader(WEIGHT, "Weight");
		table.setColumnHeader(INSURANCE, "Insurance");
		table.setColumnHeader(DELETE, " ");
		
		return table;
	}

	public class MyComboBox extends ComboBox {

	    /**
		 * 
		 */
		private static final long serialVersionUID = -8584263967056053843L;

		@Override
	    public void changeVariables(Object source, Map<String, Object> variables) {
	        if (variables.containsKey("filter")) {
	            final String text = variables.get("filter").toString();
	            fireEvent(new TextChangeEvent(this) {

	                @Override
	                public String getText() {
	                    return text;
	                }

	                @Override
	                public int getCursorPosition() {
	                    return text.length();
	                }
	            });
	        }
	        super.changeVariables(source, variables);
	    }

	    public void addListener(TextChangeListener listener) {
	        addListener(TextChangeListener.EVENT_ID, TextChangeEvent.class,
	                listener, TextChangeListener.EVENT_METHOD);
	    }

	    public void removeListener(TextChangeListener listener) {
	        removeListener(TextChangeListener.EVENT_ID, TextChangeEvent.class,
	                listener);
	    }
	}
	
	private class TableButtonDelete implements ClickListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object itemId;
		private Table table;
		public TableButtonDelete(Object itemId, Table table) {
			this.itemId = itemId;
			this.table = table;
		}
		@Override
		public void buttonClick(ClickEvent event) {
			table.removeItem(itemId);
			
		}
		
	}
}
