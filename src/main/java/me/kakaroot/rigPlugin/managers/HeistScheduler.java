package me.kakaroot.rigPlugin.managers;

import me.kakaroot.rigPlugin.RigPlugin;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class HeistScheduler {

    private final RigPlugin plugin;
    private int taskId = -1;
    private final Map<String, Long> lastStartedMap = new HashMap<>();

    public HeistScheduler(RigPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (taskId != -1) return; // already running

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::startScheduledHeists, 0L, 20L * 60).getTaskId();
    }

    public void cancel() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    private void startScheduledHeists() {
        plugin.getRigManager().getAllRigNames().forEach(rigName -> {
            int frequency = plugin.getRigManager().getFrequency(rigName);
            long lastStarted = lastStartedMap.getOrDefault(rigName, 0L);
            long now = System.currentTimeMillis();

            if (now - lastStarted >= frequency * 60 * 1000L) {
                plugin.getHeistManager().startHeist(rigName);
                lastStartedMap.put(rigName, now);
                plugin.getRigManager().updateLastStart(rigName);
            }
        });
    }
}
