package net.newtownia.NTAC.Checks;

import net.newtownia.NTAC.Checks.Combat.AntiKnockback;
import net.newtownia.NTAC.Checks.Combat.AutoClicker;
import net.newtownia.NTAC.Checks.Combat.Killaura.KillauraNPC;
import net.newtownia.NTAC.Checks.Movement.AntiAFK.AntiAFKBase;
import net.newtownia.NTAC.Checks.Movement.FastLadder;
import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.Checks.Movement.NoSlowBlock;
import net.newtownia.NTAC.Checks.Movement.Sneak;
import net.newtownia.NTAC.Checks.Player.Headless;
import net.newtownia.NTAC.Checks.Player.SkinDerp;
import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CheckManager
{
    NTAC pl;

    List<AbstractCheck> allChecks;
    MovementBase movementBase = null;

    public CheckManager(NTAC pl)
    {
        this.pl = pl;

        registerBases();

        addChecks();
        registerCheckListeners();
        switchChecksByConfig();
    }

    public void reload()
    {
        for(AbstractCheck check : allChecks)
            check.loadConfig();

        switchChecksByConfig();
    }

    private void registerBases()
    {
        if (Boolean.valueOf(pl.getConfiguration().getString("Bases.Enable-Movement-Base")))
        {
            movementBase = new MovementBase();
            pl.getServer().getPluginManager().registerEvents(movementBase, pl);
            Bukkit.getLogger().info("Registered movement-base.");
        }
    }

    private void addChecks()
    {
        allChecks = new ArrayList<>();

        allChecks.add(new KillauraNPC(pl));
        allChecks.add(new AntiKnockback(pl));
        allChecks.add(new AutoClicker(pl));

        allChecks.add(new SkinDerp(pl));
        allChecks.add(new Headless(pl));

        allChecks.add(new AntiAFKBase(pl, movementBase));
        allChecks.add(new Sneak(pl, movementBase));
        //allChecks.add(new NoSlowBlock(pl, movementBase)); Can't block anymore
        allChecks.add(new FastLadder(pl, movementBase));
    }

    private void registerCheckListeners()
    {
        for(AbstractCheck check : allChecks)
            pl.getServer().getPluginManager().registerEvents(check, pl);
    }

    private void switchChecksByConfig()
    {
        for(AbstractCheck check : allChecks)
        {
            check.setEnabled(Boolean.valueOf(pl.getConfiguration().getString("Enabled." + check.getName())));
            Bukkit.getLogger().info((check.isEnabled() ? "Enabled" : "Disabled") + " " + check.getName());
        }
    }
}
