package net.newtownia.NTAC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PlayerUtils
{
    public static boolean isPlayerOnGround(Player p)
    {
        return isPlayerOnGroundNTAC(p);
    }

    public static boolean isPlayerOnGroundNTAC(Player p)
    {
        List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0, -0.001, 0));
        for (Material m : materials)
            if (!MaterialUtils.isUnsolid(m) && m != Material.WATER && m != Material.STATIONARY_WATER &&
                    m != Material.LAVA && m != Material.STATIONARY_LAVA)
                return true;
        return false;
    }

    public static boolean isPlayerOnGroundNTACOld(Player p)
    {
        List<Material> materials = getMaterialsBelowOld(p);
        for (Material m : materials)
            if (m != Material.AIR && !MaterialUtils.isUnsolid(m) &&
                    m != Material.WATER && m != Material.STATIONARY_WATER &&
                    m != Material.LAVA && m != Material.STATIONARY_LAVA)
                return true;
        return false;
    }

    public static boolean isInWeb(Location loc)
    {
        return loc.getBlock().getType() == Material.WEB ||
                loc.getBlock().getRelative(BlockFace.UP).getType() == Material.WEB;
    }

    public static Location getPlayerStandOnBlockLocation(Location locationUnderPlayer, Material mat) {
        Location b11 = locationUnderPlayer.clone().add(0.3, 0, -0.3);
        if (b11.getBlock().getType() != mat) {
            return b11;
        }
        Location b12 = locationUnderPlayer.clone().add(-0.3, 0, -0.3);
        if (b12.getBlock().getType() != mat) {
            return b12;
        }
        Location b21 = locationUnderPlayer.clone().add(0.3, 0, 0.3);
        if (b21.getBlock().getType() != mat) {
            return b21;
        }
        Location b22 = locationUnderPlayer.clone().add(-0.3, 0, +0.3);
        if (b22.getBlock().getType() != mat) {
            return b22;
        }
        return locationUnderPlayer;
    }

    public static boolean isInWater(Player p) {
        Location loc = p.getLocation().subtract(0, 0.2, 0);
        return getPlayerStandOnBlockLocation(loc, Material.STATIONARY_WATER).getBlock().getType() == Material.STATIONARY_WATER
                || getPlayerStandOnBlockLocation(loc, Material.WATER).getBlock().getType() == Material.WATER;
    }

    public static boolean isInBlock(Player p, Material block) {
        Location loc = p.getLocation().add(0, 0, 0);
        return getPlayerStandOnBlockLocation(loc, block).getBlock().getType() == block;
    }

    public static boolean isOnWater(Player p) {
        Location loc = p.getLocation().subtract(0, 1, 0);
        return getPlayerStandOnBlockLocation(loc, Material.STATIONARY_WATER).getBlock().getType() == Material.STATIONARY_WATER;
    }

    public static boolean isOnBlock(Player p, Material mat) {
        Location loc = p.getLocation().subtract(0, 1, 0);
        return getPlayerStandOnBlockLocation(loc, mat).getBlock().getType() == mat;
    }

    public static List<Material> getMaterialsAround(Location loc)
    {
        List<Material> result = new ArrayList<>();
        result.add(loc.getBlock().getType());
        result.add(loc.clone().add(0.3, 0, -0.3).getBlock().getType());
        result.add(loc.clone().add(-0.3, 0, -0.3).getBlock().getType());
        result.add(loc.clone().add(0.3, 0, 0.3).getBlock().getType());
        result.add(loc.clone().add(-0.3, 0, 0.3).getBlock().getType());
        return result;
    }

    public static List<Material> getMaterialsBelowOld(Player p)
    {
        return getMaterialsBelowOld(p.getLocation());
    }

    public static List<Material> getMaterialsBelowOld(Location loc)
    {
        Block blockDown = loc.getBlock().getRelative(BlockFace.DOWN);

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

        return materials;
    }

    public static boolean materialsBelowContains(Player p, Material m)
    {
        return getMaterialsBelowOld(p).contains(m);
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
            Method getHandle = craftPlayer.getClass().getMethod("getHandle");
            Object EntityPlayer = getHandle.invoke(craftPlayer);

            //Get Ping-Field
            Field ping = EntityPlayer.getClass().getDeclaredField("ping");

            //Return value of the Ping-Field
            return ping.getInt(EntityPlayer);
        }
        catch (Exception e) { }

        return 100;
    }
}
