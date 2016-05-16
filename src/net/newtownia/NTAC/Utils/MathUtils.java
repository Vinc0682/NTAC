package net.newtownia.NTAC.Utils;

import org.bukkit.Location;
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
}
