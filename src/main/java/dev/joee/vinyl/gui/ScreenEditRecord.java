package dev.joee.vinyl.gui;

import com.mojang.nbt.tags.CompoundTag;
import dev.joee.vinyl.network.NetworkMessageModifyRecord;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.ItemElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.TextFieldElement;
import net.minecraft.core.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Environment(EnvType.CLIENT)
public class ScreenEditRecord extends Screen {
	private final ItemElement itemElement;
	private final ItemStack record;
	private final int slot;
	private TextFieldElement recordNameField;
	private TextFieldElement recordArtistField;
	private TextFieldElement recordUrlField;
	private ButtonElement buttonSave;

	public ScreenEditRecord(ItemStack disc, int slot) {
		super();
		this.record = disc;
		this.slot = slot;

		Minecraft mc = Minecraft.getMinecraft();
		this.itemElement = new ItemElement(mc);
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);

		// TODO: i18n
		this.buttons.clear();
		this.buttons.add(
			this.buttonSave = new ButtonElement(
				0,
				this.width / 2 - 100, this.height / 2 + 85 - 20,
				"Save"
			)
		);
		this.buttons.add(
			new ButtonElement(
				1,
				this.width / 2 - 100,
				this.height / 2 + 85 - 20 + 25,
				"Cancel"
			)
		);

		String recordName = this.record.getData().getString("RecordName");
		String recordArtist = this.record.getData().getString("RecordArtist");
		String recordUrl = this.record.getData().getString("RecordUrl");

		int textFieldsWidth = 160;
		this.recordNameField = new TextFieldElement(
			this, this.font,
			this.width / 2 - textFieldsWidth / 2 + 70, this.height / 2 - 10 - 30 - 20,
			textFieldsWidth, 20,
			recordName, "Name"
		);
		this.recordArtistField = new TextFieldElement(
			this, this.font,
			this.width / 2 - textFieldsWidth / 2 + 70, this.height / 2 - 10 - 20,
			textFieldsWidth, 20,
			recordArtist, "Artist"
		);
		this.recordUrlField = new TextFieldElement(
			this, this.font,
			this.width / 2 - textFieldsWidth / 2 + 70, this.height / 2 - 10 + 30 - 20,
			textFieldsWidth, 20,
			recordUrl, "Music URL"
		);

		this.buttonSave.enabled =
			!this.recordNameField.getText().isEmpty() &&
			!this.recordArtistField.getText().isEmpty() &&
			!this.recordUrlField.getText().isEmpty();
	}

	@Override
	protected void buttonClicked(ButtonElement button) {
		super.buttonClicked(button);

		if (button.id == 0) {
			CompoundTag tag = this.record.getData();
			tag.putString("RecordName", this.recordNameField.getText());
			tag.putString("RecordArtist", this.recordArtistField.getText());
			tag.putString("RecordUrl", this.recordUrlField.getText());

			if (!EnvironmentHelper.isSinglePlayer()) {
				NetworkHandler.sendToServer(
					new NetworkMessageModifyRecord(
						this.slot,
						this.recordNameField.getText(),
						this.recordArtistField.getText(),
						this.recordUrlField.getText()
					)
				);
			}

			this.mc.displayScreen(null);
		} else if (button.id == 1) {
			this.mc.displayScreen(null);
		}
	}

	@Override
	public void keyPressed(char c, int key, int mouseX, int mouseY) {
		if (key != 14) {
			super.keyPressed(c, key, mouseX, mouseY);
		}

		if (key == 15) {
			if (recordNameField.isFocused) {
				recordNameField.isFocused = false;
				recordArtistField.isFocused = true;
			} else if (recordArtistField.isFocused) {
				recordArtistField.isFocused = false;
				recordUrlField.isFocused = true;
			} else if (recordUrlField.isFocused) {
				recordUrlField.isFocused = false;
				recordNameField.isFocused = true;
			}
		}

		this.recordNameField.textboxKeyTyped(c, key);
		this.recordArtistField.textboxKeyTyped(c, key);
		this.recordUrlField.textboxKeyTyped(c, key);

		this.buttonSave.enabled =
			!this.recordNameField.getText().isEmpty() &&
			!this.recordArtistField.getText().isEmpty() &&
			!this.recordUrlField.getText().isEmpty();

		if (this.buttonSave.enabled && c == '\r') {
			this.buttonClicked(this.buttons.get(0));
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.recordNameField.mouseClicked(mouseX, mouseY, mouseButton);
		this.recordArtistField.mouseClicked(mouseX, mouseY, mouseButton);
		this.recordUrlField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.renderBackground();

		GL11.glPushMatrix();
		GL11.glTranslatef(this.width * 0.5F - 100, this.height * 0.5F - 20, 0);
		GL11.glScalef(10, 10, 10);
		GL11.glTranslatef(-8 + 0.5F, -8, 0);
		this.itemElement.render(record, 0, 0);
		GL11.glPopMatrix();

		this.drawStringCentered(
			this.font, "Customize Disc",
			this.width / 2, this.height / 4 - 60 + 20,
			0xFFFFFF
		);

		this.recordNameField.drawTextBox();
		this.recordArtistField.drawTextBox();
		this.recordUrlField.drawTextBox();

		super.render(mouseX, mouseY, partialTick);
	}
}
