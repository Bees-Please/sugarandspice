package com.beesplease.sugarandspice;

import com.beesplease.sugarandspice.registry.CRBlockPartials;
import com.beesplease.sugarandspice.registry.CRPonderIndex;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class SugarAndSpiceClient {
    public static void clientSetup(FMLClientSetupEvent event) {
    }

    public static void clientCtor() {
        CRPonderIndex.register();
        CRBlockPartials.init();
    }
}
