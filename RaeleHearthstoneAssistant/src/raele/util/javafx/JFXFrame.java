package raele.util.javafx;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;

import raele.util.awt.EmptyWindowListenerStub;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Callback;

public class JFXFrame<GenericController> extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JFXPanel jfxPanel;
	private Parent parent;
	private GenericController controller;
	private FXMLLoader loader;
	
	public JFXFrame(String title, URL fxmlUrl)
	throws IOException
	{
		super(title);

		this.jfxPanel = new JFXPanel();
		this.load(fxmlUrl);
		
		try {
			FXUtilities.runAndWait(()->setup(null));
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JFXFrame(String title, URL fxmlUrl, Callback<JFXFrame<GenericController>, Void> callback)
	throws IOException
	{
		super(title);
		
		this.jfxPanel = new JFXPanel();
		this.load(fxmlUrl);

		Platform.runLater(()->setup(callback));
	}
	
	private void load(URL url)
	throws IOException
	{
		this.loader = new FXMLLoader(url);
		this.parent = this.loader.load();
		this.controller = this.loader.getController();
	}
	
	private void setup(Callback<JFXFrame<GenericController>, Void> callback) {
		// Setup JFX
		this.jfxPanel.setScene(new Scene(this.parent));
		
		// Setup Swing
		this.add(this.jfxPanel);
		this.pack();
		this.addWindowListener(new EmptyWindowListenerStub() {
			@Override
			public void windowClosing(WindowEvent e) {
				JFXFrame.this.dispose();
			}
		});
		
		// Calls back
		if (callback != null)
		{
			callback.call(this);
		}
	}

	public JFXPanel getJfxPanel() {
		return jfxPanel;
	}

	public GenericController getController() {
		return controller;
	}
	
	public Parent getParentNode()
	{
		return this.parent;
	}

}
