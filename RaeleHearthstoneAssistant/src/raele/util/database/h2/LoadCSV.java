package raele.util.database.h2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import persistence.Dao;
import persistence.entity.Card;
import persistence.entity.CardSet;
import persistence.entity.Category;
import persistence.entity.Hero;
import persistence.entity.Quality;
import persistence.entity.Race;

public class LoadCSV {
	
	private static class Attrs {
		public String name;
		public String hero;
		public String rarity;
		public String type;
		public String race;
		public String mana;
		public String attack;
		public String health;
		public String set;
		public String ability;

		public Attrs(String line) {
			String[] split = line.split(",");
			this.name = split[1];
			this.hero = split[2];
			this.rarity = split[3];
			this.type = split[4];
			this.race = split[5];
			this.mana = split[6];
			this.attack = split[7];
			this.health = split[8];
			this.set = split[9];
			try {
				this.ability = split[10];
			} catch (ArrayIndexOutOfBoundsException e) {
				this.ability = "";
				System.err.println("Treating " + this.name + " as vanilla.");
			}
		}
	}
	
	private static Locale enUS = new Locale("en", "US");
	
	public static void main(String[] args) throws IOException {
		String filename;
		
		if (args.length > 0)
		{
			filename = args[0];
		}
		else
		{
			System.out.print("Csv file: ");
			Scanner scanner = new Scanner(System.in);
			filename = scanner.nextLine();
			scanner.close();
		}
		
		File csv = new File(filename);
		
		if (!csv.exists())
		{
			System.err.println("File " + filename + " not found.");
		}
		
		FileInputStream input = new FileInputStream(csv);
		Scanner scanner = new Scanner(input);
		Dao dao = new Dao("H2");
		dao.beginTransaction();
		scanner.nextLine();
		while (scanner.hasNext())
		{
			String line = scanner.nextLine();
			Attrs attrs = new Attrs(line);
			Card card = convert(attrs);
			dao.insert(card);
		}
		dao.commitTransaction();
		dao.close();
		scanner.close();
		input.close();
	}
	
	private static Card convert(Attrs attrs)
	{
		Card result = new Card();
		result.setHero(Hero.valueOf(attrs.hero.toLowerCase()));
		result.setCategory(Category.valueOf(attrs.type.toLowerCase()));
		result.setQuality("".equals(attrs.rarity) ? null : Quality.valueOf(attrs.rarity.toLowerCase()));
		result.setRace("".equals(attrs.race) ? null : Race.valueOf(attrs.race.toLowerCase()));
		result.setCardSet(CardSet.valueOf(attrs.set.toLowerCase()));
		result.setMana(Integer.parseInt(attrs.mana));
		result.setAttack("".equals(attrs.attack) ? null : Integer.parseInt(attrs.attack));
		result.setHealth("".equals(attrs.health) ? null : Integer.parseInt(attrs.health));
		result.setCollectible(true);
		result.getName().put(enUS, attrs.name);
		result.getDescription().put(enUS, attrs.ability);
		return result;
	}
	
}
