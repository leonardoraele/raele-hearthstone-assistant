package raele.rha.persistence.entity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;

@Entity
public class Card {

	private Long id;
	private Map<Locale, String> name = new HashMap<Locale, String>();
	private Map<Locale, String> description = new HashMap<Locale, String>();
	private CardImage image;
	private Hero hero;
	private Category category;
	private Quality quality;
	private Race race;
	private CardSet cardSet;
	private Integer mana;
	private Integer attack;
	private Integer health;
	private Boolean collectible;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	public CardImage getImage() {
		return image;
	}
	public void setImage(CardImage image) {
		this.image = image;
	}
	@Enumerated
	public Hero getHero() {
		return hero;
	}
	public void setHero(Hero hero) {
		this.hero = hero;
	}
	@Enumerated
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	@Enumerated
	public Quality getQuality() {
		return quality;
	}
	public void setQuality(Quality quality) {
		this.quality = quality;
	}
	@Enumerated
	public Race getRace() {
		return race;
	}
	public void setRace(Race race) {
		this.race = race;
	}
	@Enumerated
	public CardSet getCardSet() {
		return cardSet;
	}
	public void setCardSet(CardSet cardSet) {
		this.cardSet = cardSet;
	}
	public Integer getMana() {
		return mana;
	}
	public void setMana(Integer mana) {
		this.mana = mana;
	}
	public Integer getAttack() {
		return attack;
	}
	public void setAttack(Integer attack) {
		this.attack = attack;
	}
	public Integer getHealth() {
		return health;
	}
	public void setHealth(Integer health) {
		this.health = health;
	}
	public Boolean getCollectible() {
		return collectible;
	}
	public void setCollectible(Boolean collectible) {
		this.collectible = collectible;
	}
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable
    @MapKeyJoinColumn
	public Map<Locale, String> getName() {
		return name;
	}
	public void setName(Map<Locale, String> name) {
		this.name = name;
	}
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable
    @MapKeyJoinColumn
	public Map<Locale, String> getDescription() {
		return description;
	}
	public void setDescription(Map<Locale, String> description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		String localizedName = getName().get(Locale.getDefault());
		return localizedName != null ? localizedName : "Card #" + id;
	}
	
	@Override
	public int hashCode() {
		return id == null ? 0 : id.intValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result;
		if (this == obj) {
			result = true;
		} else if (obj == null || this.getClass() != obj.getClass()) {
			result = false;
		} else {
			Card other = (Card) obj;
			result = (this.id != null && other.id != null && this.id.equals(other.id));
		}
		return result;
	}

}
