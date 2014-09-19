package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import persistence.entity.Card;
import persistence.entity.Deck;
import persistence.entity.DeckEntry;

public class DeckModel {

	private List<CardlistEntry> cardlist;
	private Deck deck;
	
	public DeckModel()
	{
		this(null);
	}
	
	public DeckModel(Deck deck)
	{
		this.cardlist = new ArrayList<CardlistEntry>(30);
		this.load(deck);
	}
	
	public void reset()
	{
		for (CardlistEntry entry : this.cardlist)
		{
			entry.reset();
		}
	}
	
	public void load(Deck deck)
	{
		this.deck = deck;
		this.cardlist.clear();
		
		if (this.deck == null)
		{
			this.deck = new Deck("Novo Deck", "");
		}
		
		for (DeckEntry entry : this.deck.getEntries())
		{
			Card card = entry.getCard();
			Integer quantity = entry.getQuantity();
			CardlistEntry cardlistEntry = new CardlistEntry(card, quantity);
			this.cardlist.add(cardlistEntry);
		}
	}
	
	public void rename(String name)
	{
		this.deck.setName(name);
	}
	
	public String getName()
	{
		return this.deck.getName();
	}
	
	public boolean contains(Card card) {
		return this.cardlist.stream()
				.anyMatch(e -> card.equals(e.getCard()));
	}
	
	public void addCard(Card card)
	{
		CardlistEntry entry = this.cardlist.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		if (entry != null)
		{
			entry.setMaximum(
					entry.getMaximum() + 1,
					CardlistEntry.MaximumResizeStrategy.ADAPT_TRUNCATE
				);
		}
		else
		{
			entry = new CardlistEntry(card, 1);
			this.cardlist.add(entry);
		}
		
		this.sort();
	}

	private void sort() {
		this.cardlist.sort(new Comparator<CardlistEntry>() {
			private final Locale locale = new Locale("en", "US"); // TODO
			@Override
			public int compare(CardlistEntry a, CardlistEntry b) {
				int diff = a.getCard().getMana() - b.getCard().getMana();
				
				if (diff == 0)
				{
					diff = a.getCard().getName().get(locale).compareTo(
							b.getCard().getName().get(locale)
							);
				}
				
				return diff;
			}
		});
	}

	public void removeCard(Card card)
	{
		CardlistEntry entry = this.cardlist.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		if (entry != null)
		{
			entry.setMaximum(entry.getMaximum() - 1, CardlistEntry.MaximumResizeStrategy.NOADAPT_TRUNCATE);
			
			if (entry.getMaximum() == 0)
			{
				this.cardlist.remove(entry);
			}
		}
	}
	
	public void drawCard(Card card)
	{
		CardlistEntry entry = this.cardlist.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		if (entry == null)
		{
			entry = new CardlistEntry(card, 1);
			entry.setCurrent(0);
			this.cardlist.add(entry);
		}
		else if (entry.getCurrent() == 0)
		{
			entry.setMaximum(entry.getMaximum() + 1);
		}
		else
		{
			entry.setCurrent(entry.getCurrent() - 1);
		}
	}
	
	public void mulligan(Card card)
	{
		CardlistEntry entry = this.cardlist.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		if (entry != null)
		{
			entry.setCurrent(entry.getCurrent() + 1);
		}
		else
		{
			assert false: "Trying to mulligan a card that doesn't exists in the deck.";
		}
	}
	
	public Deck createDeck()
	{
		Deck result = new Deck(this.deck.getName(), this.deck.getDescription());

		HashSet<DeckEntry> entries = new HashSet<DeckEntry>(this.cardlist.size());
		for (CardlistEntry entry : this.cardlist)
		{
			Card card = entry.getCard();
			Integer quantity = entry.getMaximum();
			DeckEntry deckEntry = new DeckEntry(card, quantity);
			entries.add(deckEntry);
		}
		result.setEntries(entries);
		
		this.deck = result;
		return result;
	}
	
	public Set<Card> getCards()
	{
		return this.cardlist.stream()
				.map(entry -> entry.getCard())
				.collect(Collectors.toSet());
	}
	
	public List<CardlistEntry> getEntries()
	{
		return this.cardlist;
	}
	
	public void setEntries(List<CardlistEntry> entries)
	{
		this.cardlist = entries;
	}
	
	public int getCurrentCount()
	{
		return this.cardlist.stream()
				.mapToInt(CardlistEntry::getCurrent)
				.sum();
	}
	
	public int getMaximumCount()
	{
		return this.cardlist.stream()
				.mapToInt(CardlistEntry::getMaximum)
				.sum();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("<");
		builder.append(deck.getName());
		builder.append(">");
		for (CardlistEntry entry : this.cardlist)
		{
			builder.append("\n");
			builder.append(entry);
		}
		
		return builder.toString();
	}

}
