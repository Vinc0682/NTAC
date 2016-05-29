package net.newtownia.NTAC.Gui;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTApi.Config.ConfigManager;
import net.newtownia.NTApi.GUI.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 29.05.2016.
 */
public class Gui
{
    private NTAC pl;
    private YamlConfiguration config;
    private GUIManager<Windows> manager;

    public Gui(NTAC pl)
    {
        this.pl = pl;
        config = ConfigManager.loadOrCreateConfigFile("gui.yml", pl);
        manager = new GUIManager<>(pl, Windows.MAIN, ChatColor.translateAlternateColorCodes('&', config.getString("Title")));
        Bukkit.getPluginManager().registerEvents(manager, pl);

        manager.addWindow(Windows.MAIN, new MainWindow(this));
        manager.addWindow(Windows.SETTINGS, new SettingsWindow(this));
    }

    public void reload()
    {
        config = ConfigManager.loadOrCreateConfigFile("gui.yml", pl);
    }

    public GUIManager<Windows> getManager() {
        return manager;
    }
    public void openGUI(Player p)
    {
        manager.Navigate(p, Windows.MAIN);
    }
    public NTAC getPl() {
        return pl;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
