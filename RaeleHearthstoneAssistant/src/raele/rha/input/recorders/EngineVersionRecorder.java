package raele.rha.input.recorders;

import java.util.Scanner;

import raele.rha.input.HearthstoneLogScanner;
import raele.rha.input.Recorder;

public class EngineVersionRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return line.matches("^Initialize engine version: .+");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		String engineVersion = line.substring(27);
		log.setEngineVersion(engineVersion);
		return true;
	}

}
