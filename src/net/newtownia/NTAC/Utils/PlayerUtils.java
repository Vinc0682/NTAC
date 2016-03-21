package net.newtownia.NTAC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PlayerUtils
{
    private static ArrayList<Material> usableItems = null;

    public static boolean isPlayerOnGround(Player p)
    {
        Block blockDown = p.getLocation().getBlock().getRelative(BlockFace.DOWN);

        ArrayList<Material> materials = new ArrayList<>();
        materials.add(blockDown.getType());
        materials.add(blockDown.getRelative(BlockFace.NORTH).getType());
        materials.add(blockDown.getRelative(BlockFace.NORTH_EAST).getType());
        materials.add(blockDown.getRelative(BlockFace.EAST).getType());
        materials.add(blockDown.getRelative(BlockFace.SOUTH_EAST).getType());
        materials.add(blockDown.getRelative(BlockFace.SOUTH).getType());
        materials.add(blockDown.getRelative(BlockFace.SOUTH_WEST).getType());
        materials.add(blockDown.getRelative(BlockFace.WEST).getType());
        materials.add(blockDown.getRelative(BlockFace.NORTH_WEST).getType());

        for (Material m : materials)
            if (m != Material.AIR)
                return true;
        return false;
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
