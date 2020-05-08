package eu.cloudnetservice.simplenametag;

import eu.cloudnetservice.simplenametag.listener.SimpleNameTagListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleNameTagPlugin extends JavaPlugin {

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        getLogger().config("Initiate Bukkit SimpleNameTag System");
        getServer().getPluginManager().registerEvents(new SimpleNameTagListener(), this);
    }
}
