package ca.mcgill.ecse211.lab4;

import lejos.hardware.lcd.TextLCD;

public class Display {
  private TextLCD lcd;

  public Display(TextLCD lcd) {
    this.lcd = lcd;
  }
  
  public void run() {    
	  lcd.clear();
	  lcd.drawString("press middle button", 0, 0);
	  lcd.drawString("when localizer     ", 0, 1);
	  lcd.drawString("is done            ", 0, 2);
  }
}
