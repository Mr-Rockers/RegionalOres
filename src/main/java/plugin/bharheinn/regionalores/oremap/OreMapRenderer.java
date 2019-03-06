package plugin.bharheinn.regionalores.oremap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import plugin.bharheinn.regionalores.RegionalOres;

public class OreMapRenderer extends MapRenderer {
    private MapCursor playerCursor;
    private MapCursorCollection cursorCollection;
    private MinecraftFont mcFont;

    private int mapScale;

    public OreMapRenderer() {
        playerCursor = new MapCursor((byte)0, (byte)0, (byte)0, MapCursor.Type.WHITE_POINTER, true);
        cursorCollection = new MapCursorCollection();
        cursorCollection.addCursor(playerCursor);
        mcFont = new MinecraftFont();

        mapScale = RegionalOres.INSTANCE.configIO.configData_MapScale;
    }

    private byte getOreColour (MapCanvas canvas, int x, int z, Material oreMaterial) {
        if (RegionalOres.INSTANCE.configIO.configTable_MapColors.get(oreMaterial) != null) {
            return RegionalOres.INSTANCE.configIO.configTable_MapColors.get(oreMaterial);
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

            playerCursor.setCaption("X: " + playerX + ", Z: " + playerZ);

            int rawDirection = (int) ((player.getLocation().getYaw() / 360.0f) * 15.0f);
            playerCursor.setDirection((byte) (rawDirection > 15 ? 15 : rawDirection < 0 ? rawDirection + 15 : rawDirection));
            canvas.setCursors(cursorCollection);

            int infoTextHeight = mcFont.getHeight() * 2;
            Material currentPositionMaterial = RegionalOres.INSTANCE.worldGen.getOreMaterialType(playerX, playerZ);

            for (int x = 0; x < 128; x++) {
                for (int z = 0; z < 128; z++) {

                    Material mapRenderMaterial = RegionalOres.INSTANCE.worldGen.getOreMaterialType(playerX + (x - 64) * mapScale, playerZ + (z - 64) * mapScale);

                    //Draw ore colour square in the top right.
                    if (x > 128 - 3 - infoTextHeight && x < 128 - 3 && z > 2 && z < 2 + infoTextHeight) {
                        canvas.setPixel(x, z, getOreColour(canvas, playerX, playerZ, currentPositionMaterial));
                    }
                    //Draw ore map from noise data.
                    else {
                        if (z > infoTextHeight + 4) {

                            //Draw base colours - the actual ore map.
                            byte currentOreColour = getOreColour(canvas, x, z, mapRenderMaterial);
                            canvas.setPixel(x, z, currentOreColour);

                            //Effectively, apply an "outline" filter.
                            byte outlineColour = (byte) 34;
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
                            canvas.setPixel(x, z, (byte) 91); //TODO Make the colour configurable in the config.yml
                        }
                    }
                }
            }
            canvas.drawText(2, 2, mcFont, "§33;Current Region: §34;\n" + RegionalOres.INSTANCE.worldGen.getSimpleOreName(currentPositionMaterial));
        }
    }
}
