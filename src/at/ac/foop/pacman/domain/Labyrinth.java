package at.ac.foop.pacman.domain;

/**
*
* @author Phil
* Sebastian Geiger
*/
public class Labyrinth {
	//Fields
	int width;
	int height;
	Square[][] squares; //the game field
	
	//Constructors
	public Labyrinth(int width, int height) {
		this.squares = new Square[width][height];
		this.width = width;
		this.height = height;
	}
	
	public void initializeBoard() {
		//create all the square items
	}
	
	public Square getSquare(Square square, Direction direction) {
		return null;
	}
}
