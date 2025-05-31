package dev.joee.vinyl.model;

import dev.joee.vinyl.Vinyl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModelBlankRecord extends ItemModelRecord {
	public ItemModelBlankRecord() {
		super(Vinyl.blankRecord, "item/record_blank");
	}
}
