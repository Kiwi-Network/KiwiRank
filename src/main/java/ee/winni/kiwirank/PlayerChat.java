package ee.winni.kiwirank;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.HashMap;

public class PlayerChat implements Listener {

   public static HashMap<Player,String> level = new HashMap<Player, String>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);
        TextComponent a = new TextComponent(event.getPlayer().getDisplayName());
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(
                "§a%player1%\n§f%rank%\n§f%level%"
                        .replace("%rank%",Rank.ranks.getString("types."+Rank.instance.getRank(event.getPlayer().getName())+".name"))
                        .replace("%level%",level.get(event.getPlayer()))
                        .replace("%player1%",event.getPlayer().getName())
                        .replace("%player2%",event.getPlayer().getDisplayName())
        ).create()));
        a.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/p "+event.getPlayer().getName()));

        TextComponent b = new TextComponent("§7: "+event.getMessage());

        for(Player player:Bukkit.getOnlinePlayers()){
            player.spigot().sendMessage(a,b);
        }
        System.out.print(a.getText()+b.getText());
    }
}
