package at.ac.foop.pacman.application.client;

/**
 * Possible state changes of the game logic. The game state is enqued in a list.
 * An observer pattern is used to communicate the state changes to the UI which
 * then updates the Graphics.
 *
 * @author Sebastian Geiger
 */
public enum GameState {
	NEW_MAP,
	NEW_POSITION,
	NEW_POSITIONS,
	NEW_COLOR,
	NEW_PLAYER,
	PLAYER_READY,
	NEW_TURN,
	END_OF_ROUND,
	GAME_OVER,
	GAME_START
}
