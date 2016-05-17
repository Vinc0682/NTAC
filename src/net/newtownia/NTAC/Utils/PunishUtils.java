package net.newtownia.NTAC.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class PunishUtils
{
    public static void kickPlayer(final OfflinePlayer player, final String reason)
    {
        if (!player.isOnline())
            return;
        Player p = (Player)player;

        Bukkit.getScheduler().callSyncMethod(NTAC.getInstance(), new Callable<Boolean>()
        {
            public Boolean call()
            {
                p.kickPlayer(reason);
                return true;
            }
        });
    }

    public static void banPlayer(final OfflinePlayer p, final String reason, final long until)
    {
        NTAC.getInstance().getBanManger().addBan(p.getUniqueId(), until, reason);
        kickPlayer(p, NTAC.getInstance().getMessageUtils().formatMessage(reason));
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
            String realCommand = NTAC.getInstance().getMessageUtils().formatMessage(command);
            realCommand = realCommand.replaceAll("%PLAYER%", p.getName());
            realCommand = realCommand.replaceAll("%VL%", String.valueOf(realVL));
            realCommand = realCommand.replaceAll("%PING%", String.valueOf(PlayerUtils.getPing(p)));

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

    public static void crashGame(Player p, Player sender) {
        if (p != null) {
            ProtocolManager m = ProtocolLibrary.getProtocolManager();
            PacketContainer spawnPlayer = m.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawnPlayer.getIntegers().write(0, 55534).write(1, p.getLocation().getBlockX()).write(2, p.getLocation().getBlockY()).write(3, p.getLocation().getBlockZ());
            spawnPlayer.getSpecificModifier(UUID.class).write(0, p.getUniqueId());
            spawnPlayer.getBytes().write(0, (byte) (p.getLocation().getYaw() * 256.0F / 360.0F)).write(1, (byte) (p.getLocation().getPitch() * 256.0F / 360.0F));
            spawnPlayer.getIntegers().write(4, 0);
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, (byte) 0);
            watcher.setObject(1, (short) 300);
            watcher.setObject(8, (int) 0);
            spawnPlayer.getDataWatcherModifier().write(0, watcher);
            try {
                for (int i = 0; i < 101; i++) {
                    if (p == null)
                        return;
                    m.sendServerPacket(p, spawnPlayer);
                    sender.sendMessage("§8§l> §7Trying to send Entity #" + i + " to " + p.getName());
                }
                sender.sendMessage("§8§l> §7Crashed " + p.getName() + "'s §7Game!");
            } catch (InvocationTargetException ex) {
                sender.sendMessage("§8§l> §7Cannot crash " + p.getName() + "'s §7Game!");
            }
        }
    }
}
