package dev.joee.vinyl.model;

import dev.joee.vinyl.Vinyl;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;

public class ItemModelFromTexture extends ItemModelStandard {
	public ItemModelFromTexture(String texturePath) {
		super(null, null);
		this.icon = TextureRegistry.getTexture(
			NamespaceID.getTemp(Vinyl.MOD_ID, texturePath)
		);
	}
}
