package com.empcraft.psg.object;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;

public abstract class PlotWorld {

    public final String worldname;

    public PlotWorld(final String worldname) {
        this.worldname = worldname;
    }

    /**
     * When a world is created, the following method will be called for each
     *
     * @param config Configuration Section
     */
    public void loadDefaultConfiguration(final ConfigurationSection config) {
        loadConfiguration(config);
    }

    public abstract void loadConfiguration(final ConfigurationSection config);

    /**
     * Saving core plotworld settings
     *
     * @param config Configuration Section
     */
    public void saveConfiguration(final ConfigurationSection config) {
        HashMap<String, Object> options = new HashMap<>();        
        final ConfigurationNode[] settings = getSettingNodes();
        /*
         * Saving generator specific settings
         */
        for (final ConfigurationNode setting : settings) {
            System.out.print(setting.getConstant() +" : " + setting.getValue());
            options.put(setting.getConstant(), setting.getType().parseObject(setting.getValue()));
        }
        
        System.out.print("DONE!");
        
        for (final String option : options.keySet()) {
            if (!config.contains(option)) {
                config.set(option, options.get(option));
            }
        }
    }

    /**
     * Used for the <b>/plot setup</b> command Return null if you do not want to support this feature
     *
     * @return ConfigurationNode[]
     */
    public abstract ConfigurationNode[] getSettingNodes();
}
