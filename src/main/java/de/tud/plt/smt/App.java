package de.tud.plt.smt;


import de.tud.plt.smt.controller.Controller;
import de.tud.plt.smt.ui.ApplicationUI;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        Controller controller = new Controller();
        new ApplicationUI();
        
     
    }
}
