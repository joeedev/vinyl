package dev.joee.vinyl.model;

import dev.joee.vinyl.Vinyl;
import dev.joee.vinyl.block.BlockLogicVinylPress;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;

public class BlockModelVinylPress extends BlockModelStandard<BlockLogicVinylPress> {
	private final IconCoordinate topTexture;
	private final IconCoordinate sideTexture;

	public BlockModelVinylPress() {
		super(Vinyl.vinylPress);

		this.topTexture = TextureRegistry.getTexture(
			NamespaceID.getTemp(Vinyl.MOD_ID, "block/press_top")
		);
		this.sideTexture = TextureRegistry.getTexture(
			NamespaceID.getTemp(Vinyl.MOD_ID, "block/press_side")
		);
	}

	@Override
	public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int data) {
		if (side.isVertical()) {
			return this.topTexture;
		} else {
			return this.sideTexture;
		}
	}
}
