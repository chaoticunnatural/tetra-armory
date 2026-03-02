package dev.wren.tetra_armory.module;

import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.wren.tetra_armory.data.ArmorDataManager;
import dev.wren.tetra_armory.module.data.MaterialData;
import dev.wren.tetra_armory.module.data.ModuleData;
import dev.wren.tetra_armory.module.data.MaterialVariantData;
import dev.wren.tetra_armory.module.data.VariantData;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArmorModuleRegistry {

    private static final Logger logger = LogManager.getLogger();
    public static ArmorModuleRegistry INSTANCE;
    private final Map<ResourceLocation, BiFunction<ResourceLocation, ModuleData, ArmorModule>> moduleConstructors;
    private Map<ResourceLocation, ArmorModule> moduleMap;

    public ArmorModuleRegistry() {
        INSTANCE = this;
        this.moduleConstructors = new HashMap();
        this.moduleMap = Collections.emptyMap();
        ArmorDataManager.INSTANCE.moduleData.onReload(() -> this.setupModules(ArmorDataManager.INSTANCE.moduleData.getData()));
    }

    private void setupModules(Map<ResourceLocation, ModuleData> data) {
        this.moduleMap = data.entrySet().stream().filter((entry) -> this.validateModuleData(entry.getKey(), entry.getValue())).flatMap((entry) -> this.expandEntry(entry).stream()).collect(Collectors.toMap(Map.Entry::getKey, (entry) -> this.setupModule(entry.getKey(), entry.getValue())));
    }

    private boolean validateModuleData(ResourceLocation identifier, ModuleData data) {
        if (data == null) {
            logger.warn("Failed to create module from module data '{}': Data is null (probably due to it failing to parse)", identifier);
            return false;
        } else if (!this.moduleConstructors.containsKey(data.type)) {
            logger.warn("Failed to create module from module data '{}': Unknown type '{}'", identifier, data.type);
            return false;
        } else if (data.slots != null && data.slots.length >= 1) {
            return true;
        } else {
            logger.warn("Failed to create module from module data '{}': Slots field is empty", identifier);
            return false;
        }
    }

    private Collection<Pair<ResourceLocation, ModuleData>> expandEntry(Map.Entry<ResourceLocation, ModuleData> entry) {
        ModuleData moduleData = entry.getValue();
        if (moduleData.slotSuffixes.length <= 0) {
            return Collections.singletonList(new ImmutablePair<>(entry.getKey(), entry.getValue()));
        } else {
            ArrayList<Pair<ResourceLocation, ModuleData>> result = new ArrayList<>(moduleData.slots.length);

            for(int i = 0; i < moduleData.slots.length; ++i) {
                ModuleData dataCopy = moduleData.shallowCopy();
                dataCopy.slots = new String[]{moduleData.slots[i]};
                dataCopy.slotSuffixes = new String[]{moduleData.slotSuffixes[i]};
                String var10002 = entry.getKey().getNamespace();
                String var10003 = entry.getKey().getPath();
                ResourceLocation suffixedIdentifier = ResourceLocation.fromNamespaceAndPath(var10002, var10003 + moduleData.slotSuffixes[i]);
                result.add(new ImmutablePair<>(suffixedIdentifier, dataCopy));
            }

            return result;
        }
    }

    private void expandMaterialVariants(ModuleData moduleData) {
        moduleData.variants = Arrays.stream(moduleData.variants).flatMap((variant) -> variant instanceof MaterialVariantData ? this.expandMaterialVariant((MaterialVariantData)variant) : Stream.of(variant)).toArray(VariantData[]::new);
    }

    private Stream<VariantData> expandMaterialVariant(MaterialVariantData source) {
        Stream<MaterialData> var10000 = Arrays.stream(source.materials).map((rl) -> rl.getPath().endsWith("/") ? ArmorDataManager.INSTANCE.materialData.getDataIn(rl) : Optional.ofNullable(ArmorDataManager.INSTANCE.materialData.getData(rl)).map(Collections::singletonList).orElseGet(Collections::emptyList)).flatMap(Collection::stream);
        Objects.requireNonNull(source);
        return var10000.map(source::combine);
    }

    private void handleVariantDuplicates(ModuleData data) {
        data.variants = (Arrays.stream(data.variants).collect(Collectors.toMap((variant) -> variant.key, Function.identity(), VariantData::merge))).values().toArray(new VariantData[0]);
    }

    private ArmorModule setupModule(ResourceLocation identifier, ModuleData data) {
        this.expandMaterialVariants(data);
        this.handleVariantDuplicates(data);
        return this.moduleConstructors.get(data.type).apply(identifier, data);
    }

    public void registerModuleType(ResourceLocation identifier, BiFunction<ResourceLocation, ModuleData, ArmorModule> constructor) {
        this.moduleConstructors.put(identifier, constructor);
    }

    public ArmorModule getModule(ResourceLocation identifier) {
        return this.moduleMap.get(identifier);
    }

    public Collection<ArmorModule> getAllModules() {
        return this.moduleMap.values();
    }

}
