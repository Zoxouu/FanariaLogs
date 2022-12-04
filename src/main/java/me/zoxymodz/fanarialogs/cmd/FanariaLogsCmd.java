package me.zoxymodz.fanarialogs.cmd;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.zoxymodz.fanarialogs.Fanarialogs;
import me.zoxymodz.fanarialogs.manager.FilesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FanariaLogsCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (!player.hasPermission("fanaria.logs.cmd")) return true;
        if (args.length != 2) {
            player.sendMessage(Fanarialogs.getPrefix() +"§c/fanarialogs <info/logs> <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (!Bukkit.getOnlinePlayers().contains(target)) return true;
        if (args[0].equalsIgnoreCase("info")){
            if(!Files.exists(FilesManager.getDataPath(player.getName()))) {player.sendMessage(Fanarialogs.getPrefix() + "§cCe joueur ne possedes aucunes info");}
            try {
                JsonObject object = Fanarialogs.gson.fromJson(Files.newBufferedReader(Paths.get(Fanarialogs.get().getDataFolder() + "/datas/" + target.getName() +".json"), StandardCharsets.UTF_8), JsonObject.class).getAsJsonObject("player").getAsJsonObject("info");
                player.sendMessage(Fanarialogs.getPrefix() + "§7Voici les information de §e" + target.getName());
                player.sendMessage("§6Name §8: §7" + object.getAsJsonPrimitive("name").getAsString());
                player.sendMessage("§6Uuid §8: §7" + object.getAsJsonPrimitive("uuid").getAsString());
                player.sendMessage("§6Ip §8: §7" + object.getAsJsonPrimitive("ip").getAsString());
                player.sendMessage("§6Rank §8: §7" + object.getAsJsonPrimitive("rank").getAsString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (args[0].equalsIgnoreCase("logs")){
            if(!Files.exists(FilesManager.getDataPath(player.getName()))) {player.sendMessage(Fanarialogs.getPrefix() + "§cCe joueur ne possedes aucunes logs");}
            try {
                JsonObject object = Fanarialogs.gson.fromJson(Files.newBufferedReader(Paths.get(Fanarialogs.get().getDataFolder() + "/datas/" + target.getName() +".json"), StandardCharsets.UTF_8), JsonObject.class).getAsJsonObject("player").getAsJsonObject("logs");
                player.sendMessage(Fanarialogs.getPrefix() + "§7Voici les logs effectuer par §e" + target.getName());
                player.sendMessage("§7" + object.getAsJsonArray("types").toString().replace(",", "\n"));
                player.sendMessage("§e"+target.getName() + " §7a effectuer §6"+ object.getAsJsonArray("types").size() + " §7logs.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
