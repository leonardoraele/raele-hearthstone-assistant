package raele.rha.model.gui;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import raele.rha.model.CardlistEntry;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class CardlistEntryCellFactory implements Callback<ListView<CardlistEntry>, ListCell<CardlistEntry>> {

	private static final String FXML_CardlistEntry = "res/fxml/CardlistEntry.fxml";
	
	public class CardlistEntryCell extends ListCell<CardlistEntry> {

		private static final String QUANTITY_LIGHT_COLOR = "#eeba29";
		private static final String QUANTITY_DARK_COLOR = "#8c7430";
		private static final String NAME_LIGHT_COLOR = "#ffffff";
		private static final String NAME_DARK_COLOR = "#808080";
		
		private CardlistEntry entry;
		private Region parent;
		private Font hearthstoneFont14;
		
		public CardlistEntryCell()
		{
			try {
				FXMLLoader loader = new FXMLLoader(new File(FXML_CardlistEntry).toURI().toURL());
				this.parent = (Region) loader.load();
				
				this.hearthstoneFont14 = Font.loadFont(new File("res/font/hearthstone.ttf").toURI().toURL().toString(), 14);
			} catch (IOException e) {
				System.err.println("Couldn't load resource " + FXML_CardlistEntry + " because: " + e.getMessage());
			}
			
			((Button) this.parent.lookup("#add")).setOnAction(event -> add(this.entry));
			((Button) this.parent.lookup("#remove")).setOnAction(event -> remove(this.entry));
			
			// Botões de adicionar e remover como imagem e com hover:
//			ImageView addButton = (ImageView) this.parent.lookup("#add");
//			addButton.setOnMouseClicked(event -> add(this.entry));
//			FXUtilities.hoverConfigOrUndo(addButton,
//					"res/img/add.png", // Idle
//					"res/img/add.png", // Hover
//					"res/img/add.png"  // Pressed
//					);
			
//			ImageView removeButton = (ImageView) this.parent.lookup("#remove");
//			removeButton.setOnMouseClicked(event -> remove(this.entry));
//			FXUtilities.hoverConfigOrUndo(removeButton,
//					"res/img/remove.png", // Idle
//					"res/img/remove.png", // Hover
//					"res/img/remove.png"  // Pressed
//					);
		}
		
		@Override
		protected void updateItem(CardlistEntry entry, boolean empty) {
			super.updateItem(this.entry, empty);
			
			this.entry = entry;
			Locale locale = new Locale("en", "US"); // TODO
			
			if (empty || entry == null || this.parent == null)
			{
				this.setGraphic(null);
			}
			else
			{
				// Imagem de fundo:
//				CardImage cardimage = this.entry.getCard().getImage();
//				if (cardimage != null)
//				{
//					byte[] binaryImage = cardimage.getImage();
//					if (binaryImage != null)
//					{
//						ByteArrayInputStream input = new ByteArrayInputStream(binaryImage);
//						Image image = new Image(input);
//						
//						if (this.entry.getCurrent() == 0)
//						{
//							// TODO Escurecer a imagem
//						}
//						
//						BackgroundImage backgroundImage = new BackgroundImage(image,
//								BackgroundRepeat.NO_REPEAT,
//								BackgroundRepeat.NO_REPEAT,
//								BackgroundPosition.DEFAULT,
//								BackgroundSize.DEFAULT
//								);
//						Background background = new Background(backgroundImage);
//						this.parent.setBackground(background);
//					}
//				}
				
				Label manaLabel = (Label) this.parent.lookup("#mana");
				Label nameLabel = (Label) this.parent.lookup("#name");
				Label quantityLabel = (Label) this.parent.lookup("#quantity");
				Label drawChanceLabel = (Label) this.parent.lookup("#drawChance");
				
				String mana = ""+this.entry.getCard().getMana();
				String name = ""+this.entry.getCard().getName().get(locale);
				Paint nameColor = Paint.valueOf(this.entry.getCurrent() > 0 ?
														NAME_LIGHT_COLOR :
														NAME_DARK_COLOR );
				String quantity = ""+this.entry.getCurrent();
				Paint quantityColor = Paint.valueOf(this.entry.getCurrent() > 1 ?
														QUANTITY_LIGHT_COLOR :
														QUANTITY_DARK_COLOR );
				
				manaLabel.setText(mana);
				manaLabel.setFont(this.hearthstoneFont14);
				nameLabel.setText(name);
				nameLabel.setFont(this.hearthstoneFont14);
				nameLabel.textFillProperty().setValue(nameColor);
				quantityLabel.setText(quantity);
				quantityLabel.setFont(this.hearthstoneFont14);
				quantityLabel.textFillProperty().setValue(quantityColor);
				drawChanceLabel.setText(
						NumberFormat.getPercentInstance().format(
								CardlistEntryCellFactory.this.controller.getModel().getFriendlyDeck().
										chanceToDraw(this.entry.getCard())
								)
						);
				
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
