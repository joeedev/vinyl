package dev.joee.vinyl.network;

import dev.joee.vinyl.Vinyl;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class NetworkMessagePlayMusic implements NetworkMessage {
	public String filePath;
	public int x;
	public int y;
	public int z;

	public NetworkMessagePlayMusic() {

	}

	public NetworkMessagePlayMusic(String fileName, int x, int y, int z) {
		this.filePath = fileName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeString(this.filePath);
		packet.writeInt(this.x);
		packet.writeInt(this.y);
		packet.writeInt(this.z);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
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

		Minecraft.getMinecraft().sndManager.playMusic(
			Vinyl.SOUNDS.getSoundEntry(this.filePath),
			this.x, this.y, this.z, 1, 1
		);
	}
}
