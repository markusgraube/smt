package de.tud.plt.smt.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.tud.plt.smt.controller.Controller;



public class LDModel {
	
	/** The logger */
	private static Logger logger = Logger.getLogger(LDModel.class);
	
	public String label;
	public String fileName;
	public String graphName;
	public Date time_loaded;
	
	
	public LDModel(File file) {
		label = FilenameUtils.removeExtension(file.getName());
		fileName = file.getAbsolutePath();
		graphName = "file://"+file.getAbsolutePath();
		time_loaded = new Date();
		
		Model jena_model = ModelFactory.createDefaultModel();
		jena_model.read(file.getAbsolutePath());
		Controller.get().ds.addNamedModel(graphName, jena_model);
		
		logger.info("new file: "+this.fileName+" stored in graph " + graphName );
	}
	
	public String getTurtleRepresentation() {
		Controller c = Controller.get();
		
		Model model = c.ds.getGraph(graphName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		model.write(baos, "TURTLE");
		try {
			logger.debug(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public Model getJenaModel(){
		Controller c = Controller.get();
		Model model = c.ds.getGraph(this.graphName);
		return model;
	}

}
