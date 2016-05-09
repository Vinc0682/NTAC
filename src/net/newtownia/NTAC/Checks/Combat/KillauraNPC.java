package net.newtownia.NTAC.Checks.Combat;

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
import net.newtownia.NTAC.Utils.FakePlayer.FakePlayer;
import net.newtownia.NTAC.Utils.FakePlayer.Identity;
import net.newtownia.NTAC.Utils.ItemUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import net.newtownia.NTAC.Utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class KillauraNPC extends AbstractCheck
{
    //int threshold = 5;
    private int combatTime = 5000;
    private int clearFrequency = 60;
    private boolean copyAttackedType = true;
    private int slowWeaponVLIncrement = 2;

    private double distanceMin = 2;
    private double distanceMax = 4;
    private int angleMin = 100;
    private int angleMax = 120;
    private int switchTime = 1000;

    private Map<UUID, Long> playerLastHitTime;
    private Map<UUID, Long> playerFirstHitTime;
    private Map<UUID, Identity> playerBotIdentitys;

    private PacketAdapter usePacketEvent;
    private PacketAdapter moveLookPacketEvent;
    private FakePlayer bot;

    private Random rnd = new Random(System.currentTimeMillis());

    private ActionData actionData;
    private ViolationManager vlManager;

    private List<EntityType> ignoredEntityType = Arrays.asList(EntityType.BOAT, EntityType.MINECART,
            EntityType.ITEM_FRAME, EntityType.ARMOR_STAND, EntityType.MINECART_CHEST, EntityType.MINECART_COMMAND,
            EntityType.MINECART_FURNACE, EntityType.MINECART_HOPPER, EntityType.MINECART_MOB_SPAWNER,
            EntityType.MINECART_TNT);

    public KillauraNPC(NTAC pl)
    {
        super(pl, Strings.getString(0, 0x01));

        playerLastHitTime = new HashMap<>();
        playerFirstHitTime = new HashMap<>();
        playerBotIdentitys = new HashMap<>();

        vlManager = new ViolationManager();

        bot = new FakePlayer(9910 + rnd.nextInt(90));

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
            Identity botId = null;
            if (copyAttackedType)
            {
                Entity attacked = null;
                for (Entity en : p.getWorld().getEntities())
                    if (en.getEntityId() == packet.getTargetID())
                        attacked = en;

                if (attacked == null)
                {
                    Bukkit.getLogger().info("Unable to find attacked entity");
                    return;
                }

                if (ignoredEntityType.contains(attacked.getType()))
                    return;

                botId = Identity.Generator.generateIdentityForPlayer(p, attacked);
            }
            else
                botId = Identity.Generator.generateIdentityForPlayer(p);
            botId.visible = true;
            bot.spawnForPlayerWithIdentity(p, getBotLoc(p), botId);
            playerBotIdentitys.put(pUUID, botId);
            playerFirstHitTime.put(pUUID, System.currentTimeMillis());
        }

        if(packet.getTargetID() == bot.getEntityID())
        {
            if (ItemUtils.isSlowWeapon(p.getInventory().getItemInMainHand()))
                vlManager.addViolation(p, slowWeaponVLIncrement);
            else
                vlManager.addViolation(p, 1);
            PunishUtils.runViolationAction(p, vlManager, actionData);
        }

        playerLastHitTime.put(pUUID, System.currentTimeMillis());
    }

    @Override
    protected void onPlayerDisconnect(Player p)
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
        combatTime = Integer.parseInt(config.getString("Killaura-NPC.Combat-Time"));
        copyAttackedType = Boolean.valueOf(config.getString("Killaura-NPC.Copy-Attacked-Type"));
        angleMin = Integer.parseInt(config.getString("Killaura-NPC.Angle-Min"));
        angleMax = Integer.parseInt(config.getString("Killaura-NPC.Angle-Max"));
        distanceMin = Double.parseDouble(config.getString("Killaura-NPC.Distance-Min"));
        distanceMax = Double.parseDouble(config.getString("Killaura-NPC.Distance-Max"));
        switchTime = Integer.parseInt(config.getString("Killaura-NPC.Switch-Time"));
        slowWeaponVLIncrement = Integer.parseInt(config.getString("Killaura-NPC.Slow-Weapon-Increment"));

        actionData = new ActionData(config, "Killaura-NPC.Actions");
    }
}
