package input.recorders;

import java.util.Scanner;

import input.HearthstoneLogScanner;
import input.Recorder;

public class GfxDeviceRecorder implements Recorder {

	public boolean isResponsible(String line) {
		return line.matches("^GfxDevice: .+");
	}

	@Override
	public boolean record(String line, HearthstoneLogScanner log, Scanner scanner) {
		if (!this.isResponsible(line)) return false;
		String gfxDevice = line.substring(11);
		log.setGfxDevice(gfxDevice);
		return true;
	}

}
