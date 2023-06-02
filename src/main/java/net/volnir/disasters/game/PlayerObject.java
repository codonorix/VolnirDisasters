package net.volnir.disasters.game;

import org.bukkit.entity.Player;

public class PlayerObject {
	private Player player;
	private String gameId;
	private int wins;
	private int deaths;
	private int totalGames;
	private int coins;
	private int lootBoxs;

	public PlayerObject(Player player, String gameId, int wins, int deaths, int totalGames, int coins, int lootBoxs) {
		this.player = player;
		this.gameId = gameId;
		this.wins = wins;
		this.deaths = deaths;
		this.totalGames = totalGames;
		this.coins = coins;
		this.lootBoxs = lootBoxs;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getTotalGames() {
		return totalGames;
	}

	public void setTotalGames(int totalGames) {
		this.totalGames = totalGames;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public int getLootBoxs() {
		return lootBoxs;
	}

	public void setLootBoxs(int lootBoxs) {
		this.lootBoxs = lootBoxs;
	}
}
