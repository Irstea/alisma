package utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class LimitedTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	
	private boolean utilityKeyPressed=false;
	private int maxChar;

	public LimitedTextField(int max) {
		super();
		maxChar = max;
		this.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e){
					checkutilityKey(e);
				}
				public void keyTyped(KeyEvent e){
					checklenght(e);
				}
				public void keyReleased(KeyEvent e){
					e.consume();
				}
			}
		);
	}
	
	/*Vérification des touches BACKSPACE,LEFT,RIGHT pour les garder actives*/
	private void checkutilityKey(KeyEvent e) {
		utilityKeyPressed = (e.getKeyCode()==KeyEvent.VK_BACK_SPACE || 
							 e.getKeyCode()==KeyEvent.VK_LEFT || 
							 e.getKeyCode()==KeyEvent.VK_RIGHT || 
							 e.getKeyCode()==KeyEvent.VK_UP || 
							 e.getKeyCode()==KeyEvent.VK_DOWN);

	}

	/*Vérification de la longueur*/
	private void checklenght(KeyEvent e) {
		if(!utilityKeyPressed && this.getText().length() >= maxChar)
			e.consume();
	}
} 
