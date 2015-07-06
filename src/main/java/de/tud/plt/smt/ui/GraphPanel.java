package de.tud.plt.smt.ui;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import de.tud.plt.smt.controller.Controller;
import de.tud.plt.smt.model.LDModel;

public class GraphPanel extends mxGraphComponent {

	/** The logger */
	private static Logger logger = Logger.getLogger(GraphPanel.class);
	
	private static final int NODE_HEIGHT = 20;
	private static final int NODE_WIDTH = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4099161978657241049L;
	private mxGraph graph;
	private mxFastOrganicLayout layout;
	private mxGraphModel model;
	
	public GraphPanel(){
		super(new mxGraph());
		graph = this.getGraph();
		graph.setMultigraph(true);
		graph.setAllowDanglingEdges(false);
		graph.setCellsEditable(false);
		
		model = (mxGraphModel) graph.getModel();
			
		// define layout
	    layout = new mxFastOrganicLayout(graph);
	    layout.setForceConstant(100); // the higher, the more separated
//		layout.setDisableEdgeStyle(true); 
		layout.setMinDistanceLimit(25);
//		layout.setMaxDistanceLimit(35);  
	    
		this.setConnectable(false);
		this.setToolTips(false);
		this.setLocation(0, 0);	
		this.setVisible(true);
	}
	
	public void clear() {
		model.beginUpdate();
		model.clear();
		model.endUpdate();
	}
	
	public void updateGraph() {
		Object parent = graph.getDefaultParent();
		//this.clear();
		model = new mxGraphModel();
		model.beginUpdate();
		
		LDModel ld_model = Controller.get().lhs;
		Model rdf_model = ld_model.getJenaModel();
		
		StmtIterator ito = rdf_model.listStatements();
		while (ito.hasNext()) {
			Statement st = ito.next();
			Resource r_sub = st.getSubject();
			RDFNode r_obj = st.getObject();
			Object sub = model.getCell(st.getSubject().toString());
			Object obj = model.getCell(st.getObject().toString());
			
			if (sub==null){
				String label;
				if (r_sub.isResource())
					label = rdf_model.qnameFor(r_sub.toString());
				else if (r_sub.isLiteral())
					label = r_sub.asLiteral().getString();
				else
					label = "none";
				String id = r_sub.toString();
				sub = graph.insertVertex(parent, id, label, 0, 0, NODE_WIDTH, NODE_HEIGHT);
				logger.info("New Vertex: " + id + " (" + label +")");
			}
				
			if (obj==null){
				String label;
				if (r_obj.isResource())
					label = rdf_model.qnameFor(r_sub.toString());
				else if (r_obj.isLiteral())
					label = r_obj.asLiteral().getString();
				else
					label = "none";
				String id = r_obj.toString();
				obj = graph.insertVertex(parent, id, label, 0, 0, NODE_WIDTH, NODE_HEIGHT);
				logger.info("New Vertex: " + id + " (" + label +")");
			}
			String label = rdf_model.qnameFor(st.getPredicate().toString());
			graph.insertEdge(parent, st.toString(), label, sub, obj);
			logger.info("New Edge: " + sub + "->" + obj + " ("+label+")");
		}
		
		layout.execute(parent);
		model.endUpdate();
	}
	

}
