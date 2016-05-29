package net.newtownia.NTAC.Gui;

import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.Utils.GuiUtils;
import net.newtownia.NTApi.GUI.IWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vinc0682 on 29.05.2016.
 */
public class SettingsWindow implements IWindow
{
    private String enabledTitle = "&a%1s";
    private String disabledTitle = "&c%1s";
    private String enabledLore = "&7Click to turn off.";
    private String disabledLore = "&7Click to turn on.";

    private Gui gui;
    private int size;

    public SettingsWindow(Gui gui)
    {
        this.gui = gui;
    }

    @Override
    public void show(Player p)
    {
        p.openInventory(createInventory(p));
    }

    @Override
    public Inventory createInventory(Player p)
    {
        loadConfig();

        List<AbstractCheck> checks = gui.getPl().getCheckManager().getAllChecks();
        for (int i = checks.size(); i < checks.size() + 9; i += 1)
        {
            if (i % 9 == 0)
            {
                size = i;
                break;
            }
        }

        Inventory inv = Bukkit.createInventory(p, size, gui.getManager().getInventoryTitle());
        for (int i = 0; i < checks.size(); i += 1)
        {
            AbstractCheck check = checks.get(i);
            if (check.isEnabled())
                inv.setItem(i, GuiUtils.createItem(String.format(enabledTitle, check.getName()),
                        Material.STAINED_GLASS_PANE, 5,
                        Arrays.asList("", enabledLore)));
            else
                inv.setItem(i, GuiUtils.createItem(String.format(disabledTitle, check.getName()),
                        Material.STAINED_GLASS_PANE, 14,
                        Arrays.asList("", disabledLore)));
        }

        inv = GuiUtils.fillUp(inv);
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event)
    {
        List<AbstractCheck> checks = gui.getPl().getCheckManager().getAllChecks();
        if (event.getSlot() > checks.size() - 1)
            return;
        AbstractCheck check = checks.get(event.getSlot());
        check.setEnabled(!check.isEnabled());
        gui.getPl().getCheckManager().saveToConfig();
        show((Player)event.getWhoClicked());
    }

    private void loadConfig()
    {
        enabledTitle = gui.getConfig().getString("Check-Enabled-Title");
        disabledTitle = gui.getConfig().getString("Check-Disabled-Title");
        enabledLore = gui.getConfig().getString("Check-Enabled-Lore");
        disabledLore = gui.getConfig().getString("Check-Disabled-Lore");
    }

    @Override
    public boolean getInvLock() {
        return true;
    }
    @Override
    public boolean getIgnoreAir() {
        return true;
    }
    @Override
    public int getLastClickableItem() {
        return size - 1;
    }
}
