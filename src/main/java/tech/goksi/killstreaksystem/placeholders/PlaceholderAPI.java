package tech.goksi.killstreaksystem.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.goksi.killstreaksystem.Main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "KillstreakSystem";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Goksi";
    }

    @Override
    public @NotNull String getVersion() {
        return "beta";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(params.startsWith("kss")){
            String[] args = params.split("_");
            if (args.length == 3 || args.length == 4) {
                int num;
                try{
                    num = Integer.parseInt(args[2]);
                }catch (NumberFormatException e){
                    return "&4Last arg should be a number";
                }
                if(num<=0 || num > 10) return "&4You can only use numbers from 1 to 10";
                if(args[1].equals("topcurrentks")){
                    LinkedHashMap<String, Integer> lb = Main.getInstance().getDatabase().getLeaderBoardsCurrentKS();
                    return getResult(args, num, lb);
                }else if (args[1].equals("topbiggestks")){
                    LinkedHashMap<String, Integer> lb = Main.getInstance().getDatabase().getLeaderBoardsBiggestKS();
                    return getResult(args, num, lb);
                }
            }else return "&4Wrong placeholder";

        }
        return null;
    }

    @Nullable
    private String getResult(String[] args, int num, LinkedHashMap<String, Integer> lb) {
        Optional<Map.Entry<String, Integer>> selectedEntry = lb.entrySet().stream().skip(num-1).findFirst();
        if(selectedEntry.isPresent()){
            if(args.length == 4 && args[3].equals("number")) return String.valueOf(selectedEntry.get().getValue());
            else return selectedEntry.get().getKey();
        }else return "";
    }
}
