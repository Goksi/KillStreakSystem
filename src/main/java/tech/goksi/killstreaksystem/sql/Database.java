package tech.goksi.killstreaksystem.sql;

import com.google.common.io.ByteStreams;
import tech.goksi.killstreaksystem.Main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;


public class Database {
    public void createTables(){
        try{
            PreparedStatement ps = Main.getInstance().getConnectionHandler().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS killstreaks "
            + "(UUID BINARY(16), CurrentKS INTEGER DEFAULT 0, BigggestKS INTEGER DEFAULT 0)");
            ps.executeUpdate();

        }catch (SQLException e){
            Main.getInstance().getLogger().severe("Error while creating tables: ");
            e.printStackTrace();
        }
    }


    private InputStream covertUUID(UUID uuid){
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);
    }

    private UUID convertBinary(InputStream stream){
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try{
            buffer.put(ByteStreams.toByteArray(stream));
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        }catch (IOException e){
            Main.getInstance().getLogger().severe("Error while decoding uuid data: ");
            e.printStackTrace();
        }
        return null;
    }
}
