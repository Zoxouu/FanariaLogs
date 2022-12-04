package me.zoxymodz.fanarialogs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zoxymodz.fanarialogs.cmd.FanariaLogsCmd;
import me.zoxymodz.fanarialogs.listeners.PlayerListener;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class Fanarialogs extends JavaPlugin {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static LuckPerms api;
    private static Fanarialogs main;

    @Override
    public void onEnable() {
        main = this;
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
        }
        try {
            createConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getCommand("fanarialogs").setExecutor(new FanariaLogsCmd());
        registerListeners();
    }

    public void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    public void createConfig() throws IOException {
        Files.createDirectories(Paths.get(getDataFolder().toPath().toAbsolutePath().toString(), "datas"));
        if(Files.notExists(getDataFolder().toPath().resolve("config.json"))) {
            try (BufferedWriter writer = Files.newBufferedWriter(getDataFolder().toPath().resolve("config.json"), StandardOpenOption.CREATE);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(getResource("config.json")))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    public static LuckPerms getApi() {
        return api;
    }
    public static String getPrefix(){
        return "§c§lALERTE §f◆ ";
    }

    public static Fanarialogs get() {
        return main;
    }
}
