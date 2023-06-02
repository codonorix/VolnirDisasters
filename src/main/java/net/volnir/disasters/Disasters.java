package net.volnir.disasters;

import net.volnir.disasters.commands.admin.SetSpawn;
import net.volnir.disasters.game.gamemaster.GameMaster;
import net.volnir.disasters.commands.admin.GetStats;
import net.volnir.disasters.commands.admin.RemoveNPC;
import net.volnir.disasters.disasters.night.werewolf.WereWolfAbilityClick;
import net.volnir.disasters.disasters.night.werewolf.WereWolfCommand;
import net.volnir.disasters.game.gamemaster.GameMasterClick;
import net.volnir.disasters.game.gamesystem.gameItems.LeaveItem;
import net.volnir.disasters.storage.objectcreation.PlayerObjectCreation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Disasters extends JavaPlugin {
    private static Disasters instance;
    FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        config = getInstance().getConfig();

        coreSystemRegister();
        adminSystems();
        wereWolfRegister();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        wereWolfUnload();
    }

    public static Disasters getInstance() {
        return instance;
    }

    public static Configuration config() {
        return getInstance().getConfig();
    }

    private static void coreSystemRegister() {
        //COMMANDS
        //EVENTS
        Bukkit.getPluginManager().registerEvents(new PlayerObjectCreation(), getInstance());
        Bukkit.getPluginManager().registerEvents(new GameMasterClick(), getInstance());
        Bukkit.getPluginManager().registerEvents(new LeaveItem(), getInstance());
    }

    private static void adminSystems() {
        //COMMANDS
        getInstance().getCommand("stats").setExecutor(new GetStats());
        getInstance().getCommand("gamemaster").setExecutor(new GameMaster());
        getInstance().getCommand("setlobby").setExecutor(new SetSpawn());
        //EVENTS
        Bukkit.getPluginManager().registerEvents(new RemoveNPC(), getInstance());
    }

    private static void wereWolfRegister() {
        Bukkit.getPluginManager().registerEvents(new WereWolfAbilityClick(), getInstance());

        getInstance().getCommand("werewolf").setExecutor(new WereWolfCommand());
    }

    private static void wereWolfUnload() {
//        WereWolf.getObjectInstance().resetWerewolf();
    }
}
