package tech.goksi.killstreaksystem;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tech.goksi.killstreaksystem.sql.ConnectionHandler;
import tech.goksi.killstreaksystem.sql.Database;

import java.sql.SQLException;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    private static Main instance;
    private ConnectionHandler connectionHandler;
    private Database db;
    @Override
    public void onEnable() {
        connectionHandler = new ConnectionHandler();
        instance = this;
        this.saveDefaultConfig();
        try{
            connectionHandler.connect();
            Bukkit.getLogger().info("Successfully connected to SQLite");
        }catch (SQLException e){
            Bukkit.getLogger().severe( "Error while reading/writing SQLite database: ");
            e.printStackTrace();
        }
        db = new Database();
        db.createTables();



    }

    @Override
    public void onDisable() {
        connectionHandler.disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Database getDatabase() {
        return db;
    }
}
