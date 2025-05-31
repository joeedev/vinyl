package dev.joee.vinyl.mixin;

import dev.joee.vinyl.network.IVinylPacket;
import net.minecraft.core.net.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Packet.class, remap = false)
public class PacketMixin implements IVinylPacket {
	@Unique
	private boolean isAudioPacket = false;

	@Override
	public boolean vinyl$isAudioPacket() {
		return this.isAudioPacket;
	}

	@Override
	public void vinyl$markAsAudioPacket() {
		this.isAudioPacket = true;
	}
}
