package at.ac.foop.pacman.domain;

import java.io.Serializable;

/**
 *
 * @author Phil
 * Sebastian Geiger
 */
public class Labyrinth implements Serializable {
	//Fields

	int width;
	int height;
	Square[][] squares; //the game field

	//Constructors
	public Labyrinth(int width, int height) {
		this.squares = new Square[height][width];
		this.width = width;
		this.height = height;
	}

	public Labyrinth(Square[][] squares) {
		this.squares = squares;
		this.width = squares.length;
		this.height = squares[0].length;
	}

	//TODO needs refactoring -> LabyrinthGenerator should generate the maze and its squares.
	public void initializeBoard() {
		this.squares = LabyrinthGenerator.generateSquares();
	}

	public Square getSquare(int x, int y) {
		if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
			throw new IllegalArgumentException("Array index out of bounds.");
		}

		return squares[y][x];
	}

	public Square getSquare(Square square, Direction direction) {
		Coordinate coord = square.getCoordinate();
		Square tmp = null;

		switch (direction) {
			case UP:
				tmp = squares[(coord.getY() - 1) % height][coord.getX()];
				break;
			case DOWN:
				tmp = squares[(coord.getY() + 1) % height][coord.getX()];
				break;
			case LEFT:
				tmp = squares[coord.getY()][(coord.getX() - 1) % width];
				break;
			case RIGHT:
				tmp = squares[coord.getY()][(coord.getX() + 1) % width];
				break;
			case NONE:
				tmp = square;
				break;
		}

		return tmp;
	}
}
