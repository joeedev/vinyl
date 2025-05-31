package dev.joee.vinyl.recipe;

import com.mojang.nbt.tags.CompoundTag;
import dev.joee.vinyl.item.ItemBlankRecord;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingDynamic;
import net.minecraft.core.item.ItemDye;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeEntryBlankDiscDye extends RecipeEntryCraftingDynamic {
	@Override
	public boolean matches(ContainerCrafting crafting) {
		ItemStack disc = crafting.getItemStackAt(1, 1);
		if (disc == null || !(disc.getItem() instanceof ItemBlankRecord)) {
			return false;
		}

		ItemStack primaryDye = crafting.getItemStackAt(0, 0);
		if (primaryDye == null || !(primaryDye.getItem() instanceof ItemDye)) {
			return false;
		}

		ItemStack secondaryDye = crafting.getItemStackAt(2, 2);
		if (secondaryDye == null || !(secondaryDye.getItem() instanceof ItemDye)) {
			return false;
		}

		if (
			crafting.getItemStackAt(1, 0) == null || !crafting.getItemStackAt(1, 0).isItemEqual(primaryDye) ||
				crafting.getItemStackAt(2, 0) == null || !crafting.getItemStackAt(2, 0).isItemEqual(primaryDye) ||
				crafting.getItemStackAt(0, 1) == null || !crafting.getItemStackAt(0, 1).isItemEqual(primaryDye)
		) {
			return false;
		}

		return
			crafting.getItemStackAt(0, 2) != null && crafting.getItemStackAt(0, 2).isItemEqual(secondaryDye) &&
				crafting.getItemStackAt(1, 2) != null && crafting.getItemStackAt(1, 2).isItemEqual(secondaryDye) &&
				crafting.getItemStackAt(2, 1) != null && crafting.getItemStackAt(2, 1).isItemEqual(secondaryDye);
	}

	@Override
	public boolean matchesQuery(SearchQuery searchQuery) {
		return false;
	}

	@Override
	public ItemStack getCraftingResult(ContainerCrafting crafting) {
		ItemStack disc = crafting.getItemStackAt(1, 1);
		ItemStack primaryDye = crafting.getItemStackAt(0, 0);
		ItemStack secondaryDye = crafting.getItemStackAt(2, 2);

		ItemStack result = disc.copy();
		CompoundTag tag = result.getData();
		tag.putInt("PrimaryColor", primaryDye.getMetadata());
		tag.putInt("SecondaryColor", secondaryDye.getMetadata());

		return result;
	}

	@Override
	public int getRecipeSize() {
		return 3;
	}

	@Override
	public ItemStack[] onCraftResult(ContainerCrafting crafting) {
		ItemStack[] returnStack = new ItemStack[9];

		for (int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				ItemStack stack = crafting.getItemStackAt(x, y);
				if (stack != null) {
					stack.stackSize--;
					if (stack.stackSize <= 0) {
						crafting.setSlotContentsAt(x, y, null);
					}
				}
			}
		}

		return returnStack;
	}
}
