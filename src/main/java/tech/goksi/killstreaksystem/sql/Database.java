package tech.goksi.killstreaksystem.sql;

import org.bukkit.entity.Player;
import tech.goksi.killstreaksystem.Main;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class Database {
    public void createTables(){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS killstreaks "
            + "(UUID BINARY(16), CurrentKS INTEGER DEFAULT 0, BiggestKS INTEGER DEFAULT 0, TheLastKS INTEGER DEFAULT 0, PRIMARY KEY (UUID))");
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS cache "
             + "(USERNAME VARCHAR(255), UUID BINARY(16), PRIMARY KEY (USERNAME))");
            ps.executeUpdate();
        }catch (SQLException e){
            Main.getInstance().getLogger().severe("Error while creating tables: ");
            e.printStackTrace();
        }
    }
    /*on player join*/
    public void initPlayer(Player p){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("INSERT OR IGNORE INTO killstreaks (UUID) VALUES (?)");
            ps.setBytes(1, covertUUID(p.getUniqueId()));
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("INSERT OR IGNORE INTO cache (USERNAME, UUID) VALUES (?, ?)");
            ps.setString(1, p.getName());
            ps.setBytes(2, covertUUID(p.getUniqueId()));
            ps.executeUpdate();
        }catch (SQLException e){
            Main.getInstance().getLogger().severe("Error while writing player to database: ");
            e.printStackTrace();
        }
    }

    public int getKillStreaks(Player p){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("SELECT UUID, CurrentKS FROM killstreaks WHERE UUID = ?");
            ps.setBytes(1, covertUUID(p.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("CurrentKS");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public int getBiggestKS(Player p){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("SELECT UUID, BiggestKS FROM killstreaks WHERE UUID = ?");
            ps.setBytes(1, covertUUID(p.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("BiggestKS");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public void addKillStreaks(Player killer, Player dead){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("UPDATE killstreaks SET CurrentKS=CurrentKS + 1 WHERE UUID=?");
            ps.setBytes(1, covertUUID(killer.getUniqueId()));
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET TheLastKS=? WHERE UUID=?");
            ps.setBytes(2, covertUUID(dead.getUniqueId()));
            ps.setInt(1, getKillStreaks(dead));
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().
                    prepareStatement("UPDATE killstreaks SET CurrentKS=0 WHERE UUID=?");
            ps.setBytes(1, covertUUID(dead.getUniqueId()));
            ps.executeUpdate();
            int  ks;
            if(getBiggestKS(killer) < (ks =getKillStreaks(killer))){
                ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET BiggestKS=? WHERE UUID=? ");
                ps.setBytes(2, covertUUID(killer.getUniqueId()));
                ps.setInt(1, ks);
                ps.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public UUID getUUID(String name){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("SELECT UUID FROM cache WHERE LOWER(USERNAME) = LOWER(?)");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return convertBinary(rs.getBytes("UUID"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    private byte[] covertUUID(UUID uuid){
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    private UUID convertBinary(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(bytes);
        buffer.flip();
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
