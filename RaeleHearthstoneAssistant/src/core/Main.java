package core;

import java.awt.event.WindowEvent;
import java.util.Locale;

import persistence.Dao;
import raele.util.awt.SimpleWindowListener;
import raele.util.database.h2.H2Database;
import model.GameModel;
import model.gui.GameModelGUI;
import model.gui.DeckModelController;
import input.HearthstoneLogScanner;
import input.LogEvent;
import input.LogEventListener;

public class Main {
	
	private static final String OUTPUTLOG_FILENAME = "C:/Program Files (x86)/Hearthstone/Hearthstone_Data/output_log.txt";
	private static GameModel model = new GameModel();
	
	public static void main(String[] args) throws Exception {
		HearthstoneLogScanner scanner = new HearthstoneLogScanner(OUTPUTLOG_FILENAME);
		GameModelGUI gui = new GameModelGUI(model);
		
		scanner.addListener(
				new LogEventReceiver(
						model,
						new Locale("en", "US"),
						"H2"
				)
		);
		
		scanner.addListener(
				new LogEventListener() {
					@Override
					public void recordEvent(LogEvent event) {
						if (event.getZoneChange() != null)
						{
							((DeckModelController) gui.getController()).refreshDecklist();
						}
					}
				});
		
		H2Database.start();
		Dao.boot("H2");
		scanner.start();
		
		gui.addWindowListener(new SimpleWindowListener() { // TODO <- Deveria ter isso??
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				scanner.stop();
				Dao.closeAll();
				H2Database.stop();
			}
		});

		gui.setVisible(true);
	}
	
}
