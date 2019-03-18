package plugin.bharheinn.regionalores;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.bharheinn.regionalores.gen.WorldGen;
import plugin.bharheinn.regionalores.oremap.OreMap;

import java.util.logging.Level;

public class RegionalOres extends JavaPlugin implements CommandExecutor {

    public static RegionalOres INSTANCE;
    public WorldGen worldGen;
    public ConfigIO configIO;

    public static String PLUGIN_NAME, PLUGIN_VERSION;

    public RegionalOres() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        PLUGIN_NAME = getDescription().getName();
        PLUGIN_VERSION = getDescription().getVersion();
        configIO = new ConfigIO();

        if(configIO.configData_UtilStartup) {
            //The config.yml has not been set correctly so shut server down.
            getLogger().log(Level.SEVERE, "Util.ShutdownOnStartup in the config.yml is enabled. Please restart the server with the option disabled.");
            getServer().shutdown();
        }

        worldGen = new WorldGen();

        getServer().getPluginManager().registerEvents(worldGen, this); //Begin listening to events in order to catch oreReplacerEvents.
        getServer().getPluginManager().registerEvents(new OreMap.Events(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("oremap")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if(!configIO.configData_UtilPermissions || player.hasPermission("regionalores.oremap")) {
                    if (player.getWorld().getName().equals(configIO.configData_UtilGenWorld)){
                        OreMap.FindAndRemoveOreMaps(player);
                        if (IsStackEmpty(player.getInventory().getItemInOffHand())) {
                            new OreMap(player, true);
                        } else if (IsStackEmpty(player.getInventory().getItemInMainHand())) {
                            new OreMap(player, false);
                        }
                        else {
                            player.sendMessage(ChatColor.YELLOW + "Empty one of your hands!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "This command only works in the world \'" + configIO.configData_UtilGenWorld + "\'.");
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions.");
                }
            }
        }
        if (command.getName().equalsIgnoreCase("oreinfo")) {
            if(!configIO.configData_UtilPermissions || sender.hasPermission("regionalores.oreinfo")) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "----------------");
                sender.sendMessage(ChatColor.GRAY + "Showing info for world \"" + ChatColor.AQUA + configIO.configData_UtilGenWorld + ChatColor.GRAY + "\".");
                sender.sendMessage(ChatColor.DARK_PURPLE + "----------------");
                sender.sendMessage(ChatColor.RED + "The following ores are removed:");
                String removedOres = "";
                for(Material material : configIO.configTable_Gen_OresToRemove.keySet()) {
                    if(configIO.configTable_Gen_OresToRemove.get(material)) {
                        removedOres += ChatColor.GRAY + material.name() + ChatColor.DARK_RED + ", ";
                    }
                }
                removedOres = removedOres.substring(0, removedOres.length() - 2);
                removedOres += ".";
                sender.sendMessage(removedOres);

                sender.sendMessage(ChatColor.GREEN + "These are replaced with:");
                String replacedOres = "";
                for(int i = 0; i < worldGen.sortedOrePool.size(); i++) {
                    replacedOres += ChatColor.GRAY + worldGen.sortedOrePool.get(i).name();
                    if(i != worldGen.sortedOrePool.size() - 1) {
                        replacedOres += ChatColor.DARK_GREEN + ", ";
                    }
                    else {
                        replacedOres += ChatColor.DARK_GREEN + ". ";
                    }
                }
                sender.sendMessage(replacedOres);
                sender.sendMessage(ChatColor.DARK_PURPLE + "----------------");

            }
            else {
                sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
            }
        }
        return true;
    }

    public static boolean IsStackEmpty(ItemStack stack) {
        return stack == null || stack.getType() == null || stack.getType() == Material.AIR;
    }
}