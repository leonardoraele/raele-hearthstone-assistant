package raele.rha.model;

import raele.rha.persistence.entity.Card;

public class CardlistEntry {
	
	public enum MaximumResizeStrategy { // O que fazer quando o maximum for alterado?
		RESET,				// Resetar o current para ficar igual ao maximum
		ADAPT_TRUNCATE,		// Fazer com o current a mesma operação que foi feita com maximum e depois truncar para evitar valores negativos
		ADAPT_NOTRUNCATE,	// Fazer com o current a mesma operação que foi feita com maximum, mesmo que current fique negativo
		NOADAPT_TRUNCATE,	// Truncar current entre 0 e maximum, inclusive
		NOADAPT_NOTRUNCATE,	// Não fazer nada, manter current mesmo que fique fora do range
	}
	
	private Card card;
	private Integer current;
	private Integer maximum;
	
	public CardlistEntry(Card card, Integer quantity)
	{
		this.card = card;
		this.current = this.maximum = quantity;
	}

	public void reset()
	{
		this.current = this.maximum;
	}
	
	public Integer getCurrent() {
		return current;
	}
	public void setCurrent(Integer current) {
		this.current = current;
	}
	public void setCurrentTruncating(Integer current) {
		this.current = Math.max(0, Math.min(current, this.maximum));
	}
	public Integer getMaximum() {
		return maximum;
	}
	public void setMaximum(Integer maximum) {
		this.setMaximum(maximum, MaximumResizeStrategy.NOADAPT_NOTRUNCATE);
	}
	public void setMaximum(Integer maximum, MaximumResizeStrategy strategy) {
		switch (strategy) {
		case RESET:
			this.current = maximum;
			break;
		case ADAPT_TRUNCATE:
			this.current = Math.max(0, this.current - this.maximum + maximum);
			break;
		case ADAPT_NOTRUNCATE:
			this.current -= (this.maximum - maximum);
			break;
		case NOADAPT_TRUNCATE:
			this.current = Math.max(0, Math.min(this.current, maximum));
			break;
		case NOADAPT_NOTRUNCATE:
			// Do nothing
			break;
		}
		
		this.maximum = maximum;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	@Override
	public String toString() {
		return "" + card + " " + current + "/" + maximum;
	}

}
