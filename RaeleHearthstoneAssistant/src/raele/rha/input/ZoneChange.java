package raele.rha.input;
	
public class ZoneChange {
	
	public static final String ZONE_HAND = "HAND";
	public static final String ZONE_PLAY = "PLAY";
	public static final String FRIENDLY_HERO = "FRIENDLY PLAY (Hero)";
	public static final String FRIENDLY_DECK = "FRIENDLY DECK";
	public static final String FRIENDLY_HAND = "FRIENDLY HAND";
	public static final String FRIENDLY_PLAY = "FRIENDLY PLAY";
	public static final String FRIENDLY_GRAV = "FRIENDLY GRAVEYARD";
	public static final String OPPOSING_HERO = "OPPOSING PLAY (Hero)";
	public static final String OPPOSING_DECK = "OPPOSING DECK";
	public static final String OPPOSING_HAND = "OPPOSING HAND";
	public static final String OPPOSING_PLAY = "OPPOSING PLAY";
	public static final String OPPOSING_GRAV = "OPPOSING GRAVEYARD";
	
	private String type;
	private String from;
	private String to;
	
	public ZoneChange(String type, String from, String to) {
		this.type = type;
		this.from = from;
		this.to = to;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "(" + type + " from " + from + " -> " + to + ")";
	}
	
}