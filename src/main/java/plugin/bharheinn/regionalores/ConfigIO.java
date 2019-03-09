package plugin.bharheinn.regionalores;

import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfigIO {
    public final File file;

    /*UTILITY VARIABLES*/
    private static final String CONFIG_UTIL_GENWORLD = "Util.GenerateRegionalOresWorld"; //Generate the regional ore world or replace ores in default world?
    private static final String CONFIG_UTIL_SPAWNGENWORLD = "Util.SpawnInRegionalOresWorld"; //Spawn the player in the regional ore world if it is generated?
    private static final String CONFIG_UTIL_PERMISSIONS = "Util.UsePermissions"; //Utilise permissions?

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
        public byte configData_Map_ColorInfoBox;
    private static final String CONFIG_MAP_COLOR_OUTLINE = "Map.Color.Outline";
        public byte configData_Map_ColorOutline;
    private static final String CONFIG_MAP_COLOR_ORES = "Map.Color.Ores";
        public HashMap<Material, Byte> configTable_Map_ColorOres = new HashMap<>();
    private static final String CONFIG_MAP_LANG_CURRENTREGION = "Map.Language.CurrentRegion";
        public String configData_Map_LangCurrentRegion;
    private static final String CONFIG_MAP_LANG_ORENAMES = "Map.Language.OreNames";
        public HashMap<Material, String> configTable_Map_LangOreNames = new HashMap<>();

    private RegionalOres plugin;

    public ConfigIO() {
        plugin = RegionalOres.INSTANCE; //For my sake.

        //Set defaults.
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
        configTable_Gen_OresToReplace.put(Material.DIAMOND_ORE, 0);
        configTable_Gen_OresToReplace.put(Material.EMERALD_ORE, 0);
        configTable_Gen_OresToReplace.put(Material.NETHER_QUARTZ_ORE, 2);
        
        configData_Map_Scale = 20;
        configData_Map_ColorInfoBox = (byte)91;
        configData_Map_ColorOutline = (byte)34;
        configTable_Map_ColorOres.put(Material.COAL_ORE, (byte)47);
        configTable_Map_ColorOres.put(Material.IRON_ORE, (byte)57);
        configTable_Map_ColorOres.put(Material.GOLD_ORE, (byte)122);
        configTable_Map_ColorOres.put(Material.REDSTONE_ORE, (byte)114);
        configTable_Map_ColorOres.put(Material.LAPIS_ORE, (byte)102);
        configTable_Map_ColorOres.put(Material.DIAMOND_ORE, (byte)126);
        configTable_Map_ColorOres.put(Material.EMERALD_ORE, (byte)134);
        configTable_Map_ColorOres.put(Material.NETHER_QUARTZ_ORE, (byte)146);
        configData_Map_LangCurrentRegion = "§33;Current Region: §34;";

        //Begin loading / create file.
        file = new File(plugin.getDataFolder() + File.separator + "config.yml");

        if (!file.exists()) {

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
            plugin.getConfig().addDefault(CONFIG_MAP_COLOR_INFOBOX, (int)configData_Map_ColorInfoBox);
            plugin.getConfig().addDefault(CONFIG_MAP_COLOR_OUTLINE, (int)configData_Map_ColorOutline);
            for (Map.Entry<Material, Byte> p : configTable_Map_ColorOres.entrySet()) {
                plugin.getConfig().addDefault(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name(), (int)p.getValue());
            }
            plugin.getConfig().addDefault(CONFIG_MAP_LANG_CURRENTREGION, configData_Map_LangCurrentRegion);
            /////////Do not add Config_Map_Lang_OreNames as default as it is optional.

            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        } else {
            fixConfig();
        }

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
        configData_Map_ColorInfoBox = (byte)plugin.getConfig().getInt(CONFIG_MAP_COLOR_INFOBOX);
        configData_Map_ColorOutline = (byte)plugin.getConfig().getInt(CONFIG_MAP_COLOR_OUTLINE);
        configTable_Map_ColorOres.clear();
        for (String key : plugin.getConfig().getConfigurationSection(CONFIG_MAP_COLOR_ORES).getKeys(false)) {
            if(Material.getMaterial(key) != null) {
                configTable_Map_ColorOres.put(Material.getMaterial(key), (byte)plugin.getConfig().getInt(CONFIG_MAP_COLOR_ORES + "." + key));
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
            plugin.getConfig().set(CONFIG_MAP_COLOR_INFOBOX, (int)configData_Map_ColorInfoBox);
            markForUpdate = true;
        }
        if(plugin.getConfig().get(CONFIG_MAP_COLOR_OUTLINE) == null || !(plugin.getConfig().get(CONFIG_MAP_COLOR_OUTLINE) instanceof Integer)) {
            plugin.getConfig().set(CONFIG_MAP_COLOR_OUTLINE, (int)configData_Map_ColorOutline);
            markForUpdate = true;
        }
        for (Map.Entry<Material, Byte> p : configTable_Map_ColorOres.entrySet()) {
            //Store byte as an integer...
            if (plugin.getConfig().get(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name()) == null || !(plugin.getConfig().get(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name()) instanceof Integer)) {
                plugin.getConfig().set(CONFIG_MAP_COLOR_ORES + "." + p.getKey().name(), (int) p.getValue());
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
