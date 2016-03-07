package net.newtownia.NTAC.Utils;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class PunishUtils
{
    public static void kickPlayer(final Player p, final String reason)
    {
        Bukkit.getScheduler().callSyncMethod(NTAC.getInstance(), new Callable<Boolean>()
        {
            public Boolean call()
            {
                p.kickPlayer(reason);
                return true;
            }
        });
    }

    public static void runViolationAction(Player p, ViolationManager manager, ActionData data)
    {
        int vl = manager.getViolation(p);
        runViolationAction(p, vl, vl, data);
    }

    public static void runViolationActionWithValidation(Player p, ViolationManager manager, ActionData data)
    {
        int vl = manager.getViolation(p);
        runViolationAction(p, data.getValidViolationLevel(vl), vl, data);
    }

    public static void runViolationAction(Player p, int violation, int realVL, ActionData data)
    {
        List<String> violationCommands = data.getViolationCommands(violation);

        if(violationCommands == null)
            return;

        for (String command : violationCommands)
        {
            String realCommand = command.replaceAll("%PLAYER%", p.getName());
            realCommand = realCommand.replaceAll("%VL%", String.valueOf(realVL));
            dispatchCommandSynced(realCommand);
        }
    }

    public static void dispatchCommandSynced(final String command)
    {
        if(command.isEmpty())
            return;

        if(command.equalsIgnoreCase("cancel"))
            return;

        Bukkit.getScheduler().callSyncMethod(NTAC.getInstance(), new Callable<Boolean>()
        {
            public Boolean call()
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                return true;
            }
        });
    }
}
