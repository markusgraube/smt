package de.tud.plt.smt.controller;

import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;


/** 
 * Provides a interface to the TDB triple store.
 *
 * @author Stephan Hensel
 * @author Markus Graube
 *
 */
public class DataSetInMemory {

	/** dataset. **/
	private static Dataset dataset;
	

	/**
	 * The constructor.
	 */
	public DataSetInMemory() {
		dataset = DatasetFactory.createMem();	
	}
	
	public void close() {
		dataset.close();
	}
	
		
	/**
	 * Executes a SELECT query.
	 * 
	 * @param selectQueryString the SELECT query
	 * @return result set
	 */
	public ResultSet executeSelectQuery(String selectQueryString) {
		QueryExecution qExec = QueryExecutionFactory.create(selectQueryString, dataset);
		ResultSet result = qExec.execSelect();
		return result;
	}
	
	
	/**
	 * Executes a CONSTRUCT query.
	 * 
	 * @param constructQueryString the CONSTRUCT query
	 * @return model
	 */
	public Model executeConstructQuery(String constructQueryString) {
		QueryExecution qExec = QueryExecutionFactory.create(constructQueryString, dataset);
		Model result = qExec.execConstruct();
		return result;
	}
	
	
	
	/**
	 * Executes a DESCRIBE query.
	 * 
	 * @param describeQueryString the DESCRIBE query
	 * @return model
	 */
	public Model executeDescribeQuery(String describeQueryString) {
		QueryExecution qExec = QueryExecutionFactory.create(describeQueryString, dataset);
		Model result = qExec.execDescribe();
		return result;
	}
	

	/**
	 * Executes an ASK query.
	 * 
	 * @param askQueryString the ASK query
	 * @return boolean result of ASK query
	 */
	public boolean executeAskQuery(String askQueryString) {
		QueryExecution qe = QueryExecutionFactory.create(askQueryString, dataset);
		boolean result = qe.execAsk();
		return result;
	}
	
	public void addNamedModel(String graphName, Model model_jena) {
		dataset.addNamedModel(graphName, model_jena);
	}
	
	/**
	 * Executes an UPDATE query.
	 * 
	 * @param updateQueryString the UPDATE query
	 */
	public void executeUpdateQuery(String updateQueryString) {
		GraphStore graphStore = GraphStoreFactory.create(dataset) ;
		
	    UpdateRequest request = UpdateFactory.create(updateQueryString) ;
	    UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore) ;
	    proc.execute();
	}
	

	public void executeCreateGraph(String graph) {
		GraphStore graphStore = GraphStoreFactory.create(dataset) ;

	    UpdateRequest request = UpdateFactory.create("CREATE GRAPH <"+graph+">") ;
	    UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore) ;
	    proc.execute();
	}

	public Iterator<String> getGraphs() {
		Iterator<String> list = dataset.listNames();
		return list;
	}
	
	public Model getGraph(String graphName){
		Model model = dataset.getNamedModel(graphName);
		return model;
	}
	
}
