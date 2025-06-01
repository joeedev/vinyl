package dev.joee.vinyl.block;

import dev.joee.vinyl.Vinyl;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicVinylPressActive extends BlockLogicVinylPress {
	public BlockLogicVinylPressActive(Block<?> block) {
		super(block);
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case PICK_BLOCK:
			case EXPLOSION:
			case PROPER_TOOL:
			case SILK_TOUCH:
			case PISTON_CRUSH:
				return new ItemStack[]{ new ItemStack(Vinyl.vinylPress) };
			default:
				return null;
		}
	}
}
