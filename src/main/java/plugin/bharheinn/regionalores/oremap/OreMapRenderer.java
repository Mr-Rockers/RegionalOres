package plugin.bharheinn.regionalores.oremap;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class OreMapRenderer extends MapRenderer {
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        for (int x = 0; x < 127; x++) {
            for (int z = 0; z < 127; z++) {
                canvas.setPixel(x, z, (byte)66);
            }
        }
    }
}
