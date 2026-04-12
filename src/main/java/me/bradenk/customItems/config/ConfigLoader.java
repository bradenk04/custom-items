package me.bradenk.customItems.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.items.CustomItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConfigLoader {
    static File pluginFolder = CustomItems.instance.getDataFolder();
    static File itemsConfigFolder = new File(pluginFolder, "items");
    public static ConcurrentHashMap<String, CustomItem> customItems = new ConcurrentHashMap<>();

    public static void load() {
        if (!pluginFolder.exists()) {
            boolean createPluginFolderResult = pluginFolder.mkdir();
            if (!createPluginFolderResult) {
                CustomItems.instance.getLogger().severe("Failed to create plugin folder!");
                throw new IllegalStateException("Failed to create plugin folder!");
            }
            CustomItems.instance.saveResource("config.toml", false);
        }

        if (!itemsConfigFolder.exists()) {
            boolean createItemsConfigFolderResult = itemsConfigFolder.mkdir();
            if (!createItemsConfigFolderResult) {
                CustomItems.instance.getLogger().severe("Failed to create items config folder!");
                throw new IllegalStateException("Failed to create items config folder!");
            }
            CustomItems.instance.saveResource("items/cool_diamond_sword.toml", false);
            CustomItems.instance.saveResource("items/awesome_netherite_sword.toml", false);
        }

        Path itemsConfigPath = itemsConfigFolder.toPath();
        List<Path> itemsConfigPaths;
        try {
            itemsConfigPaths = getItemsConfigPaths(itemsConfigPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to get items config paths!");
        }

        for (Path path : itemsConfigPaths) {
            CommentedFileConfig fileConfig = CommentedFileConfig.of(path);
            fileConfig.load();
            CustomItem item = CustomItem.from(fileConfig);
            if (item == null) {
                CustomItems.instance.getLogger().severe("Failed to load item: " + path.getFileName());
                continue;
            }
            if (customItems.containsKey(item.getId())) {
                CustomItems.instance.getLogger().warning("Duplicate item id: " + item.getId());
                continue;
            }
            customItems.put(item.getId(), item);
            CustomItems.instance.getLogger().info("Loaded item: " + item.getId());
        }
    }

    private static List<Path> getItemsConfigPaths(Path itemsPath) throws IOException {
        try (var stream = Files.walk(itemsPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".toml"))
                    .collect(Collectors.toList());
        }
    }
}
