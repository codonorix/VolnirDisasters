package net.volnir.disasters.game.gamesystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.game.gameobject.GameStages;
import net.volnir.disasters.game.gamesystem.gameItems.LeaveItem;
import net.volnir.disasters.helper.Messages;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class GameController {
	Logger logger;
	public void joinGame(Player player) throws IOException {
		if (isInGameCheck(player)) return;

		GameObject gameObject = checkForOpenGame();

		if(gameObject == null) {
			List<Player> playerList = new ArrayList<>(Arrays.asList(player));
			gameObject = new GameObject(null, playerList, GameStages.WAITING_LOBBY, 2, 16);
			gameObject.generateMap();
			addGameObject(gameObject);
		}else{
			gameObject.addPlayer(player);
		}

		player.teleport(gameObject.getLobbySpawnPoint());

		PlayerStorage.getPlayerObject(player.getUniqueId()).setGameId(gameObject.getGameId());
		clearUserEffects(player);
		player.getInventory().setItem(8, new LeaveItem().leaveGameItem());

		gameObject.checkGameStart();
	}

	/**
	 * Determine if the player can join the game.
	 *
	 * @param player - Player joining game
	 * @return {@code true} player is in game.
	 * 		   {@code false} player is not in a game.
	 */
	private boolean isInGameCheck(Player player) {
		if(PlayerStorage.getPlayerObject(player.getUniqueId()).getGameId() != null) {
			player.sendMessage(Messages.LINE_BREAK_ONE.getMessage());
			player.sendMessage("");
			player.sendMessage(Component.text("You are already in a game!", TextColor.color(255, 0, 0)));
			player.sendMessage("");
			player.sendMessage(Messages.LINE_BREAK_ONE.getMessage());
			return true;
		}
		return false;
	}

	private void clearUserEffects(Player player) {
		player.getInventory().clear();

		for(PotionEffect potionEffect : player.getActivePotionEffects()) {
			player.removePotionEffect(potionEffect.getType());
		}
	}

	private void removeFromGameObject(Player player, String gameId) {
		GameStorage.getGameObject(gameId).removePlayer(player);
	}

	private void notifyPlayers(GameObject gameObject, Player leavingPlayer) {
		gameObject.playerLeaveNotification(leavingPlayer);
	}

	private void removeTeam(Player player) {
		for(Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
			if(team.hasPlayer(player)) team.removePlayer(player);
		}
	}

	public void playerLeaveGame(Player player) {
		PlayerObject playerObject = PlayerStorage.getPlayerObject(player.getUniqueId());
		GameObject gameObject = GameStorage.getGameObject(playerObject.getGameId());

		if(playerObject.getGameId() != null) {
			notifyPlayers(gameObject, player);
			removeFromGameObject(player, playerObject.getGameId());
		}

		removeTeam(player);
		clearUserEffects(player);
		resetTime(player);
		playerObject.setGameId(null);

		try {
			Location location = Disasters.config().getLocation("lobbyLocation");
			player.teleport(location);
		}catch (Exception ex) {
			logger.severe("No lobby set! This will affect gameplay. Set lobby ASAP /setlobby");
		}
	}

	private GameObject checkForOpenGame() {
		return GameStorage.hasOpenGames();
	}

	private void addGameObject(GameObject gameObject) {
		GameStorage.addGameObject(gameObject);
	}

	private void resetTime(Player player) {
		player.resetPlayerTime();
	}
}
