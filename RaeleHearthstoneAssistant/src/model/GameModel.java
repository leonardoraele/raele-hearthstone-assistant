package model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import persistence.entity.Card;

public class GameModel {

	private static Card unknownCard;
	static {
		unknownCard = new Card();
		unknownCard.setId(0L);
		unknownCard.getName().put(new Locale("en", "US"), "Unknown");
		unknownCard.getName().put(new Locale("pt", "BR"), "Desconhecido");
	};
	
	private DeckModel friendlyDeck;
	private ArrayList<Card> friendlyHand;
	private int opponentDeck;
	private ArrayList<Card> opponentHand;
	private LinkedList<CardlistEntry> opponentHistory;
	
	public GameModel()
	{
		this.friendlyDeck = new DeckModel();
		this.friendlyHand = new ArrayList<Card>(10);
		this.opponentHand = new ArrayList<Card>(10);
		this.opponentHistory = new LinkedList<CardlistEntry>();
		this.reset();
	}
	
	public void reset()
	{
		this.friendlyDeck.reset();
		this.friendlyHand.clear();
		this.opponentDeck = 30;
		this.opponentHand.clear();
		this.opponentHistory.clear();
	}
	
	public void setFriendlyDeck(DeckModel model)
	{
		this.friendlyDeck = model;
	}
	
	public void onFriendlyDrewCard(Card card)
	{
		this.friendlyDeck.drawCard(card);
		this.friendlyHand.add(card);
	}
	
	public void onFriendlyPlayCard(Card card)
	{
		this.friendlyHand.remove(card);
	}
	
	public void onFriendlyCardReturnedToHand(Card card)
	{
		this.friendlyHand.add(card);
	}
	
	public void onFriendlyCardReturnedToDeck(Card card)
	{
		this.friendlyHand.remove(card);
		this.friendlyDeck.mulligan(card);
	}
	
	public void onOpponentDrewCard()
	{
		this.opponentDeck--;
		this.opponentHand.add(unknownCard);
	}
	
	public void onOpponentPlayCard(Card card)
	{
		if (this.opponentHand.contains(card))
		{
			this.opponentHand.remove(card);		
		}
		else
		{
			this.opponentHand.remove(unknownCard);
		}
		
		CardlistEntry entry = this.opponentHistory.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		if (entry == null)
		{
			entry = new CardlistEntry(card, 0);
			this.opponentHistory.add(entry);
		}
		
		entry.setMaximum(entry.getMaximum() + 1,
				CardlistEntry.MaximumResizeStrategy.RESET);
	}
	
	public void onOpponentCardReturnedToHand(Card card)
	{
		CardlistEntry entry = this.opponentHistory.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		entry.setMaximum(entry.getMaximum() - 1,
				CardlistEntry.MaximumResizeStrategy.RESET);
		
		this.opponentHand.add(card);
	}
	
	public void onOpponentCardReturnedToDeck()
	{
		this.opponentHand.remove(unknownCard);
		this.opponentDeck++;
	}

	public DeckModel getFriendlyDeck() {
		return friendlyDeck;
	}

	public ArrayList<Card> getFriendlyHand() {
		return friendlyHand;
	}

	public int getOpponentDeck() {
		return opponentDeck;
	}

	public ArrayList<Card> getOpponentHand() {
		return opponentHand;
	}

	public LinkedList<CardlistEntry> getOpponentHistory() {
		return opponentHistory;
	}

	public void report(OutputStream output) {
		StringBuilder builder = new StringBuilder();

		builder.append("Opponent Deck: ");
		builder.append(this.getOpponentDeck());
		builder.append(" unknown cards.");
		builder.append("\n");
		
		builder.append("Opponent Hand: ");
		builder.append(this.opponentHand);
		builder.append("\n");

		builder.append("Friendly Deck: ");
		builder.append(this.getFriendlyDeck().getCurrentCount());
		builder.append("/");
		builder.append(this.getFriendlyDeck().getMaximumCount());
		builder.append("\n");
		
		builder.append("Friendly Hand: ");
		builder.append(this.getFriendlyHand());
		builder.append("\n");
		
		String report = builder.toString();
		
		try {
			output.write(report.getBytes());
		} catch (IOException e) {
			System.err.println("GameModel's report to " + output.toString() + " failed because " + e.getMessage());
		}
	}

	public void report() {
		report(System.out);
	}
	
}