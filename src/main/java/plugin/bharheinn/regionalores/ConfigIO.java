package plugin.bharheinn.regionalores;

import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfigIO {
    public final File file;

    private static final String CONFIG_ORE_REMOVE = "Ores.ToRemove";
    private static final String CONFIG_ORE_REPLACE = "Ores.ToReplace";

    private static final String CONFIG_MAP_SCALE = "Map.Scale";
    private static final String CONFIG_MAP_COLORS = "Map.Colors";
    private static final String CONFIG_MAP_LANG_CURRENTREGION = "Map.Language.CurrentRegion";
    private static final String CONFIG_MAP_LANG_ORENAMES = "Map.Language.OreNames";

    private static final String CONFIG_GEN_SEED = "Generator.Seed";
    private static final String CONFIG_GEN_SCALE = "Generator.Scale";

    public HashMap<Material, Boolean> configTable_OresToRemove = new HashMap<>();
    public HashMap<Material, Integer> configTable_OresToReplace = new HashMap<>();

    public int configData_MapScale;
    public HashMap<Material, Byte> configTable_MapColors = new HashMap<>();
    public String configData_Map_LangCurrentRegion;
    public HashMap<Material, String> configTable_Map_LangOreNames = new HashMap<>();

    public long configData_GenSeed;
    public double configData_GenScale;

    private RegionalOres plugin;

    public ConfigIO() {
        plugin = RegionalOres.INSTANCE; //For my sake.

        //Set defaults.
        configTable_OresToRemove.put(Material.COAL_ORE, true);
        configTable_OresToRemove.put(Material.IRON_ORE, true);
        configTable_OresToRemove.put(Material.GOLD_ORE, true);
        configTable_OresToRemove.put(Material.REDSTONE_ORE, true);
        configTable_OresToRemove.put(Material.LAPIS_ORE, true);
        configTable_OresToRemove.put(Material.DIAMOND_ORE, false);
        configTable_OresToRemove.put(Material.EMERALD_ORE, false);
        configTable_OresToReplace.put(Material.COAL_ORE, 30);
        configTable_OresToReplace.put(Material.IRON_ORE, 25);
        configTable_OresToReplace.put(Material.GOLD_ORE, 5);
        configTable_OresToReplace.put(Material.REDSTONE_ORE, 10);
        configTable_OresToReplace.put(Material.LAPIS_ORE, 3);
        configTable_OresToReplace.put(Material.DIAMOND_ORE, 0);
        configTable_OresToReplace.put(Material.EMERALD_ORE, 0);
        configTable_OresToReplace.put(Material.NETHER_QUARTZ_ORE, 2);

        configData_MapScale = 20;
        configTable_MapColors.put(Material.COAL_ORE, (byte)47);
        configTable_MapColors.put(Material.IRON_ORE, (byte)57);
        configTable_MapColors.put(Material.GOLD_ORE, (byte)122);
        configTable_MapColors.put(Material.REDSTONE_ORE, (byte)114);
        configTable_MapColors.put(Material.LAPIS_ORE, (byte)102);
        configTable_MapColors.put(Material.DIAMOND_ORE, (byte)126);
        configTable_MapColors.put(Material.EMERALD_ORE, (byte)134);
        configTable_MapColors.put(Material.NETHER_QUARTZ_ORE, (byte)146);

        configData_Map_LangCurrentRegion = "§33;Current Region: §34;";

        configData_GenSeed = new Random().nextLong();
        configData_GenScale = 3000D;

        //Begin loading / create file.
        file = new File(plugin.getDataFolder() + File.separator + "config.yml");

        if (!file.exists()) {

            for (Map.Entry<Material, Boolean> p : configTable_OresToRemove.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_ORE_REMOVE + "." + p.getKey().name(), p.getValue());
            }
            for (Map.Entry<Material, Integer> p : configTable_OresToReplace.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_ORE_REPLACE + "." + p.getKey().name(), p.getValue());
            }

            plugin.getConfig().addDefault(CONFIG_MAP_SCALE, configData_MapScale);
            for (Map.Entry<Material, Byte> p : configTable_MapColors.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_MAP_COLORS + "." + p.getKey().name(), (int)p.getValue());
            }

            plugin.getConfig().addDefault(CONFIG_MAP_LANG_CURRENTREGION, configData_Map_LangCurrentRegion);
            //Do not add Config_Map_Lang_OreNames as default as it is optional.

            plugin.getConfig().addDefault(CONFIG_GEN_SEED, configData_GenSeed);
            plugin.getConfig().addDefault(CONFIG_GEN_SCALE, configData_GenScale);

            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        } else {
            fixConfig();
        }

        configTable_OresToRemove.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_ORE_REMOVE).getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_OresToRemove.put(Material.getMaterial(key), plugin.getConfig().getBoolean(CONFIG_ORE_REMOVE + "." + key));
            }
        }
        configTable_OresToReplace.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_ORE_REPLACE).getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_OresToReplace.put(Material.getMaterial(key), plugin.getConfig().getInt(CONFIG_ORE_REPLACE + "." + key));
            }
        }

        configData_Map_LangCurrentRegion = plugin.getConfig().getString(CONFIG_MAP_LANG_CURRENTREGION);
        configTable_Map_LangOreNames.clear();

        if(plugin.getConfig().getConfigurationSection(CONFIG_MAP_LANG_ORENAMES) != null) { //Only has to be done to CONFIG_MAP_LANG_ORENAMES as it is optional.
            for (String key : plugin.getConfig().getConfigurationSection(CONFIG_MAP_LANG_ORENAMES).getKeys(false)) {
                if (Material.getMaterial(key) != null) {
                    configTable_Map_LangOreNames.put(Material.getMaterial(key), plugin.getConfig().getString(CONFIG_MAP_LANG_ORENAMES + "." + key));
                }
            }
        }

        configData_MapScale = plugin.getConfig().getInt(CONFIG_MAP_SCALE);
        configTable_MapColors.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_MAP_COLORS).getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_MapColors.put(Material.getMaterial(key), (byte)plugin.getConfig().getInt(CONFIG_MAP_COLORS + "." + key));
            }
        }

        configData_GenSeed = plugin.getConfig().getLong(CONFIG_GEN_SEED);
        configData_GenScale = plugin.getConfig().getDouble(CONFIG_GEN_SCALE);
    }

    public void fixConfig() {

        boolean markForUpdate = false;

        for (Map.Entry<Material, Boolean> p : configTable_OresToRemove.entrySet()) {
            if (plugin.getConfig().get(CONFIG_ORE_REMOVE + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_ORE_REMOVE + "." + p.getKey().name()) instanceof Boolean)) {
                plugin.getConfig().set(CONFIG_ORE_REMOVE + "." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }
        for (Map.Entry<Material, Integer> p : configTable_OresToReplace.entrySet()) {
            if (plugin.getConfig().get(CONFIG_ORE_REPLACE + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_ORE_REPLACE + "." + p.getKey().name()) instanceof Integer)) {
                plugin.getConfig().set(CONFIG_ORE_REPLACE + "." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }

        if(plugin.getConfig().get(CONFIG_MAP_SCALE) == null || !(plugin.getConfig().get(CONFIG_MAP_SCALE) instanceof Integer)) {
            plugin.getConfig().set(CONFIG_MAP_SCALE, configData_MapScale);
            markForUpdate = true;
        }
        for (Map.Entry<Material, Byte> p : configTable_MapColors.entrySet()) {
            //Store byte as an integer...
            if (plugin.getConfig().get(CONFIG_MAP_COLORS + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_MAP_COLORS + "." + p.getKey().name()) instanceof Integer)) {
                plugin.getConfig().set(CONFIG_MAP_COLORS + "." + p.getKey().name(), (int) p.getValue());
                markForUpdate = true;
            }
        }

        if(plugin.getConfig().get(CONFIG_MAP_LANG_CURRENTREGION) == null || !(plugin.getConfig().get(CONFIG_MAP_LANG_CURRENTREGION) instanceof String)) {
            plugin.getConfig().set(CONFIG_MAP_LANG_CURRENTREGION, configData_Map_LangCurrentRegion);
            markForUpdate = true;
        }

        if(plugin.getConfig().get(CONFIG_GEN_SEED) == null || !(plugin.getConfig().get(CONFIG_GEN_SEED) instanceof Long)) {
            plugin.getConfig().set(CONFIG_GEN_SEED, configData_GenSeed);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_GEN_SCALE) == null || !(plugin.getConfig().get(CONFIG_GEN_SCALE) instanceof Double)) {
            plugin.getConfig().set(CONFIG_GEN_SCALE, configData_GenScale);
            markForUpdate = true;
        }

        if (markForUpdate) {
            plugin.saveConfig();
            plugin.reloadConfig();
        }
    }
}
