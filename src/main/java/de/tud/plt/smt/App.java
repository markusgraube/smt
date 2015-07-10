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
        ApplicationUI ui = new ApplicationUI();
        Controller controller = Controller.get();
        controller.form = ui;
        String path = System.getProperty("user.dir")+"/src/main/resources/examples/";
        
        controller.addModel(new File(path + "CAE-HMI/models/hmi_meta.ttl"));
        controller.addModel(new File(path + "CAE-HMI/models/cae_meta.ttl"));
        controller.addModel(new File(path + "CAE-HMI/models/cae_example1.ttl"));
        controller.addModel(new File(path + "CAE-HMI/models/hmi_example1.ttl"));
        
        controller.addModel(new File(path + "Class-Table/models/class_instances.ttl"));
        controller.addModel(new File(path + "Class-Table/models/class_instances_simple.ttl"));
        
        controller.addRule(new File(path + "CAE-HMI/rules/01.rq"));
        controller.addRule(new File(path + "Class-Table/rules/manual/class2table/01_Class2Table.rq"));
        controller.addRule(new File(path + "Class-Table/rules/manual/class2table/02_AttributesWithPrimitiveDatatype.rq"));
     
    }
}
