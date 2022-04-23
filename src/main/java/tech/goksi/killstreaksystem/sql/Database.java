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
            + "(UUID BINARY(16), CurrentKS INTEGER DEFAULT 0, BigggestKS INTEGER DEFAULT 0, PRIMARY KEY (UUID))");
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
                    .prepareStatement("UPDATE killstreaks SET CurrentKS=CurrentKS + ? WHERE UUID=?");
            ps.setInt(1, 1);
            ps.setBytes(2, covertUUID(killer.getUniqueId()));
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().
                    prepareStatement("UPDATE killstreaks SET CurrentKS=? WHERE UUID=?");
            ps.setInt(1, 0);
            ps.setBytes(2, covertUUID(dead.getUniqueId()));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
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
