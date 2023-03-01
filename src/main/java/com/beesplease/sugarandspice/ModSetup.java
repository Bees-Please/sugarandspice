package com.beesplease.sugarandspice;

import com.beesplease.sugarandspice.registry.CRBlockEntities;
import com.beesplease.sugarandspice.registry.CRBlocks;
import com.beesplease.sugarandspice.registry.CRItems;

public class ModSetup {
    public static void register() {
        CRItems.register();
        CRBlockEntities.register();
        CRBlocks.register();
    }

    public void init() {
    }
}

