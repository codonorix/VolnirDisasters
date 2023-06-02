package net.volnir.disasters.disasters;

import net.volnir.disasters.disasters.night.werewolf.WereWolf;

import java.util.ArrayList;
import java.util.HashMap;

public class AllDisasters {
	private static final ArrayList<DisastersInterface> nightDisasters = new ArrayList<>();
	private static final ArrayList<DisastersInterface> sunDisasters = new ArrayList<>();
	private static final ArrayList<DisastersInterface> rainDisasters = new ArrayList<>();
	private static final ArrayList<DisastersInterface> cloudDisasters = new ArrayList<>();

	/**
	 * Loads the disasters if the nightDisasters list is empty.
	 * It calls the populateDisasters() method to populate the lists.
	 */
	private static void loadDisasters() {
		if(nightDisasters.isEmpty()) {
			populateDisasters();
		}
	}

	/**
	 * Populates the disasters by calling the appropriate methods
	 * for each type of disaster.
	 */
	private static void populateDisasters() {
		populateNightDisasters();
		//TODO - Add more populateXDisasters() methods for other disaster types if needed
	}

	/**
	 * Returns a randomly selected disaster from the available lists.
	 * It first loads the disasters, and currently, it always returns the
	 * first element of the nightDisasters list.
	 *
	 * @return A randomly selected disaster.
	 */
	public static DisastersInterface getRandomDisaster() {
		loadDisasters();

		return nightDisasters.get(0);
	}

	/**
	 * Populates the nightDisasters list with specific night disasters.
	 * Modify this method to add more night disasters as needed.
	 */
	private static void populateNightDisasters() {
		nightDisasters.add(new WereWolf());
		// Add more night disasters here if needed
	}
}
