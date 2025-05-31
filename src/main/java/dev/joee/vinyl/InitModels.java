package dev.joee.vinyl;

import dev.joee.vinyl.model.BlockModelVinylPress;
import dev.joee.vinyl.model.BlockModelVinylPressActive;
import dev.joee.vinyl.model.ItemModelBlankRecord;
import dev.joee.vinyl.model.ItemModelCustomRecord;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

public class InitModels implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		ModelHelper.setBlockModel(Vinyl.vinylPress, BlockModelVinylPress::new);
		ModelHelper.setBlockModel(Vinyl.vinylPressActive, BlockModelVinylPressActive::new);
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		ModelHelper.setItemModel(Vinyl.blankRecord, ItemModelBlankRecord::new);
		ModelHelper.setItemModel(Vinyl.customRecord, ItemModelCustomRecord::new);

		Vinyl.LOGGER.info("Item models initialized.");
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {

	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {

	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {

	}
}
