package net.dollar.simplegear.item.custom.cobaltsteel;

import net.dollar.simplegear.item.custom.ModMaceItem;
import net.dollar.simplegear.util.ModUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModCobaltSteelMaceItem extends ModMaceItem {
    public ModCobaltSteelMaceItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }



    /**
     * Performs normal post-hit operations but with chance to apply additional effect(s).
     * @param stack ItemStack of this Item
     * @param target Attacked (target) living entity
     * @param attacker Attacker (user) living entity
     * @return Whether attack was successfully performed
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ModUtils.applyCobaltSteelOnHit(target, attacker);
        return super.postHit(stack, target, attacker);
    }

    /**
     * Gets whether Entities of this Item are fireproof (true).
     * @return Whether this Item is fireproof
     */
    @Override
    public boolean isFireproof() {
        return true;
    }

    /**
     * Appends text to the Item's hover tooltip (lore).
     * @param stack ItemStack corresponding to this Item
     * @param world Active world
     * @param tooltip List of tooltip texts to show
     * @param context TooltipContext denoting data like simple or advanced
     */
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ModUtils.appendCobaltSteelEquipmentTooltip(tooltip, false);
    }
}
