package dev.wren.tetra_armory.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.math.Transformation;
import dev.wren.tetra_armory.TetraArmory;
import dev.wren.tetra_armory.module.data.TweakData;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.DataStore;
import se.mickelus.mutil.data.deserializer.BlockDeserializer;
import se.mickelus.mutil.data.deserializer.BlockPosDeserializer;
import se.mickelus.mutil.data.deserializer.ItemDeserializer;
import se.mickelus.mutil.data.deserializer.ResourceLocationDeserializer;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.blocks.PropertyMatcher;
import se.mickelus.tetra.blocks.workbench.action.ConfigActionImpl;
import se.mickelus.tetra.blocks.workbench.unlocks.UnlockData;
import se.mickelus.tetra.craftingeffect.condition.CraftingEffectCondition;
import se.mickelus.tetra.craftingeffect.outcome.CraftingEffectOutcome;
import se.mickelus.tetra.data.*;
import se.mickelus.tetra.data.deserializer.*;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.effect.data.ItemEffectTrigger;
import se.mickelus.tetra.effect.data.condition.ItemEffectCondition;
import se.mickelus.tetra.effect.data.outcome.ItemEffectOutcome;
import se.mickelus.tetra.effect.data.provider.entity.EntityProvider;
import se.mickelus.tetra.effect.data.provider.number.NumberProvider;
import se.mickelus.tetra.effect.data.provider.vector.VectorProvider;
import se.mickelus.tetra.effect.modifier.ModifierType;
import se.mickelus.tetra.items.modular.impl.dynamic.ArchetypeDefinition;
import se.mickelus.tetra.module.Priority;
import se.mickelus.tetra.module.ReplacementDefinition;
import se.mickelus.tetra.module.data.*;
import se.mickelus.tetra.module.schematic.OutcomeDefinition;
import se.mickelus.tetra.module.schematic.OutcomeMaterial;
import se.mickelus.tetra.module.schematic.RepairDefinition;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirementDeserializer;
import se.mickelus.tetra.module.schematic.requirement.IntegerPredicate;
import se.mickelus.tetra.module.schematic.requirement.ModuleRequirement;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ArmorDataManager implements DataDistributor {

    public static final Gson gson;
    public static ArmorDataManager INSTANCE;
    public final DataStore<ResourceLocation[]> tierData;
    public final DataStore<TweakData[]> tweakData;
    public final ArmorMaterialStore materialData;
    public final ArmorImprovementStore improvementData;
    public final ArmorModuleStore moduleData;
    public final DataStore<RepairDefinition> repairData;
    public final DataStore<EnchantmentMapping[]> enchantmentData;
    public final ArmorSynergyStore synergyData;
    public final DataStore<ReplacementDefinition[]> replacementData;
    public final SchematicStore schematicData;
    public final CraftingEffectStore craftingEffectData;
    public final DataStore<ConfigActionImpl[]> actionData;
    public final DataStore<UnlockData> unlockData;
    public final DataStore<ArchetypeDefinition> archetypeData;
    public final ArmorEffectStore itemEffectData;
    public final ModifierEffectStore modifierEffectData;
    private final Logger logger = LogManager.getLogger();
    private final DataStore<?>[] dataStores;

    public ArmorDataManager() {
        INSTANCE = this;
        this.tierData = new DataStore<>(gson, TetraArmory.MODID, "tiers", ResourceLocation[].class, this);
        this.tweakData = new DataStore<>(gson, TetraArmory.MODID, "tweaks", TweakData[].class, this);
        this.materialData = new ArmorMaterialStore(gson, TetraArmory.MODID, "materials", this);
        this.improvementData = new ArmorImprovementStore(gson, TetraArmory.MODID, "improvements", this.materialData, this);
        this.moduleData = new ArmorModuleStore(gson, TetraArmory.MODID, "modules", this);
        this.repairData = new DataStore<>(gson, TetraArmory.MODID, "repairs", RepairDefinition.class, this);
        this.enchantmentData = new DataStore<>(gson, TetraArmory.MODID, "enchantments", EnchantmentMapping[].class, this);
        this.synergyData = new ArmorSynergyStore(gson, TetraArmory.MODID, "synergies", this);
        this.replacementData = new DataStore<>(gson, TetraArmory.MODID, "replacements", ReplacementDefinition[].class, this);
        this.schematicData = new SchematicStore(gson, TetraArmory.MODID, "schematics", this);
        this.craftingEffectData = new CraftingEffectStore(gson, TetraArmory.MODID, "crafting_effects", this);
        this.actionData = new DataStore<>(gson, TetraArmory.MODID, "actions", ConfigActionImpl[].class, this);
        this.unlockData = new DataStore<>(gson, TetraArmory.MODID, "unlocks", UnlockData.class, this);
        this.archetypeData = new DataStore<>(gson, TetraArmory.MODID, "archetypes", ArchetypeDefinition.class, this);
        this.itemEffectData = new ArmorEffectStore(gson, TetraArmory.MODID, "armor_effects", this);
        this.modifierEffectData = new ModifierEffectStore(gson, TetraArmory.MODID, "modifier_effects", this);
        this.dataStores = new DataStore[]{this.tierData, this.tweakData, this.materialData, this.improvementData, this.moduleData, this.enchantmentData, this.synergyData, this.replacementData, this.schematicData, this.craftingEffectData, this.repairData, this.actionData, this.unlockData, this.archetypeData, this.itemEffectData, this.modifierEffectData};
    }

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public void addReloadListener(AddReloadListenerEvent event) {
        this.logger.debug("Setting up datastore reload listeners");
        Stream<DataStore<?>> dataStoreStream = Arrays.stream(this.dataStores);
        Objects.requireNonNull(event);
        dataStoreStream.forEach(event::addListener);
    }

    @SubscribeEvent
    public void tagsUpdated(TagsUpdatedEvent event) {
        this.logger.debug("Reloaded tags");
    }

    @SubscribeEvent
    public void playerConnected(PlayerEvent.PlayerLoggedInEvent event) {
        this.logger.info("Sending data to client: {}", event.getEntity().getName().getString());

        for(DataStore<?> dataStore : this.dataStores) {
            dataStore.sendToPlayer((ServerPlayer)event.getEntity());
        }

    }

    public void onDataRecieved(String directory, Map<ResourceLocation, String> data) {
        Arrays.stream(this.dataStores).filter((dataStore) -> dataStore.getDirectory().equals(directory)).forEach((dataStore) -> dataStore.loadFromPacket(data));
    }

    public void sendToAll(String directory, Map<ResourceLocation, JsonElement> data) {
        TetraMod.packetHandler.sendToAllPlayers(new UpdateDataPacket(directory, data));
    }

    public void sendToPlayer(ServerPlayer player, String directory, Map<ResourceLocation, JsonElement> data) {
        TetraMod.packetHandler.sendTo(new UpdateDataPacket(directory, data), player);
    }

    static {
        gson = (new GsonBuilder()).registerTypeAdapter(ToolData.class, new ToolData.Deserializer()).registerTypeAdapter(AspectData.class, new AspectData.Deserializer()).registerTypeAdapter(ItemAspect.class, new ItemAspect.Deserializer()).registerTypeAdapter(EffectData.class, new EffectData.Deserializer()).registerTypeAdapter(GlyphData.class, new GlyphDeserializer()).registerTypeAdapter(ModuleModel.class, new ModuleModelDeserializer()).registerTypeAdapter(Priority.class, new Priority.Deserializer()).registerTypeAdapter(ItemPredicate.class, new ItemPredicateDeserializer()).registerTypeAdapter(PropertyMatcher.class, new PropertyMatcherDeserializer()).registerTypeAdapter(MaterialData.class, new MaterialData.Deserializer()).registerTypeAdapter(OutcomeMaterial.class, new OutcomeMaterial.Deserializer()).registerTypeAdapter(ReplacementDefinition.class, new ReplacementDeserializer()).registerTypeAdapter(BlockPos.class, new BlockPosDeserializer()).registerTypeAdapter(Block.class, new BlockDeserializer()).registerTypeAdapter(BlockState.class, new BlockStateDeserializer()).registerTypeAdapter(AttributesDeserializer.typeToken.getRawType(), new AttributesDeserializer()).registerTypeAdapter(ItemTagKeyDeserializer.typeToken.getRawType(), new ItemTagKeyDeserializer()).registerTypeAdapter(VariantData.class, new VariantData.Deserializer()).registerTypeAdapter(ImprovementData.class, new ImprovementData.Deserializer()).registerTypeAdapter(OutcomeDefinition.class, new OutcomeDefinition.Deserializer()).registerTypeAdapter(MaterialColors.class, new MaterialColors.Deserializer()).registerTypeAdapter(CraftingEffectCondition.class, new CraftingEffectCondition.Deserializer()).registerTypeAdapter(CraftingEffectOutcome.class, new CraftingEffectOutcome.Deserializer()).registerTypeAdapter(CraftingRequirement.class, new CraftingRequirementDeserializer()).registerTypeAdapter(ModuleRequirement.class, new ModuleRequirement.Deserializer()).registerTypeAdapter(IntegerPredicate.class, new IntegerPredicate.Deserializer()).registerTypeAdapter(Item.class, new ItemDeserializer()).registerTypeAdapter(ItemStack.class, new ItemStackDeserializer()).registerTypeAdapter(Enchantment.class, new EnchantmentDeserializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocationDeserializer()).registerTypeAdapter(Vector3f.class, new VectorDeserializer()).registerTypeAdapter(Quaternionf.class, new QuaternionDeserializer()).registerTypeAdapter(Transformation.class, new TransformationDeserializer()).registerTypeAdapter(AABB.class, new AABBDeserializer()).registerTypeAdapter(ItemDisplayContext.class, new ItemDisplayContextDeserializer()).registerTypeAdapter(ItemEffect.class, new ItemEffect.Deserializer()).registerTypeAdapter(ItemEffectTrigger.class, new ItemEffectTrigger.Deserializer()).registerTypeAdapter(ItemEffectCondition.class, new ItemEffectCondition.Deserializer()).registerTypeAdapter(ItemEffectOutcome.class, new ItemEffectOutcome.Deserializer()).registerTypeAdapter(NumberProvider.class, new NumberProvider.Deserializer()).registerTypeAdapter(EntityProvider.class, new EntityProvider.Deserializer()).registerTypeAdapter(VectorProvider.class, new VectorProvider.Deserializer()).registerTypeAdapter(ModifierType.class, new ModifierType.Deserializer()).registerTypeAdapter(EntityPredicate.class, new EntityPredicateDeserializer()).registerTypeAdapter(ParticleOptions.class, new ParticleOptionsDeserializer()).registerTypeAdapter(SoundEvent.class, new SoundEventDeserializer()).registerTypeAdapter(MobEffect.class, new MobEffectDeserializer()).registerTypeAdapter(EntityType.class, new EntityTypeDeserializer()).registerTypeAdapter(CompoundTag.class, new CompoundTagDeserializer()).create();
    }

}
