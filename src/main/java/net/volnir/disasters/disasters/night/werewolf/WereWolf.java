package net.volnir.disasters.disasters.night.werewolf;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.disasters.DisastersInterface;
import net.volnir.disasters.helper.CustomHeadCreator;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class WereWolf implements DisastersInterface {
	private static WereWolf objectInstance = null;
	private Random random = new Random();
	private List<Player> gamePlayers = null;
	private Player wereWolf = null;

	public void performDisaster(List<Player> players) {
		setGamePlayers(players);
		generateWerewolf();
		sendTitle();
		createWereWolf();
	}

	private void generateWerewolf() {
		setWereWolf(getGamePlayers().get(random.nextInt(gamePlayers.size())));
	}

	private void sendTitle() {
		Title wereWolfMessage = Title.title(
				Component.text(getWereWolf().getName(), TextColor.color(255, 0, 0)),
				Component.text("is the werewolf!", TextColor.color(255, 0, 0)));

		for(Player messagePlayer : getGamePlayers()) {
			if(messagePlayer == getWereWolf()) continue;

			messagePlayer.showTitle(wereWolfMessage);
		}
	}

	private void createWereWolf() {
		getWereWolf().getEquipment().setHelmet(new CustomHeadCreator().generateHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgzYTJhYTlkMzczNGI5MTlhYzI0Yzk2NTllNWUwZjg2ZWNhZmJmNjRkNDc4OGNmYTQzM2JiZWMxODllOCJ9fX0"));
		getWereWolf().getInventory().setItem(0, wolfFang());
		addArmour();
		setName();
		setEffects();
		getWereWolf().getInventory().setItem(1, showPlayersItem());

		wereWolfParticles();
	}

	/**
	 * Adds leather armor to the werewolf entity.
	 * The armor consists of a leather chestplate, leggings, and boots, all colored in a specific RGB color.
	 */
	private void addArmour() {
		ArrayList<ItemStack> armour = new ArrayList<>();
		armour.add(new ItemStack(Material.LEATHER_CHESTPLATE));
		armour.add(new ItemStack(Material.LEATHER_LEGGINGS));
		armour.add(new ItemStack(Material.LEATHER_BOOTS));

		for(int i = 0; i < armour.size(); i++) {
			ItemStack item = armour.get(i);

			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(Color.fromRGB(57, 57, 57));

			item.setItemMeta(meta);
			switch (i) {
				case 0:
					wereWolf.getEquipment().setChestplate(item);
					break;
				case 1:
					wereWolf.getEquipment().setLeggings(item);
					break;
				case 2:
					wereWolf.getEquipment().setBoots(item);
					break;
			}
		}
	}

	private ItemStack wolfFang() {
		ItemStack itemStack = new ItemStack(Material.GHAST_TEAR);
		ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.displayName(Component.text("Wolf Fang", TextColor.color(255, 0, 0)));
		itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 2,true);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	private void setName() {
		Component customName = Component.text("WEREWOLF ", TextColor.color(255, 0, 0), TextDecoration.BOLD);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team werewolfTeam = scoreboard.getTeam("werewolf");

		if(werewolfTeam == null)
		 	werewolfTeam = scoreboard.registerNewTeam("werewolf");

		werewolfTeam.prefix(customName);
		werewolfTeam.addPlayer(wereWolf);
	}

	private void setEffects() {
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
		PotionEffect jumpBoost = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1);

		wereWolf.addPotionEffect(speed);
		wereWolf.addPotionEffect(jumpBoost);
	}

	private ItemStack showPlayersItem() {
		ItemStack showPlayers = new ItemStack(Material.REDSTONE);
		ItemMeta showPlayersMeta = showPlayers.getItemMeta();

		showPlayersMeta.displayName(Component.text("Show players", TextColor.color(0, 255,  0)));
		showPlayersMeta.lore(Arrays.asList(
				Component.text(""),
				Component.text("Will show all players in the area.", TextColor.color(155, 155, 155)),
				Component.text(""),
				Component.text("Cool down: 20 seconds", TextColor.color(155, 155, 155))
		));

		NamespacedKey key = new NamespacedKey(Disasters.getInstance(), "DISASTERS_WEREWOLF");
		showPlayersMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "SHOW_PLAYERS");

		showPlayers.setItemMeta(showPlayersMeta);

		return showPlayers;
	}

	protected void showPlayersItemCooldown() {
		wereWolf.getInventory().setItem(1, new ItemStack(Material.AIR));
		ItemStack cooldownItem = new ItemStack(Material.HEART_OF_THE_SEA, 25);
		ItemMeta cooldownMeta = cooldownItem.getItemMeta();

		cooldownMeta.displayName(Component.text("Show players", TextColor.color(155, 155,  155)));
		NamespacedKey key = new NamespacedKey(Disasters.getInstance(), "DISASTERS_WEREWOLF");
		cooldownMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "SHOW_PLAYERS_INACTIVE");

		cooldownItem.setItemMeta(cooldownMeta);

		new BukkitRunnable() {
			int counter = 25;
			@Override
			public void run() {
				cooldownItem.setAmount(counter);
				wereWolf.getInventory().setItem(1, cooldownItem);
				counter--;

				if(counter <= 0) {
					wereWolf.getInventory().setItem(1, showPlayersItem());
					cancel();
				}
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	public void highlightPlayers() {
		List<Player> noWolfList = getGamePlayers();
		noWolfList.remove(getWereWolf());

		for(Player player : noWolfList) {
			PotionEffect glowEffect = new PotionEffect(PotionEffectType.GLOWING, 5*20, 1, true);
			player.addPotionEffect(glowEffect);
		}
	}

	public void wereWolfParticles() {
		new BukkitRunnable() {

			@Override
			public void run() {
				getWereWolf().getWorld().spawnParticle(Particle.SHRIEK, getWereWolf().getEyeLocation(), 1, 80);
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	public void resetWerewolf() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team werewolfTeam = scoreboard.getTeam("werewolf");

		if(werewolfTeam == null)
			werewolfTeam = scoreboard.registerNewTeam("werewolf");

		werewolfTeam.removePlayer(this.wereWolf);
		werewolfTeam.unregister();

		for(PotionEffect potionEffect : this.wereWolf.getActivePotionEffects()) {
			this.wereWolf.removePotionEffect(potionEffect.getType());
		}

		this.wereWolf.getInventory().clear();
	}

	public static WereWolf getObjectInstance() {
		return objectInstance;
	}

	public static void setObjectInstance(WereWolf wereWolf) {
		objectInstance = wereWolf;
	}

	public Player getWereWolf() {
		return this.wereWolf;
	}

	public List<Player> getGamePlayers() {
		return this.gamePlayers;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public void setGamePlayers(List<Player> gamePlayers) {
		this.gamePlayers = gamePlayers;
	}

	public void setWereWolf(Player wereWolf) {
		this.wereWolf = wereWolf;
	}
}
