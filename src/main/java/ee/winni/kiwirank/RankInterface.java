package ee.winni.kiwirank;

import org.bukkit.entity.Player;

public interface RankInterface {
    void reloadPlayerRanks(Player player1);

     void saveRanks();

     void reloadPlayerLevels(Player player1);

     void saveLevels();

     void addExp(Player player, int amount);

     int getExp(Player player);

     int getLevel(Player player);

     String getLevelDisplay(int level);

     void reloadScoreboard(Player player1);

     String getRank(String player);

     int getRankLevel(String rank);

     boolean isAdmin(String player);
}
