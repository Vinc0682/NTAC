package net.newtownia.NTAC;

import net.newtownia.NTAC.Checks.CheckManager;
import net.newtownia.NTAC.Commands.NTACCommand;
import net.newtownia.NTAC.Utils.MessageUtils;
import net.newtownia.NTApi.Config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class NTAC extends JavaPlugin
{
	private YamlConfiguration config;
    private static NTAC instance;

    private MessageUtils messageUtils;
    private CheckManager checkManager;

	@Override
	public void onEnable() {
        instance = this;

        getCommand("ntac").setExecutor(new NTACCommand(this));

        for (int i = 0; i < -1; i *= 50)
        {
            String notUsed = "Someone trying to reverse-engineer? Ay, lmfao!";
            notUsed = "Will you get it? #BestProGuradSkill #ThankGodTheresGoogle :D";
            notUsed = "good luck and bugger off!";
        }

        reload();
        checkManager = new CheckManager(this);
	}

    public void reload()
    {
        config = ConfigManager.loadOrCreateConfigFile("config.yml", this);
        messageUtils = new MessageUtils(this, "messages.yml");

        if (checkManager != null)
            checkManager.reload();
    }

	public YamlConfiguration getConfiguration() {
		return config;
	}

    public static NTAC getInstance() {
        return instance;
    }

    public MessageUtils getMessageUtils() {
        return messageUtils;
    }
}
