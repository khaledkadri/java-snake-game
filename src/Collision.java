/*
 * Class: Collision
 * Description:
 * Handles game-over events and player collisions within the 2D game environment.
 * When a collision occurs, it displays a confirmation dialog asking whether
 * the player wants to start a new game or exit the application.
 * If the user chooses to restart, the game state and graphics panel are reinitialized.
 *
 * Dependencies:
 * - Graphic: The game panel responsible for rendering and updating visuals.
 * - Fenetre: The main game window managing the game loop and controls.
 *
 * Author: Khaled Kadri
 * License: Creative Commons - Attribution-NonCommercial (CC BY-NC)
 */



import javax.swing.JOptionPane;


public class Collision extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int confirm;
	public Collision(Graphic panel,Fenetre f){
		confirm=JOptionPane.showConfirmDialog(panel,
				"Do you want to start a new game?",
				"You lost!",
	             JOptionPane.YES_NO_OPTION);
		if(confirm==JOptionPane.YES_OPTION){
			f.init();
			panel.init(200, 200,panel.type_jeu);
			f.go();
		}
		else if(confirm==JOptionPane.NO_OPTION)
			System.exit(-1);
				
	}

}
