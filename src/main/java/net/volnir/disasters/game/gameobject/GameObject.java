package net.volnir.disasters.game.gameobject;

import net.kyori.adventure.text.Component;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.disasters.DisastersInterface;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
	private static int counter = 0;
	private String gameId;
	private DisastersInterface disaster;
	private List<Player> players;
	private GameStages gameStage;
	private String map;
	private Location spawnPoint;
	private int minPlayers;

	private int maxPlayers;

	public GameObject(DisastersInterface disaster, List<Player> players, GameStages gameStage, String map, Location spawnPoint, int minPlayers, int maxPlayers) {
		this.gameId = generateID();
		this.disaster = disaster;
		this.players = players;
		this.gameStage = gameStage;
		this.map = map;
		this.spawnPoint = spawnPoint;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}

	private String generateID() {
		counter++;
		String uniqueID = String.format("%03d", counter);
		String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));

		return "DIS" + currentDate + uniqueID;
	}

	public void checkGameStart() {
		Player newestPlayer = getPlayers().get(getPlayers().size() - 1);
		boolean gameSizeReq = playersSize() < getMinPlayers();

		for(Player player : getPlayers()) {
			player.sendMessage(Component.text("∞" + newestPlayer.getName() + " has joined the lobby. (" + playersSize() + "/" + getMaxPlayers() + ")"));
		}


		if(gameSizeReq) return;

		new BukkitRunnable() {
			int countdown = 5;
			@Override
			public void run() {
				for(Player player : getPlayers()) {
					player.sendMessage(Component.text("The game will start in " + countdown + " seconds."));
				}

				if (gameSizeReq) {
					for(Player player : getPlayers()) {
						player.sendMessage(Component.text("Not enough players! Countdown aborted!"));
						cancel();
					}
				}

				if (countdown <= 0) {
					setGameStage(GameStages.GAME_STARTING);
					cancel();
				}
				countdown--;
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	public int playersSize() {
		return getPlayers().size();
	}
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public GameStages getGameStage() {
		return gameStage;
	}

	public void setGameStage(GameStages gameStage) {
		this.gameStage = gameStage;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		getPlayers().add(player);
	}

	public void removePlayer(Player player) {
		getPlayers().remove(player);
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		GameObject.counter = counter;
	}

	public DisastersInterface getDisaster() {
		return disaster;
	}

	public void setDisaster(DisastersInterface disaster) {
		this.disaster = disaster;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public Location getSpawnPoint() {
		return spawnPoint;
	}

	public void setSpawnPoint(Location spawnPoint) {
		this.spawnPoint = spawnPoint;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
}