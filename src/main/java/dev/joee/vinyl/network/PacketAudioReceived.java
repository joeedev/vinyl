package dev.joee.vinyl.network;

import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacketAudioReceived extends Packet {
	private static final List<Integer> allReceivedFileIds = new ArrayList<>();

	private int fileId;

	public PacketAudioReceived() {

	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		this.fileId = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.fileId);
	}

	@Override
	public void handlePacket(PacketHandler packetHandler) {
		if (!EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		synchronized (allReceivedFileIds) {
			allReceivedFileIds.add(this.fileId);
			allReceivedFileIds.notifyAll();
		}
	}

	@Override
	public int getEstimatedSize() {
		return 4;
	}

	public PacketAudioReceived(int fileId) {
		this.fileId = fileId;
	}

	public static void waitForReceivedFile(int fileId) throws InterruptedException {
		while (true) {
			synchronized (allReceivedFileIds) {
				allReceivedFileIds.wait(200);
				if (allReceivedFileIds.contains(fileId)) {
					allReceivedFileIds.remove(Integer.valueOf(fileId));
					return;
				}
			}
		}
	}
}
