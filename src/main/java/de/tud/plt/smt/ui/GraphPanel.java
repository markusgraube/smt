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
//	    layout.setForceConstant(200); // the higher, the more separated
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
		this.updateUI();
	}
	
	public void updateGraph() {
		Object parent = graph.getDefaultParent();
		
		LDModel ld_model = Controller.get().lhs;
		Model rdf_model = ld_model.getJenaModel();
		
		StmtIterator ito = rdf_model.listStatements();
		while (ito.hasNext()) {
			model.beginUpdate();
			Statement st = ito.next();
			Resource r_sub = st.getSubject();
			RDFNode r_obj = st.getObject();
			Object sub = model.getCell(r_sub.toString());
			Object obj = model.getCell(r_obj.toString());			
			if (sub==null){
				String label = getNodeLabel(rdf_model, r_sub);
				String id = r_sub.toString();
				sub = graph.insertVertex(parent, id, label, 0, 0, NODE_WIDTH, NODE_HEIGHT);
				logger.info("New Vertex: " + id + " (" + label +")");
			}
			if (obj==null){
				String label = getNodeLabel(rdf_model, r_obj);
				String id = r_obj.toString();
				obj = graph.insertVertex(parent, id, label, 0, 0, NODE_WIDTH, NODE_HEIGHT);
				logger.info("New Vertex: " + id + " (" + label +")");
			}
			String label = getNodeLabel(rdf_model, st.getPredicate());
			graph.insertEdge(parent, st.toString(), label, sub, obj);
			logger.info("New Edge: " + sub + "->" + obj + " ("+label+")");
			model.endUpdate();
		}
		
		layout.execute(parent);
		
	}

	/**
	 * @param rdf_model
	 * @param r_obj
	 * @return
	 */
	private String getNodeLabel(Model rdf_model, RDFNode r_obj) {
		String label;
		if (r_obj.isResource())
			label = rdf_model.qnameFor(r_obj.toString());
		else if (r_obj.isLiteral())
			label = r_obj.asLiteral().getString();
		else
			label = "none";
		return label;
	}
	

}
