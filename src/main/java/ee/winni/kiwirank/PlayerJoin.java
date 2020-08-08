package ee.winni.kiwirank;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Rank.instance.reloadPlayerLevels(event.getPlayer());

        if(Rank.config.getBoolean("allow-scoreboard"))
            Rank.instance.reloadScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        PlayerChat.level.remove(event.getPlayer());
    }

}
