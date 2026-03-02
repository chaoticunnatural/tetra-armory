package dev.wren.tetra_armory.data;

import com.google.gson.Gson;
import dev.wren.tetra_armory.module.data.ModuleData;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.MergingDataStore;

public class ArmorModuleStore extends MergingDataStore<ModuleData, ModuleData[]> {

    public ArmorModuleStore(Gson gson, String namespace, String directory, DataDistributor distributor) {
        super(gson, namespace, directory, ModuleData.class, ModuleData[].class, distributor);
    }

    protected ModuleData mergeData(ModuleData[] data) {
        if (data.length > 0) {
            ModuleData result = data[0];

            for(int i = 1; i < data.length; ++i) {
                if (data[i].replace) {
                    result = data[i];
                } else {
                    ModuleData.copyFields(data[i], result);
                }
            }

            return result;
        } else {
            return null;
        }
    }
}
