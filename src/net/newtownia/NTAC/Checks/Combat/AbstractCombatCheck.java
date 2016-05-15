package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public abstract class AbstractCombatCheck extends AbstractCheck
{
    protected CombatBase combatBase;

    public AbstractCombatCheck(NTAC pl, CombatBase combatBase, String name)
    {
        super(pl, name);
        this.combatBase = combatBase;
        combatBase.registerCombatCheck(this);
    }

    protected void onAttackPacketReceive(PacketEvent event, WrapperPlayClientUseEntity packet) {};
    protected void onAttack(EntityDamageByEntityEvent event) {};
}
