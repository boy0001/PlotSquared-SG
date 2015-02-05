package com.empcraft.psg.plotme;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import com.empcraft.psg.generator.HybridGen;
import com.worldcretornica.plotme_core.bukkit.api.IBukkitPlotMe_ChunkGenerator;
import com.worldcretornica.plotme_core.bukkit.api.IBukkitPlotMe_GeneratorManager;

public class PlotMeGenerator extends ChunkGenerator implements IBukkitPlotMe_ChunkGenerator {

    public IBukkitPlotMe_GeneratorManager manager = new PlotMeManager();

    public final HybridGen generator;

    public PlotMeGenerator(final String world) {
        this.generator = new HybridGen(world);
    }

    @Override
    public IBukkitPlotMe_GeneratorManager getManager() {
        return manager;
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
    public short[][] generateExtBlockSections(final World world, final Random random, final int cx, final int cz, final BiomeGrid biomes) {
        return this.generator.generateExtBlockSections(world, random, cx, cz, biomes);
    }
}
