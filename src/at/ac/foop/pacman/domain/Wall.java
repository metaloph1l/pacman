package at.ac.foop.pacman.domain;

/**
 *
 * @author Phil
 */
final public class Wall extends Square {

	@Override
	public void enter(Player player) {
		throw new RuntimeException("A Player cannot walk into a wall.");
	}

	@Override
	public void leave(Player player) {
		throw new RuntimeException("A Player cannot walk into a wall, and thus is not able to leave it.");
	}

	@Override
	public void reset() {
		//Do nothing
	}

	@Override
	public SquareType getType() {
		return SquareType.WALL;
	}

	@Override
	public Integer getPoints() {
		return 0;
	}
}
