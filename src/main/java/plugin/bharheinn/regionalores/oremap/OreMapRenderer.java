package plugin.bharheinn.regionalores.oremap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import plugin.bharheinn.regionalores.RegionalOres;

public class OreMapRenderer extends MapRenderer {
    private MapCursor playerCursor;
    private MapCursor scaleCursor;
    private MapCursor versionCursor;
    private MapCursor rarityCursor;
    private MapCursorCollection cursorCollection;
    private MinecraftFont mcFont;

    private int mapScale;

    public OreMapRenderer() {
        playerCursor = new MapCursor((byte)0, (byte)0, (byte)0, MapCursor.Type.WHITE_POINTER, true);
        scaleCursor = new MapCursor((byte)-100, (byte)110, (byte)0, MapCursor.Type.GREEN_POINTER, true);
        versionCursor = new MapCursor((byte)0, (byte)110, (byte)0, MapCursor.Type.GREEN_POINTER, true);
        rarityCursor = new MapCursor((byte)100, (byte)110, (byte)0, MapCursor.Type.GREEN_POINTER, true);
        cursorCollection = new MapCursorCollection();
        cursorCollection.addCursor(playerCursor);
        cursorCollection.addCursor(scaleCursor);
        cursorCollection.addCursor(versionCursor);
        cursorCollection.addCursor(rarityCursor);
        mcFont = new MinecraftFont();

        mapScale = RegionalOres.INSTANCE.configIO.configData_Map_Scale;
    }

    private byte getOreColour (Material oreMaterial) {
        if (RegionalOres.INSTANCE.configIO.configTable_Map_ColorOres.get(oreMaterial) != null) {
            return (byte)((int)RegionalOres.INSTANCE.configIO.configTable_Map_ColorOres.get(oreMaterial));
        } else {
            return (byte)66; //Horrendous Error Pink
        }
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {

        //Little optimisation. Stops the map from rendering if it's not in the main hand or in the off hand.
        if (OreMap.IsOreMap(player.getInventory().getItemInMainHand()) || OreMap.IsOreMap(player.getInventory().getItemInOffHand())) {

            int playerX = (int) Math.floor(player.getLocation().getX());
            int playerZ = (int) Math.floor(player.getLocation().getZ());

            int infoTextHeight = mcFont.getHeight() * 2;
            Material currentPositionMaterial = RegionalOres.INSTANCE.worldGen.getOreMaterialType(playerX, playerZ);

            int rawDirection = (int) ((player.getLocation().getYaw() / 360.0f) * 15.0f);
            playerCursor.setDirection((byte) (rawDirection > 15 ? 15 : rawDirection < 0 ? rawDirection + 15 : rawDirection));
            canvas.setCursors(cursorCollection);

            playerCursor.setCaption("X: " + playerX + " Z: " + playerZ);

            //Does the player have permission to see the advanced info?
            boolean playerAdvancedPerms = (!RegionalOres.INSTANCE.configIO.configData_UtilPermissions || player.hasPermission("regionalores.oremap.advanced"));
            if(playerAdvancedPerms) {
                scaleCursor.setCaption("Scale 1:" + mapScale);
                versionCursor.setCaption("Version " + RegionalOres.PLUGIN_VERSION);
                rarityCursor.setCaption("Ore Rarity " + (int) RegionalOres.INSTANCE.worldGen.getOreMaterialRarity(currentPositionMaterial) + "%");
            }
            scaleCursor.setVisible(playerAdvancedPerms);
            versionCursor.setVisible(playerAdvancedPerms);
            rarityCursor.setVisible(playerAdvancedPerms);

            for (int x = 0; x < 128; x++) {
                for (int z = 0; z < 128; z++) {

                    Material mapRenderMaterial = RegionalOres.INSTANCE.worldGen.getOreMaterialType(playerX + (x - 64) * mapScale, playerZ + (z - 64) * mapScale);

                    //Draw ore colour square in the top right.
                    if (x > 128 - 3 - infoTextHeight && x < 128 - 3 && z > 2 && z < 2 + infoTextHeight) {
                        canvas.setPixel(x, z, getOreColour(currentPositionMaterial));
                    }
                    //Draw ore map from noise data.
                    else {
                        if (z > infoTextHeight + 4) {

                            //Draw base colours - the actual ore map.
                            byte currentOreColour = getOreColour(mapRenderMaterial);
                            canvas.setPixel(x, z, currentOreColour);

                            //Effectively, apply an "outline" filter.
                            byte outlineColour = (byte)RegionalOres.INSTANCE.configIO.configData_Map_ColorOutline;
                            boolean surroundingPixelDifferent = false;
                            if (x > 0) { //Do not factor in left edge.
                                byte previousPixel = canvas.getPixel(x - 1, z);

                                //If the previous pixel is not an outline and is not part of the same region.
                                if (previousPixel != currentOreColour && previousPixel != outlineColour) {
                                    surroundingPixelDifferent = true;
                                }
                            }
                            if (z > infoTextHeight + 4 + 1) { //Do not factor in bottom of "current region" section.
                                byte previousPixel = canvas.getPixel(x, z - 1);

                                //If the previous pixel is not an outline and is not part of the same region.
                                if (previousPixel != currentOreColour && previousPixel != outlineColour) {
                                    surroundingPixelDifferent = true;
                                }
                            }

                            if (surroundingPixelDifferent) {
                                //The marked pixel is an outline, hence, draw it as such.
                                canvas.setPixel(x, z, outlineColour);
                            }
                        } else {
                            //Draw the background to the "current region" section.
                            canvas.setPixel(x, z, (byte)RegionalOres.INSTANCE.configIO.configData_Map_ColorInfoBox);
                        }
                    }
                }
            }
            canvas.drawText(2, 2, mcFont, RegionalOres.INSTANCE.configIO.configData_Map_LangCurrentRegion + "\n" + RegionalOres.INSTANCE.worldGen.getSimpleOreName(currentPositionMaterial));
        }
    }
}
