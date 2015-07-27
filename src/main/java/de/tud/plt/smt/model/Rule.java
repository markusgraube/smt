package de.tud.plt.smt.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class Rule {
	
	public String transformationQuery;
	public String label;
	public RuleSet ruleSet;
	public RuleType type;
	public UpdateRequest upd;
	public PrefixMapping pm;
	
	
	public Rule(File file) {
		try {
			transformationQuery = FileUtils.readFileToString(file);
			label = file.getName();
		} catch (IOException e) {
			transformationQuery = "";
		}
		upd =  UpdateFactory.create(transformationQuery);
		pm = upd.getPrefixMapping();
		pm.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		pm.setNsPrefix("xml", "http://www.w3.org/2001/XMLSchema#");
	}

	
	

}
