package at.ac.foop.pacman.domain;

import java.io.Serializable;

public class Player implements Serializable {
	//Fields
	public static final int PLAYER_COUNT = 3;

	Long id; //Uniquely identified the player
	String name; //The name of the player (for statistics)
	private Long points; //current number of points of the player
	private Pacman pacman; //the pacman that represents the player on the board. If null then player has been eaten;
	//Labyrinth map; //the current map in this round 

	//Constructors
	public Player() {
	}
	
	public Player(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public void sendBounty(Player player) {
		this.pacman.setAlive(false);
		player.setPoints(this.points + player.getPoints());
		points = 0L;
	}
	
	public void initPacman(Square square) {
		this.pacman = new Pacman();
		this.pacman.setColor(PacmanColor.values()[id.intValue() - 1]);
		this.pacman.setLocation(square);
	}

	public Pacman getPacman() {
		return pacman;
	}

	public void setPacman(Pacman pacman) {
		this.pacman = pacman;
	}

	//Getters and Setters
	public void addPoints(int points) {
		this.points += new Long(points);
	}

	public Square getLocation() {
		return pacman.getLocation();
	}

	public void setDirection(Direction direction) {
		pacman.setDirection(direction);
	}

	public void changeColor() {
		pacman.changeColor();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
