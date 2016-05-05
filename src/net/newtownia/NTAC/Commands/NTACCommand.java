package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NTACCommand implements CommandExecutor
{
    NTAC pl;

    public NTACCommand(NTAC pl)
    {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
        if(args.length == 0)
        {
            pl.getMessageUtils().printMessage(cs, "Version");
            return true;
        }

        if(args.length >= 1)
        {
            switch (args[0])
            {
                case "reload":
                    if (!cs.hasPermission("ntac.command.reload"))
                    {
                        pl.getMessageUtils().printMessage(cs, "NoPermission");
                        break;
                    }

                    pl.reload();
                    pl.getMessageUtils().printMessage(cs, "Reload");
                    break;
                case "kick":
                    if (!cs.hasPermission("ntac.command.kick"))
                    {
                        pl.getMessageUtils().printMessage(cs, "NoPermission");
                        break;
                    }

                    if(args.length > 1)
                    {
                        Player p = Bukkit.getPlayer(args[1]);
                        if(p != null)
                        {
                            String message = "";
                            if(args.length > 2) {
                                for (int i = 2; i < args.length; i += 1)
                                    message += args[i] + " ";
                            }
                            message = pl.getMessageUtils().formatMessage(message);
                            PunishUtils.kickPlayer(p, message);
                        }
                    }
                    break;
                case "notify":
                    if (!cs.hasPermission("ntac.command.notify"))
                    {
                        pl.getMessageUtils().printMessage(cs, "NoPermission");
                        break;
                    }

                    if(args.length > 1)
                        pl.getMessageUtils().printNotify(args);
                    break;
            }
        }

        return true;
    }
}
