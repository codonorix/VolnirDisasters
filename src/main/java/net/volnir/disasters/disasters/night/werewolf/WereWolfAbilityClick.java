package net.volnir.disasters.disasters.night.werewolf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.volnir.disasters.Disasters;
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
		System.out.println(event.getAction() == Action.RIGHT_CLICK_AIR);
		if(event.getAction() != Action.RIGHT_CLICK_AIR) return;

		ItemStack itemStack = event.getItem();
		NamespacedKey activeAbility = new NamespacedKey(Disasters.getInstance(), "DISASTERS_WEREWOLF");

		PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

		if(!(container.has(activeAbility))) return;

		event.setCancelled(true);
		String data = container.get(activeAbility, PersistentDataType.STRING);

		if (data.equals("SHOW_PLAYERS")) {
			WereWolf.getObjectInstance().showPlayersItemCooldown();
			WereWolf.getObjectInstance().highlightPlayers();
		}else{
			int countdown = WereWolf.getObjectInstance().getWereWolf().getInventory().getItem(1).getAmount();
			WereWolf.getObjectInstance().getWereWolf().sendMessage(Component.text("You can only use that ability in " + countdown + " seconds!", TextColor.color(255, 0, 0)));
		}
	}
}
