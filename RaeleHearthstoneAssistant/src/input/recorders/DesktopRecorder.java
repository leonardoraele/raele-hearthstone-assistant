package input.recorders;

import java.util.Scanner;

import input.HearthstoneLogScanner;
import input.Recorder;

public class DesktopRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return line.matches("^desktop: .+");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		log.setDesktop(line.replaceAll("; virtual: .*$", "").replaceAll("^desktop: ", ""));
		log.setVirtual(line.replaceAll("^desktop: .*; virtual: ", ""));
		return true;
	}

}
