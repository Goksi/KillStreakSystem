package tech.goksi.killstreaksystem.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {
    private Connection connection;

    public void connect() throws SQLException {
        if(!isConnected()){
            connection = DriverManager.getConnection("jdbc:sqlite:KillStreakSystem/database.db");

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
