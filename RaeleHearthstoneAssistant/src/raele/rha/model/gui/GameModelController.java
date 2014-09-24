package raele.rha.model.gui;

import java.util.List;
import java.util.Locale;

import org.controlsfx.dialog.Dialogs;

import raele.rha.model.CardlistEntry;
import raele.rha.model.DeckModel;
import raele.rha.model.GameModel;
import raele.rha.persistence.CardDao;
import raele.rha.persistence.entity.Card;
import raele.rha.persistence.entity.Deck;
import raele.rha.persistence.entity.Hero;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

public class GameModelController {
	
	@FXML private TextField addText;
	@FXML private ListView<CardlistEntry> decklist;
	@FXML private Button addButton;
	@FXML private Label quantity;
	@FXML private Button resetButton;
	@FXML private Button clearButton;
	@FXML private MenuItem newDeckItem;
	@FXML private ImageView deckPortrait;
	@FXML private Label deckName;
	@FXML private FlowPane flowPane;
	
	private CardlistEntryCellFactory cardlistCellfactory;
	private GameModelGUI gui;
	private GameModel gameModel;
	private DeckModel deckModel;
	
//	@FXML
	public void initialize()
	{
		this.addButton.setOnAction(this::addButtonAction);
		this.resetButton.setOnAction(this::resetButtonAction);
		this.clearButton.setOnAction(this::clearButtonAction);
		this.newDeckItem.setOnAction(this::newDeckItemAction);
		this.cardlistCellfactory = new CardlistEntryCellFactory(this);
		this.decklist.setCellFactory(this.cardlistCellfactory);
		this.deckPortrait.setOnMouseClicked(this::portraitMouseClicked);
		this.deckName.setOnMouseClicked(this::portraitMouseClicked);
	}

	public void setup(GameModelGUI gui)
	{
		this.gui = gui;
		this.gameModel = gui.getModel();
		this.deckModel = this.gameModel.getFriendlyDeck();
		Platform.runLater(() -> this.loadDeck(this.deckModel.createDeck()));
	}
	
	public GameModel getModel()
	{
		return this.gameModel;
	}

	public void loadDeck(Deck deck) {
		this.deckModel.load(deck);
		this.gui.setTitle(deck.getName());
		this.deckName.setText(deck.getName());
		String heroName = deck.getHero() != null ? deck.getHero().toString() : Hero.neutral.toString();
		String filePath = "res/img/portrait_" + heroName + ".png";
		Image portraitImage = new Image("file:" + filePath);
		this.deckPortrait.setImage(portraitImage);
		this.refreshDecklist();
	}
	
	public void refreshDecklist() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				List<CardlistEntry> cards = GameModelController.this.deckModel.getEntries();
				cards.sort((a, b) -> a.getCard().getMana() - b.getCard().getMana());
				ObservableList<CardlistEntry> observableList = FXCollections.observableArrayList(cards);
				GameModelController.this.decklist.setItems(observableList);
				GameModelController.this.quantity.setText("" + GameModelController.this.deckModel.getCurrentCount() + "/" + GameModelController.this.deckModel.getMaximumCount());
			}
		});
	}

	private void addButtonAction(ActionEvent event)
	{
		String cardName = this.addText.getText();
		this.addCard(cardName);
		this.addText.selectAll();
	}
	
	public void addCard(String cardName)
	{
		Locale locale = new Locale("en", "US"); // TODO
		
		CardDao dao = new CardDao("H2");
		Card card = dao.selectSingleByName(locale, cardName);
		dao.close();
		
		if (card != null)
		{
			this.deckModel.addCard(card);
			this.refreshDecklist();
		}
		else
		{
			// TODO
		}
	}
	
	private void resetButtonAction(ActionEvent event)
	{
		this.resetModel();
	}
	
	public void resetModel()
	{
		this.deckModel.reset();
		this.refreshDecklist();
	}

	private void clearButtonAction(ActionEvent event)
	{
		this.clearModel();
	}
	
	public void clearModel()
	{
		this.deckModel.clear();
		this.refreshDecklist();
	}

	private void newDeckItemAction(ActionEvent event)
	{
		this.newDeck();
	}
	
	public void newDeck()
	{
		// TODO Should be an one step wizard
		String deckName = Dialogs.create()
				.title("Deck Wizard")
				.message("Deck name")
				.showTextInput()
				.orElse(null);
		
		Hero hero = Dialogs.create()
				.title("Deck Wizard")
				.message("Hero")
				.showChoices(Hero.values())
				.orElse(null);
		
		Deck deck = this.deckModel.createDeck();
		deck.setName(deckName);
		deck.setHero(hero);
		
		this.loadDeck(deck);
	}
	
	private void portraitMouseClicked(MouseEvent event)
	{
		Hero hero = this.deckModel.getHero();
		if (hero == null || Hero.neutral.equals(hero))
		{
			this.newDeck();
		}
		else
		{
			this.rename();
		}
	}

	public void rename() {
		String newName = Dialogs.create()
				.title("Rename")
				.message("New deck name")
				.showTextInput(this.deckModel.getName())
				.orElse(null);
		
		if (newName != null)
		{
			this.deckModel.rename(newName);
			this.deckName.setText(newName);
		}
	}

}
