package dev.joee.vinyl.network;

import dev.joee.vinyl.Vinyl;
import net.minecraft.client.Minecraft;
import net.minecraft.core.lang.I18n;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class NetworkMessagePlayMusic implements NetworkMessage {
	public String name;
	public String artist;
	public String filePath;
	public int x;
	public int y;
	public int z;

	public NetworkMessagePlayMusic() {

	}

	public NetworkMessagePlayMusic(String name, String artist, String fileName, int x, int y, int z) {
		this.name = name;
		this.artist = artist;
		this.filePath = fileName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeString(this.name);
		packet.writeString(this.artist);
		packet.writeString(this.filePath);
		packet.writeInt(this.x);
		packet.writeInt(this.y);
		packet.writeInt(this.z);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.name = packet.readString();
		this.artist = packet.readString();
		this.filePath = packet.readString();
		this.x = packet.readInt();
		this.y = packet.readInt();
		this.z = packet.readInt();
	}

	@Override
	public void handle(NetworkContext context) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();

		mc.hudIngame.setRecordPlayingMessage(String.format(
			"%s - %s",
			this.artist, this.name
		));

		mc.sndManager.playMusic(
			Vinyl.SOUNDS.getSoundEntry(this.filePath),
			this.x, this.y, this.z, 1, 1
		);
	}
}
