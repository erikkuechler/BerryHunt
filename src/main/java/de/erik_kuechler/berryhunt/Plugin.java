package de.erik_kuechler.berryhunt;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * berryhunt java plugin
 */
public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("berryhunt");
  private GameManager gameManager;
  private PlayerManager playerManager;
  public void onEnable()
  {
    LOGGER.info("Good morning!");
    saveDefaultConfig();

    playerManager = new PlayerManager(this);
    gameManager = new GameManager(this);
    gameManager.setPlayerManager(playerManager);
    playerManager.setGameManager(gameManager);

    getServer().getPluginManager().registerEvents(playerManager, this);
    getCommand("berryhunt").setExecutor(new Commands(this, gameManager, playerManager));
  }

  public void onDisable()
  {
    LOGGER.info("Good evening!");
    playerManager.saveHighscores();
  }

  public PlayerManager getPlayerManager() {
      return playerManager;
  }
}
