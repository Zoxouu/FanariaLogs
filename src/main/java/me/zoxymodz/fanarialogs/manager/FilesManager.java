package me.zoxymodz.fanarialogs.manager;

import me.zoxymodz.fanarialogs.Fanarialogs;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesManager {

    private static final JavaPlugin plugin = JavaPlugin.getPlugin(Fanarialogs.class);

    public static void createFile(String fileName) throws IOException {
        Path path = Paths.get(plugin.getDataFolder()+ "/datas");
        if (!path.toFile().exists()){
            Files.createDirectories(path);
        }
        File file = new File(path.toFile(), fileName+".json");
        if (!file.exists()) file.createNewFile();
    }

    public static File getFile(String fileName){
        return new File(plugin.getDataFolder() + "/datas", fileName + ".json");
    }

    public static Path getDataPath(String fileName) {
        return Paths.get(plugin.getDataFolder().getAbsolutePath(), "datas", fileName + ".json");
    }

    public static Path getPath(String fileName) {
        return Paths.get(plugin.getDataFolder().getAbsolutePath(), fileName + ".json");
    }
}
