package dev.joee.vinyl.network;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.gui.ScreenDownloadMusic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.net.handler.PacketHandlerClient;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientFileManager extends FileManagerBase {
	public static final ClientFileManager instance = new ClientFileManager();

	private final Map<Integer, byte[]> fileBytes = new HashMap<>();

	public File getAudioDir() {
		return Vinyl.SOUNDS.rootFile;
	}

	public synchronized void handleChunk(PacketAudioChunk chunk, PacketHandlerClient packetHandler) {
		if (this.fileBytes.containsKey(chunk.fileId)) {
			byte[] currentData = this.fileBytes.get(chunk.fileId);
			byte[] newData = ArrayUtils.addAll(currentData, chunk.data);
			this.fileBytes.put(chunk.fileId, newData);
		} else {
			this.fileBytes.put(chunk.fileId, chunk.data);
		}

		if (chunk.isFinalChunk()) {
			byte[] currentData = this.fileBytes.get(chunk.fileId);
			byte[] trimmedData = Arrays.copyOfRange(currentData, 0, chunk.size);

			Vinyl.SOUNDS.saveSound(chunk.filePath, trimmedData);

			this.fileBytes.remove(chunk.fileId);

			packetHandler.addToSendQueue(
				new PacketAudioReceived(chunk.fileId)
			);

			Screen currentScreen = Minecraft.getMinecraft().currentScreen;
			if (currentScreen instanceof ScreenDownloadMusic) {
				((ScreenDownloadMusic) currentScreen).incrementLoadingBar();
			}
		}
	}
}
