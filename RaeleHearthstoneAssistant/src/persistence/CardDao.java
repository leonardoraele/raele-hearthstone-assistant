package persistence;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import persistence.entity.Card;

public class CardDao extends Dao {

	public CardDao(String persistenceUnit) {
		super(persistenceUnit);
	}
	
	public Card selectByCardId(Integer id) {
		EntityManager manager = this.getEntityManager();
		
		String jpql = "SELECT card FROM Card card WHERE card.cardId = :id";
		TypedQuery<Card> query = manager.createQuery(jpql, Card.class);
		query.setParameter("id", id);
		Card card;
		
		try {
			card = query.getSingleResult();
		} catch (NoResultException e) {
			card = null;
		}
		
		return card;
	}
	
	public Card selectSingleByName(Locale locale, String name) {
		List<Card> cards = this.selectByName(locale, name);
		
		if (cards.size() > 0)
		{
			return cards.get(0);
		}
		else
		{
			return null;
		}
	}

	public List<Card> selectByName(Locale locale, String name) {
		EntityManager manager = this.getEntityManager();
		
		String jpql = "SELECT card FROM Card card WHERE KEY(card.name) = :locale and upper(VALUE(card.name)) like :name";
		TypedQuery<Card> query = manager.createQuery(jpql, Card.class);
		query.setParameter("locale", locale);
		query.setParameter("name", "%" + name.toUpperCase() + "%");
		List<Card> cards = query.getResultList();
		
		return cards;
	}

}
