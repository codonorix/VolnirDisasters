package net.volnir.disasters.storage.objectcreation;

import net.volnir.disasters.game.gamesystem.GameController;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class RemovePlayerData implements Listener {
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		playerLogoffRemove(event.getPlayer());
	}

	public void playerLogoffRemove(Player player) {
		new GameController().playerLeaveGame(player);
		PlayerStorage.removePlayer(player.getUniqueId());
	}
}
