package raele.rha.model.gui;

import java.io.File;

import raele.rha.model.GameModel;
import raele.util.javafx.JFXFrame;

public class GameModelGUI extends JFXFrame<GameModelController> {
	
	private static final long serialVersionUID = 1L;
	private GameModel model;

	public GameModelGUI(GameModel model)
	throws Exception //TODO <- Tentar remover essa exceção
	{
		super(model.getFriendlyDeck().getName(), new File("res/fxml/GameModel.fxml").toURI().toURL());
		this.model = model;
		
		GameModelController controller = this.getController();
		controller.setup(this);
	}

	public GameModel getModel() {
		return model;
	}

	public void setModel(GameModel model) {
		this.model = model;
	}
	
}
