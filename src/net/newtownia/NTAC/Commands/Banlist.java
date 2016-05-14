package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.Action.BanManger;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class Banlist extends SubCommand
{
    public Banlist() {
        super("banlist");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.punish.banlist"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }

        BanManger banManger = pl.getBanManger();
        for (UUID pUUID : banManger.getBannedUUIDS())
        {
            String timeString = "&cForever";
            long time = banManger.getBanTime(pUUID);
            if (time != -1)
                timeString = DateUtils.formatDateDiff(time);
            String reason = banManger.getReason(pUUID);
            String message ="&c" + pUUID.toString() + "&7 - &c" + timeString + "&7 - &c" + reason;
            message = ChatColor.translateAlternateColorCodes('&', message);

            cs.sendMessage(message);
        }
    }
}
