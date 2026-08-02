package com.ghozix.idlegenerators.generator;

import com.ghozix.idlegenerators.config.IGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/** BlockItem con aviso en el tooltip cuando su generador está desactivado por config (v1.3.0). */
public class GeneratorBlockItem extends BlockItem {
    public GeneratorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        GeneratorType type = ((GeneratorBlock) getBlock()).type();
        if (!IGConfig.get().isGeneratorEnabled(type.key(), type.category())) {
            tooltip.accept(Component.translatable("tooltip.idlegenerators.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }
}
