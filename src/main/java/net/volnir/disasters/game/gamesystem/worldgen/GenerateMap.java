package net.volnir.disasters.game.gamesystem.worldgen;

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
import com.sk89q.worldedit.session.ClipboardHolder;
import net.volnir.disasters.game.gameobject.GameObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class GenerateMap {
	String minigameWorld = "disastersWorld";
	Random random = new Random();
	public void generateMap(GameObject gameObject) throws IOException {
		Location mapArea = findZone(gameObject);
		gameObject.setLobbyItem(loadLobby(mapArea));
		gameObject.setMapItem(loadMap(mapArea));
	}

	private World getMinigameWorld() {
		return Bukkit.getWorld(minigameWorld) == null ? Bukkit.createWorld(new WorldCreator(minigameWorld)) : Bukkit.getWorld(minigameWorld);
	}

	private Location findZone(GameObject gameObject) {
		World world = getMinigameWorld();
		int maxDistance = 5000;
		int increment = 500;

		for(int x = -maxDistance; x <= maxDistance; x += increment) {
			for(int z = -maxDistance; z <= maxDistance; z += increment) {
				Block block = world.getBlockAt(x, 0, z);
				Block block1 = world.getBlockAt(x, -1, z);
				Block block2 = world.getBlockAt(x, -2, z);

				boolean check1 = block.getType().isAir();
				boolean check2 = block1.getType().isAir();
				boolean check3 = block2.getType().isAir();

				System.out.println(x + " " + z);

				if(!check1 || !check2 || !check3) {
					System.out.println("block found, skipping.");
					continue;
				}

				gameObject.setLobbySpawnPoint(new Location(world, x, 120, z));
				gameObject.setMapSpawnPoint(new Location(world, x, 2, z));
				return new Location(world, x, 0, z);
			}
		}
		return null;
	}

	private EditSession loadLobby(Location location) throws IOException {
		return pasteItem(location, getSchem(0), 120);
	}

	private EditSession loadMap(Location location) throws IOException {
		return pasteItem(location, getSchem(1), 0);
	}

	/**
	 * Gets the desired configuration based on the provided configuration type.
	 *
	 * @param schemType the type of schematic to retrieve
	 *                   - 0: Get the lobby schematic
	 *                   - 1: Get the map schematic
	 * @return the corresponding schematic object
	 */
	private File getSchem(int schemType) throws IOException {
		String folderPath = (Bukkit.getServer().getWorldContainer().getCanonicalPath() + (schemType == 0 ? "\\!Schematics\\lobby" : "\\!Schematics\\map\\"));
		File folder = new File(folderPath);
		File[] lobbies = folder.listFiles();

		if (lobbies == null || lobbies.length == 0) {
			System.out.println("The folder is empty or there are no files.");
			return null;
		}

		return lobbies[random.nextInt(lobbies.length)];
	}

	private EditSession pasteItem(Location location, File lobby, int addY) {
		BlockVector3 blockVector3 = BlockVector3.at(location.getX(), location.getY() + addY, location.getZ());

		com.sk89q.worldedit.world.World weWorld = FaweAPI.getWorld(minigameWorld);
		ClipboardFormat format = ClipboardFormats.findByFile(lobby);
		ClipboardReader reader = null;

		try {
			reader = format.getReader(new FileInputStream(lobby));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Clipboard clipboard = null;
		try {
			clipboard = reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EditSession schem = null;
		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1)) {
			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(blockVector3)
					.ignoreAirBlocks(false)
					.build();
			Operations.complete(operation);
			editSession.close();
			schem = editSession;
		}
		return schem;
	}
}
