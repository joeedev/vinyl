package dev.joee.vinyl.network;

import dev.joee.vinyl.mixin.SaveHandlerBaseMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import net.minecraft.server.net.handler.PacketHandlerServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Environment(EnvType.SERVER)
public class ServerFileManager extends FileManagerBase {
	public static final ServerFileManager instance = new ServerFileManager();

	private int globalFileId = 0;

	private synchronized int getFileId() {
		return this.globalFileId++;
	}

	public File getAudioDir() {
		World world = MinecraftServer.getInstance().getDimensionWorld(0);
		File saveDir = ((SaveHandlerBaseMixin) world.saveHandler).invokeGetSaveDirectory();
		return new File(saveDir, "vinyl/sounds");
	}

	public File getAudioFile(String filePath) {
		return new File(this.getAudioDir(), filePath);
	}

	public CompletableFuture<Boolean> sendAudioFile(Player player, String filePath) throws IOException {
		File file = this.getAudioFile(filePath);
		return this.sendAudioFile(((PlayerServer) player).playerNetServerHandler, file, filePath);
	}

	public CompletableFuture<Boolean> sendAudioFile(PacketHandler packetHandler, String filePath) throws IOException {
		File file = this.getAudioFile(filePath);
		return this.sendAudioFile(packetHandler, file, filePath);
	}

	public CompletableFuture<Boolean> sendAudioFile(PacketHandler packetHandler, File file, String filePath) throws IOException {
		byte[] data = Files.readAllBytes(file.toPath());
		return this.sendAudioFile(packetHandler, data, filePath);
	}

	public CompletableFuture<Boolean> sendAudioFile(PacketHandler packetHandler, byte[] data, String filePath) {
		int fileId = this.getFileId();
		int size = data.length;
		int chunkSize = PacketAudioChunk.CHUNK_SIZE;
		int numChunks = size / chunkSize;
		if (size % chunkSize > 0) {
			numChunks += 1;
		}

		for (int i = 0; i < numChunks; i++) {
			if (i == numChunks - 1) {
				this.sendPacket(
					packetHandler,
					new PacketAudioChunk(
						fileId,
						Arrays.copyOfRange(data, i * chunkSize, (i + 1) * chunkSize),
						filePath,
						size
					)
				);
			} else {
				this.sendPacket(
					packetHandler,
					new PacketAudioChunk(
						fileId,
						Arrays.copyOfRange(data, i * chunkSize, (i + 1) * chunkSize)
					)
				);
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			try {
				PacketAudioReceived.waitForReceivedFile(fileId);
			} catch (InterruptedException e) {
				throw new CompletionException(e);
			}
			return null;
		});
	}

	private void sendPacket(PacketHandler packetHandler, Packet packet) {
		if (packetHandler instanceof PacketHandlerLogin) {
			((PacketHandlerLogin) packetHandler).netManager.addToSendQueue(packet);
		} else if (packetHandler instanceof PacketHandlerServer) {
			((PacketHandlerServer) packetHandler).netManager.addToSendQueue(packet);
		} else {
			assert false;
		}
	}
}
