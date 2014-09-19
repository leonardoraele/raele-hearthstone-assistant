package input.recorders;

import java.util.Scanner;

import input.HearthstoneLogScanner;
import input.Recorder;

public class IgnoreRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return  line.matches("^\\s*$") ||
				line.matches("^Begin MonoManager ReloadAssembly$") ||
				line.matches("^Platform assembly: .*") ||
				line.matches("^Loading .*") ||
				line.matches("^Unloading .*") ||
				line.matches("^- Completed reload.*") ||
				line.matches("^<RI>.*") ||
				line.matches("^Platform assembly: .*") ||
				line.matches("^Unloading [0-9]+ Unused Serialized files .*") ||
				line.matches("^Total: .*") ||
				line.matches("^\\(Filename: .*");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		return true;
	}

}
