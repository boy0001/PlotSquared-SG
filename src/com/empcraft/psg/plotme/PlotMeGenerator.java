package com.empcraft.psg.plotme;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;

import com.empcraft.psg.generator.HybridGen;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.bukkit.BukkitServerBridge;
import com.worldcretornica.plotme_core.bukkit.PlotMe_CorePlugin;
import com.worldcretornica.plotme_core.bukkit.api.IBukkitPlotMe_ChunkGenerator;
import com.worldcretornica.plotme_core.bukkit.api.IBukkitPlotMe_GeneratorManager;

public class PlotMeGenerator extends ChunkGenerator implements IBukkitPlotMe_ChunkGenerator {

    public static IBukkitPlotMe_GeneratorManager manager = new PlotMeManager();
    
    public final HybridGen generator;
    
    public PlotMeGenerator(String world, String id) {
        this.generator = new HybridGen(world);
    }
    
    @Override
    public IBukkitPlotMe_GeneratorManager getManager() {
        return PlotMeGenerator.manager;
    }
    
    @Override
    public boolean canSpawn(final World world, final int x, final int z) {
        return true;
    }
    
    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return this.generator.getDefaultPopulators(world);
    }
    
    @Override
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return this.generator.getFixedSpawnLocation(world, random);
    }
    
    @Override
    public short[][] generateExtBlockSections(final World world, final Random random, int cx, int cz, final BiomeGrid biomes) {
        return this.generator.generateExtBlockSections(world, random, cx, cz, biomes);
    }
}
