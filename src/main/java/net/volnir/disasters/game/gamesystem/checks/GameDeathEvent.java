package net.volnir.disasters.game.gamesystem.checks;

import net.volnir.disasters.Disasters;
import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.game.gamesystem.gameItems.LeaveItem;
import net.volnir.disasters.game.gamesystem.gameItems.spectateitem.SpectateItem;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameDeathEvent implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();

		if(PlayerStorage.getPlayerObject(player.getUniqueId()).getGameId() == null) return;

		event.deathMessage(null);
		respawnPlayer(event.getPlayer(), event.getPlayer().getLocation());
		hideFromGamePlayers(player);
	}

	private void respawnPlayer(Player player, Location location) {
		player.setBedSpawnLocation(location, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				player.spigot().respawn();
				player.setGameMode(GameMode.CREATIVE);
				PotionEffect potionEffect = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2);
				player.addPotionEffect(potionEffect);
				player.getInventory().setItem(8, new LeaveItem().leaveGameItem());
				player.getInventory().setItem(0, new SpectateItem().spectatePlayerItem());
			}
		}.runTaskLater(Disasters.getInstance(), 1);
	}

	private void hideFromGamePlayers(Player player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team spectators = scoreboard.getTeam("spectators");

		if(spectators == null) {
			spectators = scoreboard.registerNewTeam("spectators");
			spectators.setCanSeeFriendlyInvisibles(true);
			spectators.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			spectators.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		}

		//working on stopping spectator pvp
		spectators.addPlayer(player);
	}


}
