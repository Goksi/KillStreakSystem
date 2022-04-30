package tech.goksi.killstreaksystem.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import tech.goksi.killstreaksystem.Main;

public class PlayerKill implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player target = e.getEntity();
        Entity killer = ((EntityDamageByEntityEvent) target.getLastDamageCause()).getDamager();
        if (killer == null) return;
        if (killer instanceof Monster && Main.getInstance().getConfig().getBoolean("Settings.ResetKSOnMobKill")) {
            Main.getInstance().getDatabase().resetKillstreak(target);
        } else if (killer instanceof Player) {
            Player p = ((Player) killer).getPlayer();
            Main.getInstance().getDatabase().addKillStreaks(p, target);
            double conf = Main.getInstance().getConfig().getDouble("Settings.MoneyOnKill");
            double amount = conf + Main.getInstance().getDatabase().getKillStreaks(target)*conf;
            Main.getInstance().getEconomy().depositPlayer(p ,amount);
            p.sendMessage(Main.getInstance().getConfig().getString("Messages.KillerKill").replaceAll("%amount", String.valueOf(amount)).replaceAll("%player", target.getName()));
            if(Main.getInstance().getConfig().getBoolean("Settings.TakeMoneyOnDeath")){
                amount = Main.getInstance().getConfig().getDouble("Settings.MoneyOnKill");
                Main.getInstance().getEconomy().withdrawPlayer(target, amount);
                target.sendMessage(Main.getInstance().getConfig().getString("Messages.DeadKill").replaceAll("%amount", String.valueOf(amount)).replaceAll("%player", p.getName()));
            }
        }

    }
}
