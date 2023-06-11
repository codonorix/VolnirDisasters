package net.volnir.disasters.game.gamesystem.gameItems.spectateitem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SpectateHolder implements InventoryHolder {
	private Inventory inventory;

	SpectateHolder() {
		this.inventory = Bukkit.createInventory(this, 9*3, Component.text("Players", TextColor.color(0, 255, 0)));
	}

	public Inventory getInventory() {
		return inventory;
	}
}
