package plugin.bharheinn.regionalores;

import org.bukkit.Material;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfigIO {
    public final File file;
    private RegionalOres plugin;

    /*UTILITY VARIABLES*/
    private static final String CONFIG_UTIL_STARTUP = "Util.ShutdownOnStartup";
        public boolean configData_UtilStartup;
    private static final String CONFIG_UTIL_GENWORLD = "Util.GenerateRegionalOresWorld";
        public String configData_UtilGenWorld;
    private static final String CONFIG_UTIL_PERMISSIONS = "Util.UsePermissions";
        public boolean configData_UtilPermissions;

    /*GENERATION VARIABLES*/
    private static final String CONFIG_GEN_SEED = "Gen.Seed";
        public long configData_GenSeed;
    private static final String CONFIG_GEN_SCALE = "Gen.Scale";
        public double configData_GenScale;
    private static final String CONFIG_GEN_ORES_TOREMOVE = "Gen.Ores.ToRemove";
        public HashMap<Material, Boolean> configTable_Gen_OresToRemove = new HashMap<>();
    private static final String CONFIG_GEN_ORES_TOREPLACE = "Gen.Ores.ToReplace";
        public HashMap<Material, Integer> configTable_Gen_OresToReplace = new HashMap<>();

    /*OREMAP VARIABLES*/
    private static final String CONFIG_MAP_SCALE = "Map.Scale";
        public int configData_Map_Scale;
    private static final String CONFIG_MAP_COLOR_INFOBOX = "Map.Color.Infobox";
        public int configData_Map_ColorInfoBox;
    private static final String CONFIG_MAP_COLOR_OUTLINE = "Map.Color.Outline";
        public int configData_Map_ColorOutline;
    private static final String CONFIG_MAP_COLOR_ORES = "Map.Color.Ores";
        public HashMap<Material, Integer> configTable_Map_ColorOres = new HashMap<>();
    private static final String CONFIG_MAP_LANG_CURRENTREGION = "Map.Language.CurrentRegion";
        public String configData_Map_LangCurrentRegion;
    private static final String CONFIG_MAP_LANG_ORENAMES = "Map.Language.Ores";
        public HashMap<Material, String> configTable_Map_LangOreNames = new HashMap<>();

    public ConfigIO() {
        plugin = RegionalOres.INSTANCE; //For my sake.

        //Set defaults.
        /*UTILITY*/
        configData_UtilStartup = true;
        configData_UtilGenWorld = "world";
        configData_UtilPermissions = true;

        /*GENERATION*/
        configData_GenSeed = new Random().nextLong();
        configData_GenScale = 3000D;
        configTable_Gen_OresToRemove.put(Material.COAL_ORE, true);
        configTable_Gen_OresToRemove.put(Material.IRON_ORE, true);
        configTable_Gen_OresToRemove.put(Material.GOLD_ORE, true);
        configTable_Gen_OresToRemove.put(Material.REDSTONE_ORE, true);
        configTable_Gen_OresToRemove.put(Material.LAPIS_ORE, true);
        configTable_Gen_OresToRemove.put(Material.DIAMOND_ORE, false);
        configTable_Gen_OresToRemove.put(Material.EMERALD_ORE, false);
        configTable_Gen_OresToReplace.put(Material.COAL_ORE, 30);
        configTable_Gen_OresToReplace.put(Material.IRON_ORE, 25);
        configTable_Gen_OresToReplace.put(Material.GOLD_ORE, 5);
        configTable_Gen_OresToReplace.put(Material.REDSTONE_ORE, 10);
        configTable_Gen_OresToReplace.put(Material.LAPIS_ORE, 3);
        configTable_Gen_OresToReplace.put(Material.NETHER_QUARTZ_ORE, 2);

        /*OREMAP*/
        configData_Map_Scale = 20;
        configData_Map_ColorInfoBox = 91;
        configData_Map_ColorOutline = 34;
        configTable_Map_ColorOres.put(Material.COAL_ORE, 47);
        configTable_Map_ColorOres.put(Material.IRON_ORE, 57);
        configTable_Map_ColorOres.put(Material.GOLD_ORE, 122);
        configTable_Map_ColorOres.put(Material.REDSTONE_ORE, 114);
        configTable_Map_ColorOres.put(Material.LAPIS_ORE, 102);
        configTable_Map_ColorOres.put(Material.DIAMOND_ORE, 126);
        configTable_Map_ColorOres.put(Material.EMERALD_ORE, 134);
        configTable_Map_ColorOres.put(Material.NETHER_QUARTZ_ORE, 146);
        configData_Map_LangCurrentRegion = "§33;Current Region: §34;";

        //Begin loading / create file.
        file = new File(plugin.getDataFolder() + File.separator + "config.yml");

        if (!file.exists()) {

            try {
                file.createNewFile();
                BufferedWriter commentWriter = new BufferedWriter(new FileWriter(file));
                commentWriter.write("# " + RegionalOres.PLUGIN_NAME + " v" + RegionalOres.PLUGIN_VERSION);
                commentWriter.newLine();
                commentWriter.write("# Please visit https://github.com/Mr-Rockers/RegionalOres/wiki/Configuration for more info.");
                commentWriter.newLine();
                commentWriter.close();
            } catch(IOException exception) {
                System.out.println("Unable to write comments to " + file.getPath() + ". Is there something wrong?");
            }

            /*UTILITY*/
            plugin.getConfig().addDefault(CONFIG_UTIL_STARTUP, configData_UtilStartup);
            plugin.getConfig().addDefault(CONFIG_UTIL_GENWORLD, configData_UtilGenWorld);
            plugin.getConfig().addDefault(CONFIG_UTIL_PERMISSIONS, configData_UtilPermissions);

            /*GENERATION*/
            plugin.getConfig().addDefault(CONFIG_GEN_SEED, configData_GenSeed);
            plugin.getConfig().addDefault(CONFIG_GEN_SCALE, configData_GenScale);
            for (Map.Entry<Material, Boolean> p : configTable_Gen_OresToRemove.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_GEN_ORES_TOREMOVE + "." + p.getKey().name(), p.getValue());
            }
            for (Map.Entry<Material, Integer> p : configTable_Gen_OresToReplace.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_GEN_ORES_TOREPLACE + "." + p.getKey().name(), p.getValue());
            }

            /*OREMAP*/
            plugin.getConfig().addDefault(CONFIG_MAP_SCALE, configData_Map_Scale);
            plugin.getConfig().addDefault(CONFIG_MAP_COLOR_INFOBOX, configData_Map_ColorInfoBox);
            plugin.getConfig().addDefault(CONFIG_MAP_COLOR_OUTLINE, configData_Map_ColorOutline);
            for (Map.Entry<Material, Integer> p : configTable_Map_ColorOres.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name(), p.getValue());
            }
            plugin.getConfig().addDefault(CONFIG_MAP_LANG_CURRENTREGION, configData_Map_LangCurrentRegion);
            /////////Do not add Config_Map_Lang_OreNames as default as it is optional.

            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        } else {
            fixConfig();
        }

        /*UTILITY*/
        configData_UtilStartup = plugin.getConfig().getBoolean(CONFIG_UTIL_STARTUP);
        configData_UtilGenWorld = plugin.getConfig().getString(CONFIG_UTIL_GENWORLD);
        configData_UtilPermissions = plugin.getConfig().getBoolean(CONFIG_UTIL_PERMISSIONS);

        /*GENERATION*/
        configData_GenSeed = plugin.getConfig().getLong(CONFIG_GEN_SEED);
        configData_GenScale = plugin.getConfig().getDouble(CONFIG_GEN_SCALE);
        configTable_Gen_OresToRemove.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_GEN_ORES_TOREMOVE).getKeys(false)) {
            Material material = Material.getMaterial(key);
            if(material != null && material.isBlock() && material != Material.AIR && material != Material.CAVE_AIR && material != Material.VOID_AIR) { //Hardcoded on purpose.
                configTable_Gen_OresToRemove.put(Material.getMaterial(key), plugin.getConfig().getBoolean(CONFIG_GEN_ORES_TOREMOVE + "." + key));
            }
        }
        configTable_Gen_OresToReplace.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_GEN_ORES_TOREPLACE).getKeys(false)) {

            Material material = Material.getMaterial(key);
            if(material != null && material.isBlock() && material.isSolid() && !isMaterialRail(material) && material != Material.REDSTONE_WIRE) { //Hardcoded on purpose.
                int precedence = plugin.getConfig().getInt(CONFIG_GEN_ORES_TOREPLACE + "." + key);
                if(precedence < 0) {
                    precedence = 0;
                }
                configTable_Gen_OresToReplace.put(Material.getMaterial(key), precedence);
            }
        }

        /*OREMAP*/
        configData_Map_Scale = plugin.getConfig().getInt(CONFIG_MAP_SCALE);
        configData_Map_ColorInfoBox = plugin.getConfig().getInt(CONFIG_MAP_COLOR_INFOBOX);
        configData_Map_ColorOutline = plugin.getConfig().getInt(CONFIG_MAP_COLOR_OUTLINE);
        configTable_Map_ColorOres.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_MAP_COLOR_ORES).getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_Map_ColorOres.put(Material.getMaterial(key), plugin.getConfig().getInt(CONFIG_MAP_COLOR_ORES + "." + key));
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
    }

    public void fixConfig() {

        boolean markForUpdate = false;

        /*UTILITY*/
        if(plugin.getConfig().get(CONFIG_UTIL_STARTUP) == null || !(plugin.getConfig().get(CONFIG_UTIL_STARTUP) instanceof Boolean)) {
            plugin.getConfig().set(CONFIG_UTIL_STARTUP, configData_UtilStartup);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_UTIL_GENWORLD) == null || !(plugin.getConfig().get(CONFIG_UTIL_GENWORLD) instanceof String)) {
            plugin.getConfig().set(CONFIG_UTIL_GENWORLD, configData_UtilGenWorld);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_UTIL_PERMISSIONS) == null || !(plugin.getConfig().get(CONFIG_UTIL_PERMISSIONS) instanceof Boolean)) {
            plugin.getConfig().set(CONFIG_UTIL_PERMISSIONS, configData_UtilPermissions);
            markForUpdate = true;
        }

        /*GENERATION*/
        if(plugin.getConfig().get(CONFIG_GEN_SEED) == null || !(plugin.getConfig().get(CONFIG_GEN_SEED) instanceof Long)) {
            plugin.getConfig().set(CONFIG_GEN_SEED, configData_GenSeed);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_GEN_SCALE) == null || !(plugin.getConfig().get(CONFIG_GEN_SCALE) instanceof Double)) {
            plugin.getConfig().set(CONFIG_GEN_SCALE, configData_GenScale);
            markForUpdate = true;
        }
        for (Map.Entry<Material, Boolean> p : configTable_Gen_OresToRemove.entrySet()) {
            if (plugin.getConfig().get(CONFIG_GEN_ORES_TOREMOVE + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_GEN_ORES_TOREMOVE + "." + p.getKey().name()) instanceof Boolean)) {
                plugin.getConfig().set(CONFIG_GEN_ORES_TOREMOVE + "." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }
        for (Map.Entry<Material, Integer> p : configTable_Gen_OresToReplace.entrySet()) {
            if (plugin.getConfig().get(CONFIG_GEN_ORES_TOREPLACE + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_GEN_ORES_TOREPLACE + "." + p.getKey().name()) instanceof Integer)) {
                plugin.getConfig().set(CONFIG_GEN_ORES_TOREPLACE + "." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }

        /*OREMAP*/
        if(plugin.getConfig().get(CONFIG_MAP_SCALE) == null || !(plugin.getConfig().get(CONFIG_MAP_SCALE) instanceof Integer)) {
            plugin.getConfig().set(CONFIG_MAP_SCALE, configData_Map_Scale);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_MAP_COLOR_INFOBOX) == null || !(plugin.getConfig().get(CONFIG_MAP_COLOR_INFOBOX) instanceof Integer)) {
            plugin.getConfig().set(CONFIG_MAP_COLOR_INFOBOX, configData_Map_ColorInfoBox);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_MAP_COLOR_OUTLINE) == null || !(plugin.getConfig().get(CONFIG_MAP_COLOR_OUTLINE) instanceof Integer)) {
            plugin.getConfig().set(CONFIG_MAP_COLOR_OUTLINE, configData_Map_ColorOutline);
            markForUpdate = true;
        }
        for (Map.Entry<Material, Integer> p : configTable_Map_ColorOres.entrySet()) {
            //Store byte as an integer...
            if (plugin.getConfig().get(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name()) instanceof Integer)) {
                plugin.getConfig().set(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name(), p.getValue());
                markForUpdate = true;
            }
        }
        if(plugin.getConfig().get(CONFIG_MAP_LANG_CURRENTREGION) == null || !(plugin.getConfig().get(CONFIG_MAP_LANG_CURRENTREGION) instanceof String)) {
            plugin.getConfig().set(CONFIG_MAP_LANG_CURRENTREGION, configData_Map_LangCurrentRegion);
            markForUpdate = true;
        }

        if (markForUpdate) {
            plugin.saveConfig();
            plugin.reloadConfig();
        }
    }

    private boolean isMaterialRail(Material material) {
        return material == Material.ACTIVATOR_RAIL || material == Material.DETECTOR_RAIL || material == Material.POWERED_RAIL || material == Material.RAIL;
    }
}
