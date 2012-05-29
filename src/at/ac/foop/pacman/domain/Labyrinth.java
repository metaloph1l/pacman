package at.ac.foop.pacman.domain;

import java.io.Serializable;

/**
 *
 * @author Phil
 * Sebastian Geiger
 * Stefan
 */
public class Labyrinth implements Serializable {
	//Fields

	int width;
	int height;
	Square[][] squares; //the game field
	
	//Constructors
	public Labyrinth(Labyrinth lab) {
		this.squares = lab.getSquares();
		this.width = lab.width;
		this.height = lab.height;
	}

	//Constructors
	public Labyrinth(int width, int height) {
		this.squares = new Square[height][width];
		this.width = width;
		this.height = height;
	}

	public Labyrinth(Square[][] squares) {
		this.squares = squares;
		this.width = squares[0].length;
		this.height = squares.length;
	}

	/**
	 * returns Width of Labyrinth
	 */
	public Integer getWidth(){return this.width;}
	
	/**
	 * returns Height of Labyrinth
	 */
	public Integer getHeight(){return this.height;}

	public void initializeBoard() {
		this.squares = LabyrinthGenerator.generateSquares();
	}

	/**
	 * This is used only when initialising the game. During initialisation
	 * the game server sends the initial player positions as coordinates.
	 */
	public Square getSquare(int x, int y) {
		if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
			throw new IllegalArgumentException("Array index out of bounds.");
		}

		return squares[y][x];
	}
	
	public Square getSquare(Coordinate coord) {
		if (coord.getX() < 0 || coord.getY() < 0 || coord.getX() > width - 1 || coord.getY() > height - 1) {
			throw new IllegalArgumentException("Array index out of bounds.");
		}

		return squares[coord.getY()][coord.getX()];
	}
	
	public Square[][] getSquares() {
		return this.squares;
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
				tmp = squares[coord.getY()][coord.getX()];
				break;
		}

		return tmp;
	}
}
