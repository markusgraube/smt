package de.tud.plt.smt.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.tud.plt.smt.model.LDModel;
import de.tud.plt.smt.model.Rule;
import de.tud.plt.smt.ui.ApplicationUI;

public class Controller {
	
	/** The logger */
	private static Logger logger = Logger.getLogger(Controller.class);
	
	private static Controller controller;

	public DataSetInMemory ds;
	public LDModel lhs =  null;
	public LDModel rhs =  null;
	public LDModel cs =  null;
	
	public Rule current_rule =  null;
	public HashMap<String, LDModel> models = new HashMap<String, LDModel>();
	public HashMap<String, Rule> rules = new HashMap<String, Rule>();
	
	public ApplicationUI form;
	
	private Controller() {
		logger.info("Create controller");
		ds = new DataSetInMemory();	
	}
	
	
	public static Controller get(){
		if (controller==null)
			controller = new Controller();
		return controller;
	}
	
	
	public LDModel addModel(File file) {
		LDModel new_model =  new LDModel(file);
		models.put(new_model.label, new_model);	
		form.model_tree_node.add(new DefaultMutableTreeNode(new_model.label));		
		return new_model;
	}
	
	
	public void addRule(File file) {
		Rule new_rule = new Rule(file);
		rules.put(new_rule.label, new_rule);
		current_rule = new_rule;
		form.tree_node_rules.add(new DefaultMutableTreeNode(new_rule.label));
		form.tree_rules.expandPath(form.tree_rules.getPathForRow(form.tree_rules.getRowCount()));
	}
	
	
	public void loadModeltoLHS(LDModel model) {
		lhs = model;
		String sparql_query = String.format("CLEAR GRAPH <graph://LHS>; COPY <%s> TO <graph://LHS>", model.graphName);
		ds.executeUpdateQuery(sparql_query);
	}
	
	public void loadModeltoRHS(LDModel model) {
		rhs = model;
		String sparql_query = String.format("COPY <%s> TO <RHS>", model.graphName);
		ds.executeUpdateQuery(sparql_query);
	}
	
	public void loadModeltoCS(LDModel model) {
		cs = model;
		String sparql_query = String.format("COPY <%s> TO <CS>", model.graphName);
		ds.executeUpdateQuery(sparql_query);
	}
	
	public void saveLHS(String path) throws IOException {
		Model model = ds.getGraph("graph://LHS");
		model.write( System.out, "TURTLE");
		
		File file = new File(path);
		OutputStream out = new FileOutputStream(file);
		model.write( out, "TURTLE");
		out.close();
		
		logger.info("LHS model saved");
	}



	public void transform() {
		ds.executeUpdateQuery("INSERT DATA { GRAPH <test> { <a> <b> <c> }}");
		logger.info("Sparql transformation performed");
		
	}
	
	public void transform(String lhs_graph, String rhs_graph) {
		transform(lhs_graph, rhs_graph, "");
	}
	
	public void transform(String lhs_graph, String rhs_graph, String cs_graph) {
		
		String query = current_rule.transformationQuery.replaceAll("LHS", lhs_graph);
		query= query.replaceAll("RHS", rhs_graph);
		query= query.replaceAll("CS", cs_graph);
	}
}
