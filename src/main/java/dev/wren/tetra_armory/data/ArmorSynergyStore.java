package dev.wren.tetra_armory.data;

import com.google.gson.Gson;
import dev.wren.tetra_armory.TetraArmory;
import dev.wren.tetra_armory.module.data.SynergyData;
import net.minecraft.resources.ResourceLocation;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.DataStore;

import java.util.Arrays;

public class ArmorSynergyStore extends DataStore<SynergyData[]> {

    public ArmorSynergyStore(Gson gson, String namespace, String directory, DataDistributor synchronizer) {
        super(gson, namespace, directory, SynergyData[].class, synchronizer);
    }

    public SynergyData[] getOrdered(String path) {
        SynergyData[] data = this.getDataIn(ResourceLocation.fromNamespaceAndPath(TetraArmory.MODID, path)).stream().flatMap(Arrays::stream).toArray(SynergyData[]::new);

        for(SynergyData entry : data) {
            Arrays.sort(entry.moduleVariants);
            Arrays.sort(entry.modules);
        }

        return data;
    }

}
