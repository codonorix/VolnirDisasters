package net.volnir.disasters.storage;

import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.game.gameobject.GameObject;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStorage {
	private static HashMap<UUID, PlayerObject> instance = new HashMap<>();

	// Private constructor to prevent direct instantiation
	private PlayerStorage() {
	}

	// Accessor method to get the singleton instance
	public static HashMap<UUID, PlayerObject> getInstance() {
		if (instance == null) {
			// Create the singleton instance if it doesn't exist
			instance = new HashMap<>();
		}
		return instance;
	}

	public static void addPlayer(UUID uuid, PlayerObject playerObject) {
		instance.put(uuid, playerObject);
	}

	public static void removePlayer(UUID uuid) {
		instance.remove(uuid);
	}

	public static PlayerObject getPlayerObject(UUID uuid) {
		return instance.get(uuid);
	}

	public static void clearStorage() {
		instance.clear();
	}

	public static boolean existsInStorage(UUID uuid) {
		return instance.containsKey(uuid);
	}

	public static GameObject getGameObj(Player player) {
		return GameStorage.getGameObject(PlayerStorage.getPlayerObject(player.getUniqueId()).getGameId());
	}
}
