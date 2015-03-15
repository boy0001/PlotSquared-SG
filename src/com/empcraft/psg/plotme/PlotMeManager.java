package com.empcraft.psg.plotme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
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
import com.empcraft.psg.object.PlotBlock;
import com.empcraft.psg.object.RegionWrapper;
import com.worldcretornica.plotme_core.PlotId;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.api.IBlock;
import com.worldcretornica.plotme_core.api.ILocation;
import com.worldcretornica.plotme_core.api.IPlayer;
import com.worldcretornica.plotme_core.api.IPlotMe_GeneratorManager;
import com.worldcretornica.plotme_core.api.IWorld;
import com.worldcretornica.plotme_core.bukkit.PlotMe_CorePlugin;
import com.worldcretornica.plotme_core.bukkit.api.BukkitBlock;
import com.worldcretornica.plotme_core.bukkit.api.BukkitLocation;
import com.worldcretornica.schematic.Schematic;

@SuppressWarnings("deprecation")
public class PlotMeManager implements IPlotMe_GeneratorManager {
    
    private static PlotMe_Core core;
    private static PlotMe_CorePlugin corePl;
    private static PlotMeManager THIS;
    
    public PlotMeManager() {
        Main.pmm = this;
        THIS = this;
    }
    
    public static void register(final String world) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        corePl = (PlotMe_CorePlugin) Main.plotme;
        core = corePl.getAPI();
        core.addManager(world, THIS);
    }
    
    public HybridPlotWorld gPW(final IWorld world) {
        return gPW(world.getName());
    }
    
    public HybridPlotWorld gPW(final String world) {
        return Main.worlds.get(world);
    }
    
    @Override
    public void adjustPlotFor(final IWorld iworld, final PlotId id, final boolean claimed, final boolean protect, final boolean auctioned, final boolean forSale) {
        final World world = Bukkit.getWorld(iworld.getName());
        final HybridPlotWorld plotworld = gPW(world.getName());
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
        
        final ILocation bottom = getPlotBottomLoc(iworld, id);
        final ILocation top = getPlotTopLoc(iworld, id);
        
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
    
    @Override
    public int bottomX(final PlotId id, final IWorld world) {
        final ILocation loc = getPlotBottomLoc(world, id);
        return loc.getBlockX();
    }
    
    @Override
    public int bottomZ(final PlotId id, final IWorld world) {
        final ILocation loc = getPlotBottomLoc(world, id);
        return loc.getBlockZ();
    }
    
    public Location getLocation(final ILocation loc) {
        final World world = Bukkit.getWorld(loc.getWorld().getName());
        return new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public ILocation getLocation(final Location loc) {
        return new BukkitLocation(loc);
    }
    
    @Override
    public void clear(final ILocation bot, final ILocation top) {
        final World world = Bukkit.getWorld(bot.getWorld().getName());
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(world, getLocation(bot), getLocation(top));
        Main.manager.CURRENT_PLOT_CLEAR = null;
    }
    
    //    @Override
    //    public Long[] clear(final ILocation bot, final ILocation top, final long arg2, final Long[] arg3) {
    //        clear(bot, top);
    //        return null;
    //    }
    
    @Override
    public Long[] clear(final IWorld world, final PlotId id, final long arg2, final Long[] arg3) {
        final ILocation bot = getBottom(world, id);
        final ILocation top = getTop(world, id);
        clear(bot, top);
        return null;
    }
    
    public Location getLocation(final IWorld iworld, final int x, final int y, final int z) {
        final World world = Bukkit.getWorld(iworld.getName());
        return new Location(world, x, y, z);
    }
    
    @Override
    public ILocation getBottom(final IWorld iworld, final PlotId id) {
        final int px = id.getX();
        final int pz = id.getZ();
        
        final HybridPlotWorld plotworld = gPW(iworld);
        
        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;
        
        System.out.print(plotSize);
        
        final int x = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        final int z = (pz * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        return new BukkitLocation(getLocation(iworld, x, 0, z));
    }
    
    @Override
    public List<IPlayer> getPlayersInPlot(final PlotId id) {
        final List<IPlayer> playersInPlot = new ArrayList<>();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (getPlotId(p).equals(id)) {
                playersInPlot.add(corePl.wrapPlayer(p));
            }
        }
        return playersInPlot;
    }
    
    @Override
    public ILocation getPlotBottomLoc(final IWorld iworld, final PlotId id) {
        final int px = id.getX();
        final int pz = id.getZ();
        
        final HybridPlotWorld plotworld = gPW(iworld);
        
        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;
        
        final int x = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        final int z = (pz * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        return new BukkitLocation(getLocation(iworld, x, 0, z));
    }
    
    @Override
    public ILocation getPlotHome(final IWorld iworld, final PlotId id) {
        final HybridPlotWorld plotworld = gPW(iworld);
        if (plotworld != null) {
            return new BukkitLocation(getLocation(iworld, bottomX(id, iworld) + ((topX(id, iworld) - bottomX(id, iworld)) / 2), plotworld.PLOT_HEIGHT + 2, bottomZ(id, iworld) - 2));
        } else {
            return getLocation(Bukkit.getWorld(iworld.getName()).getSpawnLocation());
        }
    }
    
    @Override
    public PlotId getPlotId(final ILocation loc) {
        
        System.out.print(1);
        
        return getPlotId(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ());
    }
    
    public PlotId getPlotId(final String world, int x, int z) {
        final HybridPlotWorld plotworld = gPW(world);
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
            return null;
        }
        return new PlotId((dx + 1), (dz + 1));
    }
    
    @Override
    public PlotId getPlotId(final IPlayer player) {
        return getPlotId(player.getLocation());
    }
    
    public PlotId getPlotId(final Player player) {
        System.out.print(2);
        return getPlotId(getLocation(player.getLocation()));
    }
    
    //    @Override
    //    public int getPlotSize(final String world) {
    //        final HybridPlotWorld plotworld = gPW(world);
    //        return plotworld.PLOT_WIDTH;
    //    }
    
    @Override
    public ILocation getPlotTopLoc(final IWorld iworld, final PlotId id) {
        
        final int px = id.getX();
        final int pz = id.getZ();
        
        final HybridPlotWorld plotworld = gPW(iworld);
        
        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;
        
        final int x = (px * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;
        final int z = (pz * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;
        
        return getLocation(getLocation(iworld, x, 256, z));
    }
    
    //    @Override
    //    public int getRoadHeight(final String world) {
    //        final HybridPlotWorld plotworld = gPW(world);
    //        return plotworld.ROAD_HEIGHT;
    //    }
    
    @Override
    public ILocation getTop(final IWorld iworld, final PlotId id) {
        
        final int px = id.getX();
        final int pz = id.getZ();
        
        final HybridPlotWorld plotworld = gPW(iworld);
        
        final int plotSize = plotworld.PLOT_WIDTH;
        final int pathWidth = plotworld.ROAD_WIDTH;
        
        System.out.print(plotSize);
        
        final int x = (px * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;
        final int z = (pz * (plotSize + pathWidth)) - ((int) Math.floor(pathWidth / 2)) - 1;
        
        return new PLocation(iworld, x, 256, z);
    }
    
    public class PLocation implements ILocation {
        
        public double x;
        public double y;
        public double z;
        public IWorld world;
        
        private Location location = null;
        
        private Location getLocation() {
            this.location = new Location(Bukkit.getWorld(this.world.getName()), this.x, this.y, this.z);
            return this.location;
        }
        
        public PLocation(final IWorld world, final double x, final double y, final double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
        }
        
        @Override
        public ILocation add(final double x, final double y, final double z) {
            return new PLocation(this.world, x + this.x, y + this.y, z + this.z);
        }
        
        @Override
        public IBlock getBlock() {
            return new BukkitBlock(getLocation().getBlock());
        }
        
        @Override
        public int getBlockX() {
            return (int) this.x;
        }
        
        @Override
        public int getBlockY() {
            return (int) this.y;
        }
        
        @Override
        public int getBlockZ() {
            return (int) this.z;
        }
        
        @Override
        public IWorld getWorld() {
            return this.world;
        }
        
        @Override
        public double getX() {
            return this.x;
        }
        
        @Override
        public double getY() {
            return this.y;
        }
        
        @Override
        public double getZ() {
            return this.z;
        }
        
        @Override
        public void setX(final double arg0) {
            this.x = arg0;
        }
        
        @Override
        public void setY(final double arg0) {
            this.y = arg0;
        }
        
        @Override
        public void setZ(final double arg0) {
            this.z = arg0;
        }
        
        @Override
        public ILocation subtract(final double arg0, final double arg1, final double arg2) {
            return new PLocation(this.world, -this.x + this.x, -this.y + this.y, -this.z + this.z);
        }
    }
    
    @Override
    public boolean isBlockInPlot(final PlotId id, final ILocation loc) {
        return (id.equals(getPlotId(loc)));
    }
    
    @Override
    public boolean movePlot(final IWorld iworld, final PlotId idFrom, final PlotId idTo) {
        final ILocation plot1Bottom = getPlotBottomLoc(iworld, idFrom);
        final ILocation plot2Bottom = getPlotBottomLoc(iworld, idTo);
        final ILocation plot1Top = getPlotTopLoc(iworld, idFrom);
        final ILocation plot2Top = getPlotTopLoc(iworld, idTo);
        
        final World world = Bukkit.getWorld(iworld.getName());
        
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
    public void refreshPlotChunks(final IWorld iworld, final PlotId id) {
        // Nothing
    }
    
    private ILocation getSignLocation(final IWorld iworld, final PlotId id) {
        final HybridPlotWorld plotworld = gPW(iworld);
        final ILocation bottom = getPlotBottomLoc(iworld, id);
        return new PLocation(iworld, bottom.getX() - 1, plotworld.PLOT_HEIGHT + 1, bottom.getZ() - 1);
    }
    
    @Override
    public void removeAuctionDisplay(final IWorld iworld, final PlotId id) {
        final ILocation loc = getSignLocation(iworld, id);
        final IBlock b = loc.add(-1, 0, 1).getBlock();
        b.setTypeId(0, false);
    }
    
    @Override
    public void removeOwnerDisplay(final IWorld iworld, final PlotId id) {
        final ILocation loc = getSignLocation(iworld, id);
        final IBlock b = loc.add(0, 0, -1).getBlock();
        b.setTypeId(0, false);
    }
    
    @Override
    public void removeSellerDisplay(final IWorld iworld, final PlotId id) {
        final ILocation loc = getSignLocation(iworld, id);
        final IBlock b = loc.add(-1, 0, 0).getBlock();
        b.setTypeId(0, false);
    }
    
    //    @Override
    //    public void setAuctionDisplay(final IWorld iworld, final PlotId id, final String line1, final String line2, final String line3, final String line4) {
    //        final ILocation loc = getSignLocation(iworld, id);
    //        Block block = ((PLocation) loc).getLocation().getBlock();
    //        removeSellerDisplay(iworld, id);
    //        block.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
    //        final Sign sign = (Sign) block.getState();
    //        sign.setLine(0, line1);
    //        sign.setLine(1, line2);
    //        sign.setLine(2, line3);
    //        sign.setLine(3, line4);
    //        sign.update(true);
    //    }
    
    //    @Override
    //    public void setBiome(final IWorld iworld, final PlotId id, final IBiome biome) {
    //        World world = Bukkit.getWorld(iworld.getName());
    //        final int bottomX = bottomX(id, world) - 1;
    //        final int topX = topX(id, world) + 1;
    //        final int bottomZ = bottomZ(id, world) - 1;
    //        final int topZ = topZ(id, world) + 1;
    //        for (int x = bottomX; x <= topX; x++) {
    //            for (int z = bottomZ; z <= topZ; z++) {
    //                world.getBlockAt(x, 0, z).setBiome(biome);
    //            }
    //        }
    //    }
    
    @Override
    public void setOwnerDisplay(final IWorld iworld, final PlotId id, final String line1, final String line2, final String line3, final String line4) {
        final ILocation loc = getSignLocation(iworld, id);
        final Block block = ((PLocation) loc.add(0, 0, -1)).getLocation().getBlock();
        removeSellerDisplay(iworld, id);
        block.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        final Sign sign = (Sign) block.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }
    
    @Override
    public void setSellerDisplay(final IWorld iworld, final PlotId id, final String line1, final String line2, final String line3, final String line4) {
        final ILocation loc = getSignLocation(iworld, id);
        final Block block = ((PLocation) loc.add(-1, 0, 0)).getLocation().getBlock();
        removeSellerDisplay(iworld, id);
        block.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        final Sign sign = (Sign) block.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }
    
    @Override
    public int topX(final PlotId id, final IWorld iworld) {
        final ILocation loc = getPlotBottomLoc(iworld, id);
        return loc.getBlockX();
    }
    
    @Override
    public int topZ(final PlotId id, final IWorld iworld) {
        final ILocation loc = getPlotBottomLoc(iworld, id);
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
    
    @Override
    public ILocation getPlotMiddle(final IWorld world, final PlotId id) {
        final ILocation bottom = getPlotBottomLoc(world, id);
        final ILocation top = getPlotTopLoc(world, id);
        
        final double x = (top.getX() + bottom.getX() + 1) / 2;
        final double y = getRoadHeight() + 1;
        final double z = (top.getZ() + bottom.getZ() + 1) / 2;
        
        return new BukkitLocation(new Location(Bukkit.getWorld(world.getName()), x, y, z));
    }
    
    @Override
    //(final IWorld iworld, final PlotId id, final boolean claimed, final boolean protect, final boolean auctioned, final boolean forSale)
    public void adjustPlotFor(final IWorld arg0, final PlotId arg1, final boolean arg2, final boolean arg3, final boolean arg4) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void fillMiddleRoad(final PlotId arg0, final PlotId arg1, final IWorld arg2) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void fillRoad(final PlotId arg0, final PlotId arg1, final IWorld arg2) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Schematic getPlotSchematic(final IWorld arg0, final PlotId arg1) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getPlotSize() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getRoadHeight() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    //    @Override
    //    public Schematic getPlotSchematic(IWorld arg0, String arg1) {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
}
