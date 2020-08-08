package net.lawaxi.rank;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event)
    {
        Rank.reloadPlayerLevels(event.getPlayer());
        if(Rank.config.getBoolean("allow-scoreboard"))
            Rank.reloadScoreboard(event.getPlayer());
    }

}
