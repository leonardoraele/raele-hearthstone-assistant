package input.recorders;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import input.LogEvent;
import input.HearthstoneLogScanner;
import input.Recorder;

public class ZoneRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return line.matches("^\\[Zone\\].*");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		String eventName = line.replaceAll("^\\[Zone\\] ", "").replaceAll(" - .*", "");
		LogEvent event = new LogEvent(eventName);
		
		Matcher zone = Pattern.compile("(\\w+) from ([\\w\\(\\) ]+)? -> ([\\w\\(\\) ]+)?").matcher(line);
		if (zone.find())
		{
			event.setZoneChange(zone.group(1), zone.group(2), zone.group(3));
			line = zone.replaceFirst("");
		}
		
		Matcher matcher = Pattern.compile("(\\w+)=((?:(?!\\s*\\w+=).)*)").matcher(line);
		while (matcher.find()) {
			String param = matcher.group(1);
			String value = matcher.group(2).replaceAll("\\s*[\\[\\]]\\s*", "");
			event.getProperties().setProperty(param, value);
		}
		
		log.recordEvent(event);
		return true;
	}

}
