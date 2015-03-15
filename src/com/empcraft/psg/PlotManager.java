package com.empcraft.psg;

import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import com.empcraft.psg.object.BlockLoc;
import com.empcraft.psg.object.ChunkLoc;
import com.empcraft.psg.object.RegionWrapper;

public class PlotManager {
    public RegionWrapper CURRENT_PLOT_CLEAR = null;
    public HashMap<ChunkLoc, HashMap<Short, Short>> GENERATE_BLOCKS = new HashMap<>();
    public HashMap<ChunkLoc, HashMap<Short, Byte>> GENERATE_DATA = new HashMap<>();
    
    public boolean clearPlotExperimental(final World world, final Location pos1, final Location pos2) {
        final Chunk c1 = world.getChunkAt(pos1);
        final Chunk c2 = world.getChunkAt(pos2);
        
        this.CURRENT_PLOT_CLEAR = new RegionWrapper(pos1.getBlockX(), pos2.getBlockX(), pos1.getBlockZ(), pos2.getBlockZ());
        
        final int sx = pos1.getBlockX();
        final int sz = pos1.getBlockZ();
        final int ex = pos2.getBlockX();
        final int ez = pos2.getBlockZ();
        
        final int c1x = c1.getX();
        final int c1z = c1.getZ();
        final int c2x = c2.getX();
        final int c2z = c2.getZ();
        
        final int maxY = world.getMaxHeight();
        
        for (int x = c1x; x <= c2x; x++) {
            for (int z = c1z; z <= c2z; z++) {
                
                final Chunk chunk = world.getChunkAt(x, z);
                
                boolean loaded = true;
                
                if (!chunk.isLoaded()) {
                    final boolean result = chunk.load(false);
                    if (!result) {
                        loaded = false;
                        ;
                    }
                    if (!chunk.isLoaded()) {
                        loaded = false;
                    }
                }
                
                if (loaded) {
                    final int absX = x << 4;
                    final int absZ = z << 4;
                    
                    this.GENERATE_BLOCKS = new HashMap<>();
                    this.GENERATE_DATA = new HashMap<>();
                    
                    final HashMap<BlockLoc, ItemStack[]> chestContents = new HashMap<>();
                    final HashMap<BlockLoc, ItemStack[]> furnaceContents = new HashMap<>();
                    final HashMap<BlockLoc, ItemStack[]> dispenserContents = new HashMap<>();
                    final HashMap<BlockLoc, ItemStack[]> brewingStandContents = new HashMap<>();
                    final HashMap<BlockLoc, ItemStack[]> beaconContents = new HashMap<>();
                    final HashMap<BlockLoc, ItemStack[]> hopperContents = new HashMap<>();
                    final HashMap<BlockLoc, Note> noteBlockContents = new HashMap<>();
                    final HashMap<BlockLoc, String[]> signContents = new HashMap<>();
                    
                    if ((x == c1x) || (z == c1z)) {
                        for (int X = 0; X < 16; X++) {
                            for (int Z = 0; Z < 16; Z++) {
                                if ((((X + absX) < sx) || ((Z + absZ) < sz)) || (((X + absX) > ex) || ((Z + absZ) > ez))) {
                                    final HashMap<Short, Short> ids = new HashMap<>();
                                    final HashMap<Short, Byte> datas = new HashMap<>();
                                    for (short y = 1; y < maxY; y++) {
                                        final Block block = world.getBlockAt(X + absX, y, Z + absZ);
                                        final short id = (short) block.getTypeId();
                                        if (id != 0) {
                                            ids.put(y, id);
                                            final byte data = block.getData();
                                            if (data != 0) {
                                                datas.put(y, data);
                                            }
                                            BlockLoc bl;
                                            switch (id) {
                                                case 54:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Chest chest = (Chest) block.getState();
                                                    final ItemStack[] inventory = chest.getBlockInventory().getContents().clone();
                                                    chestContents.put(bl, inventory);
                                                    break;
                                                case 63:
                                                case 68:
                                                case 323:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Sign sign = (Sign) block.getState();
                                                    sign.getLines();
                                                    signContents.put(bl, sign.getLines().clone());
                                                    break;
                                                case 61:
                                                case 62:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Furnace furnace = (Furnace) block.getState();
                                                    final ItemStack[] invFur = furnace.getInventory().getContents().clone();
                                                    furnaceContents.put(bl, invFur);
                                                    break;
                                                case 23:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Dispenser dispenser = (Dispenser) block.getState();
                                                    final ItemStack[] invDis = dispenser.getInventory().getContents().clone();
                                                    dispenserContents.put(bl, invDis);
                                                    break;
                                                case 117:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final BrewingStand brewingStand = (BrewingStand) block.getState();
                                                    final ItemStack[] invBre = brewingStand.getInventory().getContents().clone();
                                                    brewingStandContents.put(bl, invBre);
                                                    break;
                                                case 25:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final NoteBlock noteBlock = (NoteBlock) block.getState();
                                                    final Note note = noteBlock.getNote();
                                                    noteBlockContents.put(bl, note);
                                                    break;
                                                case 138:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Beacon beacon = (Beacon) block.getState();
                                                    final ItemStack[] invBea = beacon.getInventory().getContents().clone();
                                                    beaconContents.put(bl, invBea);
                                                    break;
                                                case 154:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Hopper hopper = (Hopper) block.getState();
                                                    final ItemStack[] invHop = hopper.getInventory().getContents().clone();
                                                    hopperContents.put(bl, invHop);
                                                    break;
                                            }
                                        }
                                    }
                                    final ChunkLoc loc = new ChunkLoc(X + absX, Z + absZ);
                                    this.GENERATE_BLOCKS.put(loc, ids);
                                    this.GENERATE_DATA.put(loc, datas);
                                }
                            }
                        }
                    } else if ((x == c2x) || (z == c2z)) {
                        for (int X = 0; X < 16; X++) {
                            for (int Z = 0; Z < 16; Z++) {
                                if ((((X + absX) > ex) || ((Z + absZ) > ez)) || (((X + absX) < sx) || ((Z + absZ) < sz))) {
                                    final HashMap<Short, Short> ids = new HashMap<>();
                                    final HashMap<Short, Byte> datas = new HashMap<>();
                                    for (short y = 1; y < maxY; y++) {
                                        final Block block = world.getBlockAt(X + absX, y, Z + absZ);
                                        final short id = (short) block.getTypeId();
                                        if (id != 0) {
                                            ids.put(y, id);
                                            final byte data = block.getData();
                                            if (data != 0) {
                                                datas.put(y, data);
                                            }
                                            BlockLoc bl;
                                            switch (id) {
                                                case 54:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Chest chest = (Chest) block.getState();
                                                    final ItemStack[] inventory = chest.getBlockInventory().getContents().clone();
                                                    chestContents.put(bl, inventory);
                                                    break;
                                                case 63:
                                                case 68:
                                                case 323:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Sign sign = (Sign) block.getState();
                                                    sign.getLines();
                                                    signContents.put(bl, sign.getLines().clone());
                                                    break;
                                                case 61:
                                                case 62:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Furnace furnace = (Furnace) block.getState();
                                                    final ItemStack[] invFur = furnace.getInventory().getContents().clone();
                                                    furnaceContents.put(bl, invFur);
                                                    break;
                                                case 23:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Dispenser dispenser = (Dispenser) block.getState();
                                                    final ItemStack[] invDis = dispenser.getInventory().getContents().clone();
                                                    dispenserContents.put(bl, invDis);
                                                    break;
                                                case 117:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final BrewingStand brewingStand = (BrewingStand) block.getState();
                                                    final ItemStack[] invBre = brewingStand.getInventory().getContents().clone();
                                                    brewingStandContents.put(bl, invBre);
                                                    break;
                                                case 25:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final NoteBlock noteBlock = (NoteBlock) block.getState();
                                                    final Note note = noteBlock.getNote();
                                                    noteBlockContents.put(bl, note);
                                                    break;
                                                case 138:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Beacon beacon = (Beacon) block.getState();
                                                    final ItemStack[] invBea = beacon.getInventory().getContents().clone();
                                                    beaconContents.put(bl, invBea);
                                                    break;
                                                case 154:
                                                    bl = new BlockLoc(X + absX, y, Z + absZ);
                                                    final Hopper hopper = (Hopper) block.getState();
                                                    final ItemStack[] invHop = hopper.getInventory().getContents().clone();
                                                    hopperContents.put(bl, invHop);
                                                    break;
                                            }
                                        }
                                    }
                                    final ChunkLoc loc = new ChunkLoc(X + absX, Z + absZ);
                                    this.GENERATE_BLOCKS.put(loc, ids);
                                    this.GENERATE_DATA.put(loc, datas);
                                }
                            }
                        }
                    }
                    world.regenerateChunk(x, z);
                    
                    for (final BlockLoc loc : chestContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof Chest) {
                            final Chest chest = (Chest) state;
                            chest.getInventory().setContents(chestContents.get(loc));
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : signContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof Sign) {
                            final Sign sign = (Sign) state;
                            int i = 0;
                            for (final String line : signContents.get(loc)) {
                                sign.setLine(i, line);
                                i++;
                            }
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : dispenserContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof Dispenser) {
                            ((Dispenser) (state)).getInventory().setContents(dispenserContents.get(loc));
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : beaconContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof Beacon) {
                            ((Beacon) (state)).getInventory().setContents(beaconContents.get(loc));
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : hopperContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof Hopper) {
                            ((Hopper) (state)).getInventory().setContents(hopperContents.get(loc));
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : noteBlockContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof NoteBlock) {
                            ((NoteBlock) (state)).setNote(noteBlockContents.get(loc));
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : brewingStandContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof BrewingStand) {
                            ((BrewingStand) (state)).getInventory().setContents(brewingStandContents.get(loc));
                            state.update(true);
                        }
                    }
                    
                    for (final BlockLoc loc : furnaceContents.keySet()) {
                        final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
                        final BlockState state = block.getState();
                        if (state instanceof Furnace) {
                            ((Furnace) (state)).getInventory().setContents(furnaceContents.get(loc));
                            state.update(true);
                        }
                    }
                    chunk.unload();
                    chunk.load();
                }
            }
        }
        
        this.CURRENT_PLOT_CLEAR = null;
        
        return true;
    }
    
}
