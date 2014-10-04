package raele.rha.model.gui;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.util.Callback;

public class FileMenuItem extends MenuItem {
	
	private File file;
	private GameModelController controller;
	private Callback<IOException, Void> handler;

	public FileMenuItem(File file, GameModelController controller, Callback<IOException, Void> errorHandler)
	{
		super(file.getName());
		this.file = file;
		this.controller = controller;
		this.handler = errorHandler;
		this.setOnAction(this::handle);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public GameModelController getController() {
		return controller;
	}

	public void setController(GameModelController controller) {
		this.controller = controller;
	}

	public Callback<IOException, Void> getHandler() {
		return handler;
	}

	public void setHandler(Callback<IOException, Void> handler) {
		this.handler = handler;
	}

	private void handle(ActionEvent event) {
		try {
			this.controller.open(this.file);
		} catch (IOException e) {
			this.handler.call(e);
		}
	}

}
