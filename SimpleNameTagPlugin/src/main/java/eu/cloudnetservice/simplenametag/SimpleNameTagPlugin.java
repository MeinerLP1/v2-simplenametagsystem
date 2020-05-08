package eu.cloudnetservice.simplenametag;

import eu.cloudnetservice.simplenametag.listener.SimpleNameTagListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleNameTagPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SimpleNameTagListener(), this);
    }
}
