package raele.rha.persistence.entity;

public enum Hero {
	// TODO Using locale dependent names
	warrior("Garrosh Hellscream"),
	shaman("Thrall"),
	rogue("Valeera Sanguinar"),
	paladin("Uther the Lightbringer"),
	hunter("Rexxar"),
	druid("Malfurion Stormrage"),
	warlock("Gul'dan"),
	mage("Jaina Proudmoore"),
	priest("Anduin Wrynn"),
	neutral("null")
	;
	private String name;
	
	private Hero(String heroName)
	{
		this.name = heroName;
	}

	public String getName() {
		return name;
	}
	
	public static Hero forName(String heroName)
	{
		if (heroName != null && !heroName.isEmpty())
		{
			Hero[] heroes = Hero.values();
			
			for (Hero hero : heroes)
			{
				if (heroName.equals(hero.getName()))
				{
					return hero;
				}
			}
		}
		
		return Hero.neutral;
	}
	
}
