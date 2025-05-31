package dev.joee.vinyl.model;

import net.minecraft.client.render.Font;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class ItemModelLayered extends ItemModel {
	private final ItemModel[] layers;

	public ItemModelLayered(Item item, ItemModel[] layers) {
		super(item);
		this.layers = layers;
	}

	@Override
	public void renderItemFirstPerson(Tessellator tessellator, ItemRenderer itemRenderer, Player player, ItemStack itemStack, float f) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderItemFirstPerson(tessellator, itemRenderer, player, itemStack, f);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderItem(Tessellator tessellator, ItemRenderer itemRenderer, ItemStack itemStack, @Nullable Entity entity, float f, boolean bl) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderItem(tessellator, itemRenderer, itemStack, entity, f, bl);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderItemThirdPerson(Tessellator tessellator, ItemRenderer itemRenderer, Entity entity, ItemStack itemStack, boolean bl) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderItemThirdPerson(tessellator, itemRenderer, entity, itemStack, bl);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderItemIntoGui(Tessellator tessellator, Font font, TextureManager textureManager, ItemStack itemStack, int i, int j, float f, float g) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderItemIntoGui(tessellator, font, textureManager, itemStack, i, j, f, g);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderItemOverlayIntoGUI(Tessellator tessellator, Font font, TextureManager textureManager, ItemStack itemStack, int i, int j, String string, float f) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderItemOverlayIntoGUI(tessellator, font, textureManager, itemStack, i, j, string, f);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderAsItemEntity(Tessellator tessellator, @Nullable Entity entity, Random random, ItemStack itemStack, int i, float f, float g, float h) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderAsItemEntity(tessellator, entity, random, itemStack, i, f, g, h);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderItemInWorld(Tessellator tessellator, Entity entity, ItemStack itemStack, float f, float g, boolean bl) {
		for (ItemModel layer : this.layers) {
			GL11.glPushMatrix();
			layer.renderItemInWorld(tessellator, entity, itemStack, f, g, bl);
			GL11.glPopMatrix();
		}
	}

	@Override
	public IconCoordinate getIcon(@Nullable Entity entity, ItemStack itemStack) {
		return null;
	}
}
