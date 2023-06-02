package net.volnir.disasters.commands;

public enum Permissions {
	ADMIN_PERMISSIONS("DISASTERS_PERMISSION_ADMIN"),
	;
	private final String permissionValue;

	private Permissions(String permissionValue) {
		this.permissionValue = permissionValue;
	}

	public String toString() {
		return permissionValue;
	}
}
