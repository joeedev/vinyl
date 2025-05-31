package dev.joee.vinyl.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenMainMenu;
import net.minecraft.client.gui.ScreenSelectServer;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.packet.PacketKeepAlive;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ScreenDownloadMusic extends Screen {
	private final PacketHandlerClient packetHandler;
	private int updateCounter = 0;

	private int filesDownloaded = 0;
	private final int totalFilesNeeded;

	public ScreenDownloadMusic(PacketHandlerClient packetHandler, int totalFilesNeeded) {
		this.packetHandler = packetHandler;
		this.totalFilesNeeded = totalFilesNeeded;
	}

	public synchronized void incrementLoadingBar() {
		this.filesDownloaded++;
	}

	public void keyPressed(char c, int i, int mouseX, int mouseY) {

	}

	public void init() {
		I18n i18n = I18n.getInstance();
		this.buttons.clear();
		this.buttons.add(new ButtonElement(
			0,
			this.width / 2 - 100, this.height / 4 + 120 + 12,
			i18n.translateKey("gui.connecting.button.cancel"))
		);
	}

	public void tick() {
		++this.updateCounter;
		if (this.updateCounter % 20 == 0) {
			this.packetHandler.addToSendQueue(new PacketKeepAlive());
		}

		if (this.packetHandler != null) {
			this.packetHandler.tick();
		}

	}

	protected void buttonClicked(ButtonElement button) {
		if (button.id == 0) {
			if (this.packetHandler != null) {
				this.packetHandler.disconnect();
			}

			this.mc.displayScreen(new ScreenSelectServer(new ScreenMainMenu()));
		}
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.renderTexturedBackground();
		I18n i18n = I18n.getInstance();
		this.drawStringCentered(
			this.font,
			i18n.translateKey("gui.downloading_music.label.title"),
			this.width / 2, this.height / 2 - 50,
			0xFFFFFF
		);
		this.drawStringCentered(
			this.font,
			"[" + this.filesDownloaded + "/" + this.totalFilesNeeded + "]",
			this.width / 2, this.height / 2 - 16,
			0xFFFFFF
		);

		super.render(mouseX, mouseY, partialTick);

		Tessellator t = Tessellator.instance;

		int progress = 100 * this.filesDownloaded / this.totalFilesNeeded;

		byte barWidth = 100;
		byte barHeight = 2;
		int x = width / 2 - barWidth / 2;
		int y = height / 2 + 16;
		GL11.glDisable(3553);
		t.startDrawingQuads();
		t.setColorOpaque_I(0x808080);
		t.addVertex(x, y, 0);
		t.addVertex(x, y + barHeight, 0);
		t.addVertex(x + barWidth, y + barHeight, 0);
		t.addVertex(x + barWidth, y, 0);
		t.setColorOpaque_I(0x80FF80);
		t.addVertex(x, y, 0);
		t.addVertex(x, y + barHeight, 0);
		t.addVertex(x + progress, y + barHeight, 0);
		t.addVertex(x + progress, y, 0);
		t.draw();
	}
}
