package tech.goksi.killstreaksystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.goksi.killstreaksystem.Main;

public class Join implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Main.getInstance().getDatabase().initPlayer(e.getPlayer());
    }
}
