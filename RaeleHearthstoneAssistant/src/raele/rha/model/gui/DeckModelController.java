package raele.rha.model.gui;

import java.util.List;
import java.util.Locale;

import raele.rha.model.CardlistEntry;
import raele.rha.model.DeckModel;
import raele.rha.persistence.CardDao;
import raele.rha.persistence.entity.Card;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DeckModelController {
	
	@FXML private TextField addText;
	@FXML private ListView<CardlistEntry> decklist;
	@FXML private Button addButton;
	@FXML private Label quantity;
	@FXML private Button resetButton;
	@FXML private Button clearButton;
	
	private DeckModel deckModel;
	private CardlistEntryCellFactory cardlistCellfactory;
	
	@FXML
	public void initialize()
	{
		this.addButton.setOnAction(this::addButtonAction);
		this.resetButton.setOnAction(this::resetButtonAction);
		this.clearButton.setOnAction(this::clearButtonAction);
		this.cardlistCellfactory = new CardlistEntryCellFactory();
		this.decklist.setCellFactory(this.cardlistCellfactory);
	}

	public void setDeck(DeckModel friendlyDeck)
	{
		this.deckModel = friendlyDeck;
		this.cardlistCellfactory.setDeckModel(this.deckModel);
		this.refreshDecklist();
	}
	
	public void refreshDecklist() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				List<CardlistEntry> cards = DeckModelController.this.deckModel.getEntries();
				cards.sort((a, b) -> a.getCard().getMana() - b.getCard().getMana());
				ObservableList<CardlistEntry> observableList = FXCollections.observableArrayList(cards);
				DeckModelController.this.decklist.setItems(observableList);
				DeckModelController.this.quantity.setText("" + DeckModelController.this.deckModel.getCurrentCount() + "/" + DeckModelController.this.deckModel.getMaximumCount());
			}
		});
	}

	private void addButtonAction(ActionEvent event)
	{
		String cardName = this.addText.getText();
		this.addCard(cardName);
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

}
