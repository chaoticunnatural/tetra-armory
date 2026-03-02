package dev.wren.tetra_armory.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import dev.wren.tetra_armory.effect.ArmorEffect;
import dev.wren.tetra_armory.effect.data.ArmorEffectData;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.DataStore;
import com.google.common.collect.Multimap;

import java.util.stream.Stream;

public class ArmorEffectStore extends DataStore<ArmorEffectData> {
    public static Multimap<ArmorEffect, ArmorEffectData> onArmorHitEffects = ArrayListMultimap.create();
    public static Multimap<ArmorEffect, ArmorEffectData> onArmorModuleUseEffects = ArrayListMultimap.create();

    public ArmorEffectStore(Gson gson, String namespace, String directory, DataDistributor synchronizer) {
        super(gson, namespace, directory, ArmorEffectData.class, synchronizer);
    }

    @Override
    protected void processData() {
        onArmorHitEffects = this.dataMap.values().stream().filter(data -> data.trigger.getType().equals("tetra_armory:on_hit")).collect(Multimaps.flatteningToMultimap((entry) -> entry.effect, Stream::of, ArrayListMultimap::create));
        onArmorModuleUseEffects = this.dataMap.values().stream().filter(data -> data.trigger.getType().equals("tetra_armory:on_module_use")).collect(Multimaps.flatteningToMultimap((entry) -> entry.effect, Stream::of, ArrayListMultimap::create));
    }
}
