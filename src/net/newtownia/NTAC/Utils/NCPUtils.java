package net.newtownia.NTAC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Created by Vinc0682 on 13.05.2016.
 */
public class NCPUtils
{
    public static boolean hasNoCheatPlus()
    {
        Plugin pl = Bukkit.getPluginManager().getPlugin("NoCheatPlus");
        return pl != null && pl.isEnabled();
    }
}
