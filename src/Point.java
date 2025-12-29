/*
 * Class: Point
 * Description:
 * Represents a 2D coordinate (x, y) used for positioning and movement
 * of graphical elements in the game (such as player segments, obstacles, or items).
 * Provides basic getter and setter methods to manipulate positions dynamically.
 *
 * Author: Khaled Kadri
 * License: Creative Commons - Attribution-NonCommercial (CC BY-NC)
 */


public class Point {
	private int posx;
	private int posy;
	
	public Point(int x, int y){
		this.posx = x;
		this.posy = y;
	}
	
	void setposition(int x , int y){
		this.posx = x;
		this.posy = y;
	}
	int getposx(){
		return posx;
	}
	
	int getposy(){
		return posy;
	}
	
	void setposx(int x){
		this.posx = x;
	}
	
	void setposy(int y){
		this.posy = y;
	}
}
