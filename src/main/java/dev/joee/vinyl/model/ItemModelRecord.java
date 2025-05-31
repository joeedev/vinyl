package dev.joee.vinyl.model;

import dev.joee.vinyl.Vinyl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemModelRecord extends ItemModelLayered {
	private static final int[] allColors = new int[] {
		1973019, 11743532, 3887386, 5320730, 2437522, 8073150,
		2651799, 7566195, 4408131, 14188952, 4312372, 14602026,
		6719955, 12801229, 15435844, 15790320
	};

	public ItemModelRecord(Item item, String baseTexture) {
		super(item, new ItemModel[] {
			new ItemModelFromTexture(baseTexture),
			new ItemModelFromTexture("item/record_split_top") {
				@Override
				public int getColor(ItemStack stack) {
					return allColors[stack.getData().getInteger("PrimaryColor")];
				}
			},
			new ItemModelFromTexture("item/record_split_bottom") {
				@Override
				public int getColor(ItemStack stack) {
					return allColors[stack.getData().getInteger("SecondaryColor")];
				}
			},
		});
	}
}
