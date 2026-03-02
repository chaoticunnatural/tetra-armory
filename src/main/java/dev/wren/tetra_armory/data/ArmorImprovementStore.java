package dev.wren.tetra_armory.data;

import com.google.gson.Gson;
import dev.wren.tetra_armory.module.data.ImprovementData;
import dev.wren.tetra_armory.module.data.MaterialData;
import dev.wren.tetra_armory.module.data.MaterialImprovementData;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.DataStore;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArmorImprovementStore extends DataStore<ImprovementData[]> {

    private final ArmorMaterialStore materialStore;

    public ArmorImprovementStore(Gson gson, String namespace, String directory, ArmorMaterialStore materialStore, DataDistributor distributor) {
        super(gson, namespace, directory, ImprovementData[].class, distributor);
        this.materialStore = materialStore;
    }

    protected void processData() {
        this.dataMap = this.dataMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> this.processData((ImprovementData[])entry.getValue())));
    }

    private ImprovementData[] processData(ImprovementData[] data) {
        return  Arrays.stream(data).flatMap((improvement) -> improvement instanceof MaterialImprovementData ? this.expandMaterialImprovement((MaterialImprovementData)improvement) : Stream.of(improvement)).toArray(ImprovementData[]::new);
    }

    private Stream<ImprovementData> expandMaterialImprovement(MaterialImprovementData data) {
        Stream<MaterialData> var10000 = Arrays.stream(data.materials).map((rl) -> rl.getPath().endsWith("/") ? this.materialStore.getDataIn(rl) : Optional.ofNullable(this.materialStore.getData(rl)).map(Collections::singletonList).orElseGet(Collections::emptyList)).flatMap(Collection::stream);
        Objects.requireNonNull(data);
        return var10000.map(data::combine);
    }
}
