package dev.joee.vinyl;

import dev.joee.vinyl.block.BlockLogicVinylPress;
import dev.joee.vinyl.item.ItemBlankRecord;
import dev.joee.vinyl.item.ItemCustomRecord;
import dev.joee.vinyl.network.*;
import dev.joee.vinyl.sound.VinylSoundRepository;
import dev.joee.vinyl.tileentity.TileEntityVinylPress;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class Vinyl implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    public static final String MOD_ID = "vinyl";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config CONFIG = new Config();

	public static VinylSoundRepository SOUNDS;

	public static Block<BlockLogicVinylPress> vinylPress;
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

	}

	@Override
	public void beforeGameStart() {
		vinylPress = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityVinylPress::new)
			.build(
				"vinylPress", CONFIG.getBlockId("vinylPressId"),
				BlockLogicVinylPress::new
			);

		blankRecord = new ItemBuilder(MOD_ID)
			.setStackSize(1)
			.build(new ItemBlankRecord());

		customRecord = new ItemBuilder(MOD_ID)
			.setStackSize(1)
			.setTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.build(new ItemCustomRecord());
	}

	@Override
	public void afterGameStart() {
		if (!EnvironmentHelper.isServerEnvironment()) {
			SOUNDS = new VinylSoundRepository();
		}

		EntityHelper.createTileEntity(
			TileEntityVinylPress.class,
			NamespaceID.getPermanent(MOD_ID, "vinylPress")
		);
	}
}
