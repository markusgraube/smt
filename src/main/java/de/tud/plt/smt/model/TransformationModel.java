package de.tud.plt.smt.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.tud.plt.smt.controller.DataSetInMemory;



public class TransformationModel {
	
	/** The logger */
	private static Logger logger = Logger.getLogger(TransformationModel.class);
	
	public String label;
	public String fileName;
	public Date time_loaded;
	
	public String lhs_graphName;
	public String cs_graphName;
	public String rhs_graphName;
	
	private Model jena_model;
	private DataSetInMemory dataset;
	
	public PrefixMapping pm;
	
	public TransformationModel(final DataSetInMemory ds, final String label, final String lhs, final String cs, final String rhs) {
		dataset = ds;
		this.label = label;
		lhs_graphName = lhs;
		rhs_graphName = rhs;
		cs_graphName = cs;
		pm = PrefixMapping.Factory.create();
	}
	
	public TransformationModel(final File file, final DataSetInMemory ds) {
		dataset = ds;
		label = FilenameUtils.removeExtension(file.getName());
		fileName = file.getAbsolutePath();
		lhs_graphName = "file://"+file.getAbsolutePath()+"#lhs";
		cs_graphName = "file://"+file.getAbsolutePath()+"#cs";
		rhs_graphName = "file://"+file.getAbsolutePath()+"#rhs";
		time_loaded = new Date();
		pm = PrefixMapping.Factory.create();
		
		jena_model = ModelFactory.createDefaultModel();
		jena_model.read(file.getAbsolutePath());
		
		
		Model lhs_model = ModelFactory.createDefaultModel();
		QueryExecution qExec = QueryExecutionFactory.create("SELECT ?file WHERE { [] <http://eatld.et.tu-dresden.de/smt/lhs_file> ?file.}", jena_model);
		ResultSet result = qExec.execSelect();
		if (result.hasNext()) {
			Literal a = result.next().getLiteral("file");
			lhs_model.read(file.getParent() +"/"+ a.getString());
			Map<String, String> b = lhs_model.getNsPrefixMap();
			pm.setNsPrefixes(b);
			
		}
		
		Model cs_model = ModelFactory.createDefaultModel();
		qExec = QueryExecutionFactory.create("SELECT ?file WHERE { [] <http://eatld.et.tu-dresden.de/smt/cs_file> ?file.}", jena_model);
		result = qExec.execSelect();
		if (result.hasNext()) {
			Literal a = result.next().getLiteral("file");
			cs_model.read(file.getParent() +"/"+ a.getString());
			pm.setNsPrefixes(cs_model.getNsPrefixMap());
		}
		
		Model rhs_model = ModelFactory.createDefaultModel();
		qExec = QueryExecutionFactory.create("SELECT ?file WHERE { [] <http://eatld.et.tu-dresden.de/smt/rhs_file> ?file.}", jena_model);
		result = qExec.execSelect();
		if (result.hasNext()) {
			Literal a = result.next().getLiteral("file");
			rhs_model.read(file.getParent() +"/"+ a.getString());
			pm.setNsPrefixes(rhs_model.getNsPrefixMap());
		}
		
		dataset.addNamedModel(lhs_graphName, lhs_model);
		dataset.addNamedModel(cs_graphName, cs_model);
		dataset.addNamedModel(rhs_graphName, rhs_model);
		
		logger.info("new file: "+this.fileName+" stored in graph " + lhs_graphName );
	}
	
	public String get_lhs_as_turtle() {
		Model model = dataset.getGraph(lhs_graphName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		model.write(baos, "TURTLE");
		try {
			logger.debug(baos.toString("UTF-8"));
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String get_trig() {
		Model lhs_model = dataset.getGraph(lhs_graphName);
		Model cs_model = dataset.getGraph(cs_graphName);
		Model rhs_model = dataset.getGraph(rhs_graphName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		lhs_model.write(baos, "TURTLE");
		cs_model.write(baos, "TURTLE");
		rhs_model.write(baos, "TURTLE");
		try {
			logger.debug(baos.toString("UTF-8"));
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public Model get_lhs(){
		Model model = dataset.getGraph(this.lhs_graphName);
		return model;
	}


	public Model get_rhs() {
		Model model = dataset.getGraph(this.rhs_graphName);
		return model;
	}

	public Model get_cs() {
		Model model = dataset.getGraph(this.cs_graphName);
		return model;
	}

}
