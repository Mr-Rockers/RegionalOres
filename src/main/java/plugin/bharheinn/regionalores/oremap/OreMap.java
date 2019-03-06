package plugin.bharheinn.regionalores.oremap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import plugin.bharheinn.regionalores.RegionalOres;

import java.util.Collections;

public class OreMap implements Listener {

    public OreMap(Player player, boolean inOffhand) {

        MapView mv = RegionalOres.INSTANCE.getServer().createMap(RegionalOres.INSTANCE.worldGen.regionalOresWorld);
        mv.addRenderer(new OreMapRenderer());

        ItemStack mapStack = new ItemStack(Material.FILLED_MAP, 1);

        MapMeta mm = (MapMeta) mapStack.getItemMeta();
        mm.setMapView(mv);
        mm.setDisplayName("Ore Map");
        mm.setLore(Collections.singletonList("RegionalOres Plugin Addition"));

        mapStack.setItemMeta(mm);

        if(inOffhand) {
            player.getInventory().setItemInOffHand(mapStack);
        }
        else {
            player.getInventory().setItemInMainHand(mapStack);
        }
    }

    static boolean IsOreMap(ItemStack check) {
        if(!RegionalOres.IsStackEmpty(check) &&
                check.getType() == Material.FILLED_MAP && //If is a filled map.
                check.hasItemMeta() && //Contains metadata.
                check.getItemMeta() instanceof MapMeta) { //Has map metadata.

            if(check.getItemMeta().hasLore()) {
                for (String lore : check.getItemMeta().getLore()) { //Obtain lore strings.
                    if (lore.equals("RegionalOres Plugin Addition")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void FindAndRemoveOreMaps(Player player) {
        if(player != null && player.getInventory() != null) {
            Inventory inv = player.getInventory();
            for (ItemStack stack : inv.getContents()) {
                if (IsOreMap(stack)) {
                    inv.remove(stack);
                }
            }
        }
    }


    public static class Events implements Listener {

        @EventHandler
        public void onDroppedItem (PlayerDropItemEvent event) {
            if(IsOreMap(event.getItemDrop().getItemStack())) {
                event.getItemDrop().remove();
            }
        }

        @EventHandler
        public void onClickedOnItem (InventoryClickEvent event) {
            ItemStack stack = event.getCurrentItem();
            if(IsOreMap(stack)) {
                event.setCancelled(true);
                event.getInventory().remove(stack);
                event.setCurrentItem(null);
            }
        }

        @EventHandler
        public void onPlayerInteractEntityEvent (PlayerInteractEntityEvent event) {
            if (IsOreMap(event.getPlayer().getInventory().getItemInMainHand())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerChangeWorld (PlayerChangedWorldEvent event) {
            FindAndRemoveOreMaps(event.getPlayer());
        }

        @EventHandler
        public void onPlayerDied (PlayerDeathEvent event) { FindAndRemoveOreMaps(event.getEntity() );}

        @EventHandler
        public void onPlayerJoin (PlayerJoinEvent event) { FindAndRemoveOreMaps(event.getPlayer()); } //Just for compatibility.

        @EventHandler
        public void onPlayerQuit (PlayerQuitEvent event) {
            FindAndRemoveOreMaps(event.getPlayer());
        }

    }
}