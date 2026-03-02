package dev.wren.tetra_armory.data;

import com.google.gson.Gson;
import dev.wren.tetra_armory.module.data.MaterialData;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.MergingDataStore;

public class ArmorMaterialStore extends MergingDataStore<MaterialData, MaterialData[]> {

    public ArmorMaterialStore(Gson gson, String namespace, String directory, DataDistributor distributor) {
        super(gson, namespace, directory, MaterialData.class, MaterialData[].class, distributor);
    }

    protected MaterialData mergeData(MaterialData[] data) {
        if (data.length > 0) {
            MaterialData result = data[0];

            for(int i = 1; i < data.length; ++i) {
                if (data[i].replace) {
                    result = data[i];
                } else {
                    MaterialData.copyFields(data[i], result);
                }
            }

            return result;
        } else {
            return null;
        }
    }
}
