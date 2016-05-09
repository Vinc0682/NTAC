package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Vinc0682 on 07.05.2016.
 */
public class AutoArmor extends AbstractCheck
{
    private int checkFreq = 300000;
    private int checkTime = 20;
    private String itemName = "&4&lNICHT ANZIEHEN";
    private ActionData actionData;

    private ViolationManager vlManager;
    private long lastCheckTime = 0;
    private ItemStack item = null;

    public AutoArmor(NTAC pl) {
        super(pl, "Auto-Armor");

        vlManager = new ViolationManager();
        loadConfig();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;

        if (System.currentTimeMillis() - lastCheckTime > checkFreq)
        {
            lastCheckTime = System.currentTimeMillis();

            item = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(pl.getMessageUtils().formatMessage(itemName));
            item.setItemMeta(meta);

            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.getInventory().addItem(item);
            }

            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                @Override
                public void run()
                {
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (p.getInventory().contains(item))
                            p.getInventory().remove(item);
                    }
                }
            }, checkTime);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getWhoClicked() instanceof Player &&
                event.getCurrentItem().equals(item))
        {
            Player p = (Player)event.getWhoClicked();

            event.setCancelled(true);
            event.getWhoClicked().getInventory().remove(item);

            ItemStack[] armors = p.getInventory().getArmorContents();
            for (int i = 0; i < armors.length; i += 1)
                if (armors[i] != null && armors[i].equals(item))
                    armors[i] = null;
            p.getInventory().setArmorContents(armors);

            int deltaTime = (int)(System.currentTimeMillis() - lastCheckTime);
            int vl = (checkTime * 250 - deltaTime) / (checkTime * 25);
            if (vl <= 0)
                return;

            vlManager.setViolation(p, vl);
            PunishUtils.runViolationAction(p, vlManager, actionData);
        }
    }

    @Override
    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();
        checkFreq = Integer.parseInt(config.getString("Auto-Armor.Check-Frequency")) * 1000;
        checkTime = Integer.parseInt(config.getString("Auto-Armor.Check-Time"));
        itemName = config.getString("Auto-Armor.Item-Name");
        actionData = new ActionData(config, "Auto-Armor.Actions");
    }
}
