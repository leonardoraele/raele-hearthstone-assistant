package raele.rha.model.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.controlsfx.dialog.Dialogs;

import raele.rha.model.CardlistEntry;
import raele.rha.model.DeckModel;
import raele.rha.model.GameModel;
import raele.rha.persistence.CardDao;
import raele.rha.persistence.Dao;
import raele.rha.persistence.RecentDecks;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class GameModelController {
	
	@FXML private TextField addText;
	@FXML private ListView<CardlistEntry> decklist;
	@FXML private Button addButton;
	@FXML private Label quantity;
	@FXML private Button resetButton;
	@FXML private Button clearButton;
	@FXML private MenuItem newDeckItem;
	@FXML private MenuItem openDeckItem;
	@FXML private MenuItem saveItem;
	@FXML private MenuItem saveAsItem;
	@FXML private MenuItem closeItem;
	@FXML private MenuItem renameItem;
	@FXML private MenuItem changeHeroItem;
	@FXML private Menu recentDecksMenu;
	@FXML private ImageView deckPortrait;
	@FXML private ImageView changeHeroButton;
	@FXML private Label deckName;
	@FXML private FlowPane flowPane;
	
	private CardlistEntryCellFactory cardlistCellfactory;
	private GameModelGUI gui;
	private GameModel gameModel;
	private DeckModel deckModel;
	private File currentFile;
	private List<RecentDecks> recentDecks;
	
	@FXML
	public void initialize()
	{
		setupRecentDecks();
		this.addButton.setOnAction(this::addButtonAction);
		this.resetButton.setOnAction(this::resetButtonAction);
		this.clearButton.setOnAction(this::clearButtonAction);
		this.newDeckItem.setOnAction(this::newDeckItemAction);
		this.openDeckItem.setOnAction(this::openDeckItemAction);
		this.saveItem.setOnAction(this::saveItemAction);
		this.saveAsItem.setOnAction(this::saveAsItemAction);
		this.closeItem.setOnAction(this::closeItemAction);
		this.renameItem.setOnAction(this::renameItemAction);
		this.changeHeroItem.setOnAction(this::changeHeroItemAction);
		this.cardlistCellfactory = new CardlistEntryCellFactory(this);
		this.decklist.setCellFactory(this.cardlistCellfactory);
		this.deckName.setOnMouseClicked(this::nameMouseClicked);
		this.changeHeroButton.setOnMouseClicked(this::changeHeroMouseClicked);
	}

	private void setupRecentDecks()
	{
		if (this.recentDecks == null)
		{
			this.recentDecks = new LinkedList<RecentDecks>();
		}
		else
		{
			this.recentDecks.clear();
		}
		
		Dao dao = new Dao("H2");
		List<RecentDecks> loadedRecentDecks = dao.selectAll(RecentDecks.class);
		dao.close();
		
		for (RecentDecks recentDeck : loadedRecentDecks)
		{
			File file = new File(recentDeck.getPath());
			FileMenuItem item = new FileMenuItem(file, this, this::popupError);
			this.recentDecksMenu.getItems().add(item);
		}
	}

	private void addRecentDeck(File file)
	{
		boolean alreadyContains = this.recentDecks.stream().anyMatch(
				recent -> file.getAbsolutePath().equals(recent.getPath())
				);
		
		if (!alreadyContains)
		{
			FileMenuItem item = new FileMenuItem(file, this, this::popupError);
			this.recentDecksMenu.getItems().add(item);
			
			RecentDecks deck = new RecentDecks();
			deck.setPath(file.getAbsolutePath());
			this.recentDecks.add(0, deck);
			
			Dao dao = new Dao("H2");
			dao.insert(deck);
			dao.close();
		}
	}
	
	public Void popupError(IOException e)
	{
		e.printStackTrace();
		Dialogs.create()
				.title("Error")
				.message(e.toString())
				.showError();
		return null;
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
		this.refresh();
	}
	
	public void refresh() {
		Platform.runLater(this::privateRefresh);
	}
	
	private void privateRefresh()
	{
		String deckName = ""+this.deckModel.getName();
		this.gui.setTitle(deckName + " - Raele Hearthstone Assistant");
		this.deckName.setText(deckName);
		
		this.setPortrait(GameModelController.this.deckModel.getHero());
		
		List<CardlistEntry> cards = this.deckModel.getEntries();
		cards.sort((a, b) -> a.getCard().getMana() - b.getCard().getMana());
		ObservableList<CardlistEntry> observableList = FXCollections.observableArrayList(cards);
		this.decklist.setItems(observableList);
		this.quantity.setText("" + this.deckModel.getCurrentCount() + "/" + this.deckModel.getMaximumCount());
	}

	private void setPortrait(Hero hero) {
		String heroName = hero != null ? hero.toString() : Hero.neutral.toString();
		String filePath = "res/img/portrait_" + heroName + ".png";
		Image portraitImage = new Image("file:" + filePath);
		this.deckPortrait.setImage(portraitImage);
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
			this.refresh();
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
		this.refresh();
	}

	private void clearButtonAction(ActionEvent event)
	{
		this.clearModel();
	}
	
	public void clearModel()
	{
		this.deckModel.setName("");
		this.deckModel.setHero(Hero.neutral);
		this.deckModel.clear();
		this.refresh();
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
		
		Deck deck = new Deck();
		deck.setName(deckName);
		deck.setHero(hero);
		
		this.loadDeck(deck);
	}
	
	private void openDeckItemAction(ActionEvent event)
	{
		this.openDeck();
	}
	
	public void openDeck()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Deck");
		fileChooser.setInitialDirectory(new File("decks"));
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("RHA Deck", "*.rha"));
		File choosen = fileChooser.showOpenDialog(null);
		
		if (choosen != null)
		{
			try {
				this.open(choosen);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}
	
	public void open(File choosen)
	throws IOException
	{
		FileInputStream input = new FileInputStream(choosen);
		this.deckModel.importDeck(input);
		input.close();
		
		this.addRecentDeck(choosen);
		this.refresh();
		this.currentFile = choosen;
	}

	private void saveItemAction(ActionEvent event)
	{
		this.save();
	}
	
	public void save()
	{
		if (this.currentFile != null)
		{
			try {
				this.save(this.currentFile);
			} catch (IOException e) {
				e.printStackTrace();
				Dialogs.create()
						.title("Error")
						.message(e.toString())
						.showError();
			}
		}
		else
		{
			this.saveAs();
		}
	}

	private void saveAsItemAction(ActionEvent event)
	{
		this.saveAs();
	}
	
	public void saveAs()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Deck As...");
		fileChooser.setInitialDirectory(new File("decks"));
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("RHA Deck", "*.rha"));
		File choosen = fileChooser.showSaveDialog(null);
		
		if (choosen != null)
		{
			try {
				this.save(choosen);
			} catch (IOException e) {
				e.printStackTrace();
				Dialogs.create()
						.title("Error")
						.message(e.toString())
						.showError();
			}
		}
	}
	
	public void save(File choosen)
	throws IOException
	{
		FileOutputStream output = new FileOutputStream(choosen);
		this.deckModel.exportDeck(output);
		output.close();
		
		this.addRecentDeck(choosen);
		this.currentFile = choosen;
	}

	private void closeItemAction(ActionEvent event)
	{
		this.close();
	}

	public void close()
	{
		this.gui.dispose();
	}
	
	private void changeHeroItemAction(ActionEvent event)
	{
		this.changeHero();
	}

	private void changeHeroMouseClicked(MouseEvent event)
	{
		this.changeHero();
	}

	private void changeHero() {
		Hero hero = Dialogs.create()
				.title("Deck Wizard")
				.message("Hero")
				.showChoices(Hero.values())
				.orElse(null);
		
		if (hero != null)
		{
			this.deckModel.setHero(hero);
			this.refresh();
		}
	}
	
	private void renameItemAction(ActionEvent event)
	{
		this.rename();
	}

	private void nameMouseClicked(MouseEvent event)
	{
		this.rename();
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
			this.refresh();
		}
	}

}
