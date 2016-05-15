package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.DateUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class Ban extends SubCommand
{
    public Ban()
    {
        super("ban");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.punish.ban"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }

        if(args.length > 1)
        {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if(p != null)
            {
                long banUntil = -1;
                if (args.length > 2)
                {
                    try
                    {
                        banUntil = DateUtils.parseDateDiff(args[2], true);
                    }
                    catch (Exception e)
                    {
                        pl.getMessageUtils().printMessage(cs, e.getMessage());
                    }
                }

                String message = "%%Ban-Default-Reason%%";
                if(args.length > 3)
                {
                    message = "";
                    for (int i = 3; i < args.length; i += 1)
                        message += args[i] + " ";
                }
                PunishUtils.banPlayer(p, message, banUntil);
            }
        }
    }
}
