package net.volnir.disasters.disasters;

import org.bukkit.entity.Player;

import java.util.List;

public interface DisastersInterface {
	void performDisaster(List<Player> players);
	void disasterMessage(List<Player> players);
	void disasterEffect(List<Player> players);

	/**
	 * Designed to check if the disaster should be run singularly or if it can have other disasters.
	 * @return if solo disaster
	 */
	boolean soloDisaster();
}
