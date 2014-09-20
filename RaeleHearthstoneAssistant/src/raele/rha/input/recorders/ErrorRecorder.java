package raele.rha.input.recorders;

import java.util.Scanner;

import raele.rha.input.HearthstoneLogScanner;
import raele.rha.input.Recorder;

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
