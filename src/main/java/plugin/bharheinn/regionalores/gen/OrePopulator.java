package plugin.bharheinn.regionalores.gen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import plugin.bharheinn.regionalores.RegionalOres;

import java.util.Map;
import java.util.Random;

public class OrePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material blockMaterial = block.getType();
                    for(Map.Entry<Material, Boolean> p : RegionalOres.INSTANCE.configIO.configTable_Gen_OresToRemove.entrySet()) {
                        if(p.getKey() == blockMaterial) {
                            if(p.getValue()) {
                                block.setType(RegionalOres.INSTANCE.worldGen.getOreMaterialType(chunk.getX() * 16 + x, chunk.getZ() * 16 + z), false);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
