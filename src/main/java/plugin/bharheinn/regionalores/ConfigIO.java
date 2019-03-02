package plugin.bharheinn.regionalores;

import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfigIO {

    public final File file;
    public HashMap<Material, Boolean> configTable_ToRemove = new HashMap<>();
    public HashMap<Material, Integer> configTable_ToReplace = new HashMap<>();
    public long config_oreGenSeed;

    private RegionalOres plugin;

    public ConfigIO() {
        plugin = RegionalOres.INSTANCE; //For my sake.

        //Set defaults.
        configTable_ToRemove.put(Material.COAL_ORE, true);
        configTable_ToRemove.put(Material.IRON_ORE, true);
        configTable_ToRemove.put(Material.GOLD_ORE, true);
        configTable_ToRemove.put(Material.REDSTONE_ORE, true);
        configTable_ToRemove.put(Material.LAPIS_ORE, true);
        configTable_ToRemove.put(Material.DIAMOND_ORE, false);
        configTable_ToRemove.put(Material.EMERALD_ORE, false);

        configTable_ToReplace.put(Material.COAL_ORE, 5);
        configTable_ToReplace.put(Material.IRON_ORE, 5);
        configTable_ToReplace.put(Material.GOLD_ORE, 2);
        configTable_ToReplace.put(Material.REDSTONE_ORE, 4);
        configTable_ToReplace.put(Material.LAPIS_ORE, 2);
        configTable_ToReplace.put(Material.DIAMOND_ORE, 0);
        configTable_ToReplace.put(Material.EMERALD_ORE, 0);
        configTable_ToReplace.put(Material.NETHER_QUARTZ_ORE, 0);

        config_oreGenSeed = new Random().nextLong();

        file = new File(plugin.getDataFolder() + File.separator + "config.yml");

        if (!file.exists()) {

            plugin.getConfig().addDefault("Ores.Seed", config_oreGenSeed);

            for (Map.Entry<Material, Boolean> p : configTable_ToRemove.entrySet()) {
                plugin.getConfig().addDefault("Ores.ToRemove." + p.getKey().name(), p.getValue());
            }

            for (Map.Entry<Material, Integer> p : configTable_ToReplace.entrySet()) {
                plugin.getConfig().addDefault("Ores.ToReplace." + p.getKey().name(), p.getValue());
            }

            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        } else {
            fixConfig();
        }

        config_oreGenSeed = plugin.getConfig().getLong("Ores.Seed");

        configTable_ToRemove.clear();
        for (String key : plugin.getConfig().getConfigurationSection("Ores.ToRemove").getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_ToRemove.put(Material.getMaterial(key), plugin.getConfig().getBoolean("Ores.ToRemove." + key));
            }
        }

        configTable_ToReplace.clear();
        for (String key : plugin.getConfig().getConfigurationSection("Ores.ToReplace").getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_ToReplace.put(Material.getMaterial(key), plugin.getConfig().getInt("Ores.ToReplace." + key));
            }
        }
    }

    public void fixConfig() {

        boolean markForUpdate = false;

        if(plugin.getConfig().get("Ores.Seed") == null || !(plugin.getConfig().get("Ores.Seed") instanceof Long)) {
            plugin.getConfig().set("Ores.Seed", config_oreGenSeed);
            markForUpdate = true;
        }

        for (Map.Entry<Material, Boolean> p : configTable_ToRemove.entrySet()) {
            if (plugin.getConfig().get("Ores.ToRemove." + p.getKey().name()) == null || !(plugin.getConfig().get("Ores.ToRemove." + p.getKey().name()) instanceof Boolean)) {
                plugin.getConfig().set("Ores.ToRemove." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }
        for (Map.Entry<Material, Integer> p : configTable_ToReplace.entrySet()) {
            if (plugin.getConfig().get("Ores.ToReplace." + p.getKey().name()) == null || !(plugin.getConfig().get("Ores.ToReplace." + p.getKey().name()) instanceof Boolean)) {
                plugin.getConfig().set("Ores.ToReplace." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }

        if (markForUpdate) {
            plugin.saveConfig();
            plugin.reloadConfig();
        }
    }
}
