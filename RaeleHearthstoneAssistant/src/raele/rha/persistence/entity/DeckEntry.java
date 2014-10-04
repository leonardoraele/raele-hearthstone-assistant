package raele.rha.persistence.entity;

public class DeckEntry {

	private Long id;
	private Integer quantity;
	private Card card;
	private Deck deck;

	public DeckEntry(Card card, Integer quantity) {
		super();
		this.quantity = quantity;
		this.card = card;
		this.deck = null;
	}

	public DeckEntry() {}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public Deck getDeck() {
		return deck;
	}
	public void setDeck(Deck deck) {
		this.deck = deck;
	}

}
