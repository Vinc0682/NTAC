package net.newtownia.NTAC.Action;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionData
{
    Map<Integer, List<String>> violationCommands;

    public ActionData(ConfigurationSection section)
    {
        load(section);
    }

    public ActionData(YamlConfiguration config, String path)
    {
        load(config, path);
    }

    public void load(ConfigurationSection section)
    {
        violationCommands = new HashMap<>();

        for(Map.Entry<String, Object> testEntry : section.getValues(false).entrySet())
        {
            int violation = Integer.parseInt(testEntry.getKey());
            Object action = testEntry.getValue();

            List<String> commands = new ArrayList<>();

            if(action instanceof String)
                commands.add((String) action);

            if(action instanceof List)
                commands.addAll((List<String>)action);

            violationCommands.put(violation, commands);
        }
    }

    public void load(YamlConfiguration config, String path)
    {
        load(config.getConfigurationSection(path));
    }

    public int getValidViolationLevel(int realViolation)
    {
        int resultViolationLevel = 0;

        for(Integer currentViolationLevel : violationCommands.keySet())
        {
            if(currentViolationLevel > resultViolationLevel &&
                    currentViolationLevel <= realViolation)
                resultViolationLevel = currentViolationLevel;
        }

        return resultViolationLevel;
    }

    public List<String> getViolationCommands(int violationLevel)
    {
        if(!violationCommands.containsKey(violationLevel))
            return null;

        return violationCommands.get(violationLevel);
    }

    public List<String> getViolationCommandsWithValidation(int violationLevel)
    {
        int validViolationLevel = violationLevel;

        if(!violationCommands.containsKey(validViolationLevel))
            validViolationLevel = getValidViolationLevel(violationLevel);

        return getViolationCommands(validViolationLevel);
    }

    public boolean doesLastViolationCommandsContains(int vl, String s)
    {
        List<String> lastCommands = getViolationCommandsWithValidation(vl);
        return lastCommands != null && lastCommands.contains(s);
    }
}
