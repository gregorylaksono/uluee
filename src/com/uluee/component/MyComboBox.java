package com.uluee.component;

import java.util.Map;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.ComboBox;

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