package raele.rha.persistence;

import raele.rha.persistence.entity.Card;
import raele.rha.persistence.entity.Deck;
import raele.rha.persistence.entity.DeckEntry;

public class DeckDao extends Dao {

	private String persistenceUnit;

	public DeckDao(String persistenceUnit) {
		super(persistenceUnit);
		this.persistenceUnit = persistenceUnit;
	}
	
	@Override
	public void insert(Object entity) {
		if (!(entity instanceof Deck))
		{
			throw new RuntimeException("" + entity + " is not a Deck.");
		}
		
		Deck deck = Deck.class.cast(entity);
		Dao dao = new Dao(this.persistenceUnit);
		
		dao.beginTransaction();
		for (DeckEntry entry : deck.getEntries())
		{
			Card card = dao.selectById(entry.getCard().getId(), Card.class);
			entry.setCard(card);
			dao.insert(entry);
		}
		dao.insert(deck);
		dao.commitTransaction();
		
		dao.close();
	}

}
