package dev.joee.vinyl.item;

import com.mojang.nbt.tags.CompoundTag;
import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.network.NetworkMessagePlayMusic;
import net.minecraft.core.block.BlockLogicJukebox;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.Random;

public class ItemCustomRecord extends Item {
	public ItemCustomRecord() {
		super(
			"record.custom",
			String.format("%s:item/%s", Vinyl.MOD_ID, "record.custom"),
			Vinyl.CONFIG.getItemId("customRecordId")
		);
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
		if (world.getBlockId(x, y, z) == Blocks.JUKEBOX.id() && world.getBlockMetadata(x, y, z) == 0) {
			if (!world.isClientSide) {
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
			stack.consumeItem(null);
		}

	}

	@Override
	public CompoundTag getDefaultTag() {
		CompoundTag tag = super.getDefaultTag();
		tag.putString("RecordName", "");
		tag.putString("RecordArtist", "");
		tag.putString("RecordFilePath", "");
		return tag;
	}

	@Override
	public String getTranslatedDescription(ItemStack stack) {
		CompoundTag tag = stack.getData();
		return String.format("%s - %s", tag.getString("RecordArtist"), tag.getString("RecordName"));
	}
}
