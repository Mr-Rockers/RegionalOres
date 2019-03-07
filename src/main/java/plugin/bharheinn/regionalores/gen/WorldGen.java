package plugin.bharheinn.regionalores.gen;

import javafx.util.Pair;
import org.bukkit.Bukkit;
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
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class WorldGen implements Listener{

    public World regionalOresWorld;
    private SimplexOctaveGenerator oreRedistributionNoise;
    private ArrayList<Material> sortedOrePool = new ArrayList<>();
    private ArrayList<Pair<Double, Double>> sortedOrePrecedenceBounds = new ArrayList<>();
    private HashMap<Material, String> simpleOreNames = new HashMap<>(); //Populated during createOrePool();

    public void createRegionalOresWorld() {
        //Sort ore pool.
        RegionalOres.INSTANCE.getLogger().info("Sorting ore pool...");
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

        //Generate ore precedence bounds.
        RegionalOres.INSTANCE.getLogger().info("Establishing ore precedence bounds...");
        establishOrePrecedenceBounds();

        //Create the actual world.
        RegionalOres.INSTANCE.getLogger().info("Creating the RegionalOres world.");
        oreRedistributionNoise = new SimplexOctaveGenerator(RegionalOres.INSTANCE.configIO.configData_GenSeed, 1);
        oreRedistributionNoise.setScale(1.0D);

        //Add the OrePopulator through onWorldInit event.
        RegionalOres.INSTANCE.getServer().getPluginManager().registerEvents(this, RegionalOres.INSTANCE); //Begin listening to events in order to catch oreReplacerEvents.
        WorldCreator wc = new WorldCreator("world_regionalores").environment(World.Environment.NORMAL); //Establish the criteria for the new world.
        regionalOresWorld = wc.createWorld(); //Create the world.
        HandlerList.unregisterAll(this); //Stop listening as we are finished.
    }

    @EventHandler
    public void onWorldInit (WorldInitEvent event) {
        RegionalOres.INSTANCE.getLogger().info("Added the OrePopulator BlockPopulator to " + event.getWorld().getName() + ".");
        event.getWorld().getPopulators().add(new OrePopulator());
    }

    // Used to create a loose of standardization for the generator (that isn't otherwise hardcoded).
    // Essentially an insertion sort algorithm that takes certain properties of the Materials into accounts.
    private void createOrePool() {
        Set<Material> unorderedOrePool = RegionalOres.INSTANCE.configIO.configTable_OresToReplace.keySet();
        for(Material unorderedOre : unorderedOrePool) {
            if(RegionalOres.INSTANCE.configIO.configTable_OresToReplace.get(unorderedOre) == 0) {
                continue; //We don't want to add disabled ores to the pool.
            }

            if(sortedOrePool.isEmpty()) {
                sortedOrePool.add(unorderedOre);
                simpleOreNames.put(unorderedOre, simplifyMaterialName(unorderedOre));
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
                            simpleOreNames.put(unorderedOre, simplifyMaterialName(unorderedOre));
                            finished = true;
                            break;
                    }
                    if(finished) {
                        break;
                    } else if (i == sortedOrePool.size() - 1) { //We are at the last element.
                        sortedOrePool.add(unorderedOre);
                        simpleOreNames.put(unorderedOre, simplifyMaterialName(unorderedOre));
                    }
                }
            }
        }
    }

    // Create a simple lookup table for ore precedence bounds.
    private void establishOrePrecedenceBounds() {
        int[] rawPrecedence = new int[sortedOrePool.size()];
        for(int i = 0; i < rawPrecedence.length; i++) {
            rawPrecedence[i] = RegionalOres.INSTANCE.configIO.configTable_OresToReplace.get(sortedOrePool.get(i));
        }
        int totalPrecedence = 0;
        for (int i = 0; i < rawPrecedence.length; i++) {
            totalPrecedence += rawPrecedence[i];
        }
        if (totalPrecedence == 0) {
            RegionalOres.INSTANCE.getLogger().log(Level.SEVERE, "TOTAL PRECEDENCE IS ZERO. PLUGIN CANNOT FUNCTION. PLEASE EDIT plugins/RegionalOres/config.yml");
            Bukkit.shutdown();
            return;
        }

        //Calculate precedence in pairs of <lowerBound, upperBound>
        double previousPrecedence = 0.0D;
        for(int i = 0; i < rawPrecedence.length; i++) {

            double precedence = previousPrecedence + (double)rawPrecedence[i] / (double)totalPrecedence;
            if(i == 0) {
                sortedOrePrecedenceBounds.add(new Pair<>(0.0D, precedence));
            }
            else if(i == rawPrecedence.length - 1) {
                sortedOrePrecedenceBounds.add(new Pair<>(previousPrecedence, 1.0D));
            }
            else {
                sortedOrePrecedenceBounds.add(new Pair<>(previousPrecedence, precedence));
            }

            previousPrecedence = precedence;
        }

    }

    public Material getOreMaterialType(int x, int z) {

        double genScale = RegionalOres.INSTANCE.configIO.configData_GenScale;

        //Obtain noise value.
        double noiseValue = (oreRedistributionNoise.noise((double)x / genScale, (double)z / genScale, 1.0D, 1.0D, true) + 1.0D) / 2.0D;

        //Get index for value in ore precedence bounds.
        int materialIndex = 0;
        for(int i = 0; i < sortedOrePrecedenceBounds.size(); i++) {
            if(noiseValue >= sortedOrePrecedenceBounds.get(i).getKey() && noiseValue < sortedOrePrecedenceBounds.get(i).getValue()) {
                materialIndex = i;
                break;
            }
        }

        //Return ore type.
        return sortedOrePool.get(materialIndex);
    }

    public String getSimpleOreName(Material oreMaterial) {
        if(!simpleOreNames.isEmpty()) {
            String simpleMaterialName = simpleOreNames.get(oreMaterial);
            if(simpleMaterialName != null) {
                return simpleMaterialName;
            }
        }
        return oreMaterial.name();
    }

    private String simplifyMaterialName(Material material) {
        //First, check configTable_Map_LangOreNames just to make sure that there hasn't been a custom value set by the server owner.
        if(!RegionalOres.INSTANCE.configIO.configTable_Map_LangOreNames.isEmpty()) {
            String customMaterialName = RegionalOres.INSTANCE.configIO.configTable_Map_LangOreNames.get(material);
            if(customMaterialName != null) {
                return customMaterialName;
            }
        }

        //Otherwise, "fix" the default material names to something a bit more pleasant.
        String rawName = material.name().toLowerCase();
        String simpleName = "";

        boolean capitalise = true;
        for(int i = 0; i < rawName.length(); i++) {
            char currentChar = rawName.charAt(i);
            if(currentChar == '_') {
                simpleName += ' ';
                capitalise = true;
            }
            else if(capitalise) {
                simpleName += Character.toUpperCase(currentChar);
                capitalise = false;
            }
            else {
                simpleName += Character.toLowerCase(currentChar);
            }
        }
        return simpleName;
    }
}
