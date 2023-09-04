package de.erik_kuechler.berryhunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Score;
import java.util.Comparator;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.block.data.Ageable;
import java.util.logging.Logger;
public class PlayerManager implements Listener {

    private JavaPlugin plugin;
    private GameManager gameManager;
    private final List<Player> joinedPlayers = new ArrayList<>();
    private final HashMap<String, Integer> beerenstandMap = new HashMap<>();
    private HashMap<UUID, Integer> highscores = new HashMap<>();

    public PlayerManager(JavaPlugin plugin){
        this.plugin = plugin;

        // Beim Start des Plugins Highscores aus der Konfigurationsdatei laden
        ConfigurationSection highscoresSection = plugin.getConfig().getConfigurationSection("highscores");

        // Wenn der Abschnitt highscores noch nicht existiert, lege ihn an
        if (highscoresSection == null) {
            highscoresSection = plugin.getConfig().createSection("highscores");
            plugin.saveConfig();
        }
        // Lade die Highscores aus der Konfigurationsdatei
        if (highscoresSection != null) {
            for (String uuid : highscoresSection.getKeys(false)) {
                highscores.put(UUID.fromString(uuid), highscoresSection.getInt(uuid));
            }
        }
    }

    public List<Player> getJoinedPlayers() {
        return joinedPlayers;
    }

    public HashMap<String, Integer> getBeerenstandMap() {
        return beerenstandMap;
    }

    public void incrementPlayerBeerenstand(Player player) {
        int currentBeerenstand = beerenstandMap.getOrDefault(player.getName(), 0);
        beerenstandMap.put(player.getName(), currentBeerenstand + 1);

        // Highscores aktualisieren, falls Spieler einen neuen Highscore erreicht hat
        int currentHighscore = highscores.getOrDefault(player.getUniqueId(), 0);
        if (currentBeerenstand + 1 > currentHighscore) {
            highscores.put(player.getUniqueId(), currentBeerenstand + 1);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("New highscore: " + "§c§l" + beerenstandMap.getOrDefault(player.getName(), 0) + " Berries"));
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§l" + beerenstandMap.getOrDefault(player.getName(), 0) + " Berries"));
        }
    }

    public void resetBeerenstand() {
        for (Player player : joinedPlayers) {
            beerenstandMap.put(player.getName(), 0);
        }
    }

    public void teleportAllToSpawn() {
        int x = plugin.getConfig().getInt("spawn.x");
        int y = plugin.getConfig().getInt("spawn.y");
        int z = plugin.getConfig().getInt("spawn.z");
        int pitch = plugin.getConfig().getInt("spawn.pitch");
        int yaw = plugin.getConfig().getInt("spawn.yaw");
        String worldName = plugin.getConfig().getString("spawn.world");
        World world = plugin.getServer().getWorld(worldName);
        Location location = new Location(world, x, y, z);
        location.setPitch(pitch);
        location.setYaw(yaw);
        for (Player player : joinedPlayers) {
            player.teleport(location);
        }
    }

    public void addPlayer(Player player) {
        // Nix tun, wenn Spieler schon im Spiel
        if (joinedPlayers.contains(player)) {
            return;
        }

        // STARTING wenn NOCH nix los
        if (gameManager.gameState == GameState.EMPTY) {
            gameManager.setGameState(GameState.STARTING);
        }

        joinedPlayers.add(player);
        beerenstandMap.put(player.getName(), 0);

        // tp
        int x = plugin.getConfig().getInt("spawn.x");
        int y = plugin.getConfig().getInt("spawn.y");
        int z = plugin.getConfig().getInt("spawn.z");
        int pitch = plugin.getConfig().getInt("spawn.pitch");
        int yaw = plugin.getConfig().getInt("spawn.yaw");
        String worldName = plugin.getConfig().getString("spawn.world");
        World world = plugin.getServer().getWorld(worldName);
        Location location = new Location(world, x, y, z);
        location.setPitch(pitch);
        location.setYaw(yaw);
        player.teleport(location);
    }

    public void removePlayer(Player player) {
        joinedPlayers.remove(player);
        beerenstandMap.remove(player.getName());

        // Scoreboard entfernen
        Scoreboard del_scoreboard = player.getScoreboard();
        if (del_scoreboard != null) {
            Objective objective = del_scoreboard.getObjective(DisplaySlot.SIDEBAR);
            if (objective != null) {
                objective.unregister();
            }
        }

        // tp
        int x = plugin.getConfig().getInt("lobby.x");
        int y = plugin.getConfig().getInt("lobby.y");
        int z = plugin.getConfig().getInt("lobby.z");
        int pitch = plugin.getConfig().getInt("lobby.pitch");
        int yaw = plugin.getConfig().getInt("lobby.yaw");
        String worldName = plugin.getConfig().getString("lobby.world");
        World world = plugin.getServer().getWorld(worldName);
        Location location = new Location(world, x, y, z);
        location.setPitch(pitch);
        location.setYaw(yaw);
        player.teleport(location);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (joinedPlayers.contains(event.getPlayer())) {
            if (clickedBlock.getType() == Material.SWEET_BERRY_BUSH) {
                Ageable sweetBerryBush = (Ageable) clickedBlock.getState().getBlockData();
                if (sweetBerryBush.getAge() == 2 || sweetBerryBush.getAge() == 3) {
                    event.setCancelled(true); // Aktion abbrechen
                    sweetBerryBush.setAge(1);
                    clickedBlock.setBlockData(sweetBerryBush);
                    // Beerenstand +1
                    Player player = event.getPlayer();
                    incrementPlayerBeerenstand(player);
                } else {
                    event.setCancelled(true); // Aktion abbrechen
                }
            } else {
                event.setCancelled(true); // Aktion abbrechen
            }
        }        
    }

    // EventHandler für Diebstahl 1
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player && event.getPlayer() instanceof Player) {
            if (joinedPlayers.contains((Player) event.getRightClicked()) && joinedPlayers.contains(event.getPlayer())) {
                if (event.getHand() == EquipmentSlot.HAND) {
                    if (gameManager.gameState == GameState.ACTIVE) {
                        tryStealBerry(event.getPlayer(), (Player) event.getRightClicked());
                    }
                }
            }
        }
    }

    // EventHandler für Diebstahl 2
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (joinedPlayers.contains((Player) event.getDamager()) && joinedPlayers.contains((Player) event.getEntity())) {
                if (gameManager.gameState == GameState.ACTIVE) {
                    tryStealBerry((Player) event.getDamager(), (Player) event.getEntity());
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (joinedPlayers.contains(player)) {
            joinedPlayers.remove(player);
            beerenstandMap.remove(player.getName());
        }
    }

    // EventHandler, um Hungerverlust zu verhindern
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // Prüfen, ob der betroffene Entity ein Spieler ist und ob er in joinedPlayers steht
        if (event.getEntity() instanceof Player && joinedPlayers.contains(event.getEntity())) {
            // Hungerverlust verhindern
            event.setCancelled(true);
        }
    }

    public void showScoreboardToPlayers(int duration) {
        if (joinedPlayers.isEmpty()) {
            return;
        }
        for (Player player : joinedPlayers) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = scoreboard.registerNewObjective("berryhunt", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "BerryHunt");
            obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "BerryHunt");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    
            // Spielerliste sortieren
            List<Entry<String, Integer>> sortedEntries = new ArrayList<>(beerenstandMap.entrySet());
            sortedEntries.sort(new Comparator<Entry<String, Integer>>() {
                @Override
                public int compare(Entry<String, Integer> a, Entry<String, Integer> b) {
                    return b.getValue().compareTo(a.getValue());
                }
            });
    
            int i = 1;
            for (Entry<String, Integer> entry : sortedEntries) {
                if (i > 10) {
                    break;
                }
                String playerName = entry.getKey();
                String berryCount = entry.getValue().toString();
                if (playerName.equals(player.getName())) {
                    playerName = ChatColor.RED + playerName;
                    berryCount = ChatColor.RED + berryCount;
                }
                Score score = obj.getScore(playerName + " " + berryCount);
                score.setScore(11 - i);
                i++;
            }
            
    
            obj.getScore("").setScore(0); // leere Zeile

            String durationText = ChatColor.RED.toString() + ChatColor.BOLD.toString() + duration;
            if (gameManager.gameState == GameState.STARTING) {
                obj.getScore("Start in: " + durationText).setScore(-1);
            } else if (gameManager.gameState == GameState.ACTIVE) {
                obj.getScore("End in: " + durationText).setScore(-1);
            }

            player.setScoreboard(scoreboard);
        }
    }

    public void showHighscore() {
        for (Player player : joinedPlayers) {
            int currentHighscore = highscores.getOrDefault(player.getUniqueId(), 0);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Your highscore: " + "§c§l" + currentHighscore + " Berries"));
        }
    }

    // Beim Beenden des Plugins Highscores in Konfigurationsdatei schreiben
    public void saveHighscores() {
        ConfigurationSection highscoresSection = plugin.getConfig().createSection("highscores");
        for (Map.Entry<UUID, Integer> entry : highscores.entrySet()) {
            highscoresSection.set(entry.getKey().toString(), entry.getValue());
        }
        plugin.saveConfig();
    }
    
    public void evaluation(){
        // Spielerliste sortieren
        List < Entry < String, Integer >> sortedEntries = new ArrayList < > (beerenstandMap.entrySet());
        sortedEntries.sort(new Comparator < Entry < String, Integer >> () {
            @Override
            public int compare(Entry < String, Integer > a, Entry < String, Integer > b) {
                return b.getValue().compareTo(a.getValue());
            }
        });  
        
        // Platzierungen bestimmen
        StringBuilder evaluationMessage = new StringBuilder();
        evaluationMessage.append("§8[§6BerryHunt§8]§f Placements:");

        for (int i = 0; i < sortedEntries.size(); i++) {
            Entry<String, Integer> entry = sortedEntries.get(i);
            Player player = Bukkit.getPlayerExact(entry.getKey());
            int beerenstand = entry.getValue();
            String placeTitle = "";
            switch (i) {
                case 0:
                    placeTitle = ChatColor.AQUA + "1. place";
                    evaluationMessage.append("\n").append(placeTitle + "§f: ");
                    evaluationMessage.append(ChatColor.RESET + player.getName()).append(" ").append(beerenstand);
                    spawnFirework(player.getLocation(), Color.WHITE, Color.AQUA);
                    break;
                case 1:
                    placeTitle = ChatColor.GREEN + "2. place";
                    evaluationMessage.append("\n").append(placeTitle + "§f: ");
                    evaluationMessage.append(ChatColor.RESET + player.getName()).append(" ").append(beerenstand);
                    spawnFirework(player.getLocation(), Color.WHITE, Color.GREEN);
                    break;
                case 2:
                    placeTitle = ChatColor.GOLD + "3. place";
                    evaluationMessage.append("\n").append(placeTitle + "§f: ");
                    evaluationMessage.append(ChatColor.RESET + player.getName()).append(" ").append(beerenstand);
                    spawnFirework(player.getLocation(), Color.WHITE, Color.YELLOW);
                    break;
                default:
                    placeTitle = ChatColor.WHITE + "" + (i + 1) + ". place";
                    evaluationMessage.append("\n").append(placeTitle + "§f: ");
                    evaluationMessage.append(ChatColor.RESET + player.getName()).append(" ").append(beerenstand);
                    break;
            }

            player.sendTitle(placeTitle, "Your berry count: " + beerenstand, 10, 100, 20);
        }

        // Auswertung im Chat
        for (Player player : joinedPlayers) {
            player.sendMessage(evaluationMessage.toString());
        }
        Logger.getLogger("berryhunt").info(evaluationMessage.toString().replaceAll("\n", " ").replace("§8[§6BerryHunt§8]§f ", "").replaceAll("(?i)§[0-9A-FK-OR]", ""));
    }

    private void spawnFirework(Location location, Color primaryColor, Color secondaryColor) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder().withColor(primaryColor, secondaryColor).withTrail().withFlicker().build());
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
    }

    public void tryStealBerry(Player thief, Player victim) {
        // Check if the players are in the same game
        if (!joinedPlayers.contains(thief) || !joinedPlayers.contains(victim)) {
            return;
        }

        // Check game status
        if (gameManager.gameState != GameState.ACTIVE) {
            return;
        }
    
        // Check if the victim has any berries to steal
        int victimBeerenstand = beerenstandMap.getOrDefault(victim.getName(), 0);
        if (victimBeerenstand <= 0) {
            return;
        }
    
        // Steal a berry from the victim
        beerenstandMap.put(victim.getName(), victimBeerenstand - 1);
        incrementPlayerBeerenstand(thief);
    
        // Notify the players in the chat
        thief.sendMessage("§8[§6BerryHunt§8]§a You stole " + victim.getName() + " a berry!");
        victim.sendMessage("§8[§6BerryHunt§8]§c " + thief.getName() + " stole a berry from you!");
    }    

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
