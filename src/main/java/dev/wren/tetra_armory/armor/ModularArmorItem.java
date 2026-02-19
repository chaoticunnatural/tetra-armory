package dev.wren.tetra_armory.armor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Multimap;
import dev.wren.tetra_armory.module.ArmorData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorItem;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.StunEffect;
import se.mickelus.tetra.effect.potion.StunPotionEffect;
import se.mickelus.tetra.items.InitializableItem;
import se.mickelus.tetra.module.data.EffectData;

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
    private final Cache<String, EffectData> effectCache = CacheBuilder.newBuilder()
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
