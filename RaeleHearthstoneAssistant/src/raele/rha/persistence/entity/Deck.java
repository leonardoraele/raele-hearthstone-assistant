package raele.rha.persistence.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Deck {
	
	private Long id;
	private Hero hero;
	private String name;
	private String description;
	private Date creationDate;
	private Integer version;
	private Set<DeckEntry> entries = new HashSet<DeckEntry>();
	
	public Deck() {}
	
	public Deck(String name, String description)
	{
		this.name = name;
		this.description = description;
		this.version = 1;
		this.hero = null;
		this.creationDate = new Date();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<DeckEntry> getEntries() {
		return entries;
	}
	public void setEntries(Set<DeckEntry> entries) {
		this.entries = entries;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Hero getHero() {
		return hero;
	}
	public void setHero(Hero hero) {
		this.hero = hero;
	}

	@Override
	public String toString() {
		return ""+name;
	}

}
