package net.newtownia.NTAC.Action;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import net.newtownia.NTApi.Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class BanManger
{
    private ActionData actionData;

    private NTAC pl;
    private YamlConfiguration config;
    private String fileName;

    private Map<UUID, Long> banTimes;
    private Map<UUID, String> banReasons;
    private ViolationManager vlManager;
    private static BanListener listener = null;

    private String CONFIG_BAN_PREFIX = "Banned.";
    private String CONFIG_VL_PREFIX = "VL.";

    public BanManger(NTAC pl, String fileName)
    {
        this.pl = pl;
        this.fileName = fileName;
        this.config = ConfigManager.loadOrCreateConfigFile(fileName, pl);
        banTimes = new HashMap<>();
        banReasons = new HashMap<>();
        vlManager = new ViolationManager();

        if (listener == null)
        {
            listener = new BanListener();
            Bukkit.getPluginManager().registerEvents(listener, pl);
        }

        load();
    }

    public void addBan(UUID pUUID, long until, String reason)
    {
        banTimes.put(pUUID, until);
        banReasons.put(pUUID, reason);
        save();
    }

    public void removeBan(UUID pUUID)
    {
        if (banTimes.containsKey(pUUID))
        {
            banTimes.remove(pUUID);
            banReasons.remove(pUUID);
            save();
        }
    }

    public boolean isBanned(UUID pUUID)
    {
        if (!banTimes.containsKey(pUUID))
            return false;

        long until = banTimes.get(pUUID);
        if (until != -1 && System.currentTimeMillis() > until)
        {
            /*Bukkit.getLogger().info("Unbanning Player: Cur Time: " + System.currentTimeMillis() +
                    " Req time: " + until);
            Bukkit.getLogger().info("Delta time: " + (System.currentTimeMillis() - until));*/
            removeBan(pUUID);
            return false;
        }
        else
            return true;
    }

    public long getBanTime(UUID pUUID)
    {
        return banTimes.getOrDefault(pUUID, -2L);
    }

    public String getReason(UUID pUUID)
    {
        return banReasons.getOrDefault(pUUID, "%%Ban-Default-Reason%%");
    }

    public List<UUID> getBannedUUIDS()
    {
        return new ArrayList<>(banTimes.keySet());
    }

    public void addVL(UUID pUUID, int amount) {
        Player p = Bukkit.getPlayer(pUUID);
        vlManager.addViolation(p, amount);
        save();

        PunishUtils.runViolationActionWithValidation(p, vlManager, actionData);
    }

    public int getVL(UUID pUUID)
    {
        return vlManager.getViolation(pUUID);
    }

    private void load()
    {
        actionData = new ActionData(pl.getConfiguration(), "Auto-Ban.Actions");

        ConfigurationSection bannedSection = ConfigManager.getOrCreateSection(config,
                CONFIG_BAN_PREFIX.substring(0, CONFIG_BAN_PREFIX.length() - 1));
        for (Map.Entry<String, Object> ban : bannedSection.getValues(false).entrySet())
        {
            String uuid = CONFIG_BAN_PREFIX + ban.getKey();
            UUID pUUID = UUID.fromString(ban.getKey());
            banTimes.put(pUUID, Long.valueOf(config.getString(uuid + ".Time")));
            banReasons.put(pUUID, config.getString(uuid + ".Reason"));
        }
        ConfigurationSection vlSection = ConfigManager.getOrCreateSection(config,
                CONFIG_VL_PREFIX.substring(0, CONFIG_VL_PREFIX.length() - 1));
        for (Map.Entry<String, Object> vl : vlSection.getValues(false).entrySet())
        {
            vlManager.setViolationWithoutSetbackPos(UUID.fromString(vl.getKey()),
                    Integer.valueOf(vlSection.getString(vl.getKey())));
        }
    }

    private void save()
    {
        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet())
        {
            config.set(entry.getKey(), null);
        }
        for (Map.Entry<UUID, Long> ban : banTimes.entrySet())
        {
            String uuid = CONFIG_BAN_PREFIX + ban.getKey().toString();
            config.set(uuid + ".Time", ban.getValue());
            config.set(uuid + ".Reason", banReasons.get(ban.getKey()));
        }
        for (Map.Entry<UUID, Integer> violation : vlManager.getAllViolations().entrySet())
        {
            config.set(CONFIG_VL_PREFIX + violation.getKey().toString(), violation.getValue());
        }
        try {
            ConfigManager.SaveConfigurationToFile(config, fileName, pl);
        } catch (IOException e) {
            Bukkit.getLogger().info("Error saving bans :(");
            e.printStackTrace();
        }
    }
}
