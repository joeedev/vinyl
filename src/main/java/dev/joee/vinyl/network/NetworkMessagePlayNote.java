package dev.joee.vinyl.network;

import dev.joee.vinyl.Vinyl;
import net.minecraft.client.Minecraft;
import net.minecraft.core.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class NetworkMessagePlayNote implements NetworkMessage {
	public String filePath;
	public float pitch;
	public int x;
	public int y;
	public int z;

	public NetworkMessagePlayNote() {

	}

	public NetworkMessagePlayNote(String filePath, float pitch, int x, int y, int z) {
		this.filePath = filePath;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeString(this.filePath);
		packet.writeDouble(this.pitch);
		packet.writeInt(this.x);
		packet.writeInt(this.y);
		packet.writeInt(this.z);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.filePath = packet.readString();
		this.pitch = (float) packet.readDouble();
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

		mc.sndManager.playSound(
			Vinyl.SOUNDS.getSoundEntryForNoteBlock(this.filePath),
			SoundCategory.WORLD_SOUNDS,
			3, this.pitch
		);
	}
}
