package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class NTACCommand implements CommandExecutor
{
    private NTAC pl;
    private HashMap<String, SubCommand> commands;

    public NTACCommand(NTAC pl)
    {
        this.pl = pl;

        commands = new HashMap<>();
        add(new Version());
        add(new Reload());
        add(new Notify());
        add(new Kick());
        add(new Ban());
        add(new Banlist());
        add(new Unban());
        add(new AddBanPoint());
        add(new Gui());
        add(new Respawn());
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
        String commandName = "version";
        if (args.length > 0)
            commandName = args[0];
        commandName = commandName.toLowerCase();
        SubCommand command = null;
        if (commands.containsKey(commandName))
            command = commands.get(commandName);
        if (command == null)
            command = commands.get("version");
        command.execute(pl, cs, cmd, label, args);
        return true;
    }

    private void add(SubCommand command)
    {
        commands.put(command.getName().toLowerCase(), command);
    }
}
