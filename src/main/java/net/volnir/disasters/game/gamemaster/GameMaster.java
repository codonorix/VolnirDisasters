package net.volnir.disasters.game.gamemaster;

import net.volnir.disasters.commands.PermissionMessages;
import net.volnir.disasters.commands.Permissions;
import net.volnir.disasters.helper.CustomHeadCreator;
import net.volnir.disasters.helper.GameEntitiesNames;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GameMaster implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		// Check if the command sender has admin permissions
		if(!(commandSender.hasPermission(Permissions.ADMIN_PERMISSIONS.toString()))) {
			commandSender.sendMessage(PermissionMessages.ADMIN_PERMS_ERR.getMessage());
			return false;
		} else if (!(commandSender instanceof Player)) {
			// Check if the command sender is a player
			commandSender.sendMessage(PermissionMessages.NON_PLAYER_ERR.getMessage());
			return false;
		}

		Player player = (Player) commandSender;
		spawnGameMaster(player);
		return false;
	}

	/**
	 * Spawns a game master zombie at the specified player's location.
	 * The game master zombie has custom properties and armour.
	 *
	 * @param player The player to spawn the game master zombie for.
	 */
	private void spawnGameMaster(Player player) {
		// Spawn a game master zombie at the player's location
		Zombie gameMaster = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
		gameMaster.customName(GameEntitiesNames.GAME_MASTER.getName());
		gameMaster.setCustomNameVisible(true);
		gameMaster.setGravity(false);
		gameMaster.setAI(false);
		gameMaster.setPersistent(true);
		gameMaster.setShouldBurnInDay(false);
		gameMaster.setInvulnerable(true);
		gameMaster.setAware(true);

		// Set the game master's helmet as a custom head
		gameMaster.getEquipment().setHelmet(new CustomHeadCreator().generateHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjYyZmU1ODJmOWUxZjljMmU3NGIxNzFlYWY0NzAwNTE0ZGIwOTU1MWUyNDZiNTRiM2MzZTU0MmQ1MmVkNGQ2OCJ9fX0"));
		addArmour(gameMaster);
	}

	/**
	 * Adds custom coloured leather armour to the specified zombie.
	 *
	 * @param zombie The zombie to add the armour to.
	 */
	private void addArmour(Zombie zombie) {
		// Create an ArrayList to hold the armour items
		ArrayList<ItemStack> armour = new ArrayList<>();
		armour.add(new ItemStack(Material.LEATHER_CHESTPLATE));
		armour.add(new ItemStack(Material.LEATHER_LEGGINGS));
		armour.add(new ItemStack(Material.LEATHER_BOOTS));

		for(int i = 0; i < armour.size(); i++) {
			ItemStack item = armour.get(i);

			// Set the colour of the leather armour
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(Color.fromRGB(98, 56, 127));
			item.setItemMeta(meta);

			// Set the corresponding armour piece on the zombie
			switch (i) {
				case 0:
					zombie.getEquipment().setChestplate(item);
					break;
				case 1:
					zombie.getEquipment().setLeggings(item);
					break;
				case 2:
					zombie.getEquipment().setBoots(item);
					break;
			}
		}
	}
}
