package raele.rha.input.recorders;

import java.util.Scanner;

import raele.rha.input.HearthstoneLogScanner;
import raele.rha.input.Recorder;

public class IgnoreRecorder implements Recorder {

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		return  line.matches("^\\s*$") ||
				line.matches("^Begin MonoManager ReloadAssembly$") ||
				line.matches("^Platform assembly: .*") ||
				line.matches("^Loading .*") ||
				line.matches("^Unloading .*") ||
				line.matches("^\\[Asset\\].*") ||
				line.matches("^\\[Power\\].*") ||
				line.matches("^\\[Bob\\].*") ||
				line.matches("^- Completed reload.*") ||
				line.matches("^<RI>.*") ||
				line.matches("^Platform assembly: .*") ||
				line.matches("^Unloading [0-9]+ Unused Serialized files .*") ||
				line.matches("^Total: .*") ||
				line.matches("^\\(Filename: .*");
	}

}
