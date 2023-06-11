package net.volnir.disasters.disasters.night.werewolf;

import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WereWolfDeath implements Listener {
	@EventHandler
	public void werewolfDeathEvent(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		PlayerObject playerObject = PlayerStorage.getPlayerObject(player.getUniqueId());

		if(playerObject.getGameId() == null) return;

		GameObject gameObject = GameStorage.getGameObject(playerObject.getGameId());

		if(!(gameObject.getDisaster() instanceof WereWolf)) return;

		WereWolf wereWolf = (WereWolf) gameObject.getDisaster();

		if(!(wereWolf.getWereWolf() == player)) return;
		event.setCancelled(true);
		player.spigot().respawn();
		for(Player playerInGame : gameObject.getAllPlayers()) {
			playerInGame.sendMessage(event.getEntity().getName() + " killed the werewolf! GG!");
		}

		gameObject.setManuallyEnd(true);
	}
}
