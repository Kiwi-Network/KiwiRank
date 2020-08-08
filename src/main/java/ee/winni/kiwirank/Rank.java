package ee.winni.kiwirank;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Since;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public final class Rank extends JavaPlugin implements RankInterface, Listener {

    public static FileConfiguration config;
    public static FileConfiguration ranks;
    public static FileConfiguration levels;
    public static FileConfiguration messages;
    private static File rank;
    private static File level;
    private static File message;

    public static Logger logger;
    public static Server server;
    public static Rank instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        server = getServer();
        instance = this;

        //注册事件
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(), this);

        //1.config.yml
        saveDefaultConfig();
        config = getConfig();
        if(!config.contains("path.rank") || !config.contains("path.level"))
        {
            config.set("path.rank",getDataFolder().getAbsoluteFile()+File.separator+"ranks.yml");
            config.set("path.level",getDataFolder().getAbsoluteFile()+File.separator+"levels.yml");
            config.set("path.message",getDataFolder().getAbsoluteFile()+File.separator+"messages.yml");
            saveConfig();
        }


        //2.ranks.yml
        //初始化
        rank = new File(config.getString("path.rank"));
        if(!rank.exists())
        {
            saveResource("ranks.yml",false);

            //自定义路径
            File file =  new File(getDataFolder(),"ranks.yml");
            if(!rank.getAbsoluteFile().equals(file.getAbsoluteFile()))
            {
                FileUtil.copy(file,rank);
                file.delete();
            }

        }
        ranks = YamlConfiguration.loadConfiguration(rank);

        //必须包含default
        if(!ranks.contains("types.default.name") || !ranks.contains("types.default.list") || !ranks.contains("types.default.chat"))
        {
            ranks.set("types.default.level",0);
            ranks.set("types.default.name","§7默认");
            ranks.set("types.default.list","§7%player%");
            ranks.set("types.default.chat","§7%player%");

            saveRanks();
        }

        //3.levels.yml
        //初始化
        level = new File(config.getString("path.level"));

        if(!level.exists())
        {
            saveResource("levels.yml",false);

            File file =  new File(getDataFolder(),"levels.yml");
            if(!level.getAbsoluteFile().equals(file.getAbsoluteFile()))
            {
                FileUtil.copy(file,level);
                file.delete();
            }

        }
        levels = YamlConfiguration.loadConfiguration(level);

        //4.messages.yml
        //初始化
        message = new File(config.getString("path.message"));
        if(!message.exists()){

            saveResource("messages.yml",false);

            File file = new File(getDataFolder(),"messages.yml");
            if(!message.getAbsoluteFile().equals(file.getAbsoluteFile())){

                FileUtil.copy(file,message);
                file.delete();
            }
        }
        messages = YamlConfiguration.loadConfiguration(message);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(command.getName().equals("setrank")) {

            if(sender instanceof Player){
                if(getRankLevel(getRank(sender.getName()))<ranks.getInt("level.admin")){
                    sender.sendMessage("Unknown command. Type \"/help\" for help.");
                    return true;
                }
            }

            ranks = YamlConfiguration.loadConfiguration(rank);
            if (args.length == 2) {
                if (ranks.contains("types."+args[1]))
                {
                    ranks.set("players." + args[0], args[1]);
                    saveRanks();

                    Player a =Bukkit.getPlayer(args[0]);
                    if(a!=null)
                        reloadPlayerRanks(a);
                }
                else
                    sender.sendMessage("§c不存在该等级");
                return true;
            } else {
                return false;
            }

        }
        else if(command.getName().equals("addexp")){

            if(sender instanceof Player){
                if(getRankLevel(getRank(sender.getName()))<ranks.getInt("level.admin")){
                    sender.sendMessage("Unknown command. Type \"/help\" for help.");
                    return true;
                }
            }

            if(args.length==2){
                Player player = server.getPlayer(args[0]);
                if(player!=null) {
                    addExp(player, Integer.valueOf(args[1]));
                }
                else {
                    sender.sendMessage("§c玩家 " + args[0] + " 不在线!");
                }
                return true;
            }
            return false;
        }
        else if(command.getName().equals("level")){

            String player;
            if(args.length>0)
                player = args[0];
            else
                player = sender.getName();

            if(levels.contains("players."+player))
            {
                int exp = levels.getInt("players."+player);
                sender.sendMessage(config.getString("messages.inquire")
                        .replace("%1%",player)
                        .replace("%2%",String.valueOf(exp/ Rank.levels.getInt("types.exp.each")))
                        .replace("%3%",String.valueOf(exp)));
            }
            else
            {
                sender.sendMessage(config.getString("messages.inquirefailed").replace("%1%",player));
            }
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }


    public void reloadPlayerRanks(Player player1){
        ranks = YamlConfiguration.loadConfiguration(rank);
        if(!ranks.contains("players."+ player1.getName()))
        {
            ranks.set("players." + player1.getName(),"default");
            saveRanks();
        }

        player1.setPlayerListName(ranks.getString("types."+getRank(player1.getName())+".list").replace("%player%", player1.getName()));
        player1.setDisplayName(ranks.getString("types."+getRank(player1.getName())+".chat").replace("%player%", player1.getName()));
    }

    public void saveRanks(){

        try {
            ranks.save(rank);
        }
        catch (IOException e)
        {

        }
    }

    public void reloadPlayerLevels(Player player1){
        levels = YamlConfiguration.loadConfiguration(level);
        reloadPlayerRanks(player1);

        if(!levels.contains("players."+player1.getName())) {
            levels.set("players." + player1.getName(), levels.get("types.exp.default"));
            saveLevels();
        }

        //Chat
        int level = getLevel(player1);

        String display = getLevelDisplay(level);
        if(PlayerChat.level.containsKey(player1))
        {
            if(PlayerChat.level.get(player1).equals(display))
                return;
            else
                PlayerChat.level.remove(player1);
        }
        PlayerChat.level.put(player1,display);
    }

    public void saveLevels(){

        try {
            levels.save(level);
        }
        catch (IOException e)
        {

        }
    }

    public void addExp(Player player, int amount){

        int level = Rank.levels.getInt("players."+player.getName())/ Rank.levels.getInt("types.exp.each");
        if(level< Rank.levels.getInt("types.exp.max"))
        {
            if(amount> Rank.levels.getInt("types.exp.max")-level)
            {
                amount= Rank.levels.getInt("types.exp.max")-level;
            }
            int exp = Rank.levels.getInt("players."+player.getName())+amount;
            Rank.levels.set("players."+player.getName(),exp);
            saveLevels();

            int level2 = exp/ Rank.levels.getInt("types.exp.each");
            if(level2 != level) {
                reloadPlayerLevels(player);

                player.sendMessage(replace(replace(replace((List<String>) Rank.config.getList("messages.addexp2")
                        ,"%1%",String.valueOf(amount))
                        ,"%2%",String.valueOf(level2))
                        ,"%3%",String.valueOf((level2+1)* Rank.levels.getInt("types.exp.each")-exp))
                        .toArray(new String[0]));
            }
            else
            {
                player.sendMessage(replace(replace(replace((List<String>) Rank.config.getList("messages.addexp1")
                        ,"%1%",String.valueOf(amount))
                        ,"%2%",String.valueOf(level+1))
                        ,"%3%",String.valueOf((level+1)* Rank.levels.getInt("types.exp.each")-exp))
                        .toArray(new String[0]));
            }
        }
        else
        {
            player.sendMessage(replace((List<String>) Rank.config.getList("messages.addexp3"),"%1%",String.valueOf(amount))
                    .toArray(new String[0]));
        }

        if(config.getBoolean("allow-scoreboard"))
            reloadScoreboard(player);
    }

    private static List<String> replace(List<String> a ,String target,String replacement){
        List<String> b = new ArrayList<String>();
        for(String member : a)
        {
            b.add(member.replace(target,replacement));
        }
        return b;
    }

    public int getExp(Player player){
        return Rank.levels.getInt("players."+player.getName());
    }

    public int getLevel(Player player){
        return getExp(player)/ Rank.levels.getInt("types.exp.each");
    }

    public String getLevelDisplay(int level){
        String display = "";
        for(int i=level;i>=0;i--)
        {
            if(Rank.levels.contains("types.display."+i))
            {
                display = Rank.levels.getString("types.display."+i).replace("%n%",String.valueOf(level));
                break;
            }
        }
        return display;
    }

    public void reloadScoreboard(Player player){

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        //游戏中其他计分板恢复
        Scoreboard scoreboard1 = player.getScoreboard();
        for(Objective objective:scoreboard1.getObjectives()){
            if(!objective.getName().equals("sb") && objective.getDisplaySlot()!=DisplaySlot.SIDEBAR)
                scoreboard.getObjectives().add(objective);
        }

        Objective objective = scoreboard.registerNewObjective("sb", "dummy");
        objective.setDisplayName(Rank.config.getString("scoreboard.title"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> a = Rank.config.getStringList("scoreboard.list");
        for(String b : a)
        {
            objective.getScore(b
                    .replace("%rank%", Rank.ranks.getString("types."+ getRank(player.getName())+".name"))
                    .replace("%level%",PlayerChat.level.get(player))
            ).setScore(a.size()-a.indexOf(b));
        }
        player.setScoreboard(scoreboard);
    }

    public String getRank(String player){
        return ranks.getString("players."+player);
    }

    public int getRankLevel(String rank){
        return ranks.getInt("types."+rank+".level");
    }

}
