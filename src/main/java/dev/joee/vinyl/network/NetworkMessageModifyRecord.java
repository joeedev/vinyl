package dev.joee.vinyl.network;

import com.mojang.nbt.tags.CompoundTag;
import dev.joee.vinyl.item.ItemBlankRecord;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class NetworkMessageModifyRecord implements NetworkMessage {
	private int slot;
	private String name;
	private String artist;
	private String url;

	public NetworkMessageModifyRecord() {

	}

	public NetworkMessageModifyRecord(int slot, String name, String artist, String url) {
		this.slot = slot;
		this.name = name;
		this.artist = artist;
		this.url = url;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(this.slot);
		packet.writeString(this.name);
		packet.writeString(this.artist);
		packet.writeString(this.url);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.slot = packet.readInt();
		this.name = packet.readString();
		this.artist = packet.readString();
		this.url = packet.readString();
	}

	@Override
	public void handle(NetworkContext context) {
		if (!EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		ItemStack stack = context.player.inventory.mainInventory[this.slot];
		if (!(stack.getItem() instanceof ItemBlankRecord)) {
			return;
		}

		CompoundTag tag = stack.getData();
		tag.putString("RecordName", this.name);
		tag.putString("RecordArtist", this.artist);
		tag.putString("RecordUrl", this.url);
	}
}
