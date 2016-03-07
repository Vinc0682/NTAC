package net.newtownia.NTAC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    public static int getPing(Player p)
    {
        //Get version number
        String bpName = Bukkit.getServer().getClass().getPackage().getName();
        String version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());

        try
        {
            //Get craft player
            Class<?> CPClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Object craftPlayer = CPClass.cast(p);

            //Get EntityPlayer
            Method getHandle = craftPlayer.getClass().getMethod("getHandle", new Class[0]);
            Object EntityPlayer = getHandle.invoke(craftPlayer, new Object[0]);

            //Get Ping-Field
            Field ping = EntityPlayer.getClass().getDeclaredField("ping");

            //Return value of the Ping-Field
            return ping.getInt(EntityPlayer);
        }
        catch (Exception e) { }

        return 100;
    }
}
