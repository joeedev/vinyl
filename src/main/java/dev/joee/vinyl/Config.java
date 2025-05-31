package dev.joee.vinyl;

import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

public class Config {
	private final TomlConfigHandler handler;

	public Config() {
		Toml defaultConfig = new Toml("Vinyl configuration file.");

		int startingBlockId = 6789;

		defaultConfig
			.addCategory("BlockIDs")
			.addEntry("vinylPressId", startingBlockId++)
			.addEntry("vinylPressActiveId", startingBlockId++);

		int startingItemId = 21234;

		defaultConfig
			.addCategory("ItemIDs")
			.addEntry("blankRecordId", startingItemId++)
			.addEntry("customRecordId", startingItemId++);

		int startingPacketId = 123;

		defaultConfig
			.addCategory("PacketIDs")
			.addEntry("audioChunkId", startingPacketId++)
			.addEntry("fileListId", startingPacketId++)
			.addEntry("audioReceivedId", startingPacketId++);

		this.handler = new TomlConfigHandler(Vinyl.MOD_ID, defaultConfig);
	}

	public int getBlockId(String key) {
		return this.handler.getInt(String.format("BlockIDs.%s", key));
	}

	public int getItemId(String key) {
		return this.handler.getInt(String.format("ItemIDs.%s", key));
	}

	public int getPacketId(String key) {
		return this.handler.getInt(String.format("PacketIDs.%s", key));
	}
}
