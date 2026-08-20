package dev.kemmlow.inputoptimizer.network;

import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OptOutPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OptOutPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Main.MOD_ID, "optout"));

    public static final StreamCodec<FriendlyByteBuf, OptOutPayload> CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeByte(1),
        buf -> {
            // Make sure a malformed packet doesnt kick the player
            buf.skipBytes(buf.readableBytes());
            return new OptOutPayload();
        }
    );

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(TYPE, CODEC);

        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            if (Main.getConfig().isServerOptOut()) return;
            Main.getConfig().setServerOptOut(true);
            RawInputManager.clearPendingInput();
            if (context.player() != null) {
                context.player().sendSystemMessage(
                    Component.literal("Input Optimizer has been disabled for this server")
                        .withStyle(ChatFormatting.RED)
                );
            }
        });

        // Reset optout on world join, includes singleplayer
        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
            Main.getConfig().setServerOptOut(false);
            RawInputManager.clearPendingInput();
        });
    }

    @Override
    public Type<OptOutPayload> type() {
        return TYPE;
    }
}
