package com.empcraft.psg.plotme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.empcraft.psg.Main;
import com.empcraft.psg.generator.HybridPlotWorld;
import com.empcraft.psg.object.ChunkLoc;
import com.empcraft.psg.object.PlotBlock;
import com.empcraft.psg.object.RegionWrapper;
import com.worldcretornica.plotme_core.bukkit.api.IBukkitPlotMe_GeneratorManager;

@SuppressWarnings("deprecation")
public class PlotMeManager implements IBukkitPlotMe_GeneratorManager {
    public HybridPlotWorld gPW(final World world) {
        return gPW(world.getName());
    }

    public HybridPlotWorld gPW(final String world) {
        return Main.worlds.get(world);
    }

    @Override
    public void adjustPlotFor(final World world, final String id, final boolean claimed, final boolean protect, final boolean auctioned, final boolean forSale) {
        final HybridPlotWorld plotworld = gPW(world);
        final List<PlotBlock> wallIds = new ArrayList<>();
        final int roadHeight = plotworld.WALL_HEIGHT;
        final PlotBlock claimedId = plotworld.CLAIMED_WALL_BLOCK;
        final PlotBlock wallId = plotworld.WALL_BLOCK;
        final PlotBlock protectedWallId = plotworld.PROTECTED_WALL_BLOCK;
        final PlotBlock auctionWallId = plotworld.AUCTION_WALL_BLOCK;
        final PlotBlock forsaleWallId = plotworld.FORSALE_WALL_BLOCK;

        if (protect) {
            wallIds.add(protectedWallId);
        }
        if (auctioned && !wallIds.contains(auctionWallId)) {
            wallIds.add(auctionWallId);
        }
        if (forSale && !wallIds.contains(forsaleWallId)) {
            wallIds.add(forsaleWallId);
        }
        if (claimed && !wallIds.contains(claimedId)) {
            wallIds.add(claimedId);
        }
        if (wallIds.isEmpty()) {
            wallIds.add(wallId);
        }

        int ctr = 0;

        final Location bottom = getPlotBottomLoc(world, id);
        final Location top = getPlotTopLoc(world, id);

        int x;
        int z;

        PlotBlock currentBlockId;
        Block block;

        for (x = bottom.getBlockX() - 1; x < (top.getBlockX() + 1); x++) {
            z = bottom.getBlockZ() - 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == (wallIds.size() - 1)) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }

        for (z = bottom.getBlockZ() - 1; z < (top.getBlockZ() + 1); z++) {
            x = top.getBlockX() + 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == (wallIds.size() - 1)) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }

        for (x = top.getBlockX() + 1; x > (bottom.getBlockX() - 1); x--) {
            z = top.getBlockZ() + 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == (wallIds.size() - 1)) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }

        for (z = top.getBlockZ() + 1; z > (bottom.getBlockZ() - 1); z--) {
            x = bottom.getBlockX() - 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == (wallIds.size() - 1)) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }
    }

    private void setWall(final Block block, final PlotBlock plotblock) {
        // NOTHING
        block.setTypeIdAndData(plotblock.id, plotblock.data, true);
    }

    private Location bottomLoc = null;
    private String bottomString = null;

    private ChunkLoc getLoc(final String id) {
        final String[] split = id.split(";");
        return new ChunkLoc(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    @Override
    public int bottomX(final String id, final World world) {
        if (id.equals(this.bottomString)) {
            return this.bottomLoc.getBlockX();
        }
        final Location loc = getPlotBottomLoc(world, id);
        this.bottomLoc = loc;
        this.bottomString = id;
        return loc.getBlockX();
    }

    @Override
    public int bottomZ(final String id, final World world) {
        if (id.equals(this.bottomString)) {
            return this.bottomLoc.getBlockZ();
        }
        final Location loc = getPlotBottomLoc(world, id);
        this.bottomLoc = loc;
        this.bottomString = id;
        return loc.getBlockZ();
    }

    @Override
    public void clear(final Location bot, final Location top) {
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(bot.getWorld(), bot, top);
        Main.manager.CURRENT_PLOT_CLEAR = null;
    }

    @Override
    public Long[] clear(final Location bot, final Location top, final long arg2, final Long[] arg3) {
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(bot.getWorld(), bot, top);
        Main.manager.CURRENT_PLOT_CLEAR = null;
        return null;
    }

    @Override
    public Long[] clear(final World world, final String id, final long arg2, final Long[] arg3) {
        final Location bot = getBottom(world, id);
        final Location top = getTop(world, id);
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(bot.getWorld(), bot, top);
        Main.manager.CURRENT_PLOT_CLEAR = null;
        return null;
    }

    @Override
    public boolean createConfig(final String arg0, final Map<String, String> arg1) {
        // NOTHING
        return true;
    }

    @Override
    public void fillmiddleroad(final String arg0, final String arg1, final World world) {
        // NOTHING
    }

    @Override
    public void fillroad(final String arg0, final String arg1, final World world) {
        // NOTHING
    }

    @Override
    public Location getBottom(final World world, final String id) {
        final ChunkLoc loc = getLoc(id);
        final int px = loc.x;
        final int pz = loc.z;

        final HybridPlotWorld plotworld = gPW(world);

        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;

        final int x = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        final int z = (pz * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        return new Location(world, x, 0, z);
    }

    @Override
    public Map<String, String> getDefaultGenerationConfig() {
        // NOTHING
        return null;
    }

    private String lastString = null;
    private ChunkLoc lastId = null;

    @Override
    public int getIdX(final String id) {
        if (id.equals(this.lastString)) {
            return this.lastId.x;
        }
        final ChunkLoc loc = getLoc(id);
        this.lastId = loc;
        this.lastString = id;
        return loc.x;
    }

    @Override
    public int getIdZ(final String id) {
        if (id.equals(this.lastString)) {
            return this.lastId.z;
        }
        final ChunkLoc loc = getLoc(id);
        this.lastId = loc;
        this.lastString = id;
        return loc.z;
    }

    @Override
    public List<Player> getPlayersInPlot(final String id) {
        final List<Player> playersInPlot = new ArrayList<>();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (getPlotId(p).equals(id)) {
                playersInPlot.add(p);
            }
        }
        return playersInPlot;
    }

    @Override
    public Location getPlotBottomLoc(final World world, final String id) {
        final ChunkLoc loc = getLoc(id);
        final int px = loc.x;
        final int pz = loc.z;

        final HybridPlotWorld plotworld = gPW(world);

        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;

        final int x = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        final int z = (pz * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        return new Location(world, x, 0, z);
    }

    @Override
    public Location getPlotHome(final World world, final String id) {
        final HybridPlotWorld plotworld = gPW(world);
        if (plotworld != null) {
            return new Location(world, bottomX(id, world) + ((topX(id, world) - bottomX(id, world)) / 2), plotworld.PLOT_HEIGHT + 2, bottomZ(id, world) - 2);
        } else {
            return world.getSpawnLocation();
        }
    }

    private final Location lastPlotLoc = null;
    private final String lastPlotId = null;

    @Override
    public String getPlotId(final Location loc) {
        if (loc.equals(this.lastPlotLoc)) {
            return this.lastPlotId;
        }
        final HybridPlotWorld plotworld = gPW(loc.getWorld());
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        final int size = plotworld.PLOT_WIDTH + plotworld.ROAD_WIDTH;
        int pathWidthLower;
        if ((plotworld.ROAD_WIDTH % 2) == 0) {
            pathWidthLower = (int) (Math.floor(plotworld.ROAD_WIDTH / 2) - 1);
        } else {
            pathWidthLower = (int) Math.floor(plotworld.ROAD_WIDTH / 2);
        }
        int dx = x / size;
        int dz = z / size;
        if (x < 0) {
            dx--;
            x += ((-dx) * size);
        }
        if (z < 0) {
            dz--;
            z += ((-dz) * size);
        }
        final int rx = (x) % size;
        final int rz = (z) % size;
        final int end = pathWidthLower + plotworld.PLOT_WIDTH;
        final boolean northSouth = (rz <= pathWidthLower) || (rz > end);
        final boolean eastWest = (rx <= pathWidthLower) || (rx > end);
        if (northSouth || eastWest) {
            return "";
        }
        return (dx + 1) + ";" + (dz + 1);
    }

    @Override
    public String getPlotId(final Player player) {
        return getPlotId(player.getLocation());
    }

    @Override
    public int getPlotSize(final String world) {
        final HybridPlotWorld plotworld = gPW(world);
        return plotworld.PLOT_WIDTH;
    }

    @Override
    public Location getPlotTopLoc(final World world, final String id) {
        final ChunkLoc loc = getLoc(id);
        final int px = loc.x;
        final int pz = loc.z;

        final HybridPlotWorld plotworld = gPW(world);

        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;

        final int x = (px * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;
        final int z = (pz * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;

        return new Location(world, x, 256, z);
    }

    @Override
    public int getRoadHeight(final String world) {
        final HybridPlotWorld plotworld = gPW(world);
        return plotworld.ROAD_HEIGHT;
    }

    @Override
    public Location getTop(final World world, final String id) {
        final ChunkLoc loc = getLoc(id);
        final int px = loc.x;
        final int pz = loc.z;

        final HybridPlotWorld plotworld = gPW(world);

        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;

        final int x = (px * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;
        final int z = (pz * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;

        return new Location(world, x, 256, z);
    }

    @Override
    public boolean isBlockInPlot(final String id, final Location loc) {
        if (id.length() == 0) {
            return false;
        }
        return (id.equals(getPlotId(loc)));
    }

    @Override
    public boolean isValidId(final String id) {
        final String[] coords = id.split(";");
        if (coords.length == 2) {
            try {
                Integer.parseInt(coords[0]);
                Integer.parseInt(coords[1]);
                return true;
            } catch (final NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean movePlot(final World world, final String idFrom, final String idTo) {
        final Location plot1Bottom = getPlotBottomLoc(world, idFrom);
        final Location plot2Bottom = getPlotBottomLoc(world, idTo);
        final Location plot1Top = getPlotTopLoc(world, idFrom);
        final Location plot2Top = getPlotTopLoc(world, idTo);

        final int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
        final int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();

        final Collection<Block> lastblocks = new HashSet<>();

        final int bottomX = plot1Bottom.getBlockX();
        final int topX = plot1Top.getBlockX();
        final int bottomZ = plot1Bottom.getBlockZ();
        final int topZ = plot1Top.getBlockZ();
        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block plot1Block = world.getBlockAt(x, 0, z);
                Block plot2Block = world.getBlockAt(x - distanceX, 0, z - distanceZ);

                plot1Block.setBiome(plot2Block.getBiome());
                plot2Block.setBiome(plot1Block.getBiome());
                for (int y = 0; y < 256; y++) {
                    plot1Block = world.getBlockAt(x, y, z);
                    plot2Block = world.getBlockAt(x - distanceX, y, z - distanceZ);
                    if (!PlotMeSettings.blockPlacedLast.contains(Integer.valueOf(plot2Block.getTypeId()))) {
                        plot2Block.setTypeIdAndData(plot1Block.getTypeId(), plot1Block.getData(), false);
                    } else {
                        plot1Block.setType(Material.AIR);
                        lastblocks.add(plot2Block);
                    }
                    if (!PlotMeSettings.blockPlacedLast.contains(Integer.valueOf(plot1Block.getTypeId()))) {
                        plot1Block.setTypeIdAndData(plot2Block.getTypeId(), plot2Block.getData(), false);
                    } else {
                        plot2Block.setType(Material.AIR);
                        lastblocks.add(plot1Block);
                    }
                }
            }
        }
        for (final Block bi : lastblocks) {
            final Block block = bi.getLocation().getBlock();
            bi.setTypeIdAndData(block.getTypeId(), block.getData(), false);
        }
        lastblocks.clear();

        final int minChunkX1 = (int) Math.floor(bottomX / 16);
        final int maxChunkX1 = (int) Math.floor(topX / 16);
        final int minChunkZ1 = (int) Math.floor(bottomZ / 16);
        final int maxChunkZ1 = (int) Math.floor(topZ / 16);

        final int minChunkX2 = (int) Math.floor((bottomX - distanceX) / 16);
        final int maxChunkX2 = (int) Math.floor((topX - distanceX) / 16);
        final int minChunkZ2 = (int) Math.floor((bottomZ - distanceZ) / 16);
        final int maxChunkZ2 = (int) Math.floor((topZ - distanceZ) / 16);

        final Collection<Entity> entities1 = new HashSet<Entity>();
        final Collection<Entity> entities2 = new HashSet<Entity>();
        for (int cx = minChunkX1; cx <= maxChunkX1; cx++) {
            for (int cz = minChunkZ1; cz <= maxChunkZ1; cz++) {
                final Chunk chunk = world.getChunkAt(cx, cz);
                for (final Entity entity : chunk.getEntities()) {
                    final Location location = entity.getLocation();
                    if ((!(entity instanceof Player)) && (location.getBlockX() >= plot1Bottom.getBlockX()) && (location.getBlockX() <= plot1Top.getBlockX()) && (location.getBlockZ() >= plot1Bottom.getBlockZ()) && (location.getBlockZ() <= plot1Top.getBlockZ())) {
                        entities1.add(entity);
                    }
                }
            }
        }
        for (int cx = minChunkX2; cx <= maxChunkX2; cx++) {
            for (int cz = minChunkZ2; cz <= maxChunkZ2; cz++) {
                final Chunk chunk = world.getChunkAt(cx, cz);
                for (final Entity entity : chunk.getEntities()) {
                    final Location location = entity.getLocation();
                    if ((!(entity instanceof Player)) && (location.getBlockX() >= plot2Bottom.getBlockX()) && (location.getBlockX() <= plot2Top.getBlockX()) && (location.getBlockZ() >= plot2Bottom.getBlockZ()) && (location.getBlockZ() <= plot2Top.getBlockZ())) {
                        entities2.add(entity);
                    }
                }
            }
        }
        for (final Entity e : entities1) {
            final Location location = e.getLocation();
            Location newl = new Location(world, location.getX() - distanceX, location.getY(), location.getZ() - distanceZ);
            if (e.getType() == EntityType.ITEM_FRAME) {
                final ItemFrame i = (ItemFrame) e;
                final BlockFace bf = i.getFacing();
                final ItemStack is = i.getItem();
                final Rotation rot = i.getRotation();

                i.teleport(newl);
                i.setItem(is);
                i.setRotation(rot);
                i.setFacingDirection(bf, true);
            } else if (e.getType() == EntityType.PAINTING) {
                final Painting p = (Painting) e;
                final BlockFace bf = p.getFacing();
                final int[] mod = getPaintingMod(p.getArt(), bf);
                newl = newl.add(mod[0], mod[1], mod[2]);
                p.teleport(newl);
                p.setFacingDirection(bf, true);
            } else {
                e.teleport(newl);
            }
        }
        for (final Entity entity : entities2) {
            final Location location = entity.getLocation();
            Location newl = new Location(world, location.getX() + distanceX, location.getY(), location.getZ() + distanceZ);
            if (entity.getType() == EntityType.ITEM_FRAME) {
                final ItemFrame i = (ItemFrame) entity;
                final BlockFace bf = i.getFacing();
                final ItemStack is = i.getItem();
                final Rotation rot = i.getRotation();

                i.teleport(newl);
                i.setItem(is);
                i.setRotation(rot);
                i.setFacingDirection(bf, true);
            } else if (entity.getType() == EntityType.PAINTING) {
                final Painting p = (Painting) entity;
                final BlockFace bf = p.getFacing();
                final int[] mod = getPaintingMod(p.getArt(), bf);
                newl = newl.add(mod[0], mod[1], mod[2]);
                p.teleport(newl);
                p.setFacingDirection(bf, true);
            } else {
                entity.teleport(newl);
            }
        }
        return true;
    }

    @Override
    public void refreshPlotChunks(final World world, final String id) {
        // Nothing
    }

    private Location getSignLocation(final World world, final String id) {
        final HybridPlotWorld plotworld = gPW(world);
        final Location bottom = getPlotBottomLoc(world, id);
        return new Location(world, bottom.getX() - 1, plotworld.PLOT_HEIGHT + 1, bottom.getZ() - 1);
    }

    @Override
    public void removeAuctionDisplay(final World world, final String id) {
        final Location loc = getSignLocation(world, id);
        final Block b = loc.add(-1, 0, 1).getBlock();
        b.setType(Material.AIR);
    }

    @Override
    public void removeOwnerDisplay(final World world, final String id) {
        final Location loc = getSignLocation(world, id);
        final Block b = loc.add(0, 0, -1).getBlock();
        b.setType(Material.AIR);
    }

    @Override
    public void removeSellerDisplay(final World world, final String id) {
        final Location loc = getSignLocation(world, id);
        final Block b = loc.add(-1, 0, 0).getBlock();
        b.setType(Material.AIR);
    }

    @Override
    public void setAuctionDisplay(final World world, final String id, final String line1, final String line2, final String line3, final String line4) {
        final Location loc = getSignLocation(world, id);
        removeSellerDisplay(world, id);
        final Block b = loc.add(-1, 0, -1).getBlock();
        b.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        final Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }

    @Override
    public void setBiome(final World world, final String id, final Biome biome) {
        final int bottomX = bottomX(id, world) - 1;
        final int topX = topX(id, world) + 1;
        final int bottomZ = bottomZ(id, world) - 1;
        final int topZ = topZ(id, world) + 1;
        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                world.getBlockAt(x, 0, z).setBiome(biome);
            }
        }
    }

    @Override
    public void setOwnerDisplay(final World world, final String id, final String line1, final String line2, final String line3, final String line4) {
        final Location loc = getSignLocation(world, id);
        final Block b = loc.clone().add(0, 0, -1).getBlock();
        b.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        final Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }

    @Override
    public void setSellerDisplay(final World world, final String id, final String line1, final String line2, final String line3, final String line4) {
        final Location loc = getSignLocation(world, id);
        removeSellerDisplay(world, id);
        final Block b = loc.add(-1, 0, 0).getBlock();
        b.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        final Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }

    private Location topLoc = null;
    private String topString = null;

    @Override
    public int topX(final String id, final World world) {
        if (id.equals(this.topString)) {
            return this.topLoc.getBlockX();
        }
        final Location loc = getPlotBottomLoc(world, id);
        this.topLoc = loc;
        this.topString = id;
        return loc.getBlockX();
    }

    @Override
    public int topZ(final String id, final World world) {
        if (id.equals(this.topString)) {
            return this.topLoc.getBlockZ();
        }
        final Location loc = getPlotBottomLoc(world, id);
        this.topLoc = loc;
        this.topString = id;
        return loc.getBlockZ();
    }

    private int[] getPaintingMod(final Art a, final BlockFace bf) {
        final int H = a.getBlockHeight();
        final int W = a.getBlockWidth();
        if ((H == 2) && (W == 1)) {
            return new int[] { 0, -1, 0 };
        }
        switch (bf.ordinal()) {
            case 1:
                if (((H == 3) && (W == 4)) || ((H == 1) && (W == 2))) {
                    return new int[] { 0, 0, -1 };
                }
                if (((H == 2) && (W == 2)) || ((H == 4) && (W == 4)) || ((H == 2) && (W == 4))) {
                    return new int[] { 0, -1, -1 };
                }
                break;
            case 2:
                if (((H == 3) && (W == 4)) || ((H == 1) && (W == 2))) {
                    return new int[] { -1, 0, 0 };
                }
                if (((H == 2) && (W == 2)) || ((H == 4) && (W == 4)) || ((H == 2) && (W == 4))) {
                    return new int[] { -1, -1, 0 };
                }
                break;
            case 3:
                if (((H == 2) && (W == 2)) || ((H == 4) && (W == 4)) || ((H == 2) && (W == 4))) {
                    return new int[] { 0, -1, 0 };
                }
                break;
            case 4:
                if (((H == 2) && (W == 2)) || ((H == 4) && (W == 4)) || ((H == 2) && (W == 4))) {
                    return new int[] { 0, -1, 0 };
                }
                break;
            default:
                return new int[] { 0, 0, 0 };
        }
        return new int[] { 0, 0, 0 };
    }
}
