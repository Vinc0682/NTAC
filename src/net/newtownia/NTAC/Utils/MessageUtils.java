package net.newtownia.NTAC.Utils;

import net.newtownia.NTApi.Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils
{
    JavaPlugin pl;
    Map<String, String> messages;

    public MessageUtils(JavaPlugin pl, String fileName)
    {
        this.pl = pl;

        messages = new HashMap<>();

        YamlConfiguration config = ConfigManager.loadOrCreateConfigFile(fileName, pl);

        for(Map.Entry<String, Object> entry : config.getValues(false).entrySet())
        {
            messages.put(entry.getKey(), config.getString(entry.getKey()));
        }
    }

    public void printMessage(CommandSender cs, String message, Object... args)
    {
        cs.sendMessage(messages.get("Prefix") + String.format(messages.get(message), args));
    }

    public void printNotify(String[] args)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; i += 1)
        {
            sb.append(args[i]);
            sb.append(" ");
        }
        String msg = messages.get("Notify-Prefix") + sb.toString();
        msg = formatMessage(msg);
        Bukkit.getLogger().info(ChatColor.stripColor(msg));
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.hasPermission("ntac.notify"))
                p.sendMessage(msg);
    }

    public String formatMessage(String s)
    {
        String result = ChatColor.translateAlternateColorCodes('&', s);
        result = result.replace("%nn", "\n\n\n\n\n");
        result = result.replace("%n", "\n");

        for (Map.Entry<String, String> message: messages.entrySet())
        {
            String keyWord = "%%" + message.getKey() + "%%";
            if (result.contains(keyWord))
                result = result.replaceAll(keyWord, formatMessage(message.getValue()));
        }

        return result;
    }
}
