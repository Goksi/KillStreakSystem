package tech.goksi.killstreaksystem.sql;

import tech.goksi.killstreaksystem.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {
    private Connection connection;

    public void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Main.getInstance().getLogger().severe("No SQLite driver found: ");
            e.printStackTrace();
        }
        if(!isConnected()){
            File dataFolder = new File(Main.getInstance().getDataFolder(), "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);

        }
    }
    private boolean isConnected(){
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }
    public void disconnect(){
        if(isConnected()){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
