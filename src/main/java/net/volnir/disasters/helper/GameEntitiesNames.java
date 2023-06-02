package net.volnir.disasters.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum GameEntitiesNames {
	GAME_MASTER(Component.text("GAME MASTER", TextColor.color(0, 238, 6), TextDecoration.BOLD)),
	;
	private final Component entityName;

	private GameEntitiesNames(Component messageComponent) {
		this.entityName = messageComponent;
	}

	public Component getName() {
		return entityName;
	}
}
