package net.newtownia.NTAC;

import net.newtownia.NTAC.Action.BanManger;
import net.newtownia.NTAC.Checks.CheckManager;
import net.newtownia.NTAC.Commands.NTACCommand;
import net.newtownia.NTAC.Utils.MessageUtils;
import net.newtownia.NTApi.Config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;


public class NTAC extends JavaPlugin
{
	private YamlConfiguration config;
    private static NTAC instance;

    private MessageUtils messageUtils;
    private CheckManager checkManager;
    private BanManger banManger;

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

        // Config auto update
        YamlConfiguration newestConfig = ConfigManager.loadConfig(ConfigManager.createConfigFile("config.yml",
                "newest-config.yml", this));
        updateConfig(config, newestConfig);
        try {
            ConfigManager.SaveConfigurationToFile(config, "config.yml", this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageUtils = new MessageUtils(this, "messages.yml");
        banManger = new BanManger(this, "bans.yml");

        if (checkManager != null)
            checkManager.reload();
    }

    private void updateConfig(YamlConfiguration original, YamlConfiguration newest)
    {
        for (Map.Entry<String, Object> entry : newest.getValues(true).entrySet())
        {
            boolean hasValue = true;
            try
            {
                Object value = original.get(entry.getKey());
                if (value == null)
                    hasValue = false;
            }
            catch (Exception e)
            {
                hasValue = false;
            }
            if (!hasValue)
            {
                String path = entry.getKey();
                String[] parts = path.split("\\.");
                if (parts.length >= 2)
                {
                    String part = parts[parts.length - 2];
                    if (part.equals("Actions"))
                        continue;
                }

                original.set(entry.getKey(), entry.getValue());
            }
        }
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

    public BanManger getBanManger() {
        return banManger;
    }
}
