/*                                     
 * Author : KHALED KADRI   

 * LinkedIn : https://www.linkedin.com/in/khaled-kadri/
 * License: Creative Commons - Attribution-NonCommercial (CC BY-NC)
 * Copyright © 2013 KHALED KADRI. All rights reserved.
 *
 * This work is licensed under the Creative Commons - Attribution-NonCommercial License.
 * You may not use this work for commercial purposes. 
 * You are free to share and adapt the material as long as proper attribution is given.
 * Attribution must include the original author: "KHALED KADRI".
 */

/*
 * The Graphic class is a custom Swing component (JPanel) responsible for rendering and managing 2D graphics in a Java-based game environment.
It handles the real-time drawing of game elements such as the player’s shape (snake or object), obstacles, items (e.g., apples or bonuses), and background scenes.
This class provides the core visual and animation logic for the gameplay.

Boite() — Closed box arena
Tunnel() — Dual parallel walls
Passage() — Central corridor opening
Moulin() — Rotating cross-shaped barrier layout
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Graphic extends JPanel{
	
	/**
	 * 
	 */
	// Serialization ID for the Swing component
	private static final long serialVersionUID = 1L;

	// Current position of the snake head (X, Y coordinates)
	private int posX;
	private int posY;

	// Dimensions of a single game block or segment (20x20 pixels)
	private int volX = 20;
	private int volY = 20;

	// Center coordinates used for drawing and obstacle placement
	private int centerX, centerY;

	// Transparency level for certain visual effects
	private float opaque = 100;

	// Indicates whether the game has started
	private boolean begin = false;

	// Translation offsets used for coordinate shifting or scrolling
	int ytranslate = 0;
	int xtranslate = 0;

	// Game board dimensions (height and width in pixels)
	int heigh, width;

	// Initial number of snake segments at game start
	private int point_ini = 5;

	// List of points representing the snake's body (head to tail)
	protected ArrayList<Point> forme = new ArrayList<Point>();

	// List of static obstacles currently displayed in the game
	protected ArrayList<Point> obstacle = new ArrayList<Point>();

	// List of obstacles specific to certain game modes (e.g., windmill mode)
	protected ArrayList<Point> obstaclem = new ArrayList<Point>();

	// Current apple or bonus point displayed on the map
	protected Point point;

	// Predefined array of colors for rendering body segments
	private Color color[] = {
	    Color.black, Color.blue, Color.cyan, Color.DARK_GRAY, Color.GRAY,
	    Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.orange,
	    Color.pink, Color.RED, Color.YELLOW
	};

	// Movement direction flags (true = currently moving in that direction)
	boolean gauche, droite, bas, haut;

	// Previous X and Y positions (used for smooth movement and transitions)
	int xpos_anc, ypos_anc;

	// Current game mode (Classic, Box, Tunnel, Passage, Windmill)
	int type_jeu;

	// Bonus state variables:
	// bonus = true if a bonus apple is active,
	// pomme_max / pomme_min control pulsating apple animation size
	boolean bonus, pomme_max, pomme_min;

	// Numeric representation of the current direction
	// 1 = left, 2 = right, 3 = down, 4 = up
	int direction;

	// Current width and height of the apple sprite (for animation)
	int w_pomme = 20, h_pomme = 20;

	// General-purpose counter (used for timing or frame-based events)
	int ct;

	// Indicates whether the grid is displayed on the board
	boolean grille;

	// Flag for dark mode (true = black background theme)
	protected boolean ecran_noir;

	// Stores the previous game mode (for state change detection)
	int type_jeu_anc;

	// True for the first frame after a mode or game reset
	boolean first;

	// Timestamp for measuring elapsed time or animations
	long t1 = 0;

	// Indicates whether the first movement of the snake has occurred
	boolean firstMove;

	
	Graphic(int x , int y){
		init(x,y,type_jeu);
	}
	
	protected void init(int x , int y,int type_jeu){
		this.type_jeu=type_jeu;
		begin = false;
		forme.clear();
		obstacle.clear();
		point=null;
		this.posX = 300;
		this.posY = 360;
		forme.add(new Point(this.posX, this.posY));
		for(int i = 1 ; i < point_ini; i++)
			forme.add(new Point(forme.get(i-1).getposx()+20, this.posY));
		xpos_anc=forme.get(point_ini-2).getposx();
		ypos_anc=this.posY;
		heigh=500;
		width=700;
		ytranslate=0;
		xtranslate=0;
		gauche=false;droite=false;bas=false;haut=false;
		bonus=false;
		direction=0;
		ct=1;
		first=true;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int i;
		if(ecran_noir)
			g.setColor(Color.BLACK);
		else
			g.setColor(new Color(183,206,179));
		g.fillRect(0, 0, width, heigh);
		if(grille){
			g.setColor(new Color(18,89,11,50));
			int ver = 0;
			int hor = 0;
			for(int i1 = 0 ; i1<width/20;i1++)
				for(int j = 0 ; j < heigh/20 ; j++){
					g.drawLine(ver, 0, ver, heigh);
					g.drawLine(0, hor, width, hor);
					ver +=20;
					hor +=20;
				}
		}
		BufferedImage img = null;
		try {
			if(!ecran_noir)
				img = ImageIO.read(this.getClass().getResource("fichier/tete2.jpg"));
			else
				img = ImageIO.read(this.getClass().getResource("fichier/tete_noir.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.setColor(new Color(18,89,11));
		if(haut){
			AffineTransform identity = new AffineTransform();
			Graphics2D g2d = (Graphics2D)g;
			AffineTransform trans = new AffineTransform();
			identity.translate(posX+20,posY);
			trans.setTransform(identity);
			trans.rotate( Math.toRadians(90) );
			g2d.drawImage(img, trans, this);
			haut=false;
			
		}
		else if(bas){
			AffineTransform identity = new AffineTransform();
			Graphics2D g2d = (Graphics2D)g;
			AffineTransform trans = new AffineTransform();
			identity.translate(posX,posY+20);
			trans.setTransform(identity);
			trans.rotate( Math.toRadians(-90) );
			g2d.drawImage(img, trans, this);
			bas=false;
		}
		else if(droite){
			AffineTransform identity = new AffineTransform();
			Graphics2D g2d = (Graphics2D)g;
			AffineTransform trans = new AffineTransform();
			identity.translate(posX+20,posY+20);
			trans.setTransform(identity);
			trans.rotate( Math.toRadians(180) );
			g2d.drawImage(img, trans, this);
			droite=false;
		}
		else if(gauche){
			AffineTransform identity = new AffineTransform();
			Graphics2D g2d = (Graphics2D)g;
			AffineTransform trans = new AffineTransform();
			identity.translate(posX+20,posY+20);
			trans.setTransform(identity);
			trans.rotate( Math.toRadians(180) );
			g2d.drawImage(img, trans, this);
			droite=false;
		}
		else{
			g.drawImage(img,posX,posY,null);
		}
		
		if(opaque == 20)
			opaque = 100;
		
		/******************************/
		switch(type_jeu){
			case 1:if(type_jeu_anc!=1){first=true;type_jeu_anc=type_jeu;}Boite(g);break;
			case 2:if(type_jeu_anc!=2){first=true;type_jeu_anc=type_jeu;}tunnel(g);break;
			case 3:if(type_jeu_anc!=3){first=true;type_jeu_anc=type_jeu;}Passage(g);break;
			case 4:if(type_jeu_anc!=4){first=true;type_jeu_anc=type_jeu;}Moulin(g);break;
		}
		//Boite(g);
		//tunnel(g);
		//Passage(g);
		//Moulin(g);
		/******************************/
		try {
			if(!ecran_noir)
				img = ImageIO.read(this.getClass().getResource("fichier/forme.jpg"));
			else
				img = ImageIO.read(this.getClass().getResource("fichier/forme_noir.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(i = 1 ; i < forme.size()-1; i++){
			
			g.drawImage(img,forme.get(i).getposx(), forme.get(i).getposy() ,null);
			//g.setColor(color[i%color.length]);
			opaque -=10;
		}
		
		try {
			if(!ecran_noir)
				img = ImageIO.read(this.getClass().getResource("fichier/forme.jpg"));
			else
				img = ImageIO.read(this.getClass().getResource("fichier/forme_noir.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int xpos = forme.get(i-1).getposx();
		int ypos = forme.get(i-1).getposy();

		g.drawImage(img,forme.get(i).getposx(), forme.get(i).getposy() ,null);
		xpos_anc=xpos;
		ypos_anc=ypos;
		
		if(!bonus){
			try {
				img = ImageIO.read(this.getClass().getResource("fichier/pomme3.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(point!=null){
				g.drawImage(img,point.getposx(), point.getposy() ,null);
			}
		}
		else{
			
			dynamiser_pomme(g);
		}
	}

	private void dynamiser_pomme(Graphics g) {
		// TODO Auto-generated method stub
		Image img = null;
		if(!pomme_min){
			w_pomme +=1;
			h_pomme +=1;
			if(w_pomme==30 && h_pomme==30)
				pomme_min=true;
			try {
				img = ImageIO.read(this.getClass().getResource("fichier/pomme30px.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(point!=null){
				g.drawImage(img,point.getposx(), point.getposy(),w_pomme,h_pomme, null);
			}
		}
		else{
			w_pomme -=1;
			h_pomme -=1;
			if(w_pomme==20)
				pomme_min=false;
			try {
				img = ImageIO.read(this.getClass().getResource("fichier/pomme30px.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(point!=null){
				g.drawImage(img,point.getposx(), point.getposy(),w_pomme,h_pomme, null);
			}
		}
	}
	
	
	/***********************************************************************************************************************/
	/***********************************************************************************************************************/
	
	
	private void Moulin(Graphics g) {
		// TODO Auto-generated method stub
		//obstacle.clear();
		Image img = null;
		try {
			img = ImageIO.read(this.getClass().getResource("fichier/boite.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int atteidre_centreV=0;
		int atteidre_centreH=heigh/4-25;
		if(first){
			obstacle.clear();
		while(atteidre_centreV<width-20){
			if(atteidre_centreV<width-20){
				if(atteidre_centreV<(width-20)/2){
				g.drawImage(img, atteidre_centreV, atteidre_centreH, null);
				if(first)obstaclem.add(new Point(atteidre_centreV,atteidre_centreH));}
				if(atteidre_centreV>=(width)/2-20){
				g.drawImage(img, atteidre_centreV, atteidre_centreH*3+20, null);
				if(first)obstaclem.add(new Point(atteidre_centreV,atteidre_centreH*3+20));}
			}
			atteidre_centreV=atteidre_centreV+20;
		}
		atteidre_centreV=width/4+5;
		atteidre_centreH=0;
		while(atteidre_centreH<heigh-60){
			if(atteidre_centreH<heigh){
				if(atteidre_centreH<(heigh-60)/2){
				g.drawImage(img, atteidre_centreV*3-40,atteidre_centreH, null);
				if(first)obstaclem.add(new Point(atteidre_centreV*3-40,atteidre_centreH));}
				if(atteidre_centreH>(heigh/2)-40){
				g.drawImage(img, atteidre_centreV-20, atteidre_centreH, null);
				if(first)obstaclem.add(new Point(atteidre_centreV-20,atteidre_centreH));}
			}
			atteidre_centreH=atteidre_centreH+20;
		}
		obstacle.addAll(obstaclem);
		}
		else{
			//obstacle.addAll(obstaclem);
			for(int i = 0 ; i < obstaclem.size(); i++){
				g.drawImage(img, obstaclem.get(i).getposx(), obstaclem.get(i).getposy(),null);
			}
		}
		first=false;
	}

	private void Boite(Graphics g) {
		// TODO Auto-generated method stub
		Image img = null;
		int i;
		i =0;
		try {
			img = ImageIO.read(this.getClass().getResource("fichier/boite.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int atteidre_centreV=20;
		int atteidre_centreH=0;
		obstacle.clear();
		while(atteidre_centreV<width-40){
			g.drawImage(img, atteidre_centreV, 0, null);obstacle.add(new Point(atteidre_centreV,0));
			g.drawImage(img, atteidre_centreV, heigh-80, null);obstacle.add(new Point(atteidre_centreV,heigh-80));
			atteidre_centreV=atteidre_centreV+20;
		}
		while(atteidre_centreH<heigh-60){
			g.drawImage(img, 0,atteidre_centreH, null);obstacle.add(new Point(0,atteidre_centreH));
			g.drawImage(img, width-40,atteidre_centreH, null);obstacle.add(new Point(width-40,atteidre_centreH));
			atteidre_centreH=atteidre_centreH+20;
		}
	}
	private void Passage(Graphics g) {
		// TODO Auto-generated method stub
		centerX=width/2-110;
		centerY=heigh/2;
		Image img = null;
		int i;
		i =0;
		try {
			img = ImageIO.read(this.getClass().getResource("fichier/boite.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		obstacle.clear();
		int atteidre_centreV=0;
		int atteidre_centreH=0;
		while(atteidre_centreV<width-20){
			if(atteidre_centreV<width/2-50 || atteidre_centreV>width/2+10){
				g.drawImage(img, atteidre_centreV, 0, null);obstacle.add(new Point(atteidre_centreV,0));
				g.drawImage(img, atteidre_centreV, heigh-80, null);obstacle.add(new Point(atteidre_centreV,heigh-80));
			}
			atteidre_centreV=atteidre_centreV+20;
		}
		while(atteidre_centreH<heigh-60){
			if(atteidre_centreH<heigh/2-70 || atteidre_centreH>heigh/2-10){
				g.drawImage(img, 0,atteidre_centreH, null);obstacle.add(new Point(0,atteidre_centreH));
				g.drawImage(img, width-40,atteidre_centreH, null);obstacle.add(new Point(width-40,atteidre_centreH));
			}
			atteidre_centreH=atteidre_centreH+20;
		}
	}

	private void tunnel(Graphics g) {
		// TODO Auto-generated method stub
		centerX=width/2-110;
		centerY=heigh/4+15;
		int centx=width/2-110;
		int centy=(heigh/4)*2+10;
		Image img = null;
		int i;
		obstacle.clear();
		
			try {
				img = ImageIO.read(this.getClass().getResource("fichier/boite.jpg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(i = 1 ; i < 10; i++){
				g.drawImage(img,centerX,centerY,null);obstacle.add(new Point(centerX,centerY));
				centerX=centerX+20;
				g.drawImage(img,centx,centy,null);obstacle.add(new Point(centx,centy));
				centx=centx+20;
				opaque -=10;
			}
			
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			/*BufferedImage bi = new
		    BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);*/
			g.drawImage(img, 0, 0, null);obstacle.add(new Point(0, 0));
			g.drawImage(img, 0, 20, null);obstacle.add(new Point(0, 20));
			g.drawImage(img, 0, 40, null);obstacle.add(new Point(0, 40));
			g.drawImage(img, 20, 0, null);obstacle.add(new Point(20, 0));
			g.drawImage(img, 40, 0, null);obstacle.add(new Point(40, 0));
			
			g.drawImage(img, width-80,0, null);obstacle.add(new Point(width-80, 0));
			g.drawImage(img, width-60, 0, null);obstacle.add(new Point(width-60, 0));
			g.drawImage(img, width-40, 0, null);obstacle.add(new Point(width-40, 0));
			g.drawImage(img, width-40, 20, null);obstacle.add(new Point(width-40, 20));
			g.drawImage(img, width-40, 40, null);obstacle.add(new Point(width-40, 40));
			
			g.drawImage(img, width-80,heigh-80, null);obstacle.add(new Point(width-80,heigh-80));
			g.drawImage(img, width-60,heigh-80, null);obstacle.add(new Point(width-60,heigh-80));
			g.drawImage(img, width-40,heigh-80, null);obstacle.add(new Point(width-40,heigh-80));
			g.drawImage(img, width-40,heigh-100, null);obstacle.add(new Point(width-40,heigh-100));
			g.drawImage(img, width-40,heigh-120, null);obstacle.add(new Point(width-40,heigh-120));
			
			g.drawImage(img, 0, heigh-120, null);obstacle.add(new Point(0, heigh-120));
			g.drawImage(img, 0, heigh-100, null);obstacle.add(new Point(0, heigh-100));
			g.drawImage(img, 0, heigh-80, null);obstacle.add(new Point(0, heigh-80));
			g.drawImage(img, 20, heigh-80, null);obstacle.add(new Point(20, heigh-80));
			g.drawImage(img, 40, heigh-80, null);obstacle.add(new Point(40, heigh-80));
		
	}
	
	
	/***********************************************************************************************************************/
	/***********************************************************************************************************************/
	
	
	void filldraw(Graphics g,int x,int y){
		g.setColor(Color.gray);
		g.fillRect(x, y , volX, volY);
		g.setColor(Color.black);
		g.drawRect(x, y , volX, volY);
	}
	
	protected void decaler(){
		for(int i = forme.size()-1 ; i > 0; i--){
			forme.get(i).setposition(forme.get(i-1).getposx(),forme.get(i-1).getposy());
		}
	}
	
	protected void decaler2(){
		switch (direction){
		case 1:forme.get(forme.size()-1).setposition(forme.get(forme.size()-1).getposx()-20,forme.get(forme.size()-1).getposy());break;
		case 2:forme.get(forme.size()-1).setposition(forme.get(forme.size()-1).getposx()+20,forme.get(forme.size()-1).getposy());break;
		case 3:forme.get(forme.size()-1).setposition(forme.get(forme.size()-1).getposx(),forme.get(forme.size()-1).getposy()+20);break;
		case 4:forme.get(forme.size()-1).setposition(forme.get(forme.size()-1).getposx(),forme.get(forme.size()-1).getposy()-20);break;
		}
	}
	
	void setbegin(boolean b){
		this.begin=b;
	}
	
	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
	

}

