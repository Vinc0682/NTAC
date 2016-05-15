package net.newtownia.NTAC.Utils;

import org.bukkit.Location;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public class MathUtils
{
    public static double getYawDiff(Location loc1, Location loc2)
    {
        double diffX = loc1.getX() - loc2.getX();
        double diffZ = loc1.getZ() - loc2.getX();
        return Math.toDegrees(Math.atan2(diffZ, diffX));
    }
}
