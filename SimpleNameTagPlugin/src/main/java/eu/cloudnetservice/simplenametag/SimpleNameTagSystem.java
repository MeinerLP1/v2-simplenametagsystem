package eu.cloudnetservice.simplenametag;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class SimpleNameTagSystem {

    private static SimpleNameTagSystem simpleNameTagSystem = new SimpleNameTagSystem();

    /**
     * Get a simple instance of the simple name tag system.
     *
     * @return the instance self.
     */
    public static SimpleNameTagSystem getSimpleNameTagSystem() {
        return simpleNameTagSystem;
    }

    /**
     * Update for bukkit player the name tag.
     *
     * @param player the player to set the name tag.
     */
    public void updateNameTags(Player player) {
        this.updateNameTags(player, null);
    }

    /**
     * Update for bukkit player the name tag.
     *
     * @param player                        the player to set the name tag.
     * @param playerPermissionGroupFunction a function to find the right permission group of the player to set the name tag.
     */
    public void updateNameTags(Player player, Function<Player, PermissionGroup> playerPermissionGroupFunction) {
        this.updateNameTags(player, playerPermissionGroupFunction, null);
    }

    /**
     * Update for bukkit player the name tag.
     *
     * @param player                                the player to set the name tag.
     * @param playerPermissionGroupFunction         a function to find the right permission group of the player to set the name tag.
     * @param allOtherPlayerPermissionGroupFunction a function to find the right permission group of other players to set the name tag.
     */
    public void updateNameTags(Player player,
                               Function<Player, PermissionGroup> playerPermissionGroupFunction,
                               Function<Player, PermissionGroup> allOtherPlayerPermissionGroupFunction) {
        if (CloudAPI.getInstance().getPermissionPool() == null || !CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            return;
        }

        PermissionGroup playerPermissionGroup = playerPermissionGroupFunction != null ? playerPermissionGroupFunction.apply(player) : CloudServer.getInstance()
                .getCachedPlayer(player.getUniqueId())
                .getPermissionEntity()
                .getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());

        initScoreboard(player);

        for (Player all : player.getServer().getOnlinePlayers()) {
            initScoreboard(all);

            if (playerPermissionGroup != null) {
                addTeamEntry(player, all, playerPermissionGroup);
            }

            PermissionGroup targetPermissionGroup = allOtherPlayerPermissionGroupFunction != null ? allOtherPlayerPermissionGroupFunction.apply(
                    all) : null;

            if (targetPermissionGroup == null) {
                targetPermissionGroup = CloudServer.getInstance().getCachedPlayer(all.getUniqueId()).getPermissionEntity()
                        .getHighestPermissionGroup(CloudAPI.getInstance()
                                .getPermissionPool());
            }

            if (targetPermissionGroup != null) {
                addTeamEntry(all, player, targetPermissionGroup);
            }

        }
    }

    /**
     * Set a player they have no scoreboard a new one.
     *
     * @param player they have no scoreboard.
     */
    private void initScoreboard(Player player) {
        if (player.getScoreboard() == null) {
            player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
        }
    }

    /**
     * Add a player to a team entry.
     *
     * @param target          the player to added to the team.
     * @param all             players to update the scoreboard and the team.
     * @param permissionGroup the group to get prefix and suffix from.
     */
    private void addTeamEntry(Player target, Player all, PermissionGroup permissionGroup) {
        String teamName = permissionGroup.getTagId() + permissionGroup.getName();
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
            CloudAPI.getInstance()
                    .dispatchConsoleMessage("In order to prevent issues, the name (+ tagID) of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the name of the group in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the name (+ tagID) of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!",
                    "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the name of the group in your perms.yml", "cloudnet.notify");
        }
        Team team = all.getScoreboard().getTeam(teamName);
        if (team == null) {
            team = all.getScoreboard().registerNewTeam(teamName);
        }

        if (permissionGroup.getPrefix().length() > 16) {
            permissionGroup.setPrefix(permissionGroup.getPrefix().substring(0, 16));
            CloudAPI.getInstance()
                    .dispatchConsoleMessage("In order to prevent issues, the prefix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the prefix in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the prefix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!",
                    "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the prefix in your perms.yml", "cloudnet.notify");
        }
        if (permissionGroup.getSuffix().length() > 16) {
            permissionGroup.setSuffix(permissionGroup.getSuffix().substring(0, 16));
            CloudAPI.getInstance()
                    .dispatchConsoleMessage("In order to prevent issues, the suffix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the suffix in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the suffix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!",
                    "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the suffix in your perms.yml", "cloudnet.notify");
        }

        try {
            Method setColor = team.getClass().getDeclaredMethod("setColor", ChatColor.class);
            setColor.setAccessible(true);
            if (permissionGroup.getColor().length() != 0) {
                setColor.invoke(team, ChatColor.getByChar(permissionGroup.getColor().replaceAll("&", "").replaceAll("§", "")));
            } else {
                setColor.invoke(team, ChatColor.getByChar(ChatColor.getLastColors(permissionGroup.getPrefix().replace('&', '§'))
                        .replaceAll("&", "")
                        .replaceAll("§", "")));
            }
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        team.setPrefix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix()));
        team.setSuffix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix()));

        team.addEntry(target.getName());

        target.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay() + target.getName()));
    }
}
