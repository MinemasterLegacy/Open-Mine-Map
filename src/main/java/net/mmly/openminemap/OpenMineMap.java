package net.mmly.openminemap;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OpenMineMap implements ModInitializer {
	public static final String MOD_ID = "openminemap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // YOU UNDERESTIMATE MY POWER!
    //   .. / ... .... .- .-.. .-.. / -... . / --. --- -.. !!!

	@Override
	public void onInitialize() {

	}
}