package com.auqkwatech.auqkwacore.utils.command;

import com.auqkwatech.auqkwacore.AuqkwaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class CommandMapUtil {

    private static Field commandMap;
    private static Constructor<PluginCommand> commandConstructor;

    static {
        try {
            (commandMap = SimplePluginManager.class.getDeclaredField("commandMap")).setAccessible(true);

            (commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class)).setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void unregisterCommand(String s) {
        getCommandMap().getCommand(s).unregister(getCommandMap());
    }

    public static void unregisterCommands(Plugin plugin) {
        final CommandMap commandMap = getCommandMap();
        getCommands().values()
                .stream()
                .filter(command -> AuqkwaCore.Companion.getInstance().getServer().getPluginCommand(command.getName()).getPlugin().getName().equals(plugin.getName()))
                .forEach(command -> command.unregister(commandMap));
    }

    public static void registerCommand(Plugin plugin, String[] aliases) {
        getCommandMap().register(plugin.getDescription().getName(), getPluginCommand(plugin, aliases));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Command> getCommands() {
        try {
            return (Map<String, org.bukkit.command.Command>) commandMap.get(AuqkwaCore.Companion.getInstance().getServer().getPluginManager());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get the Bukkit KnowmCommand Map. Please contact Simpleness or LockedThread.", e);
        }
    }

    private static CommandMap getCommandMap() {
        try {
            return (CommandMap) commandMap.get(AuqkwaCore.Companion.getInstance().getServer().getPluginManager());
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Unable to get the Bukkit CommandMap. Please contact Simpleness or LockedThread.", e);
        }
    }

    private static PluginCommand getPluginCommand(Plugin plugin, String... aliases) {
        try {
            PluginCommand pluginCommand = commandConstructor.newInstance(aliases[0], plugin);
            pluginCommand.setAliases(Arrays.asList(Arrays.copyOfRange(aliases, 1, aliases.length)));
            pluginCommand.setExecutor(AuqkwaCore.Companion.getInstance().commandExecutor);
            return pluginCommand;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException("Unable to get PluginCommand. Please contact Simpleness or LockedThread.", e);
        }
    }
}