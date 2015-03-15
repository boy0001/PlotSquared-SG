package com.empcraft.psg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.empcraft.psg.command.CreateRoadSchematic;
import com.empcraft.psg.generator.HybridGen;
import com.empcraft.psg.generator.HybridPlotWorld;
import com.empcraft.psg.plotme.PlotMeManager;

public class Main extends JavaPlugin {
    public static String version;
    public static Main plugin;
    public static PlotManager manager = new PlotManager();
    public static FileConfiguration config;
    public static File configFile;
    public static String datafolder;
    public static HashMap<String, HybridPlotWorld> worlds = new HashMap<>();
    
    public static Plugin plotme = null;
    public static PlotMeManager pmm = null;
    
    @Override
    public void onEnable() {
        Main.version = this.getDescription().getVersion();
        Main.plugin = this;
        Main.manager = new PlotManager();
        Main.datafolder = "plugins" + File.separator + "PlotSquared" + File.separator;
        setupConfigs();
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotMe");
        if (plugin != null) {
            Main.plotme = plugin;
        }
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if ("createroadschematic".equalsIgnoreCase(command.getName())) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            
            if (!(sender instanceof Player)) {
                return MainUtil.sendMessage(player, "&cYou are not standing in a plot (mostly because you are executing the command from console)");
            }
            if (args.length != 1) {
                return MainUtil.sendMessage(player, "&cUsage: &7/CreateRoadSchematic <world>");
            }
            if (!worlds.containsKey(args[0])) {
                return MainUtil.sendMessage(player, "&cYou cannot find the mystical land of '&7" + args[0] + "&c' on any of your maps. Perhaps it is under a different name?");
            }
            final HybridPlotWorld plotworld = worlds.get(args[0]);
            if (!MainUtil.hasPermission(player, "plots.admin")) {
                return MainUtil.sendMessage(player, "A magical beast called 'The permissions system' has stopped you in your tracks. You must complete the quest for the 'plots.admin permission node' in order to best it.");
            }
            final Location loc = player.getLocation();
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
                return MainUtil.sendMessage("&cYou are not standing in a plot, you kind of need to do that for this command to work.");
            }
            final int px = dx + 1;
            final int pz = dz + 1;
            
            final int plotSize = plotworld.PLOT_WIDTH;
            final int pathWidth = plotworld.ROAD_WIDTH;
            
            final int botX = (px * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
            final int botZ = (pz * (plotSize + pathWidth)) - (plotSize) - ((int) Math.floor(pathWidth / 2));
            final Location bot = new Location(player.getWorld(), botX, 0, botZ).subtract(1, 0, 1);
            final Location top = new Location(player.getWorld(), (botX + plotSize) - 1, 0, (botZ + plotSize) - 1);
            MainUtil.sendMessage(player, "&9Generating schematic...");
            new CreateRoadSchematic(player.getWorld(), bot, top);
            MainUtil.sendMessage(player, "&aDone!");
            return true;
        }
        return false;
    }
    
    @Override
    final public ChunkGenerator getDefaultWorldGenerator(final String world, final String id) {
        if (plotme != null) {
            try {
                if (Main.manager == null) {
                    new PlotMeManager();
                }
                PlotMeManager.register(world);
            } catch (final Throwable e) {
                e.printStackTrace();
            }
        }
        return new HybridGen(world);
    }
    
    private void setupConfigs() {
        Main.configFile = new File(datafolder + "config" + File.separator + "settings.yml");
        final File parent = Main.configFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            MainUtil.sendMessage("Count not create parent directory for settings.yml\n - please create manually 'plugins/PlotSquared/config/settings.yml'");
        }
        try {
            if (!Main.configFile.exists() && !Main.configFile.createNewFile()) {
                MainUtil.sendMessage("Count not create parent file for settings.yml\n - please create manually 'plugins/PlotSquared/config/settings.yml'");
            }
        } catch (final IOException e) {
            MainUtil.sendMessage("Count not create parent file for settings.yml\n - please create manually 'plugins/PlotSquared/config/settings.yml'");
        }
        Main.config = YamlConfiguration.loadConfiguration(Main.configFile);
    }
    
    public static void save() {
        try {
            Main.config.save(Main.configFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
