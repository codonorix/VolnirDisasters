package net.volnir.disasters.commands.admin;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.BlockVector3Imp;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.kyori.adventure.text.Component;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.commands.PermissionMessages;
import net.volnir.disasters.commands.Permissions;
import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Debug implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if(!(commandSender.hasPermission(Permissions.ADMIN_PERMISSIONS.toString()))) {
			commandSender.sendMessage(PermissionMessages.ADMIN_PERMS_ERR.getMessage());
			return false;
		}

		String info = strings[0];
		Player player = (Player) commandSender;

		switch (info.toLowerCase()) {
			case "playerslist":
				getPlayerObjects(player);
				break;
			case "arena":
				getGameObjects(player);
				break;
			case "arenaclear":
				GameStorage.getInstance().clear();
				player.sendMessage("Arenas cleared");
				break;
			case "savearena":
				saveArena(player);
				break;
			case "testchem":
				try {
					testSchem(player);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				break;
			case "undoarena":
				undoArena(player);
				break;
			case "toggleinvis":
				player.setInvisible(!player.isInvisible());
				break;

		}
		return false;
	}

	private void undoArena(Player player) {
		GameObject gameObject = GameStorage.getGameObject(PlayerStorage.getPlayerObject(player.getUniqueId()).getGameId());
		EditSession lobby = gameObject.getLobbyItem();
		EditSession map = gameObject.getMapItem();

		try(EditSession editSession = WorldEdit.getInstance().newEditSession(lobby.getWorld())) {
			lobby.undo(editSession);
			map.undo(editSession);
		}

		player.sendMessage("Map went bye bye :)");
	}
	private void testSchem(Player player) throws IOException {
		String source = Bukkit.getServer().getWorldContainer().getCanonicalFile() + "\\!Schematics\\lobby\\lobby_1.schem";
		File file = new File(source);
		BlockVector3 blockVector3 = BlockVector3.at(player.getLocation().getX() + 64, player.getLocation().getY() + 64, player.getLocation().getZ() + 64);

		com.sk89q.worldedit.world.World weWorld = FaweAPI.getWorld(player.getWorld().getName());
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		ClipboardReader reader = null;

		try {
			reader = format.getReader(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Clipboard clipboard = null;
		try {
			clipboard = reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1)) {
			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(blockVector3)
					.ignoreAirBlocks(false)
					.build();
			Operations.complete(operation);

			player.teleport(new Location(player.getWorld(), player.getLocation().getX() + 64, player.getLocation().getY() + 64, player.getLocation().getZ() + 64));
			new BukkitRunnable() {
				int tally = 10;
				@Override
				public void run() {
					player.sendMessage(String.valueOf(tally));

					if(tally <= 0) {
						editSession.undo(editSession);
						cancel();
					}

					tally--;
				}
			}.runTaskTimer(Disasters.getInstance(), 0, 20);
		}
	}
	private void saveArena(Player player) {
		Bukkit.getWorld(player.getWorld().getUID()).save();
		player.sendMessage("World saved");
	}

	private void getPlayerObjects(Player sender) {
		Inventory inventory = Bukkit.createInventory(null, 9*5);
		for(PlayerObject player : PlayerStorage.getInstance().values()) {
			ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

			meta.displayName(player.getPlayer().displayName());
			meta.setOwningPlayer(player.getPlayer());

			meta.lore(Arrays.asList(
					Component.text(player.getGameId() == null ? "Not in game" : player.getGameId()),
					Component.text("Wins: " + player.getWins()),
					Component.text("Death: " + player.getDeaths()),
					Component.text("Total Games: " + player.getTotalGames()),
					Component.text("Coins: " + player.getCoins()),
					Component.text("Lootboxs: " + player.getLootBoxs()),
					Component.text("Teams: " + getTeams(player.getPlayer()))
			));

			itemStack.setItemMeta(meta);

			inventory.addItem(itemStack);

			sender.openInventory(inventory);
		}
	}

	private void getGameObjects(Player player) {
		Random random = new Random();
		Inventory inventory = Bukkit.createInventory(null, 9*5);

		for(GameObject gameObject : GameStorage.getInstance().values()) {
			int req = random.nextInt(20);
			int tally = 0;

			for (Material item : Material.values()) {
				if (tally >= req && !(item.isLegacy())) {
					ItemStack itemStack = new ItemStack(item);
					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.displayName(Component.text(gameObject.getGameId()));
					itemMeta.lore(
							Arrays.asList(
									Component.text(("Game Stage: " + gameObject.getGameStage()))
							)
					);
					itemStack.setItemMeta(itemMeta);
					inventory.addItem(itemStack);
					break;
				}else{
					tally++;
				}
			}
		}
		player.openInventory(inventory);
	}

	private String getTeams(Player player) {
		String teams = "";
		for(Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
			if(team.hasPlayer(player)) {
				teams += team.getName();
			}
		}
		return teams;
	}
}
