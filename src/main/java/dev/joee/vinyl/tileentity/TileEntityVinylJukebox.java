package dev.joee.vinyl.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntityJukebox;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityVinylJukebox extends TileEntityJukebox {
	public @Nullable CompoundTag recordTag;

	public TileEntityVinylJukebox() {

	}

	public TileEntityVinylJukebox(TileEntityJukebox te) {
		super();
		this.record = te.record;
		this.recordTag = null;
		this.x = te.x;
		this.y = te.y;
		this.z = te.z;
		this.carriedBlock = te.carriedBlock;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		super.readFromNBT(tag);
		this.recordTag = tag.getCompound("RecordTag");
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		super.writeToNBT(tag);
		tag.putCompound("RecordTag", recordTag);
	}

	public void dropContents(World world, int x, int y, int z) {
		if (this.record != 0) {
			ItemStack stack = new ItemStack(this.record, 1, 0);
			stack.setData(this.recordTag);

			float f = 0.7F;
			double d = (double) (world.rand.nextFloat() * f) + (double) (1 - f) * 0.5;
			double d1 = (double) (world.rand.nextFloat() * f) + (double) (1 - f) * 0.2 + 0.6;
			double d2 = (double) (world.rand.nextFloat() * f) + (double) (1 - f) * 0.5;
			EntityItem item = new EntityItem(
				world, (double) x + d, (double) y + d1, (double) z + d2, stack
			);
			item.pickupDelay = 10;
			world.entityJoinedWorld(item);
			world.playBlockEvent(1005, x, y, z, 0);
			world.playRecord(null, null, x, y, z);
			this.record = 0;
			this.setChanged();
			world.setBlockMetadataWithNotify(x, y, z, 0);
		}
	}
}
