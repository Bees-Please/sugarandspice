package com.beesplease.sugarandspice;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SugarAndSpice.MODID)
public class SugarAndSpice {
  public static final String MODID = "sugarandspice";
  public static SugarAndSpice instance;
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static final ModSetup setup = new ModSetup();

  private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

  public static IEventBus MOD_EVENT_BUS;

  public SugarAndSpice() {
  	instance = this;

  	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

    MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    ModSetup.register();


    REGISTRATE.registerEventListeners(MOD_EVENT_BUS);
    MOD_EVENT_BUS.addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);

    Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

    MOD_EVENT_BUS.addListener(SugarAndSpiceClient::clientSetup);

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SugarAndSpiceClient::clientCtor);
  }

  private void setup(final FMLCommonSetupEvent event) {
    setup.init();
  }

  public static ResourceLocation asResource(String name) {
		return new ResourceLocation(MODID, name);
	}

  public static CreateRegistrate registrate() {
    return REGISTRATE;
  }

}
