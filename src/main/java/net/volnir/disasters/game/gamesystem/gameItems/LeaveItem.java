package net.volnir.disasters.game.gamesystem.gameItems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.game.gamesystem.GameController;
import net.volnir.disasters.helper.Messages;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class LeaveItem implements Listener {
	private final NamespacedKey key = new NamespacedKey(Disasters.getInstance(), "DISASTERS_GAME_ITEM");
	private final String itemData = "LEAVE_GAME";
	private final GameController gameController = new GameController();
	private static final ArrayList<Player> playersLeaving = new ArrayList<>();

	@EventHandler
	public void itemClickEvent(PlayerInteractEvent event) {
		if(event.getItem() == null) return;

		ItemMeta itemMeta = event.getItem().getItemMeta();
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		if (!container.has(key)) return;

		String itemType = container.get(key, PersistentDataType.STRING);
		Player player = event.getPlayer();

		if(playersLeaving.contains(player)) {
			player.sendMessage(Component.text("You are already leaving!", TextColor.color(255, 0, 0)));
			return;
		}

		playersLeaving.add(player);

		if(itemType.equals(itemData)) {
			leaveGameCounter(player);
		}
	}

	private void leaveGameCounter(Player player) {
		new BukkitRunnable() {
			int counter = 3;
			@Override
			public void run() {
				player.sendMessage(Component.text(("Leaving game in " + counter + " seconds."), TextColor.color(0, 255, 0)));

				if (counter <= 0) {
					player.sendMessage(Component.text("You left the game!", TextColor.color(0, 255, 0)));
					gameController.playerLeaveGame(player);
					playersLeaving.remove(player);
					cancel();
				}
				counter--;
			}
		}.runTaskTimer(Disasters.getInstance(), 0, 20);
	}

	public ItemStack leaveGameItem() {
		ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.displayName(Component.text("Leave game", TextColor.color(255, 0, 0), TextDecoration.BOLD));
		itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemData);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}
}
