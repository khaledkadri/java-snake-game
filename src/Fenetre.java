/*                                     
 * Author : KHALED KADRI   

 * LinkedIn : https://www.linkedin.com/in/khaled-kadri/
 * License: Creative Commons - Attribution-NonCommercial (CC BY-NC)
 * Copyright © 2013 KHALED KADRI. All rights reserved.
 *
 * This work is licensed under the Creative Commons - Attribution-NonCommercial License.
 * You may not use this work for commercial purposes. 
 * You are free to share and adapt the material as long as proper attribution is given.
 * Attribution must include the original author: "KHALED KADRI". https://github.com/khaledkadri
 */

/*
 * Class: Fenetre
 * Description:
 * Main window and core controller of the Snake game.
 * This class manages the game loop, event handling, user interface, and collision logic.
 * It coordinates gameplay between the graphical panel (Graphic), the player controls, and the UI components.
 *
 * Key Features:
 * - Initializes and controls the game window, menus, and interface elements.
 * - Manages the main game thread, timing, and frame updates.
 * - Handles player input via keyboard events (arrow keys) for snake movement.
 * - Detects collisions with objects, boundaries, and obstacles, triggering game over events.
 * - Integrates multiple game levels/modes (Classic, Box, Tunnel, Transit, Mill) with adjustable speed.
 * - Displays real-time score, progress bar, and high-score tracking.
 * - Includes help, about, and settings menus with LinkedIn/GitHub integration.
 * - Custom progress bar rendering (GradientPalletProgressBarUI) for a dynamic visual indicator.
 *
 * Dependencies:
 * - Graphic: Handles 2D rendering and visual updates.
 * - Collision: Manages game-over behavior and restart options.
 * - Point: Represents coordinate positions for snake segments and objects.
 *
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicProgressBarUI;


public class Fenetre extends JFrame implements KeyListener{

	// Serialization ID (required for Serializable classes like JFrame)
	private static final long serialVersionUID = 1L;

	// Initial snake head position (X, Y coordinates in pixels)
	private int posX = 300;
	private int posY = 360;

	// Coordinates of the current apple or bonus object
	private int xObj, yObj;

	// Direction indicator for snake movement:
	// 0 = none, 1 = left, 2 = right, 3 = up, 4 = down
	private int fleche = 0;

	// Current and normal snake speed (in milliseconds per frame)
	private int vitesse = 120;
	private int vitesse_n = 120;

	// Player’s current score
	private int _score;

	// Direction flags (true = movement in that direction active)
	private boolean vg, vd, vh, vb; // gauche, droite, haut, bas

	// Indicates whether the game has started
	private boolean begin;

	// Random generator for object (apple) positions
	Random random = new Random();

	// Main game panel responsible for graphics and rendering
	private Graphic pan = new Graphic(posX, posY);

	// Main container panel for layout and UI integration
	private JPanel pan2 = new JPanel(new BorderLayout());

	// Label used to display the current score in the UI
	private JLabel score = new JLabel();

	// Counter for various timing and state controls (e.g., apple spawn logic)
	int ct = 0;

	// Current game mode type (0=Classic, 1=Box, 2=Tunnel, 3=Transit, 4=Mill)
	int type_jeu = 0;

	// Collision flag — true when collision detection is active
	boolean collision;

	// Thread controlling the main game loop
	private Thread t;

	// Progress bar showing time until bonus or apple disappears
	JProgressBar bar;

	// Timestamp of the last apple spawn (used for bonus timing)
	long tps_pomme_d;

	// Indicates if the temporary slow-down mode is active
	boolean vitesse_n_b = false;

	// Array storing the best scores for each game mode
	String[] scores = new String[5];

	
	int wframe = 695, hframe = 503;	
	
	protected void init(){
		vg=false;
		vd=false;
		vh=false;
		vb=false;
		begin = false;
		posX = 300;
		posY = 360;
		yObj = 0;
		xObj = 0;
		fleche=0;
		_score=0;
		score.setText("  Score : 0");
		ct=1;
		tps_pomme_d=System.currentTimeMillis();
		bar.setValue(0);
		xObj=0;
		yObj=0;
		t=new Thread();
		vitesse_n_b=false;
		collision=true;
	}
	
	public Fenetre(){
		this.setTitle("Snake");
		this.setSize(wframe,hframe);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(pan2);
		this.setVisible(true);
		this.addKeyListener(this);
		this.setResizable(false);
		
		BufferedImage img = null;
		try {
			img = ImageIO.read(this.getClass().getResource("fichier/icone.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Got an IOException: " + e1.getMessage());
		}
		
		this.setIconImage(img);
		JPanel p = new JPanel(new GridLayout(1,3));
		score.setBackground(Color.BLACK);
		score.setPreferredSize(new Dimension(50,20));
		score.setBackground(new Color(18,89,11));
		score.setFont(new Font("Courier New", Font.BOLD, 15));
		score.setForeground(new Color(18,89,11));
		
		pan2.add(p, BorderLayout.NORTH);
		pan2.add(pan);
		JMenuBar jmb = new JMenuBar();
		JMenu jmenu = new JMenu("Options");jmenu.setForeground(new Color(18,89,11));
		JMenu jmenu2 = new JMenu("Help");jmenu2.setForeground(new Color(18,89,11));
		JMenuItem nouveau = new JMenuItem("New Game");nouveau.setForeground(new Color(18,89,11));
		JMenuItem niveau = new JMenuItem("Level");niveau.setForeground(new Color(18,89,11));
		JMenuItem aide = new JMenuItem("Help");aide.setForeground(new Color(18,89,11));
		JMenuItem apropos = new JMenuItem("About");apropos.setForeground(new Color(18,89,11));
		JMenuItem ms = new JMenuItem("High Score");ms.setForeground(new Color(18,89,11));
		
		nouveau.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				init();
				pan.init(posX,posY,type_jeu);
			}
		});
		
		final JCheckBoxMenuItem jmi2 = new JCheckBoxMenuItem("Black Screen");jmi2.setForeground(new Color(18,89,11));
		jmi2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(!jmi2.isSelected())
					pan.ecran_noir=false;
				else
					pan.ecran_noir=true;
			}
		});
		final JCheckBoxMenuItem grille = new JCheckBoxMenuItem("Grid",new ImageIcon(this.getClass().getResource("fichier/grille.jpg")));
		grille.setForeground(new Color(18,89,11));
		jmb.setFont(new Font("Courier New", Font.PLAIN, 15));
		jmenu.setFont(new Font("Courier New", Font.PLAIN, 15));
		nouveau.setFont(new Font("Courier New", Font.PLAIN, 15));
		niveau.setFont(new Font("Courier New", Font.PLAIN, 15));
		jmi2.setFont(new Font("Courier New", Font.PLAIN, 15));
		grille.setFont(new Font("Courier New", Font.PLAIN, 15));
		jmenu2.setFont(new Font("Courier New", Font.PLAIN, 15));
		aide.setFont(new Font("Courier New", Font.PLAIN, 15));
		apropos.setFont(new Font("Courier New", Font.PLAIN, 15));
		ms.setFont(new Font("Courier New", Font.PLAIN, 15));
		
		//jmenu.add(nouveau);
		jmenu.add(niveau);
		jmenu.add(jmi2);
		jmenu.add(grille);
		jmenu.addSeparator();
		//jmenu.add(ms);
		jmb.add(jmenu);
		jmb.add(jmenu2);
		jmenu2.add(aide);
		jmenu2.addSeparator();
		jmenu2.add(apropos);
		
		
		JPanel grille_score = new JPanel();
		grille_score.setLayout(new BoxLayout(grille_score, BoxLayout.X_AXIS));
		
		
		t = new Thread (new Traitement());
		bar = new JProgressBar();
		bar.setMaximum(60*vitesse);
		bar .setMinimum(0);
		bar.setStringPainted (false);
		bar.setOpaque(false);
		bar.setUI(new GradientPalletProgressBarUI());
		
		grille_score.add(score);
		p.add(jmb);
		p.add(bar);
		p.add(grille_score);
		
		niveau.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				final JFrame frame= new JFrame("Level");
                frame.setVisible(true);
                frame.setSize(new Dimension(400,270));
                frame.setLocationRelativeTo(null);
                frame.setPreferredSize(new Dimension(300,200));
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frame.setResizable(true);
                try {
					frame.setIconImage(ImageIO.read(this.getClass().getResource("fichier/icone.png")));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                Font font = new Font("Verdana", Font.BOLD, 15);
                
                JPanel p1 = new JPanel();
                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.add(new JLabel(" "));
                container.add(new JLabel(" "));
                JLabel _type = new JLabel("Level");
                _type.setFont(font);
                final JComboBox<String> combo = new JComboBox<String>();
                combo.setFont(font);
                combo.setPreferredSize(new Dimension(130, 30));
                combo.addItem("Classic");
                combo.addItem("Box");
                combo.addItem("Tunnel");
                combo.addItem("Transit");
                combo.addItem("Mill");
                combo.setSelectedIndex(type_jeu);
                p1.add(_type);
                p1.add(combo);
                container.add(p1);
                
                JLabel _vitesse = new JLabel("Speed");
                _vitesse.setFont(font);
                JPanel p2 = new JPanel(new BorderLayout());
                final JSlider slide = new JSlider();
                slide.setMaximum(300);
                slide.setMinimum(50);
                slide.setValue(200);
                slide.setPaintTicks(true);
                slide.setPaintLabels(true);
                slide.setMinorTickSpacing(10);
                slide.setMajorTickSpacing(20);
                final JLabel niveau = new JLabel("                                Medium");
                niveau.setFont(font);
                slide.addChangeListener(new ChangeListener(){
                public void stateChanged (ChangeEvent event){
                	int val = slide.getValue();
                	if(val>200)
                		niveau.setText("                                Easy");
                	if(val>100 && val<200)
                		niveau.setText("                                Medium ");
                	if(val<100) 
                		niveau.setText("                                Hard");
                	vitesse=slide.getValue();
                	vitesse_n=slide.getValue();
                }
                });
                TitledBorder border = BorderFactory.createTitledBorder("Speed");
                slide.setPreferredSize(new Dimension(150, 50));
                slide.setAlignmentX(JSlider.CENTER );

                p2.setBorder(border);
                p2.add(slide,BorderLayout.CENTER);
                p2.add(niveau,BorderLayout.SOUTH);
                container.add(p2);
                container.add(new JLabel(" "));
                JButton ok = new JButton(" OK ");
                ok.setFont(font);
                ok.setBackground((new Color(147,181,140)));
                
                ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if(type_jeu!=combo.getSelectedIndex()){
							init();
							type_jeu=combo.getSelectedIndex();
							pan.init(posX,posY,type_jeu);
						}
						frame.dispose();
					}
				});
                JPanel south = new JPanel();
                south.add(ok);
                container.add(south);
                frame.add(container);
                Icon icon = new ImageIcon (this.getClass().getResource("fichier/forme.jpg"));
                container.setBorder(new MatteBorder(3, 3, 3, 3, new Color(41,98,32)));
                frame.setResizable(false);
			}
		});
		aide.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFrame frame= new JFrame("Help");
                frame.setVisible(true);
                frame.setSize(new Dimension(440,300));
                frame.setLocationRelativeTo(null);
                frame.setPreferredSize(new Dimension(150,140));
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frame.setBackground(new Color(183,206,179));
                try {
					frame.setIconImage(ImageIO.read(this.getClass().getResource("fichier/icone.png")));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                Font font = new Font("Courier new", Font.BOLD, 15);
                
                JPanel p1 = new JPanel();
                Icon icon = new ImageIcon (this.getClass().getResource("fichier/bordure_forme.jpg"));
                p1.setBorder(new MatteBorder(5, 5, 5, 5, icon));
                
                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.add(new JLabel(" "));
                p1.setBackground(new Color(182,206,179));
                JTextArea tx = new JTextArea(14,43);
                tx.setBackground(new Color(182,206,179));
                tx.setFont(font);
                tx.setText("Control:\n"
                        + "The player controls the snake using the arrow keys: "
                        + "left, right, up, and down. To slow down, press the opposite key. \n\n"
                        + "Game Rules:\n"
                        + "If the snake hits a wall or its own tail, you lose the game. \n"
                        + "Each time the snake eats an apple, it grows larger, and you earn points. "
                        + "Try to eat as many apples as possible!\n"
                        + "Points range from 5 to 300, depending on the time taken to reach the apple, "
                        + "and they are also relative to the speed.");

                tx.setLineWrap(true);
                tx.setWrapStyleWord(true);
                tx.setEditable(false);
                p1.add(tx,BorderLayout.CENTER);
                frame.add(p1);
                frame.setResizable(false);
			}
		});
		apropos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFrame frame= new JFrame("About");
                frame.setVisible(true);
                frame.setSize(new Dimension(360,220));
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frame.setBackground(new Color(183,206,179));
                try {
					frame.setIconImage(ImageIO.read(this.getClass().getResource("fichier/icone.png")));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Font font = new Font("Courier new", Font.PLAIN, 16);
                
                JPanel p1 = new JPanel();
                Icon icon = new ImageIcon (this.getClass().getResource("fichier/forme.jpg"));
                p1.setBorder(new MatteBorder(5, 5, 5, 5, new Color(44,101,33)));
                JPanel container = new JPanel();
                p1.setBackground(new Color(182,206,179));
                
                final JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                String link = "<a href=\"https://www.linkedin.com/in/khaled-kadri/\" style=\"color: #0077B5; text-decoration: none; font-weight: bold;\">LinkedIn</a><br>"
                		+ "<a href=\"https://github.com/khaledkadri\" style=\"color: #0077B5; text-decoration: none; font-weight: bold;\">GitHub</a>";
                textPane.setText("<html><body style=\"font-family: Arial, sans-serif; font-size: 14px; color: #333;\">" +
                        "<br><b>Author: Khaled KADRI</b><br>" +
                        "<p>Connect with me on :</p>" +
                        link +
                        "</body></html>");
                textPane.setEditable(false);
                textPane.setBackground(new Color(182,206,179));
                textPane.setFont(font);
                textPane.setOpaque(false);
                textPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

                textPane.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            try {
                                Desktop.getDesktop().browse(URI.create(e.getURL().toString()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
                                
                p1.add(textPane,BorderLayout.CENTER);
                frame.getContentPane().add(p1);
                frame.setResizable(false);
			}
		});
		
		ms.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFrame frame= new JFrame("High Score");
                frame.setVisible(true);
                frame.setSize(new Dimension(340,280));
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frame.setBackground(new Color(183,206,179));
                try {
					frame.setIconImage(ImageIO.read(this.getClass().getResource("fichier/icone.png")));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                Font font = new Font("Courier new", Font.BOLD, 22);
                
                JPanel p1 = new JPanel();
                Icon icon = new ImageIcon (this.getClass().getResource("fichier/forme.jpg"));
                p1.setBorder(new MatteBorder(5, 10, 5, 10, new Color(44,101,33)));//new Color(44,101,33)
                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.add(new JLabel(" "));
                p1.setBackground(new Color(182,206,179));
                JTextArea tx = new JTextArea(10,20);
                tx.setBackground(new Color(182,206,179));
                tx.setFont(font);
                tx.setForeground(Color.WHITE);
                tx.setText("\n   High Score \n\n");
                tx.setLineWrap(true);
                tx.setWrapStyleWord(true);
                tx.setEditable(false);
                p1.add(tx,BorderLayout.CENTER);
                frame.add(p1);
                frame.setResizable(false);
                /************fichier******************/
                String ligne;
        		/*try {
        			//ImageIcon logo = new ImageIcon(getClass().getResource("/Logo.jpg"));
        			BufferedReader br = new BufferedReader(new FileReader("C:/Program Files (x86)/Snake/" +
        					"meilleur_score.txt"));
        			int i = 0;
        			while ((ligne = br.readLine()) != null ){
        				scores[i]=ligne;
        				i++;
        			}
        			tx.append("Classic "+scores[0]+"\n");
    				tx.append("Box     "+scores[1]+"\n");
    				tx.append("Tunnel    "+scores[2]+"\n");
    				tx.append("Transit   "+scores[3]+"\n");
    				tx.append("Mill    "+scores[4]+"\n");
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e){}*/
        		/************************************************/	
        		}
		});
		grille.setSelected(false);
		pan.grille=true;
		grille.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(grille.isSelected())
					pan.grille=true;
				else
					pan.grille=false;
			}
		});
		init();
		go();
	}
	
	
	
	void go(){
		startGameThread(posX, posY);
	}
	
	public void startGameThread(int x, int y) {
		
	            double temps = System.currentTimeMillis();
	            double tps = 0;
	            boolean tempsreactiver = true;
	            boolean tpsreactiver = false;

	            while (true) {
	                if (posX >= getWidth())
	                    posX = 0;
	                if (posY >= getHeight())
	                    posY = 0;
	                if (posX < 0)
	                    posX = 34 * 20;
	                if (posY < 0)
	                    posY = 22 * 20;

	                if (begin) {
	                    pan.decaler();
	                    pan.forme.get(0).setposx(posX);
	                    pan.forme.get(0).setposy(posY);
	                }
	                pan.setPosX(posX);
	                pan.setPosY(posY);
	                
	                SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                        pan.repaint();
	                    }
	                });

	                try {
	                    Thread.sleep(vitesse);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }

	                long tps_pomme = 3000;
	                if (begin) {
	                    if (!pan.bonus && ct % 3 != 0 && collision) {
	                        if (System.currentTimeMillis() - tps_pomme_d > tps_pomme) {
	                            nv_Obj();
	                            ct++;
	                            collision = false;
	                        }
	                    } else {
	                        if (System.currentTimeMillis() - tps_pomme_d > tps_pomme && tempsreactiver && collision) {
	                            tempsreactiver = false;
	                            tpsreactiver = true;
	                            tps = System.currentTimeMillis();
	                            nv_Obj();
	                            pan.bonus = true;
	                            bar.setValue(0);
	                            ct++;
	                        } else if (System.currentTimeMillis() - tps > 60 * vitesse && tpsreactiver && collision) {
	                            tpsreactiver = false;
	                            tempsreactiver = true;
	                            pan.point = null;
	                            temps = System.currentTimeMillis();
	                            pan.bonus = false;
	                            bar.setValue(0);
	                            tps_pomme_d = System.currentTimeMillis();
	                        }
	                        if (collision && ct % 3 != 0) {
	                            bar.setMaximum(60 * vitesse);
	                            bar.setValue((int) (System.currentTimeMillis() - tps));
	                        } else {
	                            bar.setValue(0);
	                        }
	                    }

	                    // Gestion des directions de la flèche
	                    switch (fleche) {
	                        case 1:
	                            posX -= 20;
	                            pan.gauche = true;
	                            break;
	                        case 2:
	                            posX += 20;
	                            pan.droite = true;
	                            break;
	                        case 3:
	                            posY -= 20;
	                            pan.haut = true;
	                            break;
	                        case 4:
	                            posY += 20;
	                            pan.bas = true;
	                            break;
	                        default:
	                            vitesse = 200;
	                    }

	                    DetectionCollision(tps);
	                }
	            }
	}

	
	private void DetectionCollision(double tps) {
		if(posX==xObj && posY==yObj && pan.point!=null){
			for(int i=0;i<2;i++){
				pan.forme.add(new Point(pan.forme.get(pan.forme.size()-1).getposx(), pan.forme.get(pan.forme.size()-1).getposy()));
				pan.decaler2();
				for(int i1=0;i1<pan.forme.size();i1++){
				}
			}
			pan.point=null;
			if(ct%3==1){
				_score += 300-(((Math.abs(tps-System.currentTimeMillis())*300)))/(60*vitesse);
				pan.bonus=false;
			}
			else
				_score +=5;
			bar.setValue(0);
			score.setText("  Score : "+_score);
			collision=true;
			tps_pomme_d = System.currentTimeMillis();
		}
		for(int i =0 ; i< pan.forme.size(); i++) 
			if(pan.forme.get(i).getposx()==posX && pan.forme.get(i).getposy()==posY){
				/*if(scoresup(_score))
					sauvegarde(_score);*/
				init();
				pan.init(posX,posY,type_jeu);
				try {
					throw new Collision(pan,this);
				} catch (Collision e) {}
			}
		for(int i =0 ; i< pan.obstacle.size(); i++) 
			if(posX==pan.obstacle.get(i).getposx() && posY==pan.obstacle.get(i).getposy()){
				/*if(scoresup(_score))
					sauvegarde(_score);*/
				init();
				pan.init(posX,posY,type_jeu);
				try {
					throw new Collision(pan,this);
				} catch (Collision e) {}
			}
		
	}

	private void nv_Obj() {
		Point p;
		boolean b=false;
		while(!b){
			xObj = randomposition(360-20);
			yObj = randomposition(300-20);
			
			for(int i = 0 ; i < pan.forme.size(); i++){
				if(pan.forme.get(i).getposx()!=xObj ||
					pan.forme.get(i).getposy()!=yObj){
						b=true;
						//i=pan.forme.size()+1;
					}
				else{
					b=false;
					i=pan.forme.size()+1;
				}
			}
			
			for(int i = 0 ; i < pan.obstacle.size(); i++){
				if(pan.obstacle.get(i).getposx()!=xObj ||
					pan.obstacle.get(i).getposy()!=yObj){
						b=true;
						//i=pan.obstacle.size()+1;
					}
				else{
					b=false;
					i=pan.obstacle.size()+1;
				}
			}
			
			//System.out.println("xObj: "+xObj+" | yObj: "+yObj+" b: "+b);
		}
		pan.point = new Point(xObj, yObj);
		System.out.println("xObj: "+xObj+" | yObj: "+yObj);
		for(int i = 0 ; i < pan.forme.size(); i++){
			System.out.println("xForme: "+pan.forme.get(i).getposx()+" | yForme: "+pan.forme.get(i).getposy());
		}
		System.out.println("****************************************************************************");
	}
	
	private int randomposition(int intervalle){
		int pos=random.nextInt(intervalle);
		boolean b = false;
		while(pos%20!=0)
			if(pos<intervalle && !b){
				pos++;
			}
			else{
				pos--;
				b= true;
			}
		return pos;
	}

	public void keyPressed(KeyEvent e) {
		displayInfo(e);
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	private void displayInfo(KeyEvent e){
		
	    int keyCode = e.getKeyCode();
	    String keyString = KeyEvent.getKeyText(keyCode);
	    
	    if(keyCode==37 || keyCode==38 || keyCode==39 || keyCode==40) {
	    	begin = true;
		    if(keyCode==37 && !vd){
		    	fleche = 1;
		    	vg = true;
		    	vb = false;
		    	vh = false;
		    	vitesse=vitesse_n;
		    	vitesse_n_b=false;
		    }
		    else if(keyCode==39 && !vg)
		    {
		    	fleche = 2;
		    	vd = true;
		    	vb = false;
		    	vh = false;
		    	vitesse=vitesse_n;
		    	vitesse_n_b=false;
		    }
		    else if(keyCode==38 && !vb)
		    {
		    	fleche = 3;
		    	vh = true;
		    	vg = false;
		    	vd = false;
		    	vitesse=vitesse_n;
		    	vitesse_n_b=false;
		    }
		    else if(keyCode==40 && !vh){
		    	fleche = 4;
		    	vb = true;
		    	vg = false;
		    	vd = false;
		    	vitesse=vitesse_n;
		    	vitesse_n_b=false;
		    }
		    else{
		    	if(!vitesse_n_b){
		    		vitesse_n=vitesse;
		    		vitesse=200;
		    		vitesse_n_b=true;
		    	}
		    }
		    begin = true;
		    pan.setbegin(true);
	    }
	}
	
	class Traitement implements Runnable{
		public void run(){
		for(int val = 0; val <= 500; val++){
		bar.setValue(val);
		try {
		t.sleep(10);
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		}
		}
	}
	
	class GradientPalletProgressBarUI extends BasicProgressBarUI {
		  private final int[] pallet;
		  public GradientPalletProgressBarUI() {
		    super();
		    this.pallet = makeGradientPallet();
		  }
		  private int[] makeGradientPallet() {
		    BufferedImage image = new BufferedImage(100, 1, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g2  = image.createGraphics();
		    Point2D start  = new Point2D.Float(0f, 0f);
		    Point2D end    = new Point2D.Float(99f, 0f);
		    float[] dist   = {0.0f, 0.5f, 1.0f};
		    Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
		    g2.setPaint(new LinearGradientPaint(start, end, dist, colors));
		    g2.fillRect(0, 0, 100, 1);
		    g2.dispose();

		    int width  = image.getWidth(null);
		    int[] pallet = new int[width];
		    PixelGrabber pg = new PixelGrabber(image, 0, 0, width, 1, pallet, 0, width);
		    try {
		      pg.grabPixels();
		    } catch(Exception e) {
		      e.printStackTrace();
		    }
		    return pallet;
		  }
		  private Color getColorFromPallet(int[] pallet, float x) {
		    if(x < 0.0 || x > 1.0) {
		      throw new IllegalArgumentException("Parameter outside of expected range");
		    }
		    int i = (int)(pallet.length * x);
		    int max = pallet.length-1;
		    int index = i<0?0:i>max?max:i;
		    int pix = pallet[index] & 0x00ffffff | (0x64 << 24);
		    return new Color(pix, true);
		  }
		  @Override public void paintDeterminate(Graphics g, JComponent c) {
		    if (!(g instanceof Graphics2D)) {
		      return;
		    }
		    Insets b = progressBar.getInsets(); // area for border
		    int barRectWidth  = progressBar.getWidth()  - (b.right + b.left);
		    int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
		    if (barRectWidth <= 0 || barRectHeight <= 0) {
		      return;
		    }
		    int cellLength = getCellLength();
		    int cellSpacing = getCellSpacing();
		    int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

		    if(progressBar.getOrientation() == JProgressBar.HORIZONTAL) {

		      float x = amountFull / (float)barRectWidth;
		      g.setColor(getColorFromPallet(pallet, x));
		      //System.out.println("rgb : "+g.getColor().getRGB());
		      g.setColor(new Color(179,60,34));
		      g.setColor(new Color(44,101,33));
		      g.fillRect(b.left, b.top, amountFull, barRectHeight);

		    } else { 
		      //...
		    }
		    if(progressBar.isStringPainted()) {
		      paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
		    }
		  }
	}
	
	boolean scoresup(int score){
		String ligne = "",lignetj="";
		try {
			//java.io.InputStream ips = this.getClass().getResourceAsStream("fichier/meilleur_score.txt");
			BufferedReader br = new BufferedReader(new FileReader("C:/Program Files (x86)/Snake/" +
					"meilleur_score.txt"));

			int i =0;
			while ((ligne = br.readLine()) != null ){
				scores[i]=ligne;
				if(i==type_jeu)
					lignetj = ligne;
				i++;
			}
			//String[] s = ligne.split(",");
			if(Integer.valueOf(lignetj)<score)
				return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){}
		return false;
	}
	void sauvegarde(int score){
		try {
			//URL resourceUrl = getClass().getResource("C:/Program Files/Snake/fichier/meilleur_score.txt");
			//URI uri= resourceUrl.toURI();
			//System.out.println(uri.getPath());
			File file = new File("C:/Program Files (x86)/Snake/meilleur_score.txt");
			FileOutputStream fileoutput = new FileOutputStream(file);
			byte[] contenuByte = String.valueOf(score).getBytes();
			String retour="\n";
			for(int i = 0 ; i< 5;i++){
				if(i==type_jeu)
					fileoutput.write(contenuByte);
				else
					fileoutput.write(scores[i].getBytes());
				fileoutput.write(retour.getBytes());
			}
			fileoutput.flush();
			fileoutput.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){}
	}
}
