package plugin.bharheinn.regionalores.gen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import plugin.bharheinn.regionalores.RegionalOres;

import java.util.ArrayList;
import java.util.Set;

public class WorldGen implements Listener{

    public World regionalOresWorld;
    public SimplexOctaveGenerator oreRedistributionNoise;
    public ArrayList<Material> sortedOrePool = new ArrayList<>();

    public void createRegionalOresWorld() {
        RegionalOres.INSTANCE.getLogger().info("Creating the RegionalOres world.");

        RegionalOres.INSTANCE.getServer().getPluginManager().registerEvents(this, RegionalOres.INSTANCE); //Begin listening to events in order to catch oreReplacerEvents.
        WorldCreator wc = new WorldCreator("world_regionalores").environment(World.Environment.NORMAL); //Establish the criteria for the new world.
        regionalOresWorld = wc.createWorld(); //Create the world.
        HandlerList.unregisterAll(this); //Stop listening as we are finished.

        oreRedistributionNoise = new SimplexOctaveGenerator(RegionalOres.INSTANCE.configIO.config_oreGenSeed, 2);

        RegionalOres.INSTANCE.getLogger().info("Generating ore pool...");
        createOrePool();
        String orePoolMessageOutput = "Loaded materials (ores): [";
        for(int i = 0; i < sortedOrePool.size(); i++) {
            orePoolMessageOutput += (i + 1) + ". - " + sortedOrePool.get(i).name();
            if(i != sortedOrePool.size() - 1) {
                orePoolMessageOutput += ", ";
            }
            else {
                orePoolMessageOutput += "]";
            }
        }
        RegionalOres.INSTANCE.getLogger().info(orePoolMessageOutput);
    }

    @EventHandler
    public void onWorldInit (WorldInitEvent event) {
        RegionalOres.INSTANCE.getLogger().info("Added the OrePopulator BlockPopulator to " + event.getWorld().getName() + ".");
        event.getWorld().getPopulators().add(new OrePopulator());
    }

    // Used to create a loose of standardization for the generator (that isn't otherwise hardcoded).
    private void createOrePool() {
        Set<Material> unorderedOrePool = RegionalOres.INSTANCE.configIO.configTable_ToReplace.keySet();
        for(Material unorderedOre : unorderedOrePool) {
            if(RegionalOres.INSTANCE.configIO.configTable_ToReplace.get(unorderedOre) == 0) {
                continue; //We don't want to add disabled ores to the pool.
            }
            if(sortedOrePool.isEmpty()) {
                sortedOrePool.add(unorderedOre);
            }
            else {
                for (int i = 0; i < sortedOrePool.size(); i++) {
                    boolean finished = false;

                    if(unorderedOre == sortedOrePool.get(i)) {
                        break; //We don't want to add the same ore twice.
                    }

                    for(int charPos = 0; charPos < unorderedOre.name().length(); charPos++) {
                        if(sortedOrePool.get(i).name().length() <= charPos) {
                            break; //We have gotten to the end of the comparison and must continue.. or break... yeah, break.
                        }

                        char charAtCharPos = unorderedOre.name().charAt(charPos);
                        char sortedCharAtCharPos = sortedOrePool.get(i).name().charAt(charPos);
                        if(charAtCharPos > sortedCharAtCharPos) {
                            break; //We no longer need to compare this ore as it is alphabetically greater. On to the next one...
                        }
                        if(charAtCharPos == sortedCharAtCharPos) {
                            continue; //Continue to the next character.
                        }
                        //Character at this point is alphabetically lower(?) and now needs to be placed.
                            sortedOrePool.add(i, unorderedOre);
                            finished = true;
                            break;
                    }
                    if(finished) {
                        break;
                    } else if (i == sortedOrePool.size() - 1) { //We are at the last element.
                        sortedOrePool.add(unorderedOre);
                    }
                }
            }
        }
    }


}
