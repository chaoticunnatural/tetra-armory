package dev.wren.tetra_armory;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TetraArmory.MODID)
public class TetraArmory
{
    public static final String MODID = "tetra_armory";
    private static final Logger LOGGER = LogUtils.getLogger();


    public TetraArmory(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
    }
}
