package net.volnir.disasters.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum Messages {
	LINE_BREAK_ONE(Component.text("─────────────────────────", TextColor.color(0, 223, 238), TextDecoration.STRIKETHROUGH)),
	;
	private final Component messageComponent;

	private Messages(Component messageComponent) {
		this.messageComponent = messageComponent;
	}

	public Component getMessage() {
		return messageComponent;
	}
}
