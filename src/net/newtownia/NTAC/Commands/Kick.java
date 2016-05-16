package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class Kick extends SubCommand
{
    public Kick() {
        super("kick");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.punish.kick"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
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
                pl.getMessageUtils().printMessage(cs, "Kicked", p.getName());
            }
            else
            {
                pl.getMessageUtils().printMessage(cs, "PlayerNotFound", args[1]);
            }
        }
    }
}
