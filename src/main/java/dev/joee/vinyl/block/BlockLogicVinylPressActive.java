package dev.joee.vinyl.block;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.tileentity.TileEntityVinylPress;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
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
