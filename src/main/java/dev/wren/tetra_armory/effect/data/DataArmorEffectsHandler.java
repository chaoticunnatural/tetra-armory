package dev.wren.tetra_armory.effect.data;

import com.google.common.collect.ImmutableMap;
import dev.wren.tetra_armory.armor.IModularArmor;
import dev.wren.tetra_armory.data.ArmorEffectStore;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import se.mickelus.tetra.effect.data.ItemEffectContext;

public class DataArmorEffectsHandler {

    public static void applyOnHitEffects(ItemStack itemStack, LivingEntity wearer, LivingEntity attacker) {
        Level level = wearer.level();
        if (level instanceof ServerLevel serverLevel) {
            ItemEffectContext context = new ItemEffectContext(wearer, itemStack, serverLevel).withEntities(ImmutableMap.of("wearer", wearer, "attacker", attacker));
            ((IModularArmor) itemStack.getItem()).getEffects(itemStack).stream().flatMap(effect -> ArmorEffectStore.onArmorHitEffects.get(effect).stream()).forEach(effect -> prepareDataAndPerformOutcome(effect, context));
        }
    }

    public static void applyOnUseEffects(ItemStack itemStack, LivingEntity usingEntity) {
        Level var3 = usingEntity.level();
        if (var3 instanceof ServerLevel serverLevel) {
            ItemEffectContext context = (new ItemEffectContext(usingEntity, itemStack, serverLevel)).withEntities(ImmutableMap.of("user", usingEntity));
            ((IModularArmor) itemStack.getItem()).getEffects(itemStack).stream().flatMap((effect) -> ArmorEffectStore.onArmorModuleUseEffects.get(effect).stream()).forEach((effect) -> prepareDataAndPerformOutcome(effect, context));
        }
    }

    private static void prepareDataAndPerformOutcome(ArmorEffectData effect, ItemEffectContext context) {
        context = context.withMergedNumbers(ArmorEffectData.calculateNumbers(effect.data, context));
        context = context.withMergedVectors(ArmorEffectData.calculateVectors(effect.data, context));
        context = context.withMergedEntities(ArmorEffectData.calculateEntities(effect.data, context));
        if (effect.condition == null || effect.condition.test(context)) {
            effect.outcome.perform(context);
        }

    }

}
