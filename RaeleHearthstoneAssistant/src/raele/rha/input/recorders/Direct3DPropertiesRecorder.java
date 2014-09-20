package raele.rha.input.recorders;

import java.util.Properties;
import java.util.Scanner;

import raele.rha.input.HearthstoneLogScanner;
import raele.rha.input.Recorder;

public class Direct3DPropertiesRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return line.matches("^Direct3D:$");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		Properties properties = new Properties();
		properties.setProperty("Version", scanner.nextLine().substring(14));
		properties.setProperty("Renderer", scanner.nextLine().substring(14));
		properties.setProperty("Vendor", scanner.nextLine().substring(14));
		properties.setProperty("VRAM", scanner.nextLine().substring(14));
		properties.setProperty("Caps", scanner.nextLine().substring(14));
		log.setDirect3D(properties);
		log.setReadLinesCount(log.getReadLinesCount() + 5);
		return true;
	}

}
