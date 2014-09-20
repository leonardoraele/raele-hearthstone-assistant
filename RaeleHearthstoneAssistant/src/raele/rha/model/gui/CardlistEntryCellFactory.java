package raele.rha.model.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import raele.rha.model.CardlistEntry;
import raele.rha.model.DeckModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CardlistEntryCellFactory implements Callback<ListView<CardlistEntry>, ListCell<CardlistEntry>> {

	private static final String FXML_CardlistEntry = "res/fxml/CardlistEntry.fxml";
	
	public class CardlistEntryCell extends ListCell<CardlistEntry> {
		
		private CardlistEntry lastEntry;
		private Parent parent;
		
		public CardlistEntryCell()
		{
			try {
				FXMLLoader loader = new FXMLLoader(new File(FXML_CardlistEntry).toURI().toURL());
				this.parent = loader.load();
			} catch (IOException e) {
				System.err.println("Couldn't load resource " + FXML_CardlistEntry + " because: " + e.getMessage());
			}
			
			((Button) parent.lookup("#add")).setOnAction(event -> add(this.lastEntry));
			((Button) parent.lookup("#remove")).setOnAction(event -> remove(this.lastEntry));
		}
		
		@Override
		protected void updateItem(CardlistEntry entry, boolean empty) {
			super.updateItem(entry, empty);
			
			if (empty)
			{
				this.lastEntry = null;
				this.setGraphic(null);
			}
			else
			{
				this.lastEntry = entry;
				Locale locale = new Locale("en", "US"); // TODO
				
				if (this.parent != null)
				{
					((Label) this.parent.lookup("#mana")).setText(""+entry.getCard().getMana());
					((Label) this.parent.lookup("#name")).setText(entry.getCard().getName().get(locale));
					((Label) this.parent.lookup("#quantity")).setText(""+entry.getCurrent());
				}
				else
				{
					this.parent = new Label(entry.getCard().getName().get(locale));
				}
				
				this.setGraphic(this.parent);
			}
		}
		
		private void add(CardlistEntry entry)
		{
			CardlistEntryCellFactory.this.deckModel.addCard(entry.getCard());
			CardlistEntryCellFactory.this.refresh();
		}
		
		private void remove(CardlistEntry entry)
		{
			CardlistEntryCellFactory.this.deckModel.removeCard(entry.getCard());
			CardlistEntryCellFactory.this.refresh();
		}
	}
	
	private DeckModel deckModel;
	private ListView<CardlistEntry> listview;

	@Override
	public ListCell<CardlistEntry> call(ListView<CardlistEntry> listview) {
		this.listview = listview;
		return new CardlistEntryCell();
	}

	public DeckModel getDeckModel() {
		return deckModel;
	}

	public void setDeckModel(DeckModel deckModel) {
		this.deckModel = deckModel;
	}

	public void refresh() {
		List<CardlistEntry> entries = this.deckModel.getEntries();
		ObservableList<CardlistEntry> observableList = FXCollections.observableArrayList(entries);
		this.deckModel.setEntries(observableList);
		this.listview.setItems(observableList);
	}

}
