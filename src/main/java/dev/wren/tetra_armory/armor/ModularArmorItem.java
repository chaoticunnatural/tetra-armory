package dev.wren.tetra_armory.armor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Multimap;
import dev.wren.tetra_armory.data.ArmorDataManager;
import dev.wren.tetra_armory.module.data.ArmorData;
import dev.wren.tetra_armory.module.data.EffectData;
import dev.wren.tetra_armory.module.data.SynergyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.items.InitializableItem;
import se.mickelus.tetra.module.data.ItemProperties;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public abstract class ModularArmorItem extends Item implements Equipable, InitializableItem, IModularArmor {
    public static final UUID movementSpeedModifier = UUID.fromString("");
    public static final UUID armorToughnessModifier = UUID.fromString(""); // todo uuids
    public static final UUID defenseModifier = UUID.fromString("");

    private final Cache<String, Multimap<Attribute, AttributeModifier>> attributeCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Cache<String, ArmorData> armorCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Cache<String, EffectData> armorEffectCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Cache<String, ItemProperties> propertyCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    protected int honeBase = 450;
    protected int honeIntegrityMultiplier = 200;
    protected boolean canHone = true;
    protected String[] majorModuleKeys;
    protected String[] minorModuleKeys;
    protected String[] requiredModules = new String[0];
    protected int baseDurability = 0;
    protected int baseIntegrity = 0;
    protected SynergyData[] synergies = new SynergyData[0];

    public ModularArmorItem(Properties pProperties) {
        super(pProperties);

        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);

        ArmorDataManager.INSTANCE.moduleData.onReload(this::clearCaches);
    }

    public void clearCaches() {
        logger.debug("Clearing item data caches for {}...", toString());
        attributeCache.invalidateAll();
        armorCache.invalidateAll();
        armorEffectCache.invalidateAll();
        propertyCache.invalidateAll();
    }

    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            return dispenseArmor(source, stack) ? stack : super.execute(source, stack);
        }
    };

    public static boolean dispenseArmor(BlockSource source, ItemStack stack) {
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = source.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(stack)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingentity = list.get(0);
            EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(stack);
            ItemStack itemstack = stack.split(1);
            livingentity.setItemSlot(equipmentslot, itemstack);
            if (livingentity instanceof Mob) {
                ((Mob)livingentity).setDropChance(equipmentslot, 2.0F);
                ((Mob)livingentity).setPersistenceRequired();
            }

            return true;
        }
    }

    @Override
    public String[] getMajorModuleKeys(ItemStack itemStack) {
        return majorModuleKeys;
    }

    @Override
    public String[] getMinorModuleKeys(ItemStack itemStack) {
        return minorModuleKeys;
    }

    @Override
    public String[] getRequiredModules(ItemStack itemStack) {
        return requiredModules;
    }

    @Override
    public int getHoneBase(ItemStack itemStack) {
        return honeBase;
    }

    @Override
    public int getHoneIntegrityMultiplier(ItemStack itemStack) {
        return honeIntegrityMultiplier;
    }

    @Override
    public boolean canGainHoneProgress(ItemStack itemStack) {
        return canHone;
    }

    @Override
    public Cache<String, Multimap<Attribute, AttributeModifier>> getAttributeModifierCache() {
        return attributeCache;
    }

    @Override
    public Cache<String, EffectData> getArmorEffectDataCache() {
        return armorEffectCache;
    }

    @Override
    public Cache<String, ItemProperties> getPropertyCache() {
        return propertyCache;
    }

    public Cache<String, ArmorData> getArmorDataCache() {
        return armorCache;
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal(getItemName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.addAll(getTooltip(stack, world, flag));
    }

    @Override
    @NotNull
    public Rarity getRarity(ItemStack itemStack) {
        return Optional.ofNullable(getPropertiesCached(itemStack)).map(props -> props.rarity).orElse(super.getRarity(itemStack));
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        return Optional.of(getPropertiesCached(itemStack))
                .map(properties -> (properties.durability + baseDurability) * properties.durabilityMultiplier)
                .map(Math::round)
                .orElse(0);
    }

    @Override
    public void setDamage(ItemStack itemStack, int damage) {
        super.setDamage(itemStack, Math.min(itemStack.getMaxDamage() - 1, damage));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return damageItemImpl(stack, amount, entity, onBroken);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) getMaxDamage(itemStack));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float maxDamage = getMaxDamage(itemStack);
        float f = Math.max(0.0F, (maxDamage - itemStack.getDamageValue()) / maxDamage);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Level world, Player player) {
        IModularArmor.updateIdentifier(itemStack);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return ConfigHandler.enableGlint.get() && super.isFoil(itemStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public SynergyData[] getAllSynergyData(ItemStack itemStack) {
        return synergies;
    }

    @Override
    public boolean isBookEnchantable(final ItemStack itemStack, final ItemStack bookStack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack itemStack, Enchantment enchantment) {
        return acceptsEnchantment(itemStack, enchantment, true);
    }

    @Override
    public int getEnchantmentValue(ItemStack itemStack) {
        return getEnchantability(itemStack);
    }

    @Override
    public abstract @NotNull EquipmentSlot getEquipmentSlot();

    public abstract int getDefense(ArmorHitStat stat);

    public abstract float getToughness(ArmorHitStat stat);

    public abstract float getKbRes(ArmorHitStat stat);
}
