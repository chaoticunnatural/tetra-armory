package dev.wren.tetra_armory.armor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Multimap;
import dev.wren.tetra_armory.module.data.ArmorData;
import dev.wren.tetra_armory.module.data.EffectData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import se.mickelus.tetra.items.InitializableItem;
import se.mickelus.tetra.module.data.ItemProperties;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ParametersAreNonnullByDefault
public abstract class ModularArmorItem extends Item implements Equipable, InitializableItem {
    public ModularArmorItem(Properties pProperties) {
        super(pProperties);
    }

    public static final UUID movementSpeedModifier = UUID.fromString("");
    public static final UUID armorToughnessModifier = UUID.fromString("");
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

    @Override
    public abstract EquipmentSlot getEquipmentSlot();
}
