package net.newtownia.NTAC.Utils;

import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 28.05.2016.
 */
public class LogUtils
{
    private static boolean INFO = true;
    private static boolean ERROR = true;
    private static boolean DEBUG = true;

    public static void info(String message)
    {
        if (INFO)
            printToConsole("&eINFO &7" + message);
    }

    public static void error(String message)
    {
        if (ERROR)
            printToConsole("&$ERROR &7" + message);
    }

    public static void debug(Player p, String message)
    {
        if (DEBUG)
            p.sendMessage(message);
        debug(message);
    }

    public static void debug(String message)
    {
        if (DEBUG)
        {
            printToConsole("&4DEBUG &7" + message);
        }
    }

    public static void printToConsole(String message)
    {
        String realMessage = NTAC.getInstance().getMessageUtils().formatMessage("%%Notify-Prefix%%" + message);
        Bukkit.getConsoleSender().sendMessage(realMessage);
    }
}
