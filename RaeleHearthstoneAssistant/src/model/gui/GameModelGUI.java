package model.gui;

import java.io.File;

import raele.util.javafx.JFXFrame;
import model.GameModel;

public class GameModelGUI extends JFXFrame {
	
	private static final long serialVersionUID = 1L;
	private GameModel model;

	public GameModelGUI(GameModel model)
	throws Exception //TODO <- Tentar remover essa exceção
	{
		super(model.getFriendlyDeck().getName(), new File("res/fxml/DeckModel.fxml").toURI().toURL());
		this.model = model;
		
		DeckModelController controller = (DeckModelController) this.getController();
		controller.setDeck(this.model.getFriendlyDeck());
	}

	public GameModel getModel() {
		return model;
	}

	public void setModel(GameModel model) {
		this.model = model;
	}
	
}
