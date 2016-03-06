package net.newtownia.NTAC.Checks;

import net.newtownia.NTAC.NTAC;
import org.bukkit.event.Listener;

public abstract class AbstractCheck implements Listener
{
    private boolean enabled = true;
    private String name = "";

    protected final NTAC pl;

    public AbstractCheck(NTAC pl, String name)
    {
        this.pl = pl;
        this.name = name;
    }

    public abstract void loadConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }
}
