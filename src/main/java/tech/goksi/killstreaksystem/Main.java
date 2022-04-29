package tech.goksi.killstreaksystem;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tech.goksi.killstreaksystem.commands.Killstreaks;
import tech.goksi.killstreaksystem.listener.Join;
import tech.goksi.killstreaksystem.listener.PlayerKill;
import tech.goksi.killstreaksystem.placeholders.PlaceholderAPI;
import tech.goksi.killstreaksystem.sql.ConnectionHandler;
import tech.goksi.killstreaksystem.sql.Database;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public final class Main extends JavaPlugin {
    private static Main instance;
    private ConnectionHandler connectionHandler;
    private Database db;
    private LinkedHashMap<String, Integer> currentKSLeaderboard;
    private LinkedHashMap<String, Integer> biggestKSLeaderboard;

    @Override
    public void onEnable() {
        connectionHandler = new ConnectionHandler();
        instance = this;
        this.saveDefaultConfig();
        try {
            connectionHandler.connect();
            Bukkit.getLogger().info("Successfully connected to SQLite");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error while reading/writing SQLite database: ");
            e.printStackTrace();
        }
        db = new Database();
        db.createTables();
        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerKill(), this);
        this.getCommand("killstreaks").setExecutor(new Killstreaks());
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PlaceholderAPI().register();
        currentKSLeaderboard = new LinkedHashMap<>();
        biggestKSLeaderboard = new LinkedHashMap<>();
        /*updating leaderboards*/
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            currentKSLeaderboard = getDatabase().getLeaderBoardsCurrentKS();
            biggestKSLeaderboard = getDatabase().getLeaderBoardsBiggestKS(); //added some delay because of query
        }, 0, getConfig().getInt("Settings.PlaceholderRefresh") * 20L);
    }

    @Override
    public void onDisable() {
        connectionHandler.disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public LinkedHashMap<String, Integer> getBiggestKSLeaderboard() {
        return biggestKSLeaderboard;
    }

    public LinkedHashMap<String, Integer> getCurrentKSLeaderboard() {
        return currentKSLeaderboard;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Database getDatabase() {
        return db;
    }
}
