package de.tud.plt.smt.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
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
	private String ruleName;
	
	private final String rule_move_left  =  "rules/decR2opR_01_move_forward.rq";
	private final String rule_move_right  =  "rules/decR2opR_01_move_backward.rq";
	private final String rule_move_correspondence  =  "rules/decR2opR_01_move_correspondence.rq";
	private final String rule_copy_1_start =  "rules/decR2opR_02a_copy_start.rq";
	private final String rule_copy_2_markAll =  "rules/decR2opR_02b_copy_markAll.rq";
	private final String rule_copy_3_copy =  "rules/decR2opR_02c_copy_copy.rq";
	private final String rule_copy_4_connect =  "rules/decR2opR_02d_copy_connect.rq";
	private final String rule_copy_5_literals =  "rules/decR2opR_02e_copy_literals.rq";
	private final String rule_copy_6_insert =  "rules/decR2opR_02f_copy_insert.rq";
	private final String rule_copy_7_clear =  "rules/decR2opR_02g_copy_clear.rq";
	private final String rule_convert_blank_nodes  =  "rules/decR2opR_03_convert_blank_nodes.rq";
	private final String rule_modify_to_ask  =  "rules/decR2opR_04_convert_Modify_to_Ask.rq";
    
	private final Model model = ModelFactory.createDefaultModel();
	
	public DeclarativeRuleTransformator(final File declarativeRuleFile)
	{
		ruleName =declarativeRuleFile.getName(); 
		try {
			declarativeRule = FileUtils.readFileToString(declarativeRuleFile);
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
	 * Executes SPARQL query on SPIN model of declarative rule
	 * 
	 * @param model SPIN model of query
	 * @param queryPath path of SPARQL query
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
	
	
	public void generateOperationalRules(String path) throws IOException{
		File fPath = new File(path);
		if (!fPath.exists())
			fPath.mkdirs();
		
		String forward_rule = this.transformToLeft2Right();
		FileUtils.writeStringToFile(new File(path+"/operational/forward/"+ruleName), forward_rule);
		String backward_rule = this.transformToRight2Left();
		FileUtils.writeStringToFile(new File(path+"/operational/backward/"+ruleName), backward_rule);
		String correspondence_rule = this.transformToCorrespondenceCheck();
		FileUtils.writeStringToFile(new File(path+"/operational/correspondence/"+ruleName), correspondence_rule);
	}
	
	
	public String transformToLeft2Right(){
		Model modelL2R = ModelFactory.createDefaultModel();
		modelL2R.add(model);
		try {
			transform(modelL2R, rule_move_left);	
		    transform(modelL2R, rule_copy_1_start);
		    transform(modelL2R, rule_copy_2_markAll);
		    transform(modelL2R, rule_copy_3_copy);
		    transform(modelL2R, rule_copy_4_connect);
		    transform(modelL2R, rule_copy_5_literals);
		    transform(modelL2R, rule_copy_6_insert);
		    transform(modelL2R, rule_copy_7_clear);
		    transform(modelL2R, rule_convert_blank_nodes);
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
			transform(modelR2L, rule_move_right);
		    transform(modelR2L, rule_copy_1_start);
		    transform(modelR2L, rule_copy_2_markAll);
		    transform(modelR2L, rule_copy_3_copy);
		    transform(modelR2L, rule_copy_4_connect);
		    transform(modelR2L, rule_copy_5_literals);
		    transform(modelR2L, rule_copy_6_insert);
		    transform(modelR2L, rule_copy_7_clear);
		    transform(modelR2L, rule_convert_blank_nodes);
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
			transform(modelCC, rule_move_left);	
			transform(modelCC, rule_move_right);	
			transform(modelCC, rule_move_correspondence);	
		    transform(modelCC, rule_modify_to_ask);
		    transform(modelCC, rule_convert_blank_nodes);
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
