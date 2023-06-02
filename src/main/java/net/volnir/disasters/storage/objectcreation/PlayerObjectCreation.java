package net.volnir.disasters.storage.objectcreation;

import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.logging.Logger;

public class PlayerObjectCreation implements Listener {
	Logger logger;
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if(playerInPluginStorage(uuid, player)) return;

		if(playerExists(uuid)) {
			//todo - Fetch data from DB/config
		}else{
			PlayerStorage.addPlayer(uuid, createObject(player));
		}
	}

	private boolean playerInPluginStorage(UUID uuid, Player player) {
		if(PlayerStorage.existsInStorage(uuid)) {
			logger.warning("Player " + player + " is already in system memory and should not be added again...");
			return true;
		}
		return false;
	}

	private boolean playerExists(UUID uuid) {
		//TODO - Check if player exists in DB/config
		return false;
	}

	private PlayerObject createObject(Player player) {
		return new PlayerObject(player, null, 0, 0, 0, 0, 1);
	}
}
