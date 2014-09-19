package core;

import java.util.Locale;

import persistence.CardDao;
import persistence.entity.Card;
import model.GameModel;
import input.LogEvent;
import input.LogEventListener;
import input.ZoneChange;

public class LogEventReceiver implements LogEventListener {
	
	private GameModel model;
	private Locale locale;
	private String persistenceUnit;

	public LogEventReceiver(GameModel model, Locale locale, String persistenceUnit) {
		super();
		this.model = model;
		this.locale = locale;
		this.persistenceUnit = persistenceUnit;
	}

	public Locale getLocale() {
		return locale;
	}

	public GameModel getModel() {
		return model;
	}

	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	@Override
	public void recordEvent(LogEvent event) {
		ZoneChange change = event.getZoneChange();
		if (change != null)
		{
			String from = change.getFrom();
			String to = change.getTo();
			String name = event.getProperties().getProperty(LogEvent.PROPERTY_NAME);
			
			// Match start
			if (ZoneChange.FRIENDLY_HERO.equals(to))
			{
				System.out.println("GameModel.reset()");
				this.model.reset();
			}
			
			// Friendly draw
			if (ZoneChange.FRIENDLY_DECK.equals(from) &&
				ZoneChange.FRIENDLY_HAND.equals(to))
			{
				System.out.println("GameModel.onFriendlyDrewCard(" + name + ")");
				this.model.onFriendlyDrewCard(card(name));
			}
			// Friendly mulligan
			else if (ZoneChange.FRIENDLY_HAND.equals(from) &&
					 ZoneChange.FRIENDLY_DECK.equals(to))
			{
				System.out.println("GameModel.onFriendlyCardReturnedToDeck(" + name + ")");
				this.model.onFriendlyCardReturnedToDeck(card(name));
			}
			// Friendly returned
			else if (ZoneChange.FRIENDLY_PLAY.equals(from) &&
					 ZoneChange.FRIENDLY_HAND.equals(to))
			{
				System.out.println("GameModel.onFriendlyCardReturnedToHand(" + name + ")");
				this.model.onFriendlyCardReturnedToHand(card(name));
			}
			// Friendly play
			else if (ZoneChange.FRIENDLY_HAND.equals(from) &&
					 ZoneChange.FRIENDLY_DECK.equals(to) == false)
			{
				System.out.println("GameModel.onFriendlyPlayCard(" + name + ")");
				this.model.onFriendlyPlayCard(card(name));
			}

			// Opponent draw
			else if (ZoneChange.OPPOSING_DECK.equals(from) &&
					 ZoneChange.OPPOSING_HAND.equals(to))
			{
				System.out.println("GameModel.onOpponentDrewCard()");
				this.model.onOpponentDrewCard();
			}
			// Opponent mulligan
			else if (ZoneChange.OPPOSING_HAND.equals(from) &&
					ZoneChange.OPPOSING_DECK.equals(to))
			{
				System.out.println("GameModel.onOpponentCardReturnedToDeck(" + name + ")");
				this.model.onOpponentCardReturnedToDeck();
			}
			// Opponent returned
			else if (ZoneChange.OPPOSING_PLAY.equals(from) &&
					ZoneChange.OPPOSING_HAND.equals(to))
			{
				System.out.println("GameModel.onOpponentCardReturnedToHand(" + name + ")");
				this.model.onOpponentCardReturnedToHand(card(name));
			}
			// Opponent play
			else if (ZoneChange.OPPOSING_HAND.equals(from) &&
					 ZoneChange.OPPOSING_DECK.equals(to) == false)
			{
				if (name != null)
				{
					System.out.println("GameModel.onOpponentPlayCard(" + name + ")");
					this.model.onOpponentPlayCard(card(name));
				}
				else
				{
					// TODO Se name vier como null, o oponente jogou uma trap.
				}
			}
//			// Minion or spell went to graveyard
//			else if (ZoneChange.OPPOSING_GRAV.equals(to) ||
//					ZoneChange.FRIENDLY_GRAV.equals(to))
//			{
//				System.out.println("" + name + " died.");
//			}
//			// Unknown eventFRIENDLY GRAVEYARD
//			else
//			{
//				System.out.println("(!) Card " + name + " went from " + from + " to " + to + " (zone " + zone + ")");
//			}
		}
	}
	
	private Card card(String name)
	{
		CardDao dao = new CardDao(persistenceUnit);
		Card card = dao.selectSingleByName(locale, name);
		dao.close();
		
		return card;
	}

}
