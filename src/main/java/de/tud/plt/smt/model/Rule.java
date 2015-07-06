package de.tud.plt.smt.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Rule {
	
	public String transformationQuery;
	public String label;
	public RuleSet ruleSet;
	public RuleType type;
	
	
	public Rule(File file) {
		try {
			transformationQuery = FileUtils.readFileToString(file);
			label = file.getName();
		} catch (IOException e) {
			transformationQuery = "";
		}
	}

	
	

}
