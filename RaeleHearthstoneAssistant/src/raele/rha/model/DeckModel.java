package raele.rha.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import raele.rha.persistence.CardDao;
import raele.rha.persistence.entity.Card;
import raele.rha.persistence.entity.Deck;
import raele.rha.persistence.entity.DeckEntry;
import raele.rha.persistence.entity.Hero;

public class DeckModel {

	private List<CardlistEntry> cardlist;
	private Deck deck;
	
	public DeckModel()
	{
		this((Deck) null);
	}
	
	public DeckModel(Deck deck)
	{
		this.cardlist = new ArrayList<CardlistEntry>(30);
		this.load(deck);
	}
	
	public DeckModel(InputStream input)
	{
		this();
		this.importDeck(input);
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
			this.deck = new Deck();
			this.deck.setName("Novo Deck");
			this.deck.setDescription("");
			this.deck.setHero(Hero.neutral);
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
		this.setName(name);
	}

	public void setName(String name)
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
		result.setHero(this.deck.getHero());

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

	public void clear() {
		this.cardlist.clear();
	}

	public Hero getHero() {
		return this.deck.getHero();
	}
	
	public void setHero(Hero hero) {
		this.deck.setHero(hero);
	}

	public Double chanceToDraw(Card card) {
		Double result;
		
		CardlistEntry entry = this.cardlist.stream()
				.filter(e -> card.equals(e.getCard()))
				.findAny()
				.orElse(null);
		
		if (entry == null || entry.getCurrent() == 0)
		{
			result = 0.0D;
		}
		else
		{
			double current = entry.getCurrent();
			double total = this.getCurrentCount();
			result = current / total;
		}
		
		return result;
	}
	
	public void importDeck(InputStream input)
	{
		Scanner scanner = new Scanner(input);
		Deck deck = new Deck();

		deck.setName(scanner.nextLine());
		deck.setDescription(scanner.nextLine());
		try {
			deck.setHero(Hero.valueOf(scanner.nextLine()));
		} catch (IllegalArgumentException e) {}
		
		CardDao dao = new CardDao("H2");
		int n = scanner.nextInt();
		for (int i = 0; i < n; i++)
		{
			int quantity = scanner.nextInt();
			long id = scanner.nextLong();
			Card card = dao.selectById(id, Card.class);
			DeckEntry entry = new DeckEntry(card, quantity);
			deck.getEntries().add(entry);
		}
		dao.close();
		
		this.load(deck);
		scanner.close();
	}

	public void exportDeck(OutputStream output)
	{
		PrintWriter writer = new PrintWriter(output);
		writer.println(""+this.deck.getName());
		writer.println(""+this.deck.getDescription());
		writer.println(""+this.deck.getHero());
		
		List<CardlistEntry> entries = this.cardlist;
		writer.println(entries.size());
		for (CardlistEntry entry : entries)
		{
			writer.print(""+entry.getMaximum());
			writer.print(" ");
			writer.println(""+entry.getCard().getId());
		}
		
		writer.close();
	}

}
