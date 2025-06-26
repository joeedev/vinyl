package dev.joee.vinyl.item;

import com.mojang.nbt.tags.CompoundTag;
import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.network.NetworkMessagePlayMusic;
import dev.joee.vinyl.tileentity.TileEntityVinylJukebox;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityJukebox;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemCustomRecord extends Item {
	private static final Pattern FORMAT_PATTERN = Pattern.compile("§[0-9a-fk-or]|§<.*?>");

	public ItemCustomRecord() {
		super(
			"record.custom",
			String.format("%s:item/%s", Vinyl.MOD_ID, "record.custom"),
			Vinyl.CONFIG.getItemId("customRecordId")
		);
	}

	@Override
	public CompoundTag getDefaultTag() {
		CompoundTag tag = super.getDefaultTag();
		tag.putString("RecordName", "");
		tag.putString("RecordArtist", "");
		tag.putString("RecordFilePath", "");
		tag.putInt("PrimaryColor", 0);
		tag.putInt("SecondaryColor", 0);
		return tag;
	}

	@Override
	public String getTranslatedDescription(ItemStack stack) {
		CompoundTag tag = stack.getData();

		TextFormatting primary = TextFormatting.get(15 - tag.getInteger("PrimaryColor"));
		TextFormatting secondary = TextFormatting.get(15 - tag.getInteger("SecondaryColor"));

		String text = primary + stack.getData().getString("RecordArtist") +
			TextFormatting.LIGHT_GRAY + " - " +
			secondary + stack.getData().getString("RecordName");

		return propagateFormattingToWords(text);
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
		if (world.getBlockId(x, y, z) == Blocks.JUKEBOX.id() && world.getBlockMetadata(x, y, z) == 0) {
			if (!world.isClientSide) {
				this.playMusic(stack, world, x, y, z);
				this.storeRecordInJukebox(stack, world, x, y, z);
				stack.consumeItem(player);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onUseByActivator(ItemStack stack, TileEntityActivator activatorBlock, World world, Random random, int blockX, int blockY, int blockZ, double offX, double offY, double offZ, Direction direction) {
		int x = blockX + direction.getOffsetX();
		int y = blockY + direction.getOffsetY();
		int z = blockZ + direction.getOffsetZ();
		int b = world.getBlockId(x, y, z);
		if (b == Blocks.JUKEBOX.id() && world.getBlockMetadata(x, y, z) == 0) {
			this.playMusic(stack, world, x, y, z);
			this.storeRecordInJukebox(stack, world, x, y, z);
			stack.consumeItem(null);
		}

	}

	private void playMusic(ItemStack stack, World world, int x, int y, int z) {
		CompoundTag tag = stack.getData();
		NetworkHandler.sendToAllAround(
			x, y, z, 64, world.dimension.id,
			new NetworkMessagePlayMusic(
				tag.getString("RecordName"),
				tag.getString("RecordArtist"),
				tag.getString("RecordFilePath"),
				x, y, z
			)
		);
	}

	private void storeRecordInJukebox(ItemStack stack, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityJukebox) {
			tileEntity = new TileEntityVinylJukebox((TileEntityJukebox) tileEntity);
			world.setTileEntity(x, y, z, tileEntity);
		}

		if (tileEntity instanceof TileEntityVinylJukebox) {
			TileEntityVinylJukebox te = (TileEntityVinylJukebox) tileEntity;
			te.record = stack.getItem().id;
			te.recordTag = stack.getData();
			te.setChanged();
			world.setBlockMetadataWithNotify(x, y, z, 1);
		}
	}

	private static String propagateFormattingToWords(String input) {
		String[] words = input.split(" ");
		StringBuilder currentFormat = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			Matcher matcher = FORMAT_PATTERN.matcher(words[i]);
			if (!matcher.find()) {
				words[i] = currentFormat + words[i];
				continue;
			}

			String formatting = matcher.group();
			words[i] = currentFormat + words[i];
			currentFormat.append(formatting);
		}

		return String.join(" ", words);
	}
}
