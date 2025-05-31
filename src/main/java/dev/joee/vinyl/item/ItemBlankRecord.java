package dev.joee.vinyl.item;

import com.mojang.nbt.tags.CompoundTag;
import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.gui.ScreenEditRecord;
import dev.joee.vinyl.file.FileManagerClient;
import dev.joee.vinyl.file.FileManagerServer;
import dev.joee.vinyl.tileentity.TileEntityVinylPress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.concurrent.CompletableFuture;

public class ItemBlankRecord extends Item {
	public ItemBlankRecord() {
		super(
			"record.blank",
			String.format("%s:item/%s", Vinyl.MOD_ID, "record.blank"),
			Vinyl.CONFIG.getItemId("blankRecordId")
		);
	}

	@Override
	public CompoundTag getDefaultTag() {
		CompoundTag tag = super.getDefaultTag();
		tag.putString("RecordName", "");
		tag.putString("RecordArtist", "");
		tag.putString("RecordUrl", "");
		tag.putInt("PrimaryColor", 0);
		tag.putInt("SecondaryColor", 0);
		return tag;
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
		if (world.getBlockId(x, y, z) != Vinyl.vinylPress.id()) {
			return false;
		}

		if (world.isClientSide) {
			return true;
		}

		CompoundTag tag = stack.getData();
		String url = tag.getString("RecordUrl");

		if (
			tag.getString("RecordName").isEmpty() ||
				tag.getString("RecordArtist").isEmpty() ||
				url.isEmpty()
		) {
			return false;
		}

		world.setBlockWithNotify(x, y, z, Vinyl.vinylPressActive.id());

		TileEntityVinylPress te = (TileEntityVinylPress) world.getTileEntity(x, y, z);
		te.stack = stack.copy();
		te.setChanged();

		stack.consumeItem(player);

		CompletableFuture<String> filePathFuture = EnvironmentHelper.isSinglePlayer()
			? this.downloadAudioToClient(url)
			: this.downloadAudioToServerAndSend(url);

		filePathFuture.thenAccept(filePath -> {
			Vinyl.LOGGER.info("Finished downloading {}", filePath);

			if (world.getBlockId(x, y, z) != Vinyl.vinylPressActive.id()) {
				return;
			}

			ItemStack newStack = Vinyl.customRecord.getDefaultStack();

			CompoundTag oldData = te.stack.getData();
			CompoundTag newData = newStack.getData();
			newData.putString("RecordName", oldData.getString("RecordName"));
			newData.putString("RecordArtist", oldData.getString("RecordArtist"));
			newData.putString("RecordFilePath", filePath);
			newData.putInt("PrimaryColor", oldData.getInteger("PrimaryColor"));
			newData.putInt("SecondaryColor", oldData.getInteger("SecondaryColor"));

			TileEntityVinylPress.shouldDropContents = false;

			world.setBlockWithNotify(x, y, z, Vinyl.vinylPress.id());

			TileEntityVinylPress newTileEntity = new TileEntityVinylPress(newStack);
			world.setTileEntity(x, y, z, newTileEntity);

			TileEntityVinylPress.shouldDropContents = true;

			world.playSoundEffect(
				null, SoundCategory.WORLD_SOUNDS,
				((float) x + 0.5F), ((float) y + 0.5F), ((float) z + 0.5F),
				"random.fizz", 0.5F,
				2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
			);

			for (int i = 0; i < 5; i++) {
				world.spawnParticle(
					"smoke", x - 0.1F, y + 0.5F, z + world.rand.nextFloat(),
					0, 0.1, 0, 0
				);
				world.spawnParticle(
					"smoke", x + 1.1F, y + 0.5F, z + world.rand.nextFloat(),
					0, 0.1, 0, 0
				);
				world.spawnParticle(
					"smoke", x + world.rand.nextFloat(), y + 0.5F, z - 0.1F,
					0, 0.1, 0, 0
				);
				world.spawnParticle(
					"smoke", x + world.rand.nextFloat(), y + 0.5F, z + 1.1F,
					0, 0.1, 0, 0
				);
			}
		});

		return true;
	}

	@Override
	public ItemStack onUseItem(ItemStack stack, World world, Player player) {
		if (!EnvironmentHelper.isServerEnvironment()) {
			int slot = -1;

			for (int i = 0; i < player.inventory.mainInventory.length; i++) {
				if (player.inventory.mainInventory[i] == stack) {
					slot = i;
					break;
				}
			}

			if (slot != -1) {
				this.showEditScreen(stack, slot);
			}
		}

		return super.onUseItem(stack, world, player);
	}

	@Environment(EnvType.CLIENT)
	private void showEditScreen(ItemStack stack, int slot) {
		Minecraft.getMinecraft().displayScreen(new ScreenEditRecord(stack, slot));
	}

	@Environment(EnvType.CLIENT)
	private CompletableFuture<String> downloadAudioToClient(String url) {
		return FileManagerClient.instance.downloadAudioFromYouTube(url);
	}

	@Environment(EnvType.SERVER)
	private CompletableFuture<String> downloadAudioToServerAndSend(String url) {
		return FileManagerServer.instance.downloadAudioFromYouTubeAndSend(url);
	}
}
