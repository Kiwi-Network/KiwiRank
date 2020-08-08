package ee.winni.kiwirank;

import org.bukkit.Bukkit;
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

        if(Bukkit.getServer().getOperators().contains(event.getPlayer())){
            if(!Rank.instance.isAdmin(event.getPlayer().getName()))
                Bukkit.getServer().getOperators().remove(event.getPlayer());
        }else{
            if(Rank.instance.isAdmin(event.getPlayer().getName()))
                Bukkit.getServer().getOperators().add(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        PlayerChat.level.remove(event.getPlayer());
    }

}
