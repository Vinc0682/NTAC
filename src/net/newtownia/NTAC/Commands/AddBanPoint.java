package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class AddBanPoint extends SubCommand
{
    public AddBanPoint()
    {
        super("addBanPoint");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.punish.addbanpoint"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }

        if(args.length > 1)
        {
            Player p = Bukkit.getPlayer(args[1]);
            if(p != null)
            {
                int points = 1;
                if (args.length > 2)
                    points = Integer.valueOf(args[2]);
                NTAC.getInstance().getBanManger().addVL(p.getUniqueId(), points);
            }
        }
    }
}
