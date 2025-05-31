package dev.joee.vinyl.mixin;

import net.minecraft.core.world.save.SaveHandlerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;

@Mixin(value = SaveHandlerBase.class, remap = false)
public interface SaveHandlerBaseMixin {
	@Invoker
	File invokeGetSaveDirectory();
}
