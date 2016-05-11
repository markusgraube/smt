package de.tud.plt.smt.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.tud.plt.smt.model.TransformationModel;
import de.tud.plt.smt.model.Rule;
import de.tud.plt.smt.ui.ApplicationUI;

public class Controller {
	
	/** The logger */
	private static Logger logger = Logger.getLogger(Controller.class);
	
	private static Controller controller;

	public DataSetInMemory ds;
	public TransformationModel activeModel = null;

	public Rule current_rule =  null;
	public HashMap<String, TransformationModel> models = new HashMap<String, TransformationModel>();
	public HashMap<String, Rule> rules = new HashMap<String, Rule>();
	
	public ApplicationUI form;
	
	private Controller() {
		logger.info("Create controller");
		ds = new DataSetInMemory();	
		activeModel = new TransformationModel(ds, "active", "graph://LHS", "graph://CS", "graph://RHS");
	}
	
	
	public static Controller get(){
		if (controller==null)
			controller = new Controller();
		return controller;
	}
	
	
	public TransformationModel addModel(File file) {
		TransformationModel new_model =  new TransformationModel(file, ds);
		models.put(new_model.label, new_model);	
		form.model_tree_node.add(new DefaultMutableTreeNode(new_model.label));		
		return new_model;
	}
	
	
	public Rule addRule(File file) {
		Rule new_rule = new Rule(file);
		rules.put(new_rule.label, new_rule);
		current_rule = new_rule;
		form.rule_tree_node.add(new DefaultMutableTreeNode(new_rule.label));
		form.tree_rules.expandPath(form.tree_rules.getPathForRow(form.tree_rules.getRowCount()));
		form.tree_rules.updateUI();
		form.update_rule_visualisation();	
		return new_rule;
	}
	
	
	public void loadModel(TransformationModel model) {
		ds.addNamedModel(activeModel.lhs_graphName, model.get_lhs());
		ds.addNamedModel(activeModel.cs_graphName, model.get_cs());
		ds.addNamedModel(activeModel.rhs_graphName, model.get_rhs());
		activeModel.pm = model.pm;
		activeModel.jena_model = model.jena_model;
		form.update_model_visualisation();		
	}
	
	public void saveLHS(String path) throws IOException {
		Model model = ds.getGraph("graph://LHS");
		File file = new File(path);
		OutputStream out = new FileOutputStream(file);
		model.write( out, "TURTLE");
		out.close();
	}
	
	public void saveRHS(String path) throws IOException {
		Model model = ds.getGraph("graph://RHS");
		File file = new File(path);
		OutputStream out = new FileOutputStream(file);
		model.write( out, "TURTLE");
		out.close();
	}
	
	public void saveCS(String path) throws IOException {
		Model model = ds.getGraph("graph://CS");
		File file = new File(path);
		OutputStream out = new FileOutputStream(file);
		model.write( out, "TURTLE");
		out.close();
	}



	public void transform() {
		activeModel.pm.setNsPrefixes(current_rule.pm);
		ds.executeUpdateQuery(current_rule.transformationQuery);
		logger.info("Sparql transformation performed");
		form.update_model_visualisation();
		
	}


	public void saveModel(File selectedFile) throws IOException {
		OutputStream out = new FileOutputStream(selectedFile);
		activeModel.jena_model.write( out, "TURTLE");
		out.close();
		saveLHS(selectedFile.getPath()+".lhs.ttl");
		saveCS(selectedFile.getPath()+".cs.ttl");
		saveRHS(selectedFile.getPath()+".rhs.ttl");
		
		
	}
	
}
