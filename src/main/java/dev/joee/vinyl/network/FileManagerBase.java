package dev.joee.vinyl.network;

import dev.joee.vinyl.Vinyl;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public abstract class FileManagerBase {
	public abstract File getAudioDir();

	public String[] getAudioFilePaths() {
		File musicDir = new File(
			this.getAudioDir(),
			"music"
		);

		return Arrays.stream(Objects.requireNonNull(musicDir.listFiles()))
			.map(f -> String.format("music/%s", f.getName()))
			.toArray(String[]::new);
	}
}
