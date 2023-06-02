package net.volnir.disasters.game.gamemaster;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.volnir.disasters.Disasters;
import net.volnir.disasters.game.gameobject.GameObject;
import net.volnir.disasters.game.gameobject.GameStages;
import net.volnir.disasters.game.gamesystem.GameController;
import net.volnir.disasters.helper.GameEntitiesNames;
import net.volnir.disasters.helper.Messages;
import net.volnir.disasters.storage.GameStorage;
import net.volnir.disasters.storage.PlayerStorage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameMasterClick implements Listener {
	@EventHandler
	public void gameMasterClickEvent(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked().customName().equals(GameEntitiesNames.GAME_MASTER.getName()))) return;
		if(!(event.getHand().equals(EquipmentSlot.HAND))) return;

		Player player = event.getPlayer();
		new GameController().joinGame(player);
	}
}
