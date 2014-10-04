package raele.rha.model.gui;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import raele.rha.model.CardlistEntry;
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
					((Label) this.parent.lookup("#drawChance")).setText(
								NumberFormat.getPercentInstance().format(
										CardlistEntryCellFactory.this.controller.getModel().getFriendlyDeck().
												chanceToDraw(entry.getCard())
										)
								);
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
			CardlistEntryCellFactory.this.controller.getModel().getFriendlyDeck().addCard(entry.getCard());
			CardlistEntryCellFactory.this.refresh();
		}
		
		private void remove(CardlistEntry entry)
		{
			CardlistEntryCellFactory.this.controller.getModel().getFriendlyDeck().removeCard(entry.getCard());
			CardlistEntryCellFactory.this.refresh();
		}
	}
	
	private GameModelController controller;
	
	public CardlistEntryCellFactory(GameModelController controller) {
		this.controller = controller;
	}

	@Override
	public ListCell<CardlistEntry> call(ListView<CardlistEntry> listview) {
		return new CardlistEntryCell();
	}

	public void refresh() {
		this.controller.refresh();
	}

}
