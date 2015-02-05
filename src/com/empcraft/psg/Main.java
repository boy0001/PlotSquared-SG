package com.empcraft.psg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.empcraft.psg.generator.HybridGen;
import com.empcraft.psg.generator.HybridPlotWorld;
import com.empcraft.psg.plotme.PlotMeGenerator;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.bukkit.PlotMe_CorePlugin;

public class Main extends JavaPlugin {
    public static String version;
    public static Main plugin;
    public static PlotManager manager = new PlotManager();
    public static FileConfiguration config;
    public static File configFile;
    public static String datafolder;
    public static HashMap<String, HybridPlotWorld> worlds = new HashMap<>();
    
    private PlotMe_Core plotme = null;

    @Override
    public void onEnable() {
        Main.version = this.getDescription().getVersion();
        Main.plugin = this;
        Main.manager = new PlotManager();
        Main.datafolder = "plugins" + File.separator + "PlotSquared" + File.separator;
        setupConfigs();
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotMe");
        if (plugin != null && plugin.isEnabled()) {
            this.plotme = ((PlotMe_CorePlugin) plugin).getAPI();
        }
    }
    
    @Override
    final public ChunkGenerator getDefaultWorldGenerator(final String world, final String id) {
        if (plotme != null) {
            return new PlotMeGenerator(world, id);
        }
        else {
            return new HybridGen(world);
        }
    }
    
    private void setupConfigs() {
        configFile = new File(datafolder + "config" + File.separator +"settings.yml");
        File parent = configFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            System.out.print("Count not create parent directory for settings.yml\n - please create manually 'plugins/PlotSquared/config/settings.yml'");
        }
        try {
            if (!configFile.exists() && !configFile.createNewFile()) {
                System.out.print("Count not create parent file for settings.yml\n - please create manually 'plugins/PlotSquared/config/settings.yml'");
            }
        } catch (IOException e) {
            System.out.print("Count not create parent file for settings.yml\n - please create manually 'plugins/PlotSquared/config/settings.yml'");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void save() {
        try {
            Main.config.save(Main.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
