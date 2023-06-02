package net.volnir.disasters.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.volnir.disasters.commands.PermissionMessages;
import net.volnir.disasters.commands.Permissions;
import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.helper.Messages;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetStats implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if(!(commandSender.hasPermission(Permissions.ADMIN_PERMISSIONS.toString()))) {
			commandSender.sendMessage(PermissionMessages.ADMIN_PERMS_ERR.getMessage());
			return false;
		}

		if(strings.length == 0 && commandSender instanceof Player) {
			Player player = (Player) commandSender;
			statsMessage(player, player);
		}else{
			commandSender.sendMessage(Messages.LINE_BREAK_ONE.getMessage());
			commandSender.sendMessage(Component.text("INVALID USAGE"));
			commandSender.sendMessage(Component.text("✗ /stats [player name]"));
			commandSender.sendMessage(Messages.LINE_BREAK_ONE.getMessage());
			return false;
		}

		//todo - add custom user search
		return false;
	}

	private void findPlayer() {
		//todo - find the player in config/db
	}
	private void statsMessage(Player player, Player reqPlayer) {
		PlayerObject playerObject = PlayerStorage.getPlayerObject(reqPlayer.getUniqueId());
		String username = playerObject.getPlayer().getName();
		String gameId = playerObject.getGameId() == null ? "None" : playerObject.getGameId();
		int wins = playerObject.getWins();
		int deaths = playerObject.getDeaths();
		int totalGames = playerObject.getTotalGames();
		int coins = playerObject.getCoins();
		int lootboxs = playerObject.getLootBoxs();

		player.sendMessage(Messages.LINE_BREAK_ONE.getMessage());
		player.sendMessage("");
		player.sendMessage(Component.text("Username: ", TextColor.color(0, 223, 238)).append(Component.text(username, TextColor.color(255, 255, 255))));
		player.sendMessage(Component.text("Current game: ", TextColor.color(0, 223, 238)).append(Component.text(gameId, TextColor.color(255, 255, 255))));
		player.sendMessage(Component.text("Wins: ", TextColor.color(0, 223, 238)).append(Component.text(wins, TextColor.color(255, 255, 255))));
		player.sendMessage(Component.text("Deaths: ", TextColor.color(0, 223, 238)).append(Component.text(deaths, TextColor.color(255, 255, 255))));
		player.sendMessage(Component.text("Total Games: ", TextColor.color(0, 223, 238)).append(Component.text(totalGames, TextColor.color(255, 255, 255))));
		player.sendMessage(Component.text("Coins: ", TextColor.color(0, 223, 238)).append(Component.text(coins, TextColor.color(255, 255, 255))));
		player.sendMessage(Component.text("Lootboxs: ", TextColor.color(0, 223, 238)).append(Component.text(lootboxs, TextColor.color(255, 255, 255))));
		player.sendMessage("");
		player.sendMessage(Messages.LINE_BREAK_ONE.getMessage());

	}
}
