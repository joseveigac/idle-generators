package com.ghozix.idlegenerators.generator;

import com.ghozix.idlegenerators.config.ClientToggles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** BlockItem con aviso en el tooltip cuando su generador está desactivado por config (v1.3.0). */
public class GeneratorBlockItem extends BlockItem {
    public GeneratorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        GeneratorType type = ((GeneratorBlock) getBlock()).type();
        // El estado viene del SERVIDOR (S2C al login), no de la config local: en un dedicado la
        // config del jugador no tiene nada que ver con la que manda.
        if (ClientToggles.isDisabled(type.key())) {
            tooltip.add(Component.translatable("tooltip.idlegenerators.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }
}
