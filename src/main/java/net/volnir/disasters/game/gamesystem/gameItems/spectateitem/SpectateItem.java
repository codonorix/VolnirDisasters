package net.volnir.disasters.game.gamesystem.gameItems.spectateitem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.game.PlayerObject;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.game.gamesystem.GameController;
import net.volnir.disasters.helper.CustomHeadCreator;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class SpectateItem implements Listener {
	private final NamespacedKey key = new NamespacedKey(Disasters.getInstance(), "DISASTERS_GAME_ITEM");
	private final NamespacedKey playerKey = new NamespacedKey(Disasters.getInstance(), "TELEPORT_KEY");
	private final String itemData = "SPECTATE_PLAYER";
	private final String menuClickItem = "TELEPORT_PLAYER";
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
		PlayerObject playerObject = PlayerStorage.getPlayerObject(player.getUniqueId());
		GameObject gameObject = GameStorage.getGameObject(playerObject.getGameId());
		player.openInventory(spectatePlayersMenu(gameObject));
	}

	@EventHandler
	public void spectateMenuClickEvent(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) return;
		if(!(event.getClickedInventory().getHolder() instanceof SpectateHolder)) return;
		event.setCancelled(true);
		if(event.getCurrentItem() == null) return;
		if(!(event.getWhoClicked() instanceof Player)) return;

		Player player = (Player) event.getWhoClicked();
		PlayerObject playerObject = PlayerStorage.getPlayerObject(player.getUniqueId());
		GameObject gameObject = GameStorage.getGameObject(playerObject.getGameId());
		ItemStack itemStack = event.getCurrentItem();
		String itemValue = itemStack.getItemMeta().getPersistentDataContainer().get(key,PersistentDataType.STRING);
		String playerName = itemStack.getItemMeta().getPersistentDataContainer().get(playerKey, PersistentDataType.STRING);

		if(itemValue.equals(menuClickItem)) {
			for(Player tpPlayer : gameObject.getAlivePlayers()) {
				if(tpPlayer.getName().equals(playerName)) {
					player.teleport(tpPlayer);
					return;
				}
			}
			player.sendMessage("Player not found.");
		}

	}

	private Inventory spectatePlayersMenu(GameObject gameObject) {
		Inventory inventory = new SpectateHolder().getInventory();

		for(Player player : gameObject.getAlivePlayers()) {
			inventory.addItem(playerHead(player));
		}

		return inventory;
	}

	private ItemStack playerHead(Player player) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		headMeta.setOwningPlayer(player);
		headMeta.displayName(player.displayName());
		headMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, menuClickItem);
		headMeta.getPersistentDataContainer().set(playerKey, PersistentDataType.STRING, player.getName());
		head.setItemMeta(headMeta);

		return head;
	}

	public ItemStack spectatePlayerItem() {
		ItemStack itemStack = new ItemStack(Material.COMPASS);
		ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.displayName(Component.text("Players", TextColor.color(255, 0, 0), TextDecoration.BOLD));
		itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemData);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}
}
