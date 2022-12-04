package me.zoxymodz.fanarialogs.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.zoxymodz.fanarialogs.Fanarialogs;
import me.zoxymodz.fanarialogs.manager.FilesManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PlayerListener implements Listener {

    private final Fanarialogs main;

    public PlayerListener(Fanarialogs main){
        this.main = main;
    }

    @EventHandler
    public void onPlayerCMD(PlayerCommandPreprocessEvent event) throws IOException {
        Player player = event.getPlayer();
        List<String> commands = new ArrayList<>();
        try(BufferedReader reader = Files.newBufferedReader(FilesManager.getPath("config"))) {
            JsonObject config = Fanarialogs.gson.fromJson(reader, JsonObject.class);
            StreamSupport.stream(config.getAsJsonArray("locked").spliterator(), false).map(JsonElement::getAsString).forEach(commands::add);
            JsonObject object = new JsonObject();
            for (String command : commands) {
                if(StreamSupport.stream(config.getAsJsonArray("ignored").spliterator(), false).map(JsonElement::getAsString).anyMatch(s -> player.getName().equals(s))) return;
                if (!event.getMessage().substring(1).startsWith(command)) continue;
                for (Player op : Bukkit.getOnlinePlayers().stream().filter(player1 -> player1.hasPermission("fanaria.logs.alert")).collect(Collectors.toSet())) {
                    LocalTime localTime = LocalTime.now();
                    JsonObject playerObject = new JsonObject();
                    JsonObject playerInfo = new JsonObject();
                    playerInfo.addProperty("name", player.getName());
                    playerInfo.addProperty("uuid", player.getUniqueId().toString());
                    playerInfo.addProperty("ip", player.getAddress().toString());
                    playerInfo.addProperty("rank", Objects.requireNonNull(main.getApi().getUserManager().getUser(player.getName())).getPrimaryGroup());
                    playerObject.add("info", playerInfo);
                    JsonObject playerLogs = new JsonObject();
                    JsonArray playerCmd = new JsonArray();
                    playerCmd.add(new JsonPrimitive("[" + localTime + "] Logs de commandes effectué (" + event.getMessage() + ")"));
                    if(Files.exists(FilesManager.getDataPath(player.getName()))) {
                        try(BufferedReader cmdReader = Files.newBufferedReader(FilesManager.getDataPath(player.getName()))) {
                            playerCmd.addAll(Fanarialogs.gson.fromJson(cmdReader, JsonObject.class).getAsJsonObject("player").getAsJsonObject("logs").getAsJsonArray("types"));
                        }
                    }
                    playerLogs.add("types", playerCmd);
                    playerObject.add("logs", playerLogs);
                    object.add("player", playerObject);
                    try(BufferedWriter writer = Files.newBufferedWriter(FilesManager.getDataPath(player.getName()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        writer.write(Fanarialogs.gson.toJson(object));
                    }
                    op.sendMessage(Fanarialogs.getPrefix() + "§e" + player.getName() + " §7vient de taper la commande §6" + event.getMessage());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) throws IOException {
        Player player = (Player) event.getWhoClicked();
        BufferedReader reader = Files.newBufferedReader(FilesManager.getPath("config"));
        JsonObject config = Fanarialogs.gson.fromJson(reader, JsonObject.class);
        JsonObject object = new JsonObject();
        if(StreamSupport.stream(config.getAsJsonArray("ignored").spliterator(), false).map(JsonElement::getAsString).anyMatch(s -> player.getName().equals(s))) return;
        if (event.getClick().isCreativeAction() || event.getClick().equals(ClickType.MIDDLE)) {
            for (Player op : Bukkit.getOnlinePlayers().stream().filter(player1 -> player1.hasPermission("fanaria.logs.alert")).collect(Collectors.toSet())) {
                LocalTime localTime = LocalTime.now();
                JsonObject playerObject = new JsonObject();
                JsonObject playerInfo = new JsonObject();
                playerInfo.addProperty("name", player.getName());
                playerInfo.addProperty("uuid", player.getUniqueId().toString());
                playerInfo.addProperty("ip", player.getAddress().toString());
                playerInfo.addProperty("rank", Objects.requireNonNull(main.getApi().getUserManager().getUser(player.getName())).getPrimaryGroup());
                playerObject.add("info", playerInfo);
                JsonObject playerLogs = new JsonObject();
                JsonArray playerCmd = new JsonArray();
                if (event.getCurrentItem().equals(Material.AIR)){
                    playerCmd.add(new JsonPrimitive("[" + localTime + "] Logs de duplication effectué "));
                    return;
                }
                playerCmd.add(new JsonPrimitive("[" + localTime + "] Logs de duplication effectué ("+ event.getCurrentItem().getType() +")"));
                if(Files.exists(FilesManager.getDataPath(player.getName()))) {
                    try(BufferedReader cmdReader = Files.newBufferedReader(FilesManager.getDataPath(player.getName()))) {
                        playerCmd.addAll(Fanarialogs.gson.fromJson(cmdReader, JsonObject.class).getAsJsonObject("player").getAsJsonObject("logs").getAsJsonArray("types"));
                    }
                }
                playerLogs.add("types", playerCmd);
                playerObject.add("logs", playerLogs);
                object.add("player", playerObject);
                try(BufferedWriter writer = Files.newBufferedWriter(FilesManager.getDataPath(player.getName()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    writer.write(Fanarialogs.gson.toJson(object));
                }
                Group luckPerms = Fanarialogs.getApi().getGroupManager().getGroup(Fanarialogs.getApi().getUserManager().getUser(player.getName()).getPrimaryGroup());
                if (event.getCurrentItem().equals(Material.AIR)){
                    op.sendMessage(Fanarialogs.getPrefix() + "§e" + luckPerms.getCachedData().getMetaData().getPrefix() + player.getName() + " §7vient de dupliquer des items");
                    return;
                }
                op.sendMessage(Fanarialogs.getPrefix() + "§e" + luckPerms.getCachedData().getMetaData().getPrefix() + player.getName() + " §7vient de dupliquer des items §7(§6"+ event.getCurrentItem().getType() +"§7)");
                return;
            }
        }
    }
}
