package eu.cloudnetservice.simplenametag.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitPlayerUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SimpleNameTagListener implements Listener {

    @EventHandler
    public void handleJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Bukkit.getPluginManager().getPlugin("SimpleNameTags"),
                () -> CloudServer.getInstance().updateNameTags(e.getPlayer()),
                3L);
    }

    @EventHandler
    public void handleUpdate(BukkitPlayerUpdateEvent e) {
        if (Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()) != null &&
                e.getCloudPlayer().getServer() != null &&
                e.getCloudPlayer().getServer().equals(CloudAPI.getInstance().getServerId())) {
            CloudServer.getInstance().updateNameTags(Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()));
        }
    }

}
