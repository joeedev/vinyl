package dev.joee.vinyl.network;

import dev.joee.vinyl.file.FileManagerClient;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class PacketAudioChunk extends Packet {
	public static final int CHUNK_SIZE = 8192;

	public int fileId;
	public byte[] data = new byte[CHUNK_SIZE];

	// If these are set, indicates that this is the last chunk
	public String filePath;
	public int size;

	public PacketAudioChunk() {
		((IVinylPacket) this).vinyl$markAsAudioPacket();
	}

	public PacketAudioChunk(int fileId, byte[] data) {
		this();
		assert data.length == CHUNK_SIZE;
		this.fileId = fileId;
		this.data = data;
		this.filePath = "";
		this.size = -1;
	}

	public PacketAudioChunk(
		int fileId,
		byte[] data,
		@Nullable String filePath,
		int size
	) {
		this();
		assert data.length == CHUNK_SIZE;
		this.fileId = fileId;
		this.data = data;
		this.filePath = filePath;
		this.size = size;
	}

	public boolean isFinalChunk() {
		return !Objects.equals(this.filePath, "") && this.size != -1;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		this.fileId = dis.readInt();
		for (int i = 0; i < CHUNK_SIZE; i++) {
			this.data[i] = dis.readByte();
		}
		this.filePath = dis.readUTF();
		this.size = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.fileId);
		for (byte byt : this.data) {
			dos.writeByte(byt);
		}
		dos.writeUTF(this.filePath);
		dos.writeInt(this.size);
	}

	@Override
	public void handlePacket(PacketHandler packetHandler) {
		if (!EnvironmentHelper.isServerEnvironment()) {
			FileManagerClient.instance.handleChunk(
				this,
				(PacketHandlerClient) packetHandler
			);
		}
	}

	@Override
	public int getEstimatedSize() {
		return 0;
	}
}
