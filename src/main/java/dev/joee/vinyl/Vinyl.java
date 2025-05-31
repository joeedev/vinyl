package dev.joee.vinyl;

import dev.joee.vinyl.network.*;
import dev.joee.vinyl.sound.VinylSoundRepository;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.io.IOException;

public class Vinyl implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    public static final String MOD_ID = "vinyl";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config CONFIG = new Config();

	public static VinylSoundRepository SOUNDS;

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
		new BlockBuilder(MOD_ID)
			.build(
				"test", CONFIG.getBlockId("testId"),
				b -> new BlockLogic(b, Material.stone) {
					@Override
					public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
						super.onBlockRightClicked(world, x, y, z, player, side, xHit, yHit);

						if (!world.isClientSide) {
							try {
								ServerFileManager.instance.sendAudioFile(
									player,
									"music/example.ogg"
								);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}

						return true;
					}
				}
			);
	}

	@Override
	public void afterGameStart() {
		if (!EnvironmentHelper.isServerEnvironment()) {
			SOUNDS = new VinylSoundRepository();
		}
	}
}
