package de.erik_kuechler.berryhunt;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import java.util.List;

public class GameStartCountdownTask extends BukkitRunnable {

    private GameManager gameManager;
    private List<Player> joinedPlayers;
    private PlayerManager playerManager;
    private int timeLeft;

    /**
     * Initializes a new instance of the GameStartCountdownTask class.
     * 
     * @param gameManager   The GameManager instance.
     * @param playerManager The PlayerManager instance.
     * @param timeLeft      The initial time left for the countdown.
     */
    public GameStartCountdownTask(GameManager gameManager, PlayerManager playerManager, int timeLeft) {
        this.gameManager = gameManager;
        this.playerManager = playerManager;
        this.joinedPlayers = playerManager.getJoinedPlayers();
        this.timeLeft = timeLeft;
    }

    /**
     * Executes the countdown task, which counts down to the start of the game.
     * Displays countdown titles, sounds, and chat messages based on the remaining time.
     */
    @Override
    public void run() {
        timeLeft--;
        playerManager.showScoreboardToPlayers(timeLeft);
        playerManager.showHighscore();
        
        if (timeLeft == 0) {
            // Show "Start!" title for 1 second
            for (Player player : joinedPlayers) {
                player.sendTitle("Start!", "", 10, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
            }
            cancel();

            if (joinedPlayers.isEmpty()) {
                gameManager.setGameState(GameState.EMPTY);
            } else {
                gameManager.setGameState(GameState.ACTIVE);
            }

            return;
        }

        // Show titles for 3, 2, 1
        if (timeLeft <= 3) {
            for (Player player : joinedPlayers) {
                player.sendTitle(String.valueOf(timeLeft), "", 10, 20, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }
        // Show countdown in chat for 10s intervals
        else if (timeLeft % 10 == 0) {
            for (Player player : joinedPlayers) {
                player.sendMessage("§8[§6BerryHunt§8]§f The game starts in " + timeLeft + " seconds!");
            }
        }
    }
}
