package net.volnir.disasters.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.volnir.disasters.commands.Permissions;
import net.volnir.disasters.helper.GameEntitiesNames;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RemoveNPC implements Listener {
	@EventHandler
	public void removeCustomNpc(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();

		if(!(player.hasPermission(Permissions.ADMIN_PERMISSIONS.toString()))) return;
		if(!(event.getEntity().isCustomNameVisible())) return;

		if(removeEntity(event.getEntity())) player.sendMessage(Component.text("✔ NPC removed", TextColor.color(0, 255, 0)));
	}

	private boolean removeEntity(Entity entity) {
		for(GameEntitiesNames value : GameEntitiesNames.values()) {
			if(value.getName().equals(entity.customName())) {
				entity.remove();
				return true;
			}
		}
		return false;
	}
}
