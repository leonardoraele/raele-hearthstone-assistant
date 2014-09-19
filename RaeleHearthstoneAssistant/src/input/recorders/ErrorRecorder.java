package input.recorders;

import java.util.Scanner;

import input.HearthstoneLogScanner;
import input.Recorder;

public class ErrorRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return line.matches("^Error.*");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		System.out.println("Log do Hearthstone imprimiu um erro: " + line);
		return true;
	}

}
