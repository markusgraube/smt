package de.tud.plt.smt;


import java.io.File;

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
        
        
        ApplicationUI ui = new ApplicationUI();
        Controller controller = Controller.get();
        controller.form = ui;
        String path = System.getProperty("user.dir")+"/src/main/resources/examples/CAE-HMI/models/";
        
        controller.addModel(new File(path + "hmi_meta.ttl"));
        controller.addModel(new File(path + "cae_meta.ttl"));
        controller.addModel(new File(path + "cae_example1.ttl"));
        controller.addModel(new File(path + "hmi_example1.ttl"));
        
        String path_rule = System.getProperty("user.dir")+"/src/main/resources/examples/CAE-HMI/rules/";
        controller.addRule(new File(path_rule + "01.rq"));
     
    }
}
