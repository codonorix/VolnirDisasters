package net.volnir.disasters.disasters.night.werewolf;

import net.volnir.disasters.disasters.DisastersInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WereWolfCommand implements CommandExecutor {

	/**
	 * @deprecated
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
//		Player player = (Player) commandSender;
//		List<Player> players = player.getWorld().getPlayers();
//		if(WereWolf.getObjectInstance() == null) {
//			new WereWolf().performDisaster(players);
//		}
//
//		WereWolf objectInstance = WereWolf.getObjectInstance();
//
//		if(strings.length == 1) {
//			if (objectInstance == null) return false;
//			if (strings[0].equals("reset")) {
//				objectInstance.resetWerewolf();
//				commandSender.sendMessage("Reset werewolf");
//				return true;
//			}
//		}

		return false;
	}
}
