package raele.rha.core;

import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import raele.rha.input.HearthstoneLogScanner;
import raele.rha.input.LogEvent;
import raele.rha.input.LogEventListener;
import raele.rha.model.GameModel;
import raele.rha.model.gui.GameModelController;
import raele.rha.model.gui.GameModelGUI;
import raele.rha.persistence.Dao;
import raele.util.awt.EmptyWindowListenerStub;
import raele.util.database.h2.H2Database;
import raele.util.javafx.JFXFrame;

public class Main {
	
	private static final String OUTPUTLOG_FILENAME = "C:/Program Files (x86)/Hearthstone/Hearthstone_Data/output_log.txt";
	protected static final long DISPOSE_DELAY_TO_SYSTEM_EXIT = 5000l;
	
	public static void main(String[] args) throws Exception {
		JFXFrame<Void> loading = new JFXFrame<Void>("Loading...", new File("res/fxml/Loading.fxml").toURI().toURL());
		loading.dispose();
		loading.setUndecorated(true);
		loading.setLocationRelativeTo(null);
		loading.setVisible(true);
		
		H2Database.start();
		Dao.boot("H2");
		
		HearthstoneLogScanner scanner = new HearthstoneLogScanner(OUTPUTLOG_FILENAME);
		GameModel model = new GameModel();
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
							((GameModelController) gui.getController()).refresh();
						}
					}
				});
		
		gui.addWindowListener(new EmptyWindowListenerStub() { // TODO <- Deveria ter isso??
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				scanner.stop();
				Dao.closeAll();
				H2Database.stop();
				
				new Timer(true).schedule(new TimerTask() {
					@Override
					public void run() {
						System.exit(0);
					}
				}, DISPOSE_DELAY_TO_SYSTEM_EXIT);
			}
		});
		
		scanner.refresh();
		gui.getController().resetModel();
		scanner.start();
		gui.setVisible(true);
		loading.dispose();
	}
	
}
