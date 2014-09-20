package raele.rha.input;

import java.util.Scanner;

public interface Recorder {
	
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner);

}
