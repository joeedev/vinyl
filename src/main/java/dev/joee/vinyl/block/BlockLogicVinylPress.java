package dev.joee.vinyl.block;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.tileentity.TileEntityVinylPress;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicVinylPress extends BlockLogic {
	public BlockLogicVinylPress(Block<?> block) {
		super(block, Material.steel);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		TileEntityVinylPress te = (TileEntityVinylPress) world.getTileEntity(x, y, z);
		if (te.stack != null && te.stack.getItem().id == Vinyl.customRecord.id) {
			te.dropContents(world, x, y, z);
			return true;
		} else {
			return false;
		}
	}
}
