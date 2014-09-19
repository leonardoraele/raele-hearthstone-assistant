package model.gui;

import java.util.List;
import java.util.Locale;

import persistence.CardDao;
import persistence.entity.Card;
import model.CardlistEntry;
import model.DeckModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DeckModelController {
	
	@FXML private TextField addText;
	@FXML private ListView<CardlistEntry> decklist;
	@FXML private Button addButton;
	
	private DeckModel deckModel;
	private CardlistEntryCellFactory cardlistCellfactory;
	
	@FXML
	public void initialize()
	{
		this.addButton.setOnAction(this::addButtonAction);
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
		List<CardlistEntry> cards = this.deckModel.getEntries();
		ObservableList<CardlistEntry> observableList = FXCollections.observableArrayList(cards);
		this.deckModel.setEntries(observableList);
		this.decklist.setItems(observableList);
	}

	private void addButtonAction(ActionEvent event)
	{
		String cardName = this.addText.getText();
		Locale locale = new Locale("en", "US"); // TODO
		
		CardDao dao = new CardDao("H2");
		Card card = dao.selectSingleByName(locale, cardName);
		dao.close();
		
		if (card != null)
		{
			this.deckModel.addCard(card);
		}
		else
		{
			// TODO
		}
	}

}
