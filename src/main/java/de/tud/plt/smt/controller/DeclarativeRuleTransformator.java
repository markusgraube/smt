package de.tud.plt.smt.controller;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.model.Query;
import org.topbraid.spin.model.SPINFactory;
import org.topbraid.spin.model.update.Update;
import org.topbraid.spin.vocabulary.SP;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.RDF;

public class DeclarativeRuleTransformator {
	
	private static Logger logger = Logger.getLogger(DeclarativeRuleTransformator.class);
	
	private String declarativeRule; 
	
	private final String rule1_forward  =  "rules/decR2opR_01_move_forward.rq";
	private final String rule1_backward  =  "rules/decR2opR_01_move_backward.rq";
	private final String rule1_cc  =  "rules/decR2opR_01_move_correspondence.rq";
	private final String rule2a =  "rules/decR2opR_02a_copy_start.rq";
	private final String rule2b =  "rules/decR2opR_02b_copy_markAll.rq";
	private final String rule2c =  "rules/decR2opR_02c_copy_copy.rq";
	private final String rule2d =  "rules/decR2opR_02d_copy_connect.rq";
	private final String rule2e =  "rules/decR2opR_02e_copy_literals.rq";
	private final String rule2f =  "rules/decR2opR_02f_copy_insert.rq";
	private final String rule2g =  "rules/decR2opR_02g_copy_clear.rq";
	private final String rule3  =  "rules/decR2opR_03_convert_blank_nodes.rq";
	
	private final String rule4  =  "rules/decR2opR_04_convert_Modify_to_Ask.rq";
    
	private final Model model = ModelFactory.createDefaultModel();
	

	
	
	
	public DeclarativeRuleTransformator(final String declarativeRulePath)
	{
		try {
			this.declarativeRule = IOUtils.toString(ClassLoader.getSystemResourceAsStream(declarativeRulePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// first step is to convert SPARQL query to SPIN model
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("spin", "http://spinrdf.org/sp#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		
		UpdateRequest a = UpdateFactory.create(declarativeRule);
		Iterator<com.hp.hpl.jena.update.Update> iter = a.getOperations().iterator();
		ARQ2SPIN arq2SPIN = new ARQ2SPIN(model);
		while (iter.hasNext()) {
			arq2SPIN.createUpdate(iter.next(), null);
		}
		
		logger.debug("SPIN query: " + getSparqlQuery(model));
	}

	/**
	 * @throws IOException
	 */
	private void transform(Model model, String queryPath) throws IOException {
		String query_trans = IOUtils.toString(ClassLoader.getSystemResourceAsStream(queryPath));
		
		GraphStore graphStore = GraphStoreFactory.create(model) ;
	    UpdateRequest request = UpdateFactory.create(query_trans) ;
	    UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore) ;
	    proc.execute();
	}
	
	private String getSparqlQuery(Model model) {
		StmtIterator it = model.listStatements(null, RDF.type, SP.Modify);
		while (it.hasNext()) {
			Resource rsrc = it.next().getSubject();
			Update query = SPINFactory.asUpdate(rsrc);
			logger.debug(query.toString());			
			return query.toString();
		}
		it = model.listStatements(null, RDF.type, SP.Ask);
		while (it.hasNext()) {
			Resource rsrc = it.next().getSubject();
			Query query = SPINFactory.asQuery(rsrc);
			logger.debug(query.toString());			
			return query.toString();
		}
		return "";
	}

	/**
	 * 
	 */
	private void debugGraphStore(Model model) {
		model.write(System.out, "TURTLE");
	}
	
	public String transformToLeft2Right(){
		Model modelL2R = ModelFactory.createDefaultModel();
		modelL2R.add(model);
		try {
			transform(modelL2R, rule1_forward);	
		    transform(modelL2R, rule2a);
		    transform(modelL2R, rule2b);
		    transform(modelL2R, rule2c);
		    transform(modelL2R, rule2d);
		    transform(modelL2R, rule2e);
		    transform(modelL2R, rule2f);
		    transform(modelL2R, rule2g);
		    transform(modelL2R, rule3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getSparqlQuery(modelL2R);
	}
	
	
	public String transformToRight2Left(){
		Model modelR2L = ModelFactory.createDefaultModel();
		modelR2L.add(model);
		try {
			transform(modelR2L, rule1_backward);
		    transform(modelR2L, rule2a);
		    transform(modelR2L, rule2b);
		    transform(modelR2L, rule2c);
		    transform(modelR2L, rule2d);
		    transform(modelR2L, rule2e);
		    transform(modelR2L, rule2f);
		    transform(modelR2L, rule2g);
		    transform(modelR2L, rule3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getSparqlQuery(modelR2L);
	}
	
	public String transformToCorrespondenceCheck(){
		Model modelCC = ModelFactory.createDefaultModel();
		modelCC.add(model);
		try {
			transform(modelCC, rule1_forward);	
			transform(modelCC, rule1_backward);	
			transform(modelCC, rule1_cc);	
			
		    transform(modelCC, rule4);
		    transform(modelCC, rule3);
		    debugGraphStore(modelCC);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getSparqlQuery(modelCC);
	}
	
	public String transformToEvolveBoth(){
		return "";
	}

}
