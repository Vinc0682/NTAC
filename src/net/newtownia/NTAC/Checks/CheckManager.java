package net.newtownia.NTAC.Checks;

import net.newtownia.NTAC.Checks.Combat.*;
import net.newtownia.NTAC.Checks.Misc.AntiChorus;
import net.newtownia.NTAC.Checks.Movement.AntiAFK.AntiAFKBase;
import net.newtownia.NTAC.Checks.Movement.*;
import net.newtownia.NTAC.Checks.Movement.NCPDragDown.NCPDragDown;
import net.newtownia.NTAC.Checks.Player.Abilities;
import net.newtownia.NTAC.Checks.Player.Headless;
import net.newtownia.NTAC.Checks.Player.SkinDerp;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.LogUtils;
import net.newtownia.NTApi.Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckManager
{
    private NTAC pl;

    private List<AbstractCheck> allChecks = new ArrayList<>();
    private MovementBase movementBase = null;
    private CombatBase combatBase = null;

    public CheckManager(NTAC pl)
    {
        this.pl = pl;

        registerBases();

        addChecks();
        registerCheckListeners();
        switchChecksByConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                for (AbstractCheck check : allChecks)
                    for (Player p : Bukkit.getOnlinePlayers())
                        check.onUpdate(p);
            }
        }, 1L, 1L);
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
            LogUtils.info("Registered movement-base.");
        }
        if (Boolean.valueOf(pl.getConfiguration().getString("Bases.Enable-Combat-Base")))
        {
            combatBase = new CombatBase(pl);
            pl.getServer().getPluginManager().registerEvents(combatBase, pl);
            LogUtils.info("Registered combat-base.");
        }
    }

    private void addChecks()
    {
        try
        {
            allChecks = new ArrayList<>();

            allChecks.add(new KillauraNPC(pl, combatBase));
            allChecks.add(new Aimbot(pl, combatBase));
            allChecks.add(new AntiKnockback(pl, combatBase));
            allChecks.add(new AutoClicker(pl, combatBase));
            allChecks.add(new AutoArmor(pl, combatBase));
            allChecks.add(new Criticals(pl, combatBase));

            allChecks.add(new SkinDerp(pl));
            allChecks.add(new Headless(pl));
            allChecks.add(new Abilities(pl));

            allChecks.add(new AntiAFKBase(pl, movementBase));
            allChecks.add(new Sneak(pl, movementBase));
            //allChecks.add(new NoSlowBlock(pl, movementBase)); Can't block anymore
            allChecks.add(new FastLadder(pl, movementBase));
            allChecks.add(new Boatfly(pl, movementBase));
            allChecks.add(new InventoryMove(pl, movementBase));
            allChecks.add(new NCPDragDown(pl, movementBase));
            allChecks.add(new Jesus(pl, movementBase));
            allChecks.add(new Speed(pl, movementBase));
            allChecks.add(new BadPackets(pl, movementBase));

            //allChecks.add(new Tracers(pl));
            allChecks.add(new AntiChorus(pl));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            LogUtils.info((check.isEnabled() ? "&aEnabled" : "&cDisabled") + " " + check.getName());
        }
    }

    public void saveToConfig()
    {
        for (AbstractCheck check : allChecks)
            pl.getConfiguration().set("Enabled." + check.getName(), check.isEnabled());
        try {
            ConfigManager.SaveConfigurationToFile(pl.getConfiguration(), "config.yml", pl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AbstractCheck> getAllChecks() {
        return allChecks;
    }
}
