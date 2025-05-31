package dev.joee.vinyl.mixin;

import dev.joee.vinyl.item.ItemCustomRecord;
import dev.joee.vinyl.network.NetworkMessagePlayNote;
import net.minecraft.core.block.BlockLogicNote;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.core.block.BlockLogicNote.getNote;

@Mixin(value = BlockLogicNote.class, remap = false)
public class BlockLogicNoteMixin {
	@Unique
	private static final Random rand = new Random();

	@Inject(method = "triggerNote", at = @At("HEAD"), cancellable = true)
	public void triggerNote(World world, int x, int y, int z, CallbackInfo ci) {
		if (!world.isBlockOpaqueCube(x, y + 1, z)) {
			TileEntity tileEntity = world.getTileEntity(x, y - 1, z);
			if (!(tileEntity instanceof TileEntityChest)) {
				return;
			}

			TileEntityChest te = (TileEntityChest) tileEntity;
			List<ItemStack> records = new ArrayList<>();

			for (int i = 0; i < te.getContainerSize(); i++) {
				ItemStack stack = te.getItem(i);
				if (stack == null || !(stack.getItem() instanceof ItemCustomRecord)) {
					continue;
				}
				records.add(stack);
			}

			if (records.isEmpty()) {
				return;
			}

			String filePath = records
				.get(rand.nextInt(records.size()))
				.getData()
				.getString("RecordFilePath");

			int data = world.getBlockMetadata(x, y, z);
			float pitch = (float) Math.pow(2.0, (double) (getNote(data) - 12) / 12.0);

			NetworkHandler.sendToAllAround(
				x, y, z, 48, world.dimension.id,
				new NetworkMessagePlayNote(filePath, pitch, x, y, z)
			);

			world.spawnParticle(
				"note",
				(double) x + 0.5, (double) y + 1.2, (double) z + 0.5,
				0.0, 0.0, 0.0,
				data
			);

			ci.cancel();
		}
	}
}
