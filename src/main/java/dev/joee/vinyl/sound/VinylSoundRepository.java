package dev.joee.vinyl.sound;

import dev.joee.vinyl.mixin.SaveHandlerBaseMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.NamedSoundRepository;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Environment(EnvType.CLIENT)
public class VinylSoundRepository extends NamedSoundRepository {
	public VinylSoundRepository() {
		super("vinyl");
	}

	public void saveSound(String filePath, byte[] data) {
		File file = new File(this.rootFile, filePath);
		try {
			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();
			if (file.createNewFile()) {
				try (FileOutputStream outputStream = new FileOutputStream(file)) {
					outputStream.write(data);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public SoundEntry getSoundEntry(String filePath) {
		SoundEvent event = new VinylSoundEvent(this, filePath, filePath);
		return new VinylSoundEntry(event, filePath, true);
	}

	public SoundEntry getSoundEntryForNoteBlock(String filePath) {
		SoundEvent event = new VinylSoundEvent(this, filePath, filePath);
		return new VinylSoundEntry(event, filePath, false);
	}

	private static class VinylSoundEvent extends SoundEvent {
		public VinylSoundEvent(@NotNull NamedSoundRepository parentRepo, @NotNull String eventID, @Nullable String subtitleKey) {
			super(parentRepo, eventID, subtitleKey);
		}
	}

	private static class VinylSoundEntry extends SoundEntry {
		public VinylSoundEntry(@NotNull SoundEvent parentEvent, @NotNull String name, boolean shouldStream) {
			super(
				parentEvent,
				Minecraft.getMinecraft().texturePackList.getDefaultTexturePack(),
				name,
				0.5F,
				1,
				1,
				64,
				shouldStream,
				Type.FILE
			);
		}

		@Override
		public @Nullable URL getURL() {
			Minecraft mc = Minecraft.getMinecraft();

			if (mc.isMultiplayerWorld()) {
				return super.getURL();
			}

			SaveHandlerBaseMixin saveHandler = (SaveHandlerBaseMixin) mc.currentWorld.getSaveHandler();
			File file = new File(
				new File(saveHandler.invokeGetSaveDirectory(), "vinyl/sounds/"),
				this.name
			);

			if (file.exists()) {
				try {
					return file.toURI().toURL();
				} catch (MalformedURLException e) {
					System.err.println("Error getting url for sound '" + this.name + "'");
					//noinspection CallToPrintStackTrace
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}
	}
}
