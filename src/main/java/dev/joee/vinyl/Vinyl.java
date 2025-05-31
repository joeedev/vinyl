package dev.joee.vinyl;

import dev.joee.vinyl.block.BlockLogicVinylPress;
import dev.joee.vinyl.block.BlockLogicVinylPressActive;
import dev.joee.vinyl.item.ItemBlankRecord;
import dev.joee.vinyl.item.ItemCustomRecord;
import dev.joee.vinyl.network.*;
import dev.joee.vinyl.recipe.RecipeEntryBlankDiscDye;
import dev.joee.vinyl.sound.VinylSoundRepository;
import dev.joee.vinyl.tileentity.TileEntityVinylJukebox;
import dev.joee.vinyl.tileentity.TileEntityVinylPress;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class Vinyl implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    public static final String MOD_ID = "vinyl";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config CONFIG = new Config();

	public static VinylSoundRepository SOUNDS;

	public static Block<BlockLogicVinylPress> vinylPress;
	public static Block<BlockLogicVinylPressActive> vinylPressActive;

	public static Item blankRecord;
	public static Item customRecord;

    @Override
    public void onInitialize() {
        LOGGER.info("Vinyl initialized.");

		Packet.addMapping(
			CONFIG.getPacketId("audioChunkId"),
			true, false, PacketAudioChunk.class
		);
		Packet.addMapping(
			CONFIG.getPacketId("fileListId"),
			true, true, PacketFileList.class
		);
		Packet.addMapping(
			CONFIG.getPacketId("audioReceivedId"),
			false, true, PacketAudioReceived.class
		);

		NetworkHandler.registerNetworkMessage(NetworkMessageModifyRecord::new);
		NetworkHandler.registerNetworkMessage(NetworkMessagePlayMusic::new);
    }

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {
		Registries.ITEM_GROUPS.register("vinyl:disc", Registries.stackListOf(
			Items.RECORD_13,
			Items.RECORD_BLOCKS,
			Items.RECORD_CAT,
			Items.RECORD_CHIRP,
			Items.RECORD_DOG,
			Items.RECORD_FAR,
			Items.RECORD_MALL,
			Items.RECORD_MELLOHI,
			Items.RECORD_STAL,
			Items.RECORD_STRAD,
			Items.RECORD_WAIT,
			Items.RECORD_WARD,
			customRecord
		));

		RecipeBuilder.Shaped(MOD_ID)
			.setShape("SSS", "PDP", "SSS")
			.addInput('S', Blocks.BLOCK_STEEL)
			.addInput('P', Blocks.PISTON_BASE_STICKY)
			.addInput('D', "vinyl:disc")
			.create("recordPress", vinylPress.getDefaultStack());

		RecipeBuilder.BlastFurnace(MOD_ID)
			.setInput("vinyl:disc")
			.create("blankDisc", blankRecord.getDefaultStack());

		//noinspection unchecked
		((RecipeGroup<RecipeEntryCrafting<?, ?>>) RecipeBuilder.getRecipeGroup(
			MOD_ID, "workbench", new RecipeSymbol(Blocks.WORKBENCH.getDefaultStack())
		))
			.register("blankDiscDye", new RecipeEntryBlankDiscDye());
	}

	@Override
	public void beforeGameStart() {
		vinylPress = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityVinylPress::new)
			.build(
				"vinylPress", CONFIG.getBlockId("vinylPressId"),
				BlockLogicVinylPress::new
			);

		vinylPressActive = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityVinylPress::new)
			.setLuminance(15)
			.setTags(BlockTags.NOT_IN_CREATIVE_MENU)
			.build(
				"vinylPressActive", CONFIG.getBlockId("vinylPressActiveId"),
				BlockLogicVinylPressActive::new
			);

		blankRecord = new ItemBuilder(MOD_ID)
			.setStackSize(1)
			.build(new ItemBlankRecord());

		customRecord = new ItemBuilder(MOD_ID)
			.setStackSize(1)
			.setTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.build(new ItemCustomRecord());

		EntityHelper.createTileEntity(
			TileEntityVinylPress.class,
			NamespaceID.getPermanent(MOD_ID, "vinylPress")
		);

		EntityHelper.createTileEntity(
			TileEntityVinylJukebox.class,
			NamespaceID.getPermanent(MOD_ID, "vinylJukebox")
		);
	}

	@Override
	public void afterGameStart() {
		if (!EnvironmentHelper.isServerEnvironment()) {
			SOUNDS = new VinylSoundRepository();
		}
	}
}
