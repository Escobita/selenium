package org.openqa.selenium.server.configuration;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

/**
 * Main listener for listening for when a particular option is modified to notify listeners.
 * 
 * @author Matthew Purland
 */
public class SeleniumConfigurationListener implements ConfigurationListener {

	private Configuration configuration;
	
	public SeleniumConfigurationListener(Configuration configuration) {
		this.configuration = configuration;
	}

	public enum PropertyConfigurationEventType {
		EVENT_ADD_PROPERTY(AbstractConfiguration.EVENT_ADD_PROPERTY), EVENT_SET_PROPERTY(
				AbstractConfiguration.EVENT_SET_PROPERTY), EVENT_CLEAR_PROPERTY(
				AbstractConfiguration.EVENT_CLEAR_PROPERTY), EVENT_READ_PROPERTY(
				AbstractConfiguration.EVENT_READ_PROPERTY);

		private final int type;

		PropertyConfigurationEventType(int type) {
			this.type = type;
		}

		/**
		 * Get the event type for the given integer type from {@link AbstractConfiguration}.
		 * 
		 * @param type
		 *            The type
		 * @return Returns the {@link PropertyConfigurationEventType}
		 */
		public static PropertyConfigurationEventType getEventType(int type) {
			PropertyConfigurationEventType[] values = PropertyConfigurationEventType
					.values();
			PropertyConfigurationEventType eventType = null;

			for (int i = 0; i < values.length && eventType == null; i++) {
				if (values[i].getType() == type) {
					eventType = values[i];
				}
			}

			return eventType;
		}

		/**
		 * Return the type for the event.
		 * 
		 * @return Returns the type.
		 */
		public int getType() {
			return type;
		}
	}

	/**
	 * 
	 * 
	 * Did not use {@link PropertyChangeEvent} due to wanted to return specific properties without
	 * casting from Object.
	 * 
	 * @see java.beans.PropertyChangeEvent
	 * 
	 * @author Matthew Purland
	 */
	public class PropertyConfigurationChangeEvent extends EventObject {

		private String propertyName;

		private Object oldValue;

		private Object newValue;

		private PropertyConfigurationEventType eventType;

		public PropertyConfigurationChangeEvent(Object source,
				String propertyName, Object oldValue, Object newValue,
				PropertyConfigurationEventType eventType) {
			super(source);

			this.propertyName = propertyName;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.eventType = eventType;
		}

		/**
		 * Get the event type.
		 * 
		 * @return Returns the event type.
		 */
		public PropertyConfigurationEventType getEventType() {
			return eventType;
		}

		/**
		 * Get the new value after modification.
		 * 
		 * @return Returns the new value.
		 */
		public Object getNewValue() {
			return newValue;
		}

		/**
		 * Get the old value before modification.
		 * 
		 * @return Returns the old value.
		 */
		public Object getOldValue() {
			return oldValue;
		}

		/**
		 * Get the property name.
		 * 
		 * @return Returns the property name.
		 */
		public String getPropertyName() {
			return propertyName;
		}

	}

	/**
	 * A property change listener for when events get fired for whenever a property changes.
	 * 
	 * @see java.beans.PropertyChangeListener
	 * 
	 * @author Matthew Purland
	 */
	public interface PropertyConfigurationChangeListener {
//		private T host;
//
//		/**
//		 * Construct a new PropertyConfigurationChangeListener with the given host object.
//		 * 
//		 * @param host
//		 *            The host object
//		 */
//		public PropertyConfigurationChangeListener(T host) {
//			this.host = host;
//		}
//
//		/**
//		 * Get the host object.
//		 * 
//		 * @return Returns the host object.
//		 */
//		public T getHost() {
//			return host;
//		}

		/**
		 * Notify the listener of a property change with the event.
		 * 
		 * @param event
		 *            The event
		 */
		void propertyChange(
				PropertyConfigurationChangeEvent event);
	}

	private Map<String, List<PropertyConfigurationChangeListener>> optionNameToConfigurationListenerListMap = new HashMap<String, List<PropertyConfigurationChangeListener>>();

	private Map<String, Object> eventOptionNameToOldValueMap = new HashMap<String, Object>();

	/**
	 * Add a property listener with the given optionName.
	 * 
	 * @param optionName
	 *            The option name
	 * @param listener
	 *            The listener to add
	 */
	public void addPropertyListener(String optionName,
			PropertyConfigurationChangeListener listener) {
		List<PropertyConfigurationChangeListener> optionListenerList = optionNameToConfigurationListenerListMap
				.get(optionName);

		if (optionListenerList == null) {
			optionListenerList = new ArrayList<PropertyConfigurationChangeListener>();
			optionNameToConfigurationListenerListMap.put(optionName,
					optionListenerList);
		}

		optionListenerList.add(listener);
	}

	/**
	 * Remove the given listener from the list of listeners for the given optionName.
	 * 
	 * @param optionName
	 *            The option name
	 * @param listener
	 *            The listener to remove
	 */
	public void removePropertyListener(String optionName,
			PropertyConfigurationChangeListener listener) {
		List<PropertyConfigurationChangeListener> optionListenerList = optionNameToConfigurationListenerListMap
				.get(optionName);

		if (optionListenerList != null) {
			optionListenerList.remove(listener);
		}
	}

	/**
	 * Notify property listeners for the option name with the given event.
	 * 
	 * @param optionName
	 *            The option to notify
	 * @param event
	 *            The event to notify the option with
	 */
	protected void notifyPropertyListeners(String optionName,
			PropertyConfigurationChangeEvent event) {
		List<PropertyConfigurationChangeListener> optionListenerList = optionNameToConfigurationListenerListMap
				.get(optionName);

		// If there are any listeners...
		if (optionListenerList != null) {
			// Notify each listener
			// @todo fix ConcurrentModificationException here because server starts up and adds another listener...
			for (PropertyConfigurationChangeListener listener : optionListenerList) {
				listener.propertyChange(event);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void configurationChanged(ConfigurationEvent event) {
		String optionName = event.getPropertyName();

		// Before modification
		if (event.isBeforeUpdate()) {
			Object oldValue = configuration.getProperty(optionName);
			
			// Put old value before modification
//			eventOptionNameToOldValueMap.put(optionName, event.getPropertyValue());
			eventOptionNameToOldValueMap.put(optionName, oldValue);
		}
		// After modification
		else {
			Object oldValue = eventOptionNameToOldValueMap.get(optionName);
			Object newValue = event.getPropertyValue();

			// Remove option from the map
			eventOptionNameToOldValueMap.remove(optionName);

			PropertyConfigurationEventType eventType = PropertyConfigurationEventType
					.getEventType(event.getType());

			// If eventType is null then the event type is not supported
			if (eventType != null) {
				// Only notify listeners for a valid event type
				PropertyConfigurationChangeEvent propertyConfigurationChangeEvent = new PropertyConfigurationChangeEvent(
						event.getSource(), optionName, oldValue, newValue,
						eventType);
				notifyPropertyListeners(optionName,
						propertyConfigurationChangeEvent);
			}

		}
	}
}
