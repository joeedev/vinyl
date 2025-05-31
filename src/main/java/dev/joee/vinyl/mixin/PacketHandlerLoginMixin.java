package dev.joee.vinyl.mixin;

import dev.joee.vinyl.network.IVinylPacketHandlerLogin;
import dev.joee.vinyl.network.PacketFileList;
import dev.joee.vinyl.file.FileManagerServer;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerLogin.class, remap = false)
public class PacketHandlerLoginMixin implements IVinylPacketHandlerLogin {
	@Shadow
	public NetworkManager netManager;

	@Shadow
	private PacketLogin loginPacket;
	@Unique
	private PacketLogin vinylLoginPacket;

	@Unique
	private boolean hasReceivedAudio = false;

	@Override
	public void vinyl$markAsReceivedAudio() {
		this.loginPacket = vinylLoginPacket;
		this.hasReceivedAudio = true;
	}

	@Inject(method = "doLogin", at = @At("HEAD"), cancellable = true)
	public void doLogin(PacketLogin loginPacket, CallbackInfo ci) {
		if (!this.hasReceivedAudio) {
			this.vinylLoginPacket = loginPacket;
			this.netManager.addToSendQueue(
				new PacketFileList(FileManagerServer.instance.getAudioFilePaths())
			);
			ci.cancel();
		}
	}
}
