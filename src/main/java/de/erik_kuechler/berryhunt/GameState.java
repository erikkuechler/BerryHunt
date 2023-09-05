package de.erik_kuechler.berryhunt;

/**
 * Represents the different states of the BerryHunt game.
 * - EMPTY: The game is empty, with no players.
 * - STARTING: The game is in the starting countdown phase.
 * - ACTIVE: The game is actively being played.
 * - WON: The game has ended, and the winners are being determined.
 */
public enum GameState {
    EMPTY, STARTING, ACTIVE, WON;
}
