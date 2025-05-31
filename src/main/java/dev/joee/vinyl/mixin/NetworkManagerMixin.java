package dev.joee.vinyl.mixin;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.network.IVinylPacket;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = NetworkManager.class, remap = false)
public abstract class NetworkManagerMixin {
	@Unique
	private final List<Packet> outgoingAudio = Collections.synchronizedList(new ArrayList<>());
	@Unique
	private int audioWriteDelay = 0;

	@Final
	@Shadow
	private Object writeLock;
	@Shadow
	private int estimatedRemaining;
	@Shadow
	private DataOutputStream dos;
	@Shadow
	private boolean disconnected;
	@Shadow
	protected abstract void onNetworkError(Exception exception);

	@Redirect(method = "addToSendQueue", at = @At(
		value = "INVOKE",
		target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
		ordinal = 1
	))
	public <E> boolean redirectAddToSendQueue(List<Packet> outgoing, E e) {
		Packet packet = (Packet) e;

		if (((IVinylPacket) packet).vinyl$isAudioPacket()) {
			return this.outgoingAudio.add(packet);
		} else {
			return outgoing.add(packet);
		}
	}

	@Inject(
		method = "sendPacket",
		at = @At("RETURN"),
		cancellable = true
	)
	public void injectSendPacket(CallbackInfoReturnable<Boolean> cir) {
		if (this.audioWriteDelay-- <= 0 && !this.outgoingAudio.isEmpty()) {
			Packet packet;

			//noinspection SynchronizeOnNonFinalField
			synchronized (this.writeLock) {
				packet = this.outgoingAudio.remove(0);
			}

			try {
				Packet.writePacket(packet, this.dos);
				this.audioWriteDelay = 5;
			} catch (Exception e) {
				if (!this.disconnected) {
					this.onNetworkError(e);
				}

				cir.setReturnValue(false);
				return;
			}

			cir.setReturnValue(true);
		}
	}
}
