package net.volnir.disasters.storage;

import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.game.gameobject.GameStages;

import java.util.HashMap;

public class GameStorage {
	private static final HashMap<String, GameObject> instance = new HashMap<>();

	// Private constructor to prevent direct instantiation
	private GameStorage() {
	}

	public static HashMap<String, GameObject> getInstance() {
		return instance;
	}

	public static void addGameObject(GameObject gameObject) {
		instance.put(gameObject.getGameId(), gameObject);
	}

	public static GameObject getGameObject(String gameId) {
		return instance.get(gameId);
	}

	/**
	 * Checks if there are any open games.
	 *
	 * @return {@code GameObject} The game that is available.
	 *         {@code null} No available games.
	 */

	public static GameObject hasOpenGames() {
		if(getInstance().isEmpty()) return null;
		for(GameObject gameObject : getInstance().values()) {
			if(gameObject.getGameStage() == GameStages.WAITING_LOBBY) {
				return gameObject;
			}
		}
		return null;
	}
}
