package aetherial.aether.essentials.teleportation.command.warp;

import aetherial.aether.essentials.AetherEssentials;
import aetherial.aether.essentials.exception.AlreadyInitialized;
import aetherial.aether.essentials.exception.NotInitialized;
import aetherial.aether.essentials.util.persistence.Persistence;
import aetherial.aether.essentials.util.persistence.yaml.LabeledLocationYaml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarpStorage {

    private static WarpStorage instance;
    private static final Map<String, Location> warpLocations = new ConcurrentHashMap<>();
    private static final String WARPS_SUBFOLDER_NAME = "warps";

    public static WarpStorage getInstance() {
        if (instance == null) {
            throw new NotInitialized("WarpStorage has not been initialized");
        }
        return instance;
    }

    public static void init(JavaPlugin plugin) {
        if (instance != null) {
            throw new AlreadyInitialized("WarpStorage has already been initialized");
        }
        instance = new WarpStorage(plugin);
    }

    private final Logger log = LogManager.getLogger(AetherEssentials.PLUGIN_NAME);
    private final File warpsFolder;

    private WarpStorage(JavaPlugin plugin) {
        warpsFolder = Persistence.getInstance().getDataSubfolder(WARPS_SUBFOLDER_NAME);
        loadSavedWarps(plugin);
    }

    private void loadSavedWarps(JavaPlugin plugin) {
        Optional<File[]> optional = Optional.ofNullable(
            warpsFolder.listFiles((dir, name) -> name.substring(name.lastIndexOf('.')).equals(Persistence.YAML_FILE_EXT))
        );
        if (optional.isEmpty()) {
            log.info("Failed to load saved warps");
            return;
        }
        File[] files = optional.get();
        if (files.length == 0) {
            return;
        }

        for (File file : files) {
            Server server = plugin.getServer();
            Optional<LabeledLocationYaml> optionalLabeledLocationYaml = Persistence.getInstance().readYamlFile(file, LabeledLocationYaml.class);
            if (optionalLabeledLocationYaml.isEmpty()) {
                continue;
            }
            LabeledLocationYaml locationYaml = optionalLabeledLocationYaml.get();
            warpLocations.put(locationYaml.getLabel(), locationYaml.asLocation(server));
        }
    }

    public List<String> getWarpLocationNames() {
        return new ArrayList<>(warpLocations.keySet());
    }

    public Optional<Location> getWarpLocation(String warpName) {
        return Optional.ofNullable(warpLocations.get(warpName));
    }

    public Optional<Location> setWarpLocation(String warpName, Location location) {
        boolean saved = Persistence.getInstance().writeFileYaml(WARPS_SUBFOLDER_NAME, warpName, new LabeledLocationYaml(warpName, location));
        if (!saved) {
            return Optional.empty();
        }
        warpLocations.put(warpName, location);

        return Optional.of(location);
    }

    public Optional<Location> deleteWarpLocation(String warpName) {
        boolean deleted = Persistence.getInstance().deleteYamlFile(WARPS_SUBFOLDER_NAME, warpName);
        if (!deleted) {
            return Optional.empty();
        }
        Location location = warpLocations.remove(warpName);
        return Optional.ofNullable(location);
    }

    public List<String> onTabComplete(String arg) {
        List<String> allWarps = getWarpLocationNames();
        List<String> possibleCompletes = new ArrayList<>();

        for (String warp : allWarps) {
            if (warp.startsWith(arg)) {
                possibleCompletes.add(warp);
            }
        }

        return possibleCompletes;
    }

}
