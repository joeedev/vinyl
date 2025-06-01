package dev.joee.vinyl.network;

import dev.joee.vinyl.file.FileManagerClient;
import dev.joee.vinyl.file.FileManagerServer;
import dev.joee.vinyl.gui.ScreenDownloadMusic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PacketFileList extends Packet {
	public String[] filePaths;

	@SuppressWarnings("unused")
	public PacketFileList() {

	}

	public PacketFileList(String[] filePaths) {
		this.filePaths = filePaths;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		this.filePaths = new String[length];
		for (int i = 0; i < length; i++) {
			this.filePaths[i] = dis.readUTF();
		}
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.filePaths.length);
		for (String filePath : this.filePaths) {
			dos.writeUTF(filePath);
		}
	}

	@Override
	public void handlePacket(PacketHandler packetHandler) {
		if (EnvironmentHelper.isServerEnvironment() && packetHandler instanceof PacketHandlerLogin) {
			// Send the client the files they asked for

			List<CompletableFuture<?>> futures = new ArrayList<>();

			for (String filePath : this.filePaths) {
				try {
					futures.add(
						FileManagerServer.instance.sendAudioFile(packetHandler, filePath)
					);
				} catch (IOException e) {
					((PacketHandlerLogin) packetHandler).kickUser(
						"An error occurred while downloading Vinyl music."
					);
					throw new RuntimeException(e);
				}
			}

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[] {}))
				.thenAccept(ignored -> {
					((IVinylPacketHandlerLogin) packetHandler).vinyl$markAsReceivedAudio();
				})
				.exceptionally(ignored -> {
					((PacketHandlerLogin) packetHandler).kickUser(
						"An error occurred while downloading Vinyl music."
					);
					return null;
				});
		} else if (!EnvironmentHelper.isServerEnvironment() && packetHandler instanceof PacketHandlerClient) {
			// Send the server what files we need

			String[] clientFilePaths = FileManagerClient.instance.getAudioFilePaths();
			String[] filePathsNeeded = Arrays.stream(this.filePaths).filter(
				f -> !Arrays.asList(clientFilePaths).contains(f)
			).toArray(String[]::new);

			((PacketHandlerClient) packetHandler).addToSendQueue(
				new PacketFileList(filePathsNeeded)
			);

			this.showScreen((PacketHandlerClient) packetHandler, filePathsNeeded.length);
		}
 	}

	@Override
	public int getEstimatedSize() {
		return Arrays.stream(this.filePaths)
			.mapToInt(p -> p.getBytes().length)
			.sum() + 4;
	}

	@Environment(EnvType.CLIENT)
	private void showScreen(PacketHandlerClient packetHandler, int totalFilesNeeded) {
		if (totalFilesNeeded == 0) {
			return;
		}

		Minecraft.getMinecraft().displayScreen(new ScreenDownloadMusic(
			packetHandler,
			totalFilesNeeded
		));
	}
}
