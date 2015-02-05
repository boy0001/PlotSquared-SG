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
    private static final Collection<Integer> blockPlacedLast = new HashSet<>();
    static {
        blockPlacedLast.add(Integer.valueOf(Material.SAPLING.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.BED.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.POWERED_RAIL.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.DETECTOR_RAIL.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.LONG_GRASS.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.DEAD_BUSH.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.PISTON_EXTENSION.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.YELLOW_FLOWER.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.RED_ROSE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.BROWN_MUSHROOM.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.RED_MUSHROOM.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.TORCH.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.FIRE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.REDSTONE_WIRE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.CROPS.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.LADDER.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.RAILS.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.LEVER.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.STONE_PLATE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.WOOD_PLATE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.REDSTONE_TORCH_OFF.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.REDSTONE_TORCH_ON.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.STONE_BUTTON.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.SNOW.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.PORTAL.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.DIODE_BLOCK_OFF.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.DIODE_BLOCK_ON.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.TRAP_DOOR.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.VINE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.WATER_LILY.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.NETHER_WARTS.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.PISTON_BASE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.PISTON_STICKY_BASE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.PISTON_EXTENSION.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.PISTON_MOVING_PIECE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.COCOA.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.TRIPWIRE_HOOK.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.TRIPWIRE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.FLOWER_POT.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.CARROT.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.POTATO.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.WOOD_BUTTON.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.SKULL.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.GOLD_PLATE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.IRON_PLATE.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.REDSTONE_COMPARATOR_OFF.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.REDSTONE_COMPARATOR_ON.getId()));
        blockPlacedLast.add(Integer.valueOf(Material.ACTIVATOR_RAIL.getId()));
    }
    
    public HybridPlotWorld gPW(World world) {
        return gPW(world.getName());
}

    public HybridPlotWorld gPW(String world) {
        return Main.worlds.get(world);
    }
    
    @Override
    public void adjustPlotFor(World world, String id, boolean claimed, boolean protect, boolean auctioned, boolean forSale) {
        HybridPlotWorld plotworld = gPW(world);
        List<PlotBlock> wallIds = new ArrayList<>();
        int roadHeight = plotworld.WALL_HEIGHT;
        PlotBlock claimedId = plotworld.CLAIMED_WALL_BLOCK;
        PlotBlock wallId = plotworld.WALL_BLOCK;
        PlotBlock protectedWallId = plotworld.PROTECTED_WALL_BLOCK;
        PlotBlock auctionWallId = plotworld.AUCTION_WALL_BLOCK;
        PlotBlock forsaleWallId = plotworld.FORSALE_WALL_BLOCK;

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

        Location bottom = getPlotBottomLoc(world, id);
        Location top = getPlotTopLoc(world, id);

        int x;
        int z;

        PlotBlock currentBlockId;
        Block block;

        for (x = bottom.getBlockX() - 1; x < top.getBlockX() + 1; x++) {
            z = bottom.getBlockZ() - 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == wallIds.size() - 1) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }

        for (z = bottom.getBlockZ() - 1; z < top.getBlockZ() + 1; z++) {
            x = top.getBlockX() + 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == wallIds.size() - 1) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }

        for (x = top.getBlockX() + 1; x > bottom.getBlockX() - 1; x--) {
            z = top.getBlockZ() + 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == wallIds.size() - 1) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }

        for (z = top.getBlockZ() + 1; z > bottom.getBlockZ() - 1; z--) {
            x = bottom.getBlockX() - 1;
            currentBlockId = wallIds.get(ctr);
            if (ctr == wallIds.size() - 1) {
                ctr = 0;
            } else {
                ctr += 1;
            }
            block = world.getBlockAt(x, roadHeight + 1, z);
            setWall(block, currentBlockId);
        }
    }
    
    private void setWall(Block block, PlotBlock plotblock) {
        // NOTHING
        block.setTypeIdAndData(plotblock.id, plotblock.data, true);
    }
    
    private Location bottomLoc = null;
    private String bottomString = null;
    
    private ChunkLoc getLoc(String id) {
        String[] split = id.split(";");
        return new ChunkLoc(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
    
    @Override
    public int bottomX(String id, World world) {
        if (id.equals(bottomString)) {
            return bottomLoc.getBlockX();
        }
        Location loc = getPlotBottomLoc(world, id);
        bottomLoc = loc;
        bottomString = id;
        return loc.getBlockX();
    }
    
    @Override
    public int bottomZ(String id, World world) {
        if (id.equals(bottomString)) {
            return bottomLoc.getBlockZ();
        }
        Location loc = getPlotBottomLoc(world, id);
        bottomLoc = loc;
        bottomString = id;
        return loc.getBlockZ();
    }
    
    @Override
    public void clear(Location bot, Location top) {
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(bot.getWorld(), bot, top);
        Main.manager.CURRENT_PLOT_CLEAR = null;
    }
    
    @Override
    public Long[] clear(Location bot, Location top, long arg2, Long[] arg3) {
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(bot.getWorld(), bot, top);
        Main.manager.CURRENT_PLOT_CLEAR = null;
        return null;
    }
    
    @Override
    public Long[] clear(World world, String id, long arg2, Long[] arg3) {
        Location bot = getBottom(world, id);
        Location top = getTop(world, id);
        Main.manager.CURRENT_PLOT_CLEAR = new RegionWrapper(bot.getBlockX(), top.getBlockX(), bot.getBlockZ(), top.getBlockZ());
        Main.manager.clearPlotExperimental(bot.getWorld(), bot, top);
        Main.manager.CURRENT_PLOT_CLEAR = null;
        return null;
    }
    
    @Override
    public boolean createConfig(String arg0, Map<String, String> arg1) {
        // NOTHING
        return true;
    }
    
    @Override
    public void fillmiddleroad(String arg0, String arg1, World world) {
        // NOTHING
    }
    
    @Override
    public void fillroad(String arg0, String arg1, World world) {
        // NOTHING
    }
    
    @Override
    public Location getBottom(World world, String id) {
        ChunkLoc loc = getLoc(id);
        int px = loc.x;
        int pz = loc.z;

        HybridPlotWorld plotworld = gPW(world);
        
        int plotSize = plotworld.PLOT_WIDTH;
        int pathWidth = plotworld.ROAD_WIDTH;

        int x = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        int z = pz * (plotSize + pathWidth) - (plotSize) - ((int) Math.floor(pathWidth / 2));
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
    public int getIdX(String id) {
        if (id.equals(lastString)) {
            return lastId.x;
        }
        ChunkLoc loc = getLoc(id);
        lastId = loc;
        lastString = id;
        return loc.x;
    }
    
    @Override
    public int getIdZ(String id) {
        if (id.equals(lastString)) {
            return lastId.z;
        }
        ChunkLoc loc = getLoc(id);
        lastId = loc;
        lastString = id;
        return loc.z;
    }
    
    @Override
    public List<Player> getPlayersInPlot(String id) {
        List<Player> playersInPlot = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
          if (getPlotId(p).equals(id)) {
            playersInPlot.add(p);
          }
        }
        return playersInPlot;
    }
    
    @Override
    public Location getPlotBottomLoc(World world, String id) {
        ChunkLoc loc = getLoc(id);
        int px = loc.x;
        int pz = loc.z;

        HybridPlotWorld plotworld = gPW(world);
        
        int plotSize = plotworld.PLOT_WIDTH;
        int pathWidth = plotworld.ROAD_WIDTH;

        int x = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        int z = pz * (plotSize + pathWidth) - (plotSize) - ((int) Math.floor(pathWidth / 2));
        return new Location(world, x, 0, z);
    }
    
    @Override
    public Location getPlotHome(World world, String id) {
        HybridPlotWorld plotworld = gPW(world);
        if (plotworld != null) {
            return new Location(world, bottomX(id, world) + (topX(id, world) - bottomX(id, world)) / 2, plotworld.PLOT_HEIGHT + 2, bottomZ(id, world) - 2);
        } else {
            return world.getSpawnLocation();
        }
    }
    
    private Location lastPlotLoc = null;
    private String lastPlotId = null;
    
    @Override
    public String getPlotId(Location loc) {
        if (loc.equals(lastPlotLoc)) {
            return lastPlotId;
        }
        HybridPlotWorld plotworld = gPW(loc.getWorld());
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
    public String getPlotId(Player player) {
        return getPlotId(player.getLocation());
    }
    
    @Override
    public int getPlotSize(String world) {
        HybridPlotWorld plotworld = gPW(world);
        return plotworld.PLOT_WIDTH;
    }
    
    @Override
    public Location getPlotTopLoc(World world, String id) {
        ChunkLoc loc = getLoc(id);
        int px = loc.x;
        int pz = loc.z;

        HybridPlotWorld plotworld = gPW(world);
        
        int plotSize = plotworld.PLOT_WIDTH;
        int pathWidth = plotworld.ROAD_WIDTH;

        int x = px * (plotSize + pathWidth) - ((int) Math.floor(pathWidth / 2)) - 1;
        int z = pz * (plotSize + pathWidth) - ((int) Math.floor(pathWidth / 2)) - 1;

        return new Location(world, x, 256, z);
    }
    
    @Override
    public int getRoadHeight(String world) {
        HybridPlotWorld plotworld = gPW(world);
        return plotworld.ROAD_HEIGHT;
    }
    
    @Override
    public Location getTop(World world, String id) {
        ChunkLoc loc = getLoc(id);
        int px = loc.x;
        int pz = loc.z;

        HybridPlotWorld plotworld = gPW(world);
        
        int plotSize = plotworld.PLOT_WIDTH;
        int pathWidth = plotworld.ROAD_WIDTH;

        int x = px * (plotSize + pathWidth) - ((int) Math.floor(pathWidth / 2)) - 1;
        int z = pz * (plotSize + pathWidth) - ((int) Math.floor(pathWidth / 2)) - 1;

        return new Location(world, x, 256, z);
    }
    
    @Override
    public boolean isBlockInPlot(String id, Location loc) {
        if (id.length() == 0) {
            return false;
        }
        return (id.equals(getPlotId(loc)));
    }
    
    @Override
    public boolean isValidId(String id) {
        String[] coords = id.split(";");
        if (coords.length == 2) {
            try {
                Integer.parseInt(coords[0]);
                Integer.parseInt(coords[1]);
                return true;
            } catch (NumberFormatException e)   {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public boolean movePlot(World world, String idFrom, String idTo) {
        Location plot1Bottom = getPlotBottomLoc(world, idFrom);
        Location plot2Bottom = getPlotBottomLoc(world, idTo);
        Location plot1Top = getPlotTopLoc(world, idFrom);
        Location plot2Top = getPlotTopLoc(world, idTo);
        
        int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
        int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();
        
        Collection<Block> lastblocks = new HashSet<>();
        
        int bottomX = plot1Bottom.getBlockX();
        int topX = plot1Top.getBlockX();
        int bottomZ = plot1Bottom.getBlockZ();
        int topZ = plot1Top.getBlockZ();
        for (int x = bottomX; x <= topX; x++) {
          for (int z = bottomZ; z <= topZ; z++)
          {
            Block plot1Block = world.getBlockAt(x, 0, z);
            Block plot2Block = world.getBlockAt(x - distanceX, 0, z - distanceZ);
            
            plot1Block.setBiome(plot2Block.getBiome());
            plot2Block.setBiome(plot1Block.getBiome());
            for (int y = 0; y < 256; y++)
            {
              plot1Block = world.getBlockAt(x, y, z);
              plot2Block = world.getBlockAt(x - distanceX, y, z - distanceZ);
              if (!blockPlacedLast.contains(Integer.valueOf(plot2Block.getTypeId())))
              {
                plot2Block.setTypeIdAndData(plot1Block.getTypeId(), plot1Block.getData(), false);
              }
              else
              {
                plot1Block.setType(Material.AIR);
                lastblocks.add(plot2Block);
              }
              if (!blockPlacedLast.contains(Integer.valueOf(plot1Block.getTypeId())))
              {
                plot1Block.setTypeIdAndData(plot2Block.getTypeId(), plot2Block.getData(), false);
              }
              else
              {
                plot2Block.setType(Material.AIR);
                lastblocks.add(plot1Block);
              }
            }
          }
        }
        for (Block bi : lastblocks)
        {
          Block block = bi.getLocation().getBlock();
          bi.setTypeIdAndData(block.getTypeId(), block.getData(), false);
        }
        lastblocks.clear();
        

        int minChunkX1 = (int)Math.floor(bottomX / 16);
        int maxChunkX1 = (int)Math.floor(topX / 16);
        int minChunkZ1 = (int)Math.floor(bottomZ / 16);
        int maxChunkZ1 = (int)Math.floor(topZ / 16);
        
        int minChunkX2 = (int)Math.floor((bottomX - distanceX) / 16);
        int maxChunkX2 = (int)Math.floor((topX - distanceX) / 16);
        int minChunkZ2 = (int)Math.floor((bottomZ - distanceZ) / 16);
        int maxChunkZ2 = (int)Math.floor((topZ - distanceZ) / 16);
        
        Collection<Entity> entities1 = new HashSet<Entity>();
        Collection<Entity> entities2 = new HashSet<Entity>();
        for (int cx = minChunkX1; cx <= maxChunkX1; cx++) {
          for (int cz = minChunkZ1; cz <= maxChunkZ1; cz++)
          {
            Chunk chunk = world.getChunkAt(cx, cz);
            for (Entity entity : chunk.getEntities())
            {
              Location location = entity.getLocation();
              if ((!(entity instanceof Player)) && (location.getBlockX() >= plot1Bottom.getBlockX()) && (location.getBlockX() <= plot1Top.getBlockX()) && 
                (location.getBlockZ() >= plot1Bottom.getBlockZ()) && (location.getBlockZ() <= plot1Top.getBlockZ())) {
                entities1.add(entity);
              }
            }
          }
        }
        for (int cx = minChunkX2; cx <= maxChunkX2; cx++) {
          for (int cz = minChunkZ2; cz <= maxChunkZ2; cz++)
          {
            Chunk chunk = world.getChunkAt(cx, cz);
            for (Entity entity : chunk.getEntities())
            {
              Location location = entity.getLocation();
              if ((!(entity instanceof Player)) && (location.getBlockX() >= plot2Bottom.getBlockX()) && (location.getBlockX() <= plot2Top.getBlockX()) && 
                (location.getBlockZ() >= plot2Bottom.getBlockZ()) && (location.getBlockZ() <= plot2Top.getBlockZ())) {
                entities2.add(entity);
              }
            }
          }
        }
        for (Entity e : entities1)
        {
          Location location = e.getLocation();
          Location newl = new Location(world, location.getX() - distanceX, location.getY(), location.getZ() - distanceZ);
          if (e.getType() == EntityType.ITEM_FRAME)
          {
            ItemFrame i = (ItemFrame)e;
            BlockFace bf = i.getFacing();
            ItemStack is = i.getItem();
            Rotation rot = i.getRotation();
            
            i.teleport(newl);
            i.setItem(is);
            i.setRotation(rot);
            i.setFacingDirection(bf, true);
          }
          else if (e.getType() == EntityType.PAINTING)
          {
            Painting p = (Painting)e;
            BlockFace bf = p.getFacing();
            int[] mod = getPaintingMod(p.getArt(), bf);
            newl = newl.add(mod[0], mod[1], mod[2]);
            p.teleport(newl);
            p.setFacingDirection(bf, true);
          }
          else
          {
            e.teleport(newl);
          }
        }
        for (Entity entity : entities2)
        {
          Location location = entity.getLocation();
          Location newl = new Location(world, location.getX() + distanceX, location.getY(), location.getZ() + distanceZ);
          if (entity.getType() == EntityType.ITEM_FRAME)
          {
            ItemFrame i = (ItemFrame)entity;
            BlockFace bf = i.getFacing();
            ItemStack is = i.getItem();
            Rotation rot = i.getRotation();
            
            i.teleport(newl);
            i.setItem(is);
            i.setRotation(rot);
            i.setFacingDirection(bf, true);
          }
          else if (entity.getType() == EntityType.PAINTING)
          {
            Painting p = (Painting)entity;
            BlockFace bf = p.getFacing();
            int[] mod = getPaintingMod(p.getArt(), bf);
            newl = newl.add(mod[0], mod[1], mod[2]);
            p.teleport(newl);
            p.setFacingDirection(bf, true);
          }
          else
          {
            entity.teleport(newl);
          }
        }
        return true;
    }
    
    @Override
    public void refreshPlotChunks(World world, String id) {
        // Nothing
    }
    
    private Location getSignLocation(World world, String id) {
        HybridPlotWorld plotworld = gPW(world);
        Location bottom = getPlotBottomLoc(world, id);
        return new Location(world, bottom.getX() - 1, plotworld.PLOT_HEIGHT + 1, bottom.getZ() - 1);
    }
    
    @Override
    public void removeAuctionDisplay(World world, String id) {
        Location loc = getSignLocation(world, id);
        Block b = loc.add(-1, 0, 1).getBlock();
        b.setType(Material.AIR);
    }
    
    @Override
    public void removeOwnerDisplay(World world, String id) {
        Location loc = getSignLocation(world, id);
        Block b = loc.add(0, 0, -1).getBlock();
        b.setType(Material.AIR);
    }
    
    @Override
    public void removeSellerDisplay(World world, String id) {
        Location loc = getSignLocation(world, id);
        Block b = loc.add(-1, 0, 0).getBlock();
        b.setType(Material.AIR);
    }
    
    @Override
    public void setAuctionDisplay(World world, String id, String line1, String line2, String line3, String line4) {
        Location loc = getSignLocation(world, id);
        removeSellerDisplay(world, id);
        Block b = loc.add(-1, 0, -1).getBlock();
        b.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }
    
    @Override
    public void setBiome(World world, String id, Biome biome) {
        int bottomX = bottomX(id, world) - 1;
        int topX = topX(id, world) + 1;
        int bottomZ = bottomZ(id, world) - 1;
        int topZ = topZ(id, world) + 1;
        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                world.getBlockAt(x, 0, z).setBiome(biome);
            }
        }
    }
    
    @Override
    public void setOwnerDisplay(World world, String id, String line1, String line2, String line3, String line4) {
        Location loc = getSignLocation(world, id);
        Block b = loc.clone().add(0, 0, -1).getBlock();
        b.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }
    
    @Override
    public void setSellerDisplay(World world, String id, String line1, String line2, String line3, String line4) {
        Location loc = getSignLocation(world, id);
        removeSellerDisplay(world, id);
        Block b = loc.add(-1, 0, 0).getBlock();
        b.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
        Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update(true);
    }
    
    private Location topLoc = null;
    private String topString = null;
    
    @Override
    public int topX(String id, World world) {
        if (id.equals(topString)) {
            return topLoc.getBlockX();
        }
        Location loc = getPlotBottomLoc(world, id);
        topLoc = loc;
        topString = id;
        return loc.getBlockX();
    }
    
    @Override
    public int topZ(String id, World world) {
        if (id.equals(topString)) {
            return topLoc.getBlockZ();
        }
        Location loc = getPlotBottomLoc(world, id);
        topLoc = loc;
        topString = id;
        return loc.getBlockZ();
    }
    
    private static int[] getPaintingMod(Art a, BlockFace bf)
    {
      int H = a.getBlockHeight();
      int W = a.getBlockWidth();
      if ((H == 2) && (W == 1)) {
        return new int[] { 0, -1, 0 };
      }
      switch (bf.ordinal())
      {
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
