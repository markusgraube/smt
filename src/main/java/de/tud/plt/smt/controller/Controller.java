package de.tud.plt.smt.controller;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.tud.plt.smt.model.RuleSet;
import de.tud.plt.smt.ui.ApplicationUI;

public class Controller {
	
	public ArrayList<Model> models;
	public ArrayList<RuleSet> ruleSets;
	
	public ApplicationUI form;
	
	public JenaTDBInterface tdb;
	
	public Controller() {
		tdb = new JenaTDBInterface("database");	
	}
	
	public void addDataSet(Model model){
		
		models.add(model);
		
		
		com.hp.hpl.jena.rdf.model.Model model_jena = ModelFactory.createDefaultModel();
		tdb.addNamedModel(model_jena);
		
		tdb.executeUpdateQuery("INSERT DATA { "
				+ "GRAPH <http://test.com> {"
				+ "  <a> <b> <c>."
				+ "}"
				+ "}");
		
	}
	

	public void close() {
		tdb.close();
	}
	

}
