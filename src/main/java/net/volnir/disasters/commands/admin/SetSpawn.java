package net.volnir.disasters.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.commands.PermissionMessages;
import net.volnir.disasters.commands.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawn implements CommandExecutor {
	Configuration config = Disasters.config();
	Disasters instance = Disasters.getInstance();
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if(!(commandSender.hasPermission(Permissions.ADMIN_PERMISSIONS.toString()))) {
			commandSender.sendMessage(PermissionMessages.ADMIN_PERMS_ERR.getMessage());
			return false;
		}

		if(!(commandSender instanceof Player)) {
			commandSender.sendMessage("You must be in game to use this command!");
			return false;
		}

		Player player = (Player) commandSender;

		config.set("lobbyLocation", player.getLocation());
		instance.saveConfig();

		player.sendMessage(Component.text("Lobby set!", TextColor.color(0, 255, 0)));
		return false;
	}
}
