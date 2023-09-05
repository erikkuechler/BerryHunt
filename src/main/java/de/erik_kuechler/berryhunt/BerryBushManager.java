package de.erik_kuechler.berryhunt;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Ageable;
import java.util.logging.Logger;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class BerryBushManager {
    private final World world;
    private final Location pos1;
    private final Location pos2;

    /**
     * Initializes a new BerryBushManager with the specified bounding box.
     * @param pos1 pos1 The first corner of the bounding box.
     * @param pos2 pos2 The second corner of the bounding box.
     */
    public BerryBushManager(Location pos1, Location pos2) {
        this.world = pos1.getWorld();
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Places sweet berry bushes within the defined bounding box.
     * @param density The density of berry bushes to place (0.0 to 1.0).
     */
    public void placeSweetBerryBushes(double density) {
        int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    
        List<Block> potentialBlocks = new ArrayList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                for (int y = world.getMaxHeight() - 1; y >= 0; y--) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.GRASS_BLOCK) {
                        Block above = block.getRelative(BlockFace.UP);
                        if (above.getType().isAir()) {
                            potentialBlocks.add(block);
                        }  
                    }
                }
            }
        }
    
        int numBushes = (int) (potentialBlocks.size() * density);
    
        Collections.shuffle(potentialBlocks);
        int numPlaced = 0;
        for (Block block : potentialBlocks) {
            if (numPlaced >= numBushes) {
                break;
            }
    
            Block above = block.getRelative(BlockFace.UP);
            BlockData blockData = Material.SWEET_BERRY_BUSH.createBlockData();
            above.setBlockData(blockData, true);
    
            Ageable age = (Ageable) above.getBlockData();
            age.setAge(age.getMaximumAge());
            above.setBlockData(age);
    
            numPlaced++;
        }
    
        Logger.getLogger("berryhunt").info("There were placed " + numPlaced + " sweet berry bushes.");
    }
    
    /**
     * Removes all sweet berry bushes within the defined bounding box.
     */
    public void removeSweetBerryBushes() {
        int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int numRemoved = 0;
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                for (int y = world.getMaxHeight() - 1; y >= 0; y--) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.SWEET_BERRY_BUSH) {
                        block.setType(Material.AIR);
                        numRemoved++;
                    }
                }
            }
        }
        Logger.getLogger("berryhunt").info("There were removed " + numRemoved + " sweet berry bushes.");
    }
}
