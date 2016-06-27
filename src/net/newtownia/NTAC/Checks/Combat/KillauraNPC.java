package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.EntityUtils;
import net.newtownia.NTAC.Utils.FakePlayer.FakePlayer;
import net.newtownia.NTAC.Utils.FakePlayer.Identity;
import net.newtownia.NTAC.Utils.LogUtils;
import net.newtownia.NTAC.Utils.MaterialUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class KillauraNPC extends AbstractCombatCheck
{
    private int combatTime = 5000;
    private boolean onlyRandomIdentity = false;
    private boolean copyAttackedType = true;
    private int slowWeaponVLIncrement = 2;

    private double minHeight = 3.4;
    private double maxHeight = 4;
    private double jumpMultiplier = 1.3;
    private double minDist = 2;
    private double maxDist = 4;
    private double angle = 90;
    private double downProbability = 0.95;

    private Map<UUID, Long> playerLastHitTime;
    private Map<UUID, Identity> playerBotIdentity;
    private Map<UUID, Integer> playerBotDownStage;

    private MovementBase movementBase;
    private FakePlayer bot;

    private Random rnd = new Random(System.currentTimeMillis());
    private List<Integer> botDownRotations = Arrays.asList(0, 45, 91, 45, 0);

    private ActionData actionData;
    private ViolationManager vlManager;

    public KillauraNPC(NTAC pl, CombatBase combatBase, MovementBase movementBase)
    {
        super(pl, combatBase, "Killaura-NPC");
        this.movementBase = movementBase;

        playerLastHitTime = new HashMap<>();
        playerBotIdentity = new HashMap<>();
        playerBotDownStage = new HashMap<>();

        vlManager = new ViolationManager();

        bot = new FakePlayer(9910 + rnd.nextInt(90));

        PacketAdapter moveLookPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.POSITION,
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

                if(playerBotIdentity.containsKey(pUUID))
                {
                    Identity playerBotIdentity = this.playerBotIdentity.get(pUUID);

                    if(!playerBotIdentity.isAlreadyOnline)
                    {
                        bot.despawnTablistForPlayer(p, playerBotIdentity);
                    }

                    bot.destroyForPlayer(p);

                    this.playerBotIdentity.remove(pUUID);
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
            Identity botId;
            if (onlyRandomIdentity)
                botId = Identity.Generator.generateRandomIdentity();
            else if (copyAttackedType)
            {
                Entity attacked = EntityUtils.getEntityByEntityID(packet.getTargetID(), p.getLocation().getWorld());
                if (attacked == null)
                {
                    LogUtils.error("Unable to find attacked entity");
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
            playerBotIdentity.put(pUUID, botId);
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
        playerBotIdentity.remove(pUUID);
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
        UUID pUUID = p.getUniqueId();

        double height = minHeight + rnd.nextDouble() * (maxHeight - minHeight);
        double dist = minDist + rnd.nextDouble() * (maxDist - minDist);

        if (movementBase.getPlayerOnGroundMoves(pUUID) < 3)
            height *= jumpMultiplier;

        double angledPitch = -(p.getLocation().getPitch() - angle);
        double rot = Math.toRadians(angledPitch);

        if (playerBotDownStage.containsKey(pUUID))
        {
            int stage = playerBotDownStage.get(pUUID);
            double downRotation = botDownRotations.get(stage);
            if (rot < downRotation)
                rot = downRotation;
            if (stage == botDownRotations.size() - 1)
                playerBotDownStage.remove(pUUID);
            else
                playerBotDownStage.put(pUUID, stage + 1);
        }
        else
        {
            if (rnd.nextDouble() > downProbability)
                playerBotDownStage.put(pUUID, 0);
        }
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
        onlyRandomIdentity = Boolean.valueOf(config.getString("Killaura-NPC.Only-Random-Identity"));
        copyAttackedType = Boolean.valueOf(config.getString("Killaura-NPC.Copy-Attacked-Type"));
        minHeight = Double.parseDouble(config.getString("Killaura-NPC.Min-Height"));
        maxHeight = Double.parseDouble(config.getString("Killaura-NPC.Max-Height"));
        jumpMultiplier = Double.parseDouble(config.getString("Killaura-NPC.Jump-Multiplier"));
        minDist = Double.parseDouble(config.getString("Killaura-NPC.Min-Distance"));
        maxDist = Double.parseDouble(config.getString("Killaura-NPC.Max-Distance"));
        angle = Double.parseDouble(config.getString("Killaura-NPC.Angle")) - 90;
        downProbability = Double.parseDouble(config.getString("Killaura-NPC.Down-Probability")) / 100;
        slowWeaponVLIncrement = Integer.parseInt(config.getString("Killaura-NPC.Slow-Weapon-Increment"));

        actionData = new ActionData(config, "Killaura-NPC.Actions");
    }
}
