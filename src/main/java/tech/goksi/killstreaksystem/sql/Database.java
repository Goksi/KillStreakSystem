package tech.goksi.killstreaksystem.sql;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tech.goksi.killstreaksystem.Main;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;


public class Database {
    public void createTables() {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS killstreaks "
                    + "(UUID BINARY(16), CurrentKS INTEGER DEFAULT 0, BiggestKS INTEGER DEFAULT 0, TheLastKS INTEGER DEFAULT 0,LastKiller BINARY(16) DEFAULT NULL, Counter INTEGER DEFAULT 0, PRIMARY KEY (UUID))");
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS cache "
                    + "(USERNAME VARCHAR(255), UUID BINARY(16), PRIMARY KEY (USERNAME))");
            ps.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error while creating tables: ");
            e.printStackTrace();
        }
    }

    /*on player join*/
    public void initPlayer(Player p) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("INSERT OR IGNORE INTO killstreaks (UUID) VALUES (?)");
            ps.setBytes(1, covertUUID(p.getUniqueId()));
            ps.executeUpdate();
            /*maybe change uuid if different uuid is detected*/
            ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("INSERT OR IGNORE INTO cache (USERNAME, UUID) VALUES (?, ?)");
            ps.setString(1, p.getName());
            ps.setBytes(2, covertUUID(p.getUniqueId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error while writing player to database: ");
            e.printStackTrace();
        }
    }

    public int getKillStreaks(Player p) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("SELECT UUID, CurrentKS FROM killstreaks WHERE UUID = ?");
            ps.setBytes(1, covertUUID(p.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("CurrentKS");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBiggestKS(Player p) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection()
                    .prepareStatement("SELECT UUID, BiggestKS FROM killstreaks WHERE UUID = ?");
            ps.setBytes(1, covertUUID(p.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("BiggestKS");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //dodati da ne moze da umire vise puta od istog igraca
    public void addKillStreaks(Player killer, Player dead) {
        PreparedStatement ps;
        int ks;
        try {
            if(!isLatestKiller(killer, dead)){
                ps = Main.getInstance().getConnectionHandler().getConnection()
                        .prepareStatement("UPDATE killstreaks SET CurrentKS=? WHERE UUID=?");
                ps.setBytes(2, covertUUID(killer.getUniqueId()));
                ps.setInt(1, getKillStreaks(killer) + 1);
                ps.executeUpdate();
                if (getBiggestKS(killer) < (ks = getKillStreaks(killer))) {
                    ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET BiggestKS=? WHERE UUID=? ");
                    ps.setBytes(2, covertUUID(killer.getUniqueId()));
                    ps.setInt(1, ks);
                    ps.executeUpdate();
                }
            }
            resetKillstreak(dead);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID(String name) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("SELECT UUID FROM cache WHERE LOWER(USERNAME) = LOWER(?)");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return convertBinary(rs.getBytes("UUID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setKillStreak(Player target, int killStreaks) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET CurrentKS=? WHERE UUID=?");
            ps.setInt(1, killStreaks);
            ps.setBytes(2, covertUUID(target.getUniqueId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restoreKS(Player target) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET CurrentKS = TheLastKs WHERE UUID=?");
            ps.setBytes(1, covertUUID(target.getUniqueId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetKillstreak(Player target) {
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET TheLastKS=? WHERE UUID=?");
            ps.setBytes(2, covertUUID(target.getUniqueId()));
            ps.setInt(1, getKillStreaks(target));
            ps.executeUpdate();
            ps = Main.getInstance().getConnectionHandler().getConnection().
                    prepareStatement("UPDATE killstreaks SET CurrentKS=0 WHERE UUID=?");
            ps.setBytes(1, covertUUID(target.getUniqueId()));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String, Integer> getLeaderBoardsCurrentKS() {
        LinkedHashMap<String, Integer> temp = new LinkedHashMap<>();
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("SELECT UUID,CurrentKS FROM killstreaks ORDER BY CurrentKS DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                temp.put(Bukkit.getOfflinePlayer(convertBinary(rs.getBytes("UUID"))).getName(), rs.getInt("CurrentKS")); //must have offline player because if someone doesn't have premium it throws NPE
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public LinkedHashMap<String, Integer> getLeaderBoardsBiggestKS() {
        LinkedHashMap<String, Integer> temp = new LinkedHashMap<>();
        try {
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("SELECT UUID, BiggestKS FROM killstreaks ORDER BY BiggestKS DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                temp.put(Bukkit.getPlayer(convertBinary(rs.getBytes("UUID"))).getName(), rs.getInt("BiggestKS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return temp;
    }
    private boolean isLatestKiller(Player killer, Player dead){
        UUID lastKiller = null;
        int counter = 0;
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("SELECT LastKiller, Counter FROM killstreaks WHERE UUID=? ");
            ps.setBytes(1, covertUUID(dead.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                lastKiller = convertBinary(rs.getBytes("LastKiller"));
                counter = rs.getInt("Counter");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        if(killer.getUniqueId().equals(lastKiller)){
            counter++;
            return counter >= Main.getInstance().getConfig().getInt("Settings.AntiAbuse");
        }else  {

            lastKiller = killer.getUniqueId();
            counter = 0;
        }
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("UPDATE killstreaks SET LastKiller=?, Counter=? WHERE UUID=?");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    private byte[] covertUUID(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    private UUID convertBinary(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(bytes);
        buffer.flip();
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
