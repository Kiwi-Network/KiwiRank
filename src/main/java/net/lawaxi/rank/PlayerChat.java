package net.lawaxi.rank;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class PlayerChat implements Listener {

   public static HashMap<Player,String> level = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);
        /*if(event.getMessage().substring(0,1).equals("@"))
        {
            //Shout

            new BukkitRunnable(){
                @Override
                public void run() {

                    Bukkit.dispatchCommand(event.getPlayer(),"/shout "+event.getMessage().substring(1));
                }
            }.runTask(Rank.instance);

        }
        else {*/
            //Common
            TextComponent a = new TextComponent(
                    Rank.messages.getString("common.player")
                            .replace("%level%",level.get(event.getPlayer()))
                                    .replace("%player1%",event.getPlayer().getName())
                                    .replace("%player2%",event.getPlayer().getDisplayName()));

            a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(
                    Rank.messages.getString("playertext")
                            .replace("%rank%",Rank.ranks.getString("types."+Rank.getRank(event.getPlayer().getName())+".name"))
                            .replace("%level%",level.get(event.getPlayer()))
                            .replace("%player1%",event.getPlayer().getName())
                            .replace("%player2%",event.getPlayer().getDisplayName())
            ).create()));
            a.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/p "+event.getPlayer().getName()));


            TextComponent b = new TextComponent(
                    Rank.messages.getString("common.message")
                            .replace("%message%",event.getMessage()));

            for(Player player:Bukkit.getOnlinePlayers()){
                player.spigot().sendMessage(a,b);
            }
        //}
    }
}
