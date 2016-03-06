package net.newtownia.NTAC.Checks.Combat.Killaura;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.Identity;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class KillauraNPC extends AbstractCheck
{
    //int threshold = 5;
    int combatTime = 5000;
    int clearFrequency = 60;
    int changeBotFrequency = 200;

    double distanceMin = 2;
    double distanceMax = 4;
    int angleMin = 100;
    int angleMax = 120;
    int switchTime = 1000;

    Map<UUID, Long> playerLastHitTime;
    Map<UUID, Long> playerFirstHitTime;
    Map<UUID, Identity> playerBotIdentitys;

    PacketAdapter usePacketEvent;
    PacketAdapter moveLookPacketEvent;
    FakePlayer bot;

    Random rnd = new Random(System.currentTimeMillis());

    ActionData actionData;
    ViolationManager vlManager;

    public KillauraNPC(NTAC pl)
    {
        super(pl, "Killaura-NPC");

        playerLastHitTime = new HashMap<>();
        playerFirstHitTime = new HashMap<>();
        playerBotIdentitys = new HashMap<>();

        vlManager = new ViolationManager();

        bot = new FakePlayer(9910);

        usePacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            handleUsePacketEvent(event);
        }};

        moveLookPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.POSITION,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onPlayerMove(event);
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(usePacketEvent);
        ProtocolLibrary.getProtocolManager().addPacketListener(moveLookPacketEvent);

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllViolations();
            }
        }, clearFrequency, clearFrequency);

        loadConfig();
    }

    public void onPlayerMove(PacketEvent e)
    {
        if(!isEnabled())
            return;

        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(playerLastHitTime != null && playerLastHitTime.containsKey(pUUID))
        {
            if(hasAttackTimePassed(pUUID, combatTime))
            {
                playerLastHitTime.remove(pUUID);

                if(playerBotIdentitys.containsKey(pUUID))
                {
                    Identity playerBotIdentity = playerBotIdentitys.get(pUUID);

                    if(!playerBotIdentity.isAlreadyOnline)
                    {
                        bot.despawnTablistForPlayer(p, playerBotIdentity);
                    }

                    bot.destroyForPlayer(p);

                    playerBotIdentitys.remove(pUUID);
                    return;
                }
            }

            bot.moveTo(p, getBotLoc(p));
        }
    }

    private void handleUsePacketEvent(PacketEvent e)
    {
        if(!isEnabled())
            return;

        if(e.getPacketType() != PacketType.Play.Client.USE_ENTITY)
            return;

        WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(e.getPacket());

        if(packet.getType() != EnumWrappers.EntityUseAction.ATTACK)
            return;

        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(!playerLastHitTime.containsKey(pUUID) && !p.hasPermission("ntac.bypass.killaura.npc"))
        {
            Identity botId = Identity.Generator.generateIdentityForPlayer(p);
            botId.visible = true;
            bot.spawnForPlayerWithIdentity(p, getBotLoc(p), botId);
            playerBotIdentitys.put(pUUID, botId);
            playerFirstHitTime.put(pUUID, System.currentTimeMillis());
        }

        if(packet.getTarget() == bot.getEntityID())
        {
            vlManager.addViolation(p, 1);
            PunishUtils.runViolationAction(p, vlManager, actionData);
        }

        playerLastHitTime.put(pUUID, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        removeAllData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        removeAllData(event.getPlayer());
    }

    private void removeAllData(Player p)
    {
        if(!isEnabled())
            return;

        UUID pUUID = p.getUniqueId();
        vlManager.resetPlayerViolation(pUUID);
        playerLastHitTime.remove(pUUID);
        playerFirstHitTime.remove(pUUID);
        playerBotIdentitys.remove(pUUID);
    }

    private boolean hasAttackTimePassed(UUID pUUID, int milliseconds)
    {
        if(!playerLastHitTime.containsKey(pUUID))
        {
            playerLastHitTime.put(pUUID, System.currentTimeMillis());
            return false;
        }
        else
        {
            long lastAttackTime = playerLastHitTime.get(pUUID);
            return System.currentTimeMillis() > (lastAttackTime + milliseconds);
        }
    }

    private Location getBotLoc(Player p)
    {
        double angle =  angleMin + rnd.nextDouble() * (angleMax - angleMin);
        if(shouldBotBeRightSided(p))
            angle *= -1;
        double distance = distanceMin + rnd.nextDouble() * (distanceMax - distanceMin);

        return FakePlayer.getAroundPos(p, angle, distance);
    }

    private boolean shouldBotBeRightSided(Player p)
    {
        UUID pUUID = p.getUniqueId();

        if(!playerFirstHitTime.containsKey(pUUID))
        {
            playerFirstHitTime.put(pUUID, System.currentTimeMillis());
            return false;
        }

        int timeDiff = (int)(System.currentTimeMillis() - playerFirstHitTime.get(pUUID));
        boolean result = false;
        while (timeDiff > switchTime)
        {
            result = !result;
            timeDiff -= switchTime;
        }

        return result;
    }

    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();

        clearFrequency = Integer.parseInt(config.getString("Killaura-NPC.Clear-Frequency"));
        changeBotFrequency = Integer.parseInt(config.getString("Killaura-NPC.Change-Bot-Frequency"));
        combatTime = Integer.parseInt(config.getString("Killaura-NPC.Combat-Time"));
        angleMin = Integer.parseInt(config.getString("Killaura-NPC.Angle-Min"));
        angleMax = Integer.parseInt(config.getString("Killaura-NPC.Angle-Max"));
        distanceMin = Double.parseDouble(config.getString("Killaura-NPC.Distance-Min"));
        distanceMax = Double.parseDouble(config.getString("Killaura-NPC.Distance-Max"));
        switchTime = Integer.parseInt(config.getString("Killaura-NPC.Switch-Time"));

        actionData = new ActionData(config, "Killaura-NPC.Actions");
    }
}
