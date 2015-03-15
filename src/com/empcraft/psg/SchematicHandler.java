////////////////////////////////////////////////////////////////////////////////////////////////////
// PlotSquared - A plot manager and world generator for the Bukkit API                             /
// Copyright (c) 2014 IntellectualSites/IntellectualCrafters                                       /
//                                                                                                 /
// This program is free software; you can redistribute it and/or modify                            /
// it under the terms of the GNU General Public License as published by                            /
// the Free Software Foundation; either version 3 of the License, or                               /
// (at your option) any later version.                                                             /
//                                                                                                 /
// This program is distributed in the hope that it will be useful,                                 /
// but WITHOUT ANY WARRANTY; without even the implied warranty of                                  /
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                   /
// GNU General Public License for more details.                                                    /
//                                                                                                 /
// You should have received a copy of the GNU General Public License                               /
// along with this program; if not, write to the Free Software Foundation,                         /
// Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA                               /
//                                                                                                 /
// You can contact us via: support@intellectualsites.com                                           /
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.empcraft.psg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.empcraft.psg.jnbt.ByteArrayTag;
import com.empcraft.psg.jnbt.CompoundTag;
import com.empcraft.psg.jnbt.IntTag;
import com.empcraft.psg.jnbt.ListTag;
import com.empcraft.psg.jnbt.NBTInputStream;
import com.empcraft.psg.jnbt.NBTOutputStream;
import com.empcraft.psg.jnbt.ShortTag;
import com.empcraft.psg.jnbt.StringTag;
import com.empcraft.psg.jnbt.Tag;

/**
 * Schematic Handler
 *
 * @author Citymonstret
 * @author Empire92
 */
public class SchematicHandler {
    public static Schematic getSchematic(final CompoundTag tag, final File file) {
        final Map<String, Tag> tagMap = tag.getValue();
        
        byte[] addId = new byte[0];
        if (tagMap.containsKey("AddBlocks")) {
            addId = ByteArrayTag.class.cast(tagMap.get("AddBlocks")).getValue();
        }
        final short width = ShortTag.class.cast(tagMap.get("Width")).getValue();
        final short length = ShortTag.class.cast(tagMap.get("Length")).getValue();
        final short height = ShortTag.class.cast(tagMap.get("Height")).getValue();
        
        final byte[] b = ByteArrayTag.class.cast(tagMap.get("Blocks")).getValue();
        final byte[] d = ByteArrayTag.class.cast(tagMap.get("Data")).getValue();
        final short[] blocks = new short[b.length];
        
        final Dimension dimension = new Dimension(width, height, length);
        
        for (int index = 0; index < b.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding
                // AddBlocks index
                blocks[index] = (short) (b[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (b[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (b[index] & 0xFF));
                }
            }
        }
        
        final DataCollection[] collection = new DataCollection[b.length];
        
        for (int x = 0; x < b.length; x++) {
            collection[x] = new DataCollection(blocks[x], d[x]);
        }
        return new Schematic(collection, dimension, file);
    }
    
    /**
     * Get a schematic
     *
     * @param name to check
     *
     * @return schematic if found, else null
     */
    public static Schematic getSchematic(final String string) {
        final File parent = new File(Main.datafolder + "schematics");
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                throw new RuntimeException("Could not create schematic parent directory");
            }
        }
        final File file = new File(Main.datafolder + "schematics" + File.separator + string + ".schematic");
        if (!file.exists()) {
            MainUtil.sendMessage(file.toString() + " doesn't exist");
            return null;
        }
        try {
            final InputStream iStream = new FileInputStream(file);
            final NBTInputStream stream = new NBTInputStream(new GZIPInputStream(iStream));
            final CompoundTag tag = (CompoundTag) stream.readTag();
            stream.close();
            return getSchematic(tag, file);
            
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Saves a schematic to a file path
     *
     * @param tag  to save
     * @param path to save in
     *
     * @return true if succeeded
     */
    public static boolean save(final CompoundTag tag, final String path) {
        if (tag == null) {
            MainUtil.sendMessage("&cCannot save empty tag");
            return false;
        }
        try {
            final File tmp = new File(path);
            tmp.getParentFile().mkdirs();
            final OutputStream stream = new FileOutputStream(path);
            final NBTOutputStream output = new NBTOutputStream(new GZIPOutputStream(stream));
            output.writeTag(tag);
            output.close();
            stream.close();
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
    public static CompoundTag getCompoundTag(final World world, final Location pos1, final Location pos2) {
        // loading chunks
        int i = 0;
        int j = 0;
        try {
            for (i = (pos1.getBlockX() / 16) * 16; i < (16 + ((pos2.getBlockX() / 16) * 16)); i += 16) {
                for (j = (pos1.getBlockZ() / 16) * 16; j < (16 + ((pos2.getBlockZ() / 16) * 16)); j += 16) {
                    final Chunk chunk = world.getChunkAt(i, j);
                    final boolean result = chunk.load(false);
                    if (!result) {
                        
                        // Plot is not even generated
                        
                        return null;
                    }
                }
            }
        } catch (final Exception e) {
            MainUtil.sendMessage("&7 - Cannot save: corrupt chunk at " + (i / 16) + ", " + (j / 16));
            return null;
        }
        final int width = (pos2.getBlockX() - pos1.getBlockX()) + 1;
        final int height = (pos2.getBlockY() - pos1.getBlockY()) + 1;
        final int length = (pos2.getBlockZ() - pos1.getBlockZ()) + 1;
        
        final HashMap<String, Tag> schematic = new HashMap<>();
        schematic.put("Width", new ShortTag("Width", (short) width));
        schematic.put("Length", new ShortTag("Length", (short) length));
        schematic.put("Height", new ShortTag("Height", (short) height));
        schematic.put("Materials", new StringTag("Materials", "Alpha"));
        schematic.put("WEOriginX", new IntTag("WEOriginX", 0));
        schematic.put("WEOriginY", new IntTag("WEOriginY", 0));
        schematic.put("WEOriginZ", new IntTag("WEOriginZ", 0));
        schematic.put("WEOffsetX", new IntTag("WEOffsetX", 0));
        schematic.put("WEOffsetY", new IntTag("WEOffsetY", 0));
        schematic.put("WEOffsetZ", new IntTag("WEOffsetZ", 0));
        final byte[] blocks = new byte[width * height * length];
        byte[] addBlocks = null;
        final byte[] blockData = new byte[width * height * length];
        
        final int sx = pos1.getBlockX();
        pos2.getBlockX();
        
        final int sz = pos1.getBlockZ();
        pos2.getBlockZ();
        
        final int sy = pos1.getBlockY();
        pos2.getBlockY();
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    final int index = (y * width * length) + (z * width) + x;
                    
                    final Block block = world.getBlockAt(new Location(world, sx + x, sy + y, sz + z));
                    
                    @SuppressWarnings("deprecation")
                    final int id2 = block.getTypeId();
                    
                    if (id2 > 255) {
                        if (addBlocks == null) {
                            addBlocks = new byte[(blocks.length >> 1) + 1];
                        }
                        
                        addBlocks[index >> 1] = (byte) (((index & 1) == 0) ? (addBlocks[index >> 1] & 0xF0) | ((id2 >> 8) & 0xF) : (addBlocks[index >> 1] & 0xF) | (((id2 >> 8) & 0xF) << 4));
                    }
                    
                    blocks[index] = (byte) id2;
                    blockData[index] = block.getData();
                    
                    // We need worldedit to save tileentity data or entities
                    // - it uses NMS and CB internal code, which changes every
                    // update
                }
            }
        }
        schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
        schematic.put("Data", new ByteArrayTag("Data", blockData));
        schematic.put("Entities", new ListTag("Entities", CompoundTag.class, new ArrayList<Tag>()));
        schematic.put("TileEntities", new ListTag("TileEntities", CompoundTag.class, new ArrayList<Tag>()));
        
        if (addBlocks != null) {
            schematic.put("AddBlocks", new ByteArrayTag("AddBlocks", addBlocks));
        }
        
        return new CompoundTag("Schematic", schematic);
    }
    
    /**
     * Schematic Class
     *
     * @author Citymonstret
     */
    public static class Schematic {
        private final DataCollection[] blockCollection;
        private final Dimension schematicDimension;
        private final File file;
        
        public Schematic(final DataCollection[] blockCollection, final Dimension schematicDimension, final File file) {
            this.blockCollection = blockCollection;
            this.schematicDimension = schematicDimension;
            this.file = file;
        }
        
        public File getFile() {
            return this.file;
        }
        
        public Dimension getSchematicDimension() {
            return this.schematicDimension;
        }
        
        public DataCollection[] getBlockCollection() {
            return this.blockCollection;
        }
    }
    
    /**
     * Schematic Dimensions
     *
     * @author Citymonstret
     */
    public static class Dimension {
        private final int x;
        private final int y;
        private final int z;
        
        public Dimension(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public int getZ() {
            return this.z;
        }
    }
    
    /**
     * Schematic Data Collection
     *
     * @author Citymonstret
     */
    public static class DataCollection {
        private final short block;
        private final byte data;
        
        // public CompoundTag tag;
        
        public DataCollection(final short block, final byte data) {
            this.block = block;
            this.data = data;
        }
        
        public short getBlock() {
            return this.block;
        }
        
        public byte getData() {
            return this.data;
        }
    }
}
