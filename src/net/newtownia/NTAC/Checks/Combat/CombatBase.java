package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.NTAC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public class CombatBase implements Listener
{
    private NTAC pl;
    private Map<UUID, Entity> lastPlayerTarget;
    private Map<UUID, Long> lastPlayerAttackTime;

    private PacketAdapter attackPacketEvent;
    private List<AbstractCombatCheck> combatChecks;

    public CombatBase(NTAC pl)
    {
        this.pl = pl;
        lastPlayerTarget = new HashMap<>();
        lastPlayerAttackTime = new HashMap<>();
        combatChecks = new ArrayList<>();

        attackPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onAttackPacketRecieve(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(attackPacketEvent);
    }

    public Entity getLastTarget(UUID pUUID)
    {
        return lastPlayerTarget.getOrDefault(pUUID, null);
    }

    public long getLastAttackTime(UUID pUUID)
    {
        return lastPlayerAttackTime.getOrDefault(pUUID, -1L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        for (AbstractCombatCheck check : combatChecks)
            check.onAttack(event);
        if (event.getDamager() instanceof Player)
        {
            Player p = (Player)event.getDamager();
            lastPlayerTarget.put(p.getUniqueId(), event.getEntity());
        }
    }

    public void registerCombatCheck(AbstractCombatCheck check)
    {
        combatChecks.add(check);
    }

    public void onAttackPacketRecieve(PacketEvent event)
    {
        if(event.getPacketType() != PacketType.Play.Client.USE_ENTITY)
            return;
        WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
        if(packet.getType() != EnumWrappers.EntityUseAction.ATTACK)
            return;

        for (AbstractCombatCheck check : combatChecks)
            check.onAttackPacketReceive(event, packet);
        lastPlayerAttackTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}
