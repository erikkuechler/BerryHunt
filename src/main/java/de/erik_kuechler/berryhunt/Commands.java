package de.erik_kuechler.berryhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;

public class Commands implements CommandExecutor {

    private JavaPlugin plugin;
    private GameManager gameManager;
    private PlayerManager playerManager;

    /**
     * Initializes a new instance of the Commands class.
     * @param plugin        The main JavaPlugin instance.
     * @param gameManager   The GameManager instance.
     * @param playerManager The PlayerManager instance.
     */
    public Commands(JavaPlugin plugin, GameManager gameManager, PlayerManager playerManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.playerManager = playerManager;
    }

    /**
     * Executes the specified command.
     * 
     * @param sender The CommandSender that executed the command.
     * @param cmd    The Command instance representing the executed command.
     * @param label  The alias of the command used.
     * @param args   An array of arguments passed with the command.
     * @return true if the command was handled successfully; otherwise, false.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only available for players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§8[§6BerryHunt§8]§f This plugin has more berry power than any smoothie and was developed by §eEriksWorld§f with a lot of passion and enthusiasm for berries.");
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            // It should not be possible to join with ACTIVE and WON status.
            if (gameManager.gameState == GameState.ACTIVE || gameManager.gameState == GameState.WON) {
                player.sendMessage("§8[§6BerryHunt§8]§c You have to wait " + gameManager.duration + " more seconds!");
            } else {
                playerManager.addPlayer(player);
                player.sendMessage("§8[§6BerryHunt§8]§a You have joined the game!");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("leave")) {
            playerManager.removePlayer(player);
            player.sendMessage(ChatColor.RED + "§8[§6BerryHunt§8]§c You have left the game.");
            return true;
        }

        if (!player.hasPermission("berryhunt.admin")) {
            player.sendMessage("§8[§6BerryHunt§8]§c Unfortunately, your berry expertise is not enough to execute this command.");
            return true;
        }

        if (args[0].equalsIgnoreCase("pos1")) {
            plugin.getConfig().set("pos1.x", player.getLocation().getBlockX());
            plugin.getConfig().set("pos1.z", player.getLocation().getBlockZ());
            plugin.getConfig().set("pos1.world", player.getLocation().getWorld().getName());
            plugin.saveConfig();
            player.sendMessage("§8[§6BerryHunt§8]§a Position 1 was set to (" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") in " + player.getLocation().getWorld().getName() + " successfully.");
            return true;
        } else if (args[0].equalsIgnoreCase("pos2")) {
            plugin.getConfig().set("pos2.x", player.getLocation().getBlockX());
            plugin.getConfig().set("pos2.z", player.getLocation().getBlockZ());
            plugin.getConfig().set("pos2.world", player.getLocation().getWorld().getName());
            plugin.saveConfig();
            player.sendMessage("§8[§6BerryHunt§8]§a Position 2 was set to (" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") in " + player.getLocation().getWorld().getName() + " successfully.");
            return true;
        } else if (args[0].equalsIgnoreCase("lobby")) {
            plugin.getConfig().set("lobby.x", player.getLocation().getBlockX());
            plugin.getConfig().set("lobby.y", player.getLocation().getBlockY());
            plugin.getConfig().set("lobby.z", player.getLocation().getBlockZ());
            plugin.getConfig().set("lobby.pitch", player.getLocation().getPitch());
            plugin.getConfig().set("lobby.yaw", player.getLocation().getYaw());
            plugin.getConfig().set("lobby.world", player.getLocation().getWorld().getName());
            plugin.saveConfig();
            player.sendMessage("§8[§6BerryHunt§8]§a Lobby was set.");
            return true;
        } else if (args[0].equalsIgnoreCase("spawn")) {
            plugin.getConfig().set("spawn.x", player.getLocation().getBlockX());
            plugin.getConfig().set("spawn.y", player.getLocation().getBlockY());
            plugin.getConfig().set("spawn.z", player.getLocation().getBlockZ());
            plugin.getConfig().set("spawn.pitch", player.getLocation().getPitch());
            plugin.getConfig().set("spawn.yaw", player.getLocation().getYaw());
            plugin.getConfig().set("spawn.world", player.getLocation().getWorld().getName());
            plugin.saveConfig();
            player.sendMessage("§8[§6BerryHunt§8]§a Spawn was set.");
            return true;
        } else if (args[0].equalsIgnoreCase("placebushes")) {
            double density = plugin.getConfig().getDouble("settings.density");
            BerryBushManager bushManager = new BerryBushManager(getPos1(), getPos2());
            bushManager.placeSweetBerryBushes(density);
            return true;
        } else if (args[0].equalsIgnoreCase("removebushes")) {
            BerryBushManager bushManager = new BerryBushManager(getPos1(), getPos2());
            bushManager.removeSweetBerryBushes();
            return true;
        } else if (args[0].equalsIgnoreCase("starting")) {
            gameManager.setGameState(GameState.STARTING);
            return true;
        } else {
            player.sendMessage("§8[§6BerryHunt§8]§c Oops, something must have gone wrong.");
            return true;
        }
    }

    /**
     * Retrieves the Location of the first defined position (pos1).
     * @return The Location of pos1.
     */
    public Location getPos1() {
        int x = plugin.getConfig().getInt("pos1.x");
        int z = plugin.getConfig().getInt("pos1.z");
        World world = Bukkit.getWorld(plugin.getConfig().getString("pos1.world"));
        return new Location(world, x, 0, z);
    }

    /**
     * Retrieves the Location of the second defined position (pos2).
     * @return The Location of pos2.
     */
    public Location getPos2() {
        int x = plugin.getConfig().getInt("pos2.x");
        int z = plugin.getConfig().getInt("pos2.z");
        World world = Bukkit.getWorld(plugin.getConfig().getString("pos2.world"));
        return new Location(world, x, 0, z);
    }
}
