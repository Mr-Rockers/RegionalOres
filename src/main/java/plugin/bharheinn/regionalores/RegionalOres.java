package plugin.bharheinn.regionalores;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.bharheinn.regionalores.gen.WorldGen;
import plugin.bharheinn.regionalores.oremap.OreMap;

public class RegionalOres extends JavaPlugin implements CommandExecutor {

    public static RegionalOres INSTANCE;
    public WorldGen worldGen;
    public ConfigIO configIO;

    public RegionalOres() {
        INSTANCE = this;
        worldGen = new WorldGen();
    }

    @Override
    public void onEnable() {
        configIO = new ConfigIO();
        worldGen.createRegionalOresWorld();

        getServer().getPluginManager().registerEvents(new OreMap.Events(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("regionaloresworld")) {
            if (worldGen.regionalOresWorld == null) {
                return false;
            }

            sender.sendMessage(ChatColor.BLUE + "Zoop!");
            if (sender instanceof Player) {
                ((Player) sender).teleport(new Location(worldGen.regionalOresWorld,
                        worldGen.regionalOresWorld.getSpawnLocation().getX() + 0.5D,
                        worldGen.regionalOresWorld.getSpawnLocation().getY() + 0.5D,
                        worldGen.regionalOresWorld.getSpawnLocation().getZ() + 0.5D));
            }
        }

        if (command.getName().equalsIgnoreCase("oremap")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if(player.hasPermission("regionalores.oremap")) {
                    if (player.getWorld() == worldGen.regionalOresWorld) {
                        OreMap.FindAndRemoveOreMaps(player);
                        if (IsStackEmpty(player.getInventory().getItemInOffHand())) {
                            new OreMap(player, true);
                        } else if (IsStackEmpty(player.getInventory().getItemInMainHand())) {
                            new OreMap(player, false);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "This command only works in the Regional Ores world.");
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Insufficient permissions.");
                }
            }
        }
        return true;
    }

    public static boolean IsStackEmpty(ItemStack stack) {
        return stack == null || stack.getType() == null || stack.getType() == Material.AIR;
    }
}