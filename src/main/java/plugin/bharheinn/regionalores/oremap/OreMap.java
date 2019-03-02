package plugin.bharheinn.regionalores.oremap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import plugin.bharheinn.regionalores.RegionalOres;

public class OreMap implements Listener {

    private ItemStack mapStack;

    public OreMap(Player player) {

        MapView mv = RegionalOres.INSTANCE.getServer().createMap(RegionalOres.INSTANCE.worldGen.regionalOresWorld);
        mv.addRenderer(new OreMapRenderer());

        mapStack = new ItemStack(Material.FILLED_MAP, 1);

        MapMeta mm = (MapMeta) mapStack.getItemMeta();
        mm.setMapView(mv);
        mm.setDisplayName("Ore Map");

        mapStack.setItemMeta(mm);

        player.getInventory().setItemInMainHand(mapStack);
    }

    public static boolean IsOreMap(ItemStack check) {
        if(!RegionalOres.IsStackEmpty(check) &&
                check.getType() == Material.FILLED_MAP && //If is a filled map.
                check.hasItemMeta() && //Contains metadata.
                check.getItemMeta() instanceof MapMeta) { //Has map metadata.

            //TODO Change this method. This is probably not the best way to check if the map is an OreMap...
            for (MapRenderer renderer : ((MapMeta) check.getItemMeta()).getMapView().getRenderers()) { //Obtain all renderers.
                if(renderer instanceof OreMapRenderer) { //If map renderer is an OreMapRenderer, return true.
                    return true;
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
        public void onPlayerChangeWorld (PlayerChangedWorldEvent event) {
            FindAndRemoveOreMaps(event.getPlayer());
        }

        @EventHandler
        public void onPlayerJoin (PlayerJoinEvent event) { FindAndRemoveOreMaps(event.getPlayer()); } //Just for compatibility.

        @EventHandler
        public void onPlayerQuit (PlayerQuitEvent event) {
            FindAndRemoveOreMaps(event.getPlayer());
        }

    }
}