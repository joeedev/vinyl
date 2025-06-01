package dev.joee.vinyl.file;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.gui.ScreenDownloadMusic;
import dev.joee.vinyl.mixin.SaveHandlerBaseMixin;
import dev.joee.vinyl.network.PacketAudioChunk;
import dev.joee.vinyl.network.PacketAudioReceived;
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
public class FileManagerClient extends FileManagerBase {
	public static final FileManagerClient instance = new FileManagerClient();

	private final Map<Integer, byte[]> fileBytes = new HashMap<>();

	public File getAudioDir() {
		return Vinyl.SOUNDS.rootFile;
	}

	@Override
	public File getWorldAudioDir() {
		Minecraft mc = Minecraft.getMinecraft();
		SaveHandlerBaseMixin saveHandler = (SaveHandlerBaseMixin) mc.currentWorld.getSaveHandler();
		return new File(saveHandler.invokeGetSaveDirectory(), "vinyl/sounds/");
	}

	public synchronized void handleChunk(PacketAudioChunk chunk, PacketHandlerClient packetHandler) {
		if (this.fileBytes.containsKey(chunk.fileId)) {
			byte[] currentData = this.fileBytes.get(chunk.fileId);
			byte[] newData = ArrayUtils.addAll(currentData, chunk.data);
			this.fileBytes.put(chunk.fileId, newData);
		} else {
			this.fileBytes.put(chunk.fileId, chunk.data);
		}

		if (chunk.isFinalChunk() && chunk.filePath.endsWith(".ogg")) {
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
