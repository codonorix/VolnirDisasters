package net.volnir.disasters.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum PermissionMessages {
	NON_PLAYER_ERR(Component.text("✗ You must be a PLAYER to use this command", TextColor.color(180, 12, 0))),
	ADMIN_PERMS_ERR(Component.text("✗ You must be an ADMIN to use this command", TextColor.color(180, 12, 0))),
	;
	private final Component messageComponent;

	private PermissionMessages(Component messageComponent) {
		this.messageComponent = messageComponent;
	}

	public Component getMessage() {
		return messageComponent;
	}
}
