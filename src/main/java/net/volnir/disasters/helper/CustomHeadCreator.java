package net.volnir.disasters.helper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class CustomHeadCreator {
	public ItemStack generateHead(String id) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

		// Create a new player profile for the skull
		skullMeta.setPlayerProfile(Bukkit.createProfile(UUID.randomUUID(), null));
		PlayerProfile profile = skullMeta.getPlayerProfile();

		// Set the texture property for the player profile
		profile.getProperties().add(new ProfileProperty("textures", id));

		// Set the player profile for the skull metadata
		skullMeta.setPlayerProfile(profile);
		skull.setItemMeta(skullMeta);

		return skull;
	}
}
