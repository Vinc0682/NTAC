package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.EntityUtils;
import net.newtownia.NTAC.Utils.FakePlayer.FakePlayer;
import net.newtownia.NTAC.Utils.FakePlayer.Identity;
import net.newtownia.NTAC.Utils.MaterialUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class KillauraNPC extends AbstractCombatCheck
{
    //int threshold = 5;
    private int combatTime = 5000;
    private boolean copyAttackedType = true;
    private int slowWeaponVLIncrement = 2;

    private double minHeight = 3.4;
    private double maxHeight = 4;
    private double minDist = 2;
    private double maxDist = 4;

    private Map<UUID, Long> playerLastHitTime;
    private Map<UUID, Long> playerFirstHitTime;
    private Map<UUID, Identity> playerBotIdentitys;

    private PacketAdapter moveLookPacketEvent;
    private FakePlayer bot;

    private Random rnd = new Random(System.currentTimeMillis());

    private ActionData actionData;
    private ViolationManager vlManager;

    public KillauraNPC(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Killaura-NPC");

        playerLastHitTime = new HashMap<>();
        playerFirstHitTime = new HashMap<>();
        playerBotIdentitys = new HashMap<>();

        vlManager = new ViolationManager();

        bot = new FakePlayer(9910 + rnd.nextInt(90));

        moveLookPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.POSITION,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onPlayerMove(event);
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(moveLookPacketEvent);

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
                    vlManager.resetPlayerViolation(p);
                    return;
                }
            }

            bot.moveTo(p, getBotLoc(p));
        }
    }

    @Override
    protected void onAttackPacketReceive(PacketEvent event, WrapperPlayClientUseEntity packet)
    {
        if(!isEnabled())
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(!playerLastHitTime.containsKey(pUUID) && !p.hasPermission("ntac.bypass.killaura.npc"))
        {
            Identity botId = null;
            if (copyAttackedType)
            {
                Entity attacked = EntityUtils.getEntityByEntityID(packet.getTargetID(), p.getLocation().getWorld());
                if (attacked == null)
                {
                    Bukkit.getLogger().info("Unable to find attacked entity");
                    return;
                }

                if (EntityUtils.isPassive(attacked))
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
            event.setCancelled(true);
            if (MaterialUtils.isSlowWeapon(p.getInventory().getItemInMainHand()))
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
        double height = minHeight + rnd.nextDouble() * (maxHeight - minHeight);
        double dist = minDist + rnd.nextDouble() * (maxDist - minDist);

        double rot = Math.toRadians(-p.getLocation().getPitch());
        if (rnd.nextDouble() > 0.9)
            rot = Math.toRadians(91);
        height *= Math.cos(rot);
        dist *= Math.sin(rot);

        Location loc = FakePlayer.getAroundPos(p, 180, dist);
        loc.setY(p.getLocation().getY() + height);
        return loc;
    }

    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();

        combatTime = Integer.parseInt(config.getString("Killaura-NPC.Combat-Time"));
        copyAttackedType = Boolean.valueOf(config.getString("Killaura-NPC.Copy-Attacked-Type"));
        minHeight = Double.parseDouble(config.getString("Killaura-NPC.Min-Height"));
        maxHeight = Double.parseDouble(config.getString("Killaura-NPC.Max-Height"));
        minDist = Double.parseDouble(config.getString("Killaura-NPC.Min-Distance"));
        maxDist = Double.parseDouble(config.getString("Killaura-NPC.Max-Distance"));
        slowWeaponVLIncrement = Integer.parseInt(config.getString("Killaura-NPC.Slow-Weapon-Increment"));

        actionData = new ActionData(config, "Killaura-NPC.Actions");
    }
}
