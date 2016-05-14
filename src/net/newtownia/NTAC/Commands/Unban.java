package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class Unban extends SubCommand
{
    public Unban()
    {
        super("Unban");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.punish.unban"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }

        if (args.length > 1)
        {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if (p != null)
                pl.getBanManger().removeBan(p.getUniqueId());
        }
    }
}
