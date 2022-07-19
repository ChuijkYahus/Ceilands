package kittehmod.ceilands;

import com.mojang.logging.LogUtils;

import kittehmod.ceilands.block.CeilandsBlocks;
import kittehmod.ceilands.block.ModWoodType;
import kittehmod.ceilands.blockentities.CeilandsBlockEntities;
import kittehmod.ceilands.client.ClientHandler;
import kittehmod.ceilands.entity.CeilandsEntities;
import kittehmod.ceilands.item.CeilandsItems;
import kittehmod.ceilands.worldgen.biomes.CeilandsBiomes;
import kittehmod.ceilands.worldgen.features.CeilandsFeatures;
import kittehmod.ceilands.worldgen.features.trunkplacers.CeilandsTrunkPlacerType;
import kittehmod.ceilands.worldgen.features.trunkplacers.CeiltrunkTrunkPlacer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CeilandsMod.MODID)
public class CeilandsMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "ceilands";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public CeilandsMod()
    {
        CeilandsBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CeilandsItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CeilandsBlockEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CeilandsEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CeilandsTrunkPlacerType.TRUNK_PLACERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CeilandsFeatures.FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CeilandsBiomes.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
    	if (FMLEnvironment.dist == Dist.CLIENT) {
    		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    	}
    }

    private void setupCommon(final FMLCommonSetupEvent event)
    {
       	event.enqueueWork(() -> WoodType.register(ModWoodType.CEILTRUNK));
    	event.enqueueWork(() -> WoodType.register(ModWoodType.LUZAWOOD));
    	CeilandsFeatures.setupFeatures();
    }

    private void setupClient(FMLClientSetupEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(ClientHandler.class);
    	ClientHandler.setupRenderers();
    	event.enqueueWork(() -> { Sheets.addWoodType(ModWoodType.CEILTRUNK);} );
    	event.enqueueWork(() -> { Sheets.addWoodType(ModWoodType.LUZAWOOD);} );
    }
}