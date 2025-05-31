package dev.joee.vinyl.model;

import dev.joee.vinyl.Vinyl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.core.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemModelCustomRecord extends ItemModelLayered {
	public ItemModelCustomRecord() {
		super(Vinyl.blankRecord, new ItemModel[] {
			new ItemModelFromTexture("item/record_custom"),
			new ItemModelFromTexture("item/record_split_bottom") {
				@Override
				public int getColor(ItemStack stack) {
					return 0xFB016E;
				}
			},
			new ItemModelFromTexture("item/record_split_top") {
				@Override
				public int getColor(ItemStack stack) {
					return 0x013E79;
				}
			},
		});
	}
}
