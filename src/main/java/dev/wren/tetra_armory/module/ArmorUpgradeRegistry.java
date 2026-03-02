package dev.wren.tetra_armory.module;

import dev.wren.tetra_armory.TetraArmory;
import dev.wren.tetra_armory.data.ArmorDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemUpgradeRegistry;
import se.mickelus.tetra.module.ModuleRegistry;
import se.mickelus.tetra.module.ReplacementDefinition;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ArmorUpgradeRegistry {

    private static final Logger logger = LogManager.getLogger();
    public static ArmorUpgradeRegistry INSTANCE;
    private final List<BiFunction<ItemStack, ItemStack, ItemStack>> replacementHooks;
    private List<ReplacementDefinition> replacementDefinitions;

    public ArmorUpgradeRegistry() {
        INSTANCE = this;
        this.replacementHooks = new ArrayList();
        this.replacementDefinitions = Collections.emptyList();
        ArmorDataManager.INSTANCE.replacementData.onReload(() -> this.replacementDefinitions = ArmorDataManager.INSTANCE.replacementData.getData().values().stream().flatMap(Arrays::stream).filter((replacementDefinition) -> replacementDefinition.predicate != null).collect(Collectors.toList()));
    }

    public void registerReplacementHook(BiFunction<ItemStack, ItemStack, ItemStack> hook) {
        this.replacementHooks.add(hook);
    }

    public ItemStack getReplacement(ItemStack itemStack) {
        for(ReplacementDefinition replacementDefinition : this.replacementDefinitions) {
            if (replacementDefinition.predicate.matches(itemStack)) {
                ItemStack replacementStack = replacementDefinition.itemStack.copy();
                replacementStack.setDamageValue(itemStack.getDamageValue());

                for(BiFunction<ItemStack, ItemStack, ItemStack> hook : this.replacementHooks) {
                    replacementStack = hook.apply(itemStack, replacementStack);
                }

                return replacementStack;
            }
        }

        return ItemStack.EMPTY;
    }

    public ArmorModule getModule(String key) {
        return ArmorModuleRegistry.INSTANCE.getModule(ResourceLocation.fromNamespaceAndPath(TetraArmory.MODID, key));
    }

    public Collection<ArmorModule> getAllModules() {
        return ArmorModuleRegistry.INSTANCE.getAllModules();
    }

}
