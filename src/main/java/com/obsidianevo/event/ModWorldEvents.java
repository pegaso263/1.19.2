package com.obsidianevo.event;

import com.obsidianevo.ObsidianEvoMod;
import com.obsidianevo.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ObsidianEvoMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModWorldEvents {
    private static final String STARTER_CHEST_GIVEN = "obsidianevo_starter_chest";

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level;

        if (!level.isClientSide()) {
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag data = persistentData.getCompound(Player.PERSISTED_NBT_TAG);

            if (!data.getBoolean(STARTER_CHEST_GIVEN)) {
                BlockPos chestPos = player.blockPosition().offset(1, 0, 0);

                // Colocar el cofre al lado del jugador
                level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
                BlockEntity blockEntity = level.getBlockEntity(chestPos);

                if (blockEntity instanceof ChestBlockEntity chest) {
                    chest.setItem(0, new ItemStack(ModItems.OBSIDIAN_HELMET.get()));
                    chest.setItem(1, new ItemStack(ModItems.OBSIDIAN_CHESTPLATE.get()));
                    chest.setItem(2, new ItemStack(ModItems.OBSIDIAN_LEGGINGS.get()));
                    chest.setItem(3, new ItemStack(ModItems.OBSIDIAN_BOOTS.get()));
                    chest.setItem(4, new ItemStack(ModItems.OBSIDIAN_SWORD.get()));
                }

                // Enviar el mensaje en el chat con las coordenadas
                player.sendSystemMessage(Component.literal("¡Un cofre con el Equipo de Obsidiana Evolutivo ha aparecido en: ")
                        .withStyle(ChatFormatting.GOLD)
                        .append(Component.literal(chestPos.getX() + ", " + chestPos.getY() + ", " + chestPos.getZ())
                                .withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("!").withStyle(ChatFormatting.GOLD)));

                // Guardar marca para que solo aparezca una vez por mundo/jugador
                data.putBoolean(STARTER_CHEST_GIVEN, true);
                persistentData.put(Player.PERSISTED_NBT_TAG, data);
            }
        }
    }
}
