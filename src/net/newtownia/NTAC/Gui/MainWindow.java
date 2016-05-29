package net.newtownia.NTAC.Gui;

import net.newtownia.NTAC.Utils.GuiUtils;
import net.newtownia.NTApi.GUI.IWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

/**
 * Created by Vinc0682 on 29.05.2016.
 */
public class MainWindow implements IWindow
{
    private String reloadTitle = "&aReload";
    private String reloadLore = "&7Reload NTAC-Settings.";
    private Material reloadMaterial = Material.WATCH;

    private String settingsTitle = "&aSettings";
    private String settingsLore = "&7Toggle checks";
    private Material settingsMaterial = Material.DIAMOND_HOE;

    private Gui gui;

    public MainWindow(Gui gui)
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
        loadSettings();
        Inventory inv = Bukkit.createInventory(p, 9, gui.getManager().getInventoryTitle());
        inv.setItem(2, GuiUtils.createItem(settingsTitle, settingsMaterial, Arrays.asList("", settingsLore)));
        inv.setItem(6, GuiUtils.createItem(reloadTitle, reloadMaterial, Arrays.asList("", reloadLore)));
        inv = GuiUtils.fillUp(inv);
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player p = (Player) event.getWhoClicked();

        switch (event.getSlot())
        {
            case 2:
                gui.getManager().Navigate(p, Windows.SETTINGS);
                break;
            case 6:
                Bukkit.dispatchCommand(p, "ntac reload");
                p.closeInventory();
                break;
        }
    }

    private void loadSettings()
    {
        settingsMaterial = Material.getMaterial(gui.getConfig().getString("Settings-Material"));
        settingsTitle = gui.getConfig().getString("Settings-Title");
        settingsLore = gui.getConfig().getString("Settings-Lore");

        reloadMaterial = Material.getMaterial(gui.getConfig().getString("Reload-Material"));
        reloadTitle = gui.getConfig().getString("Reload-Title");
        reloadLore = gui.getConfig().getString("Reload-Lore");
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
        return 44;
    }
}
