package me.kakaroot.rigPlugin;

import me.kakaroot.rigPlugin.commands.RigCommand;
import me.kakaroot.rigPlugin.listeners.ChestBreakListener;
import me.kakaroot.rigPlugin.listeners.WandListener;
import me.kakaroot.rigPlugin.managers.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RigPlugin extends JavaPlugin {

    private static RigPlugin instance;
    private RigManager rigManager;
    private HeistManager heistManager;
    private HeistScheduler heistScheduler;

    public static RigPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        initManagers();
        initScheduler(); // Initialize scheduler safely
        initCommands();
        initListeners();
        logStartup();
    }

    @Override
    public void onDisable() {
        if (heistScheduler != null) {
            heistScheduler.cancel();
        }
    }

    private void initConfig() {
        saveDefaultConfig();
    }

    private void initManagers() {
        this.rigManager = new RigManager(this);
        this.heistManager = new HeistManager(this);
        MsgManager.init(this);
    }

    private void initScheduler() {
        if (this.heistScheduler != null) {
            this.heistScheduler.cancel();
        }
        this.heistScheduler = new HeistScheduler(this);
        this.heistScheduler.start(); // safely start scheduler
    }

    private void initCommands() {
        getCommand("rig").setExecutor(new RigCommand(this));
    }

    private void initListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new WandListener(this), this);
        pm.registerEvents(GUIManager.getListener(), this);
        pm.registerEvents(new ChestBreakListener(this),this);
    }

    private void logStartup() {
        getLogger().info("RigHeist enabled!");
    }

    public RigManager getRigManager() {
        return rigManager;
    }

    public HeistManager getHeistManager() {
        return heistManager;
    }

    public HeistScheduler getHeistScheduler() {
        return heistScheduler;
    }
}
