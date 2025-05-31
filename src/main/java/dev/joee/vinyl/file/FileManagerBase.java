package dev.joee.vinyl.file;

import dev.joee.vinyl.Vinyl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class FileManagerBase {
	public abstract File getAudioDir();
	public abstract File getWorldAudioDir();

	public String[] getAudioFilePaths() {
		File musicDir = new File(
			this.getAudioDir(),
			"music"
		);

		return Arrays.stream(Objects.requireNonNull(musicDir.listFiles()))
			.map(f -> String.format("music/%s", f.getName()))
			.toArray(String[]::new);
	}

	public CompletableFuture<String> downloadAudioFromYouTube(String url) {
		Vinyl.LOGGER.info("Downloading from {}", url);

		UUID uuid = UUID.randomUUID();
		String filePath = String.format("music/%s.ogg", uuid);
		File audioFile = new File(
			this.getWorldAudioDir(),
			filePath
		);

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[] {
				"yt-dlp", "-xf", "worst", "--audio-format", "vorbis", "-o",
				audioFile.getAbsolutePath().replace(".ogg", ""),
				url
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Process finalProcess = process;
		return CompletableFuture.supplyAsync(() -> {
			try {
				finalProcess.waitFor();
				return filePath;
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
