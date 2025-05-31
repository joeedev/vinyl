package dev.joee.vinyl.block;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.tileentity.TileEntityVinylPress;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicVinylPressActive extends BlockLogicVinylPress {
	public BlockLogicVinylPressActive(Block<?> block) {
		super(block);
	}
}
