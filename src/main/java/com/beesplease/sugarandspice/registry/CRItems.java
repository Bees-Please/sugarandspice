package com.beesplease.sugarandspice.registry;

import com.beesplease.sugarandspice.SugarAndSpice;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class CRItems {
  private static final CreateRegistrate REGISTRATE = SugarAndSpice.registrate();
  public static final CreativeModeTab itemGroup = new CreativeModeTab(SugarAndSpice.MODID) {
    @Override
    @Nonnull
    public ItemStack makeIcon() { return AllBlocks.FLYWHEEL.asStack(); }
  };

  static {
    REGISTRATE.creativeModeTab(() -> itemGroup, "Sugar and Spice");
  }

  @SuppressWarnings("EmptyMethod")
  public static void register() {}
}
