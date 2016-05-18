package net.newtownia.NTAC.Utils;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public class MathUtils
{
    public static double getYawDiff(Location attacker, Location target)
    {
        Location origin = attacker.clone();
        Vector originVec = origin.toVector();
        Vector targetVec = target.toVector();
        origin.setDirection(targetVec.subtract(originVec));
        double yaw = (double)origin.getYaw() - attacker.getYaw();
        return yaw % 180;
    }

    public static double getAverageDouble(List<Double> numbers)
    {
        double tmp = 0;
        for (double number : numbers)
            tmp += number;
        return tmp / numbers.size();
    }

    public static double getAverageInteger(List<Integer> numbers)
    {
        double tmp = 0;
        for (int number : numbers)
            tmp += number;
        return tmp / numbers.size();
    }

    public static boolean isPositionSame(Location loc1, Location loc2, double threshold)
    {
        return isSame(loc1.getX(), loc2.getX(), threshold) ||
                isSame(loc1. getY(), loc2.getY(), threshold) ||
                isSame(loc1.getZ(), loc2.getZ(), threshold);
    }

    public static boolean isRotationSame(Location loc1, Location loc2, double threshold)
    {
        return isSame((double) loc1.getYaw(), (double)loc2.getYaw(), threshold) ||
                isSame((double) loc2.getPitch(), (double) loc2.getPitch(), threshold);
    }

    public static boolean isSame(double a, double b, double threshold)
    {
        if (a == b)
            return true;
        if (threshold == 0)
            return a == b;

        double min = a - threshold;
        double max = a + threshold;
        return b > min && b < max;
    }

    public static double getYDiff(PlayerMoveEvent event)
    {
        return event.getTo().getY() - event.getFrom().getY();
    }
}
