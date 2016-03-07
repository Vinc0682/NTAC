package net.newtownia.NTAC.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class PlayerUtils
{
    public static boolean isPlayerOnGround(Player p)
    {
        Block blockDown = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        Block blockNorth = blockDown.getRelative(BlockFace.NORTH);
        Block blockEast = blockDown.getRelative(BlockFace.EAST);
        Block blockSouth = blockDown.getRelative(BlockFace.SOUTH);
        Block blockWest = blockDown.getRelative(BlockFace.WEST);

        return blockDown.getType() != Material.AIR || blockNorth.getType() != Material.AIR ||
                blockEast.getType() != Material.AIR || blockSouth.getType() != Material.AIR ||
                blockWest.getType() != Material.AIR;
    }
}
