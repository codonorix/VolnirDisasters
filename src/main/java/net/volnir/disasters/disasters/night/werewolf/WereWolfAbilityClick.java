package net.volnir.disasters.disasters.night.werewolf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class WereWolfAbilityClick implements Listener {
	@EventHandler
	public void showPlayerClickItem(PlayerInteractEvent event) {
		if(event.getItem() == null) return;
		if(event.getAction() != Action.RIGHT_CLICK_AIR) return;

		ItemStack itemStack = event.getItem();
		NamespacedKey activeAbility = new NamespacedKey(Disasters.getInstance(), "DISASTERS_WEREWOLF");

		PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

		if(!(container.has(activeAbility))) return;
		GameObject gameObject = GameStorage.getGameObject(PlayerStorage.getPlayerObject(event.getPlayer().getUniqueId()).getGameId());

		event.setCancelled(true);
		String data = container.get(activeAbility, PersistentDataType.STRING);
		WereWolf wereWolf = (WereWolf) GameStorage.getGameObject(PlayerStorage.getPlayerObject(event.getPlayer().getUniqueId()).getGameId()).getDisaster();

		if (data.equals("SHOW_PLAYERS")) {
			wereWolf.showPlayersItemCooldown();
			wereWolf.highlightPlayers();
		}else{
			int countdown = wereWolf.getWereWolf().getInventory().getItem(1).getAmount();
			wereWolf.getWereWolf().sendMessage(Component.text("You can only use that ability in " + countdown + " seconds!", TextColor.color(255, 0, 0)));
		}
	}
}
