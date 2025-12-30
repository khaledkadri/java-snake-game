# java-snake-game
A 2D Snake Game built with Java and Swing, featuring multiple levels, custom UI, and dynamic bonus system. Developed by Khaled Kadri.

# 🐍 Snake Game — Java Swing Edition

A **2D Snake Game** built entirely in **Java (Swing + AWT)**.  
Includes multiple levels, visual themes, and dynamic bonus animations.  
Developed with clean OOP structure and modular design by **[Khaled Kadri](https://www.linkedin.com/in/khaled-kadri/)**.

<img src="snake.png" alt="Screenshot of the game" width="600"/>

---

## Features

✅ Classic Snake gameplay  
✅ Multiple game modes: *Classic, Box, Tunnel, Transit, Mill*  
✅ Bonus system with pulsating apples and time-limited rewards  
✅ Real-time scoring and smooth animations  
✅ Customizable grid and dark mode  
✅ Gradient progress bar and dynamic UI elements  
✅ Keyboard control support (arrow keys)  
✅ Built with pure Java (no external frameworks)

---

## Implementation Details

- **Animations:** Handled with **Java threads**, allowing smooth, concurrent updates for the snake, bonus items, and grid elements without freezing the UI.  
- **Custom Logic & Interface:** Entire game logic, UI, and animations were **designed and implemented from scratch**. No external 2D graphics library was used — everything is done using **Java Swing, AWT, and Graphics2D**.  
- **Object-Oriented Design:** Game components like Snake, Apple, Board, and Score are modular classes with clear responsibilities.  
- **Thread Safety:** Threads are carefully managed to update game state and repaint the GUI asynchronously.

---

## Technologies Used

- **Language:** Java  
- **UI Toolkit:** Swing / AWT  
- **Graphics:** BufferedImage, Graphics2D, AffineTransform  
- **Structure:** Object-Oriented Design 

---
