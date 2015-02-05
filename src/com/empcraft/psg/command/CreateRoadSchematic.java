package com.empcraft.psg.command;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.empcraft.psg.Main;
import com.empcraft.psg.SchematicHandler;
import com.empcraft.psg.generator.HybridPlotWorld;
import com.empcraft.psg.jnbt.CompoundTag;
import com.empcraft.psg.object.PlotBlock;

public class CreateRoadSchematic {
    public CreateRoadSchematic(final World world, final Location bot, final Location top) {
        final HybridPlotWorld plotworld = Main.worlds.get(world.getName());

        final int sx = (bot.getBlockX() - plotworld.ROAD_WIDTH) + 1;
        final int sz = bot.getBlockZ() + 1;
        final int sy = plotworld.ROAD_HEIGHT;

        final int ex = bot.getBlockX();
        final int ez = top.getBlockZ();
        final int ey = get_ey(world, sx, ex, sz, ez, sy);

        final Location pos1 = new Location(world, sx, sy, sz);
        final Location pos2 = new Location(world, ex, ey, ez);

        new PlotBlock((short) 5, (byte) 0);

        final int bx = sx;
        final int bz = sz - plotworld.ROAD_WIDTH;
        final int by = sy;

        final int tx = ex;
        final int tz = sz - 1;
        final int ty = get_ey(world, bx, tx, bz, tz, by);

        final Location pos3 = new Location(world, bx, by, bz);
        final Location pos4 = new Location(world, tx, ty, tz);

        new PlotBlock((short) 7, (byte) 0);

        final CompoundTag sideroad = SchematicHandler.getCompoundTag(world, pos1, pos2);
        final CompoundTag intersection = SchematicHandler.getCompoundTag(world, pos3, pos4);

        final String dir = Main.datafolder + "schematics" + File.separator + "GEN_ROAD_SCHEMATIC" + File.separator + world.getName() + File.separator;

        SchematicHandler.save(sideroad, dir + "sideroad.schematic");
        SchematicHandler.save(intersection, dir + "intersection.schematic");

        plotworld.ROAD_SCHEMATIC_ENABLED = true;
        plotworld.setupSchematics();
    }

    public int get_ey(final World world, final int sx, final int ex, final int sz, final int ez, final int sy) {
        final int maxY = world.getMaxHeight();
        int ey = sy;
        for (int x = sx; x <= ex; x++) {
            for (int z = sz; z <= ez; z++) {
                for (int y = sy; y < maxY; y++) {
                    if (y > ey) {
                        final Block block = world.getBlockAt(new Location(world, x, y, z));
                        if (block.getTypeId() != 0) {
                            ey = y;
                        }
                    }
                }
            }
        }
        return ey;
    }
}
