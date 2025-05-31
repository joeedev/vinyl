package dev.joee.vinyl.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class TileEntityVinylPress extends TileEntity {
	public @Nullable ItemStack stack;

	public TileEntityVinylPress() {

	}

	public TileEntityVinylPress(@Nullable ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		CompoundTag tag = nbt.getCompound("Item");
		this.stack = ItemStack.readItemStackFromNbt(tag);
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		if (this.stack != null) {
			CompoundTag tag = new CompoundTag();
			this.stack.writeToNBT(tag);
			nbt.putCompound("Item", tag);
		}
	}

	@Override
	public void dropContents(World world, int x, int y, int z) {
		super.dropContents(world, x, y, z);
		if (this.stack != null) {
			world.dropItem(x, y, z, this.stack);
			this.stack = null;
		}
	}
}
