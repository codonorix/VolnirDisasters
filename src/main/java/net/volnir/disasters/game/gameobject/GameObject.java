package net.volnir.disasters.game.gameobject;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.disasters.AllDisasters;
import net.volnir.disasters.disasters.DisastersInterface;
import net.volnir.disasters.game.gamesystem.worldgen.GenerateMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GameObject {
	private static int counter = 0;
	private String gameId;
	private DisastersInterface disaster;
	private List<Player> allPlayers;
	private List<Player> alivePlayers;
	private List<Player> deadPlayers;
	private List<Player> spectator;
	private GameStages gameStage;
	private int minPlayers;
	private int maxPlayers;
	private EditSession lobbyItem;
	private EditSession mapItem;
	private Location lobbySpawnPoint;
	private Location mapSpawnPoint;
	private int gameDuration = 300;
	private boolean manuallyEnd = false;

	/**
	 * Constructs a new instance of the GameObject class with the specified parameters.
	 *
	 * @param disaster The DisastersInterface implementation associated with the game.
	 * @param players The list of players participating in the game.
	 * @param gameStage The current stage of the game.
	 * @param minPlayers The minimum number of players required to start the game.
	 * @param maxPlayers The maximum number of players allowed in the game.
	 * @throws IOException if an I/O error occurs.
	 */
	public GameObject(DisastersInterface disaster, List<Player> players, GameStages gameStage, int minPlayers, int maxPlayers) throws IOException {
		this.gameId = generateID();
		this.disaster = disaster;
		this.allPlayers = players;
		this.gameStage = gameStage;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}

	/**
	 * Generates a unique ID based on the current game number and the current date.
	 *
	 * @return The generated unique ID in the format "DISddMMyy###",
	 *         where "ddMMyy" represents the current date and "###" is a three-digit counter value.
	 */
	private String generateID() {
		counter++;
		String uniqueID = String.format("%03d", counter);
		String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));

		return "DIS" + currentDate + uniqueID;
	}

	/**
	 * Generates the map and lobby for the game players will be in.
	 *
	 * @throws IOException if map load was unsuccessful
	 */
	public void generateMap() throws IOException {
		new GenerateMap().generateMap(this);
	}

	/**
	 * Unloads the map and lobby.
	 */
	public void unLoadMap() {
		try(EditSession editSession = WorldEdit.getInstance().newEditSession(getLobbyItem().getWorld())) {
			getLobbyItem().undo(editSession);
			getMapItem().undo(editSession);
		}
	}

	/**
	 * Checks if the game can start and initiates the countdown if the conditions are met.
	 */
	public void checkGameStart() {
		Player newestPlayer = getAllPlayers().get(getAllPlayers().size() - 1);
		boolean gameSizeReq = playersSize() < getMinPlayers();

		for(Player player : getAllPlayers()) {
			player.sendMessage(Component.text("∞" + newestPlayer.getName() + " has joined the lobby. (" + playersSize() + "/" + getMaxPlayers() + ")"));
		}

		if(gameSizeReq) return;

		startCountdown();
	}

	/**
	 * Starts the countdown and handles countdown logic.
	 */
	private void startCountdown() {
		new BukkitRunnable() {
			int countdown = 10;
			@Override
			public void run() {
				sendLobbyCountdown(countdown);

				if (getMinPlayers() > playersSize()) {
					notEnoughPlayers();
					cancel();
					return;
				}

				if (countdown <= 0) {
					setupGame();
					cancel();
				}
				countdown--;
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	/**
	 * Sends countdown messages to players.
	 *
	 * @param countdown The current countdown value.
	 */
	private void sendLobbyCountdown(int countdown) {
		for(Player player : getAllPlayers()) {
			player.sendMessage(Component.text("The game will start in " + countdown + " seconds."));
		}
	}

	/**
	 * Message sent when player leaves game
	 * @param player that left game
	 */
	public void playerLeaveNotification(Player player) {
		for(Player inGamePlayers : getAllPlayers()) {
			inGamePlayers.sendMessage(Component.text(("⇦ " + player.getName() + " left the game!"), TextColor.color(255, 0, 0)));
		}
	}

	/**
	 * Handles the case when there are not enough players to start the game.
	 * Sends appropriate messages to players and cancels the countdown.
	 */
	private void notEnoughPlayers() {
		for(Player player : getAllPlayers()) {
			player.sendMessage(Component.text("Not enough players! Countdown aborted!"));
		}
	}

	/**
	 * Sets up the game by performing necessary actions such as teleporting players, generating a disaster, starting the grace period, and displaying the scoreboard.
	 */
	private void setupGame() {
		setGameStage(GameStages.GAME_STARTING);
		setAlivePlayers(allPlayers);
		clearPlayerEffectsAndItems();
		teleportToMap();
		generateDisaster();
		sendDisasterMessage();
		setDisasterEffect();
		startGracePeriod();
		displayScoreboard();
	}

	/**
	 * Clears the inventory of all players participating in the game.
	 */
	private void clearPlayerEffectsAndItems() {
		for(Player player : getAllPlayers()) {
			player.getInventory().clear();
		}
	}

	/**
	 * Teleports all players participating in the game to the map spawn point.
	 */
	private void teleportToMap() {
		for(Player player : getAllPlayers()) {
			player.teleport(getMapSpawnPoint());
		}
	}

	/**
	 * Generates a random disaster and sets it as the current disaster for the game.
	 */
	private void generateDisaster() {
		DisastersInterface disaster = AllDisasters.getRandomDisaster();
		setDisaster(disaster);
	}

	/**
	 * Sends the disaster message to all players, providing information about the current disaster.
	 */
	private void sendDisasterMessage() {
		getDisaster().disasterMessage(getAllPlayers());
	}

	/**
	 * Applies the disaster effect to all players, altering their gameplay based on the current disaster.
	 */
	private void setDisasterEffect() {
		getDisaster().disasterEffect(getAllPlayers());
	}

	/**
	 * Starts a grace period where no disasters are running to allow users to hide and prepare.
	 */
	private void startGracePeriod() {
		new BukkitRunnable() {
			int countdown = 15;

			@Override
			public void run() {
				sendGameCountdown(countdown);

				if(countdown == 0) {
					gameStartedMsg();
					getDisaster().performDisaster(getAllPlayers());
					setGameStage(GameStages.GAME_STARTED);
					startGame();
					cancel();
				}
				countdown--;
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	/**
	 * Message sent to players when the game starts.
	 */
	private void gameStartedMsg() {
		for(Player player : getAllPlayers()) {
			player.sendMessage(Component.text("The disasters have started!", TextColor.color(53, 200, 36)));
		}
	}

	/**
	 * Sends countdown message to players during grace period
	 * @param countdown current time in seconds before game starts
	 */
	private void sendGameCountdown(int countdown) {
		Component gameStarting =
				Component.text("Game starting in ", TextColor.color(53, 200, 36))
						.append(Component.text(countdown, TextColor.color(255, 213, 28)))
						.append(Component.text(" seconds!", TextColor.color(53, 200, 36)));

		for(Player player : getAllPlayers()) {
			if (countdown == 15) {
				player.sendMessage(gameStarting);
			} else if (countdown == 10) {
				player.sendMessage(gameStarting);
			} else if (countdown <= 5 && countdown > 0) {
				player.sendMessage(gameStarting);
			}
		}
	}

	/**
	 * Starts the game and checks when it should end.
	 */
	private void startGame() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(manuallyEnd) {
					cancel();
				}

				if(getGameDuration() <= 0) {
					cancel();
				}

				if(getAllPlayers().size() == 0) {
					cancel();
				}

				setGameDuration(getGameDuration() - 1);
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	//Todo - Add end game system
	private void endGame() {

	}

	/**
	 * Display the scoreboard to the players in the game
	 */
	private void displayScoreboard() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Scoreboard scoreboard = createScoreboard();
				updateScoreboardValues(scoreboard);
				setScoreboard(scoreboard);
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	/**
	 * Creates the scoreboard that will be used in the game
	 * @return scoreboard that will show hold game info
	 */
	private Scoreboard createScoreboard() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("gameboard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.displayName(Component.text("DISASTERS", TextColor.color(0, 255, 255), TextDecoration.BOLD));

		return scoreboard;
	}

	/**
	 * Adds/updates all of the lines in the scoreboard.
	 * @param scoreboard for values to be added to
	 */
	private void updateScoreboardValues(Scoreboard scoreboard) {
		Objective objective = scoreboard.getObjective("gameboard");
		objective.getScore("").setScore(15);
		objective.getScore(ChatColor.AQUA + "Players: " + ChatColor.WHITE + getAllPlayers().size()).setScore(14);
		objective.getScore(ChatColor.AQUA + "Time Left: " + ChatColor.WHITE + gameDuration).setScore(13);
		objective.getScore(" ").setScore(12);
		objective.getScore(ChatColor.GRAY + getGameId()).setScore(1);
	}

	/**
	 * Show all players in game the scoreboard
	 * @param scoreboard to be shown to players
	 */
	private void setScoreboard(Scoreboard scoreboard) {
		for(Player player : getAllPlayers()) {
			player.setScoreboard(scoreboard);
		}
	}


	public int playersSize() {
		return getAllPlayers().size();
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

	public List<Player> getAllPlayers() {
		return allPlayers;
	}

	public void addPlayer(Player player) {
		getAllPlayers().add(player);
	}

	public void removePlayer(Player player) {
		getAllPlayers().remove(player);
	}

	public void setAllPlayers(List<Player> allPlayers) {
		this.allPlayers = allPlayers;
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

	public EditSession getLobbyItem() {
		return lobbyItem;
	}

	public void setLobbyItem(EditSession lobbyItem) {
		this.lobbyItem = lobbyItem;
	}

	public EditSession getMapItem() {
		return mapItem;
	}

	public void setMapItem(EditSession mapItem) {
		this.mapItem = mapItem;
	}

	public Location getLobbySpawnPoint() {
		return lobbySpawnPoint;
	}

	public void setLobbySpawnPoint(Location lobbySpawnPoint) {
		this.lobbySpawnPoint = lobbySpawnPoint;
	}

	public Location getMapSpawnPoint() {
		return mapSpawnPoint;
	}

	public void setMapSpawnPoint(Location mapSpawnPoint) {
		this.mapSpawnPoint = mapSpawnPoint;
	}

	public List<Player> getDeadPlayers() {
		return deadPlayers;
	}

	public void setDeadPlayers(List<Player> deadPlayers) {
		this.deadPlayers = deadPlayers;
	}

	public List<Player> getSpectator() {
		return spectator;
	}

	public void setSpectator(List<Player> spectator) {
		this.spectator = spectator;
	}

	public int getGameDuration() {
		return gameDuration;
	}

	public void setGameDuration(int gameDuration) {
		this.gameDuration = gameDuration;
	}

	public boolean isManuallyEnd() {
		return manuallyEnd;
	}

	public void setManuallyEnd(boolean manuallyEnd) {
		this.manuallyEnd = manuallyEnd;
	}

	public List<Player> getAlivePlayers() {
		return alivePlayers;
	}

	public void setAlivePlayers(List<Player> alivePlayers) {
		this.alivePlayers = alivePlayers;
	}
}
