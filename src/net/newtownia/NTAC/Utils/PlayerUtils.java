package net.newtownia.NTAC.Utils;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.moving.model.MoveInfo;
import fr.neatmonster.nocheatplus.checks.moving.util.AuxMoving;
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
    private static ArrayList<Material> usableItems = null;

    public static boolean isPlayerOnGround(Player p)
    {
        if (Bukkit.getPluginManager().getPlugin("NoCheatPlus") != null)
            try
            {
                return isPlayerOnGroundNCP(p);
            }
            catch (Exception e)
            {
                return isPlayerOnGroundNTAC(p);
            }
        else
            return isPlayerOnGroundNTAC(p);
    }

    public static boolean isPlayerOnGroundNTAC(Player p)
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

    public static boolean isPlayerOnGroundNCP(Player p)
    {
        AuxMoving aux = NCPAPIProvider.getNoCheatPlusAPI().getGenericInstance(AuxMoving.class);
        MoveInfo moveInfo = aux.usePlayerMoveInfo();
        moveInfo.set(p, p.getLocation(), null, 0);
        return moveInfo.from.isOnGround();
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

    public static Location getPlayerStandOnBlockLocations(Location locationUnderPlayer, Material mat) {
        Location b11 = locationUnderPlayer.clone().add(0.3, 0, -0.3);
        if (b11.getBlock().getType() == mat) {
            return b11;
        }
        Location b12 = locationUnderPlayer.clone().add(-0.3, 0, -0.3);
        if (b12.getBlock().getType() == mat) {
            return b12;
        }
        Location b21 = locationUnderPlayer.clone().add(0.3, 0, 0.3);
        if (b21.getBlock().getType() == mat) {
            return b21;
        }
        Location b22 = locationUnderPlayer.clone().add(-0.3, 0, +0.3);
        if (b22.getBlock().getType() == mat) {
            return b22;
        }
        return locationUnderPlayer;
    }

    public static boolean isInWater(Player p) {
        Location loc = p.getLocation().subtract(0, 0.2, 0);
        if (getPlayerStandOnBlockLocations(loc, Material.STATIONARY_WATER).getBlock().getType() == Material.STATIONARY_WATER || getPlayerStandOnBlockLocations(loc, Material.WATER).getBlock().getType() == Material.WATER) {
            return true;
        }
        return false;
    }

    public static boolean isInBlock(Player p, Material block) {
        Location loc = p.getLocation().add(0, 0, 0);
        if (getPlayerStandOnBlockLocations(loc, block).getBlock().getType() == block) {
            return true;
        }
        return false;
    }

    public static boolean isOnWater(Player p) {
        Location loc = p.getLocation().subtract(0, 1, 0);
        if (getPlayerStandOnBlockLocations(loc, Material.STATIONARY_WATER).getBlock().getType() == Material.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

    public static boolean isOnBlock(Player p, Material mat) {
        Location loc = p.getLocation().subtract(0, 1, 0);
        if (getPlayerStandOnBlockLocation(loc, mat).getBlock().getType() == mat) {
            return true;
        }
        return false;
    }

    public static List<Material> getMaterialsBelow(Player p)
    {
        return getMaterialsBelow(p.getLocation());
    }

    public static List<Material> getMaterialsBelow(Location loc)
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
        return getMaterialsBelow(p).contains(m);
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
