package input;

import java.util.Properties;

public class LogEvent {
	
	public static final String PROPERTY_NAME = "name";
	
	private String name;
	private Properties properties;
	private ZoneChange zoneChange;
	
	public LogEvent(String name) {
		super();
		this.name = name;
		this.properties = new Properties();
		this.zoneChange = null;
	}

	public ZoneChange getZoneChange() {
		return zoneChange;
	}

	public void setZoneChange(ZoneChange zoneChange) {
		this.zoneChange = zoneChange;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "LogEvent:" + name;
	}

	public void setZoneChange(String type, String from, String to) {
		this.setZoneChange(new ZoneChange(type, from, to));
	}

}
