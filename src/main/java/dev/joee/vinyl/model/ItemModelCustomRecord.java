package dev.joee.vinyl.model;

import dev.joee.vinyl.Vinyl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModelCustomRecord extends ItemModelRecord {
	public ItemModelCustomRecord() {
		super(Vinyl.customRecord, "item/record_custom");
	}
}
