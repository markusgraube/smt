package de.tud.plt.smt.ui;

import java.util.HashMap;

import javax.swing.JPanel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import de.tud.plt.smt.controller.Controller;
import de.tud.plt.smt.model.LDModel;

public class GraphPanel extends JPanel {

	private static final int NODE_HEIGHT = 20;
	private static final int NODE_WIDTH = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4099161978657241049L;
	private mxGraph graph;
	private mxGraphLayout layout;
	private mxGraphComponent graphComponent;
	private mxIGraphModel model;
	
	public GraphPanel(){
		graph = new mxGraph();
		
		Object parent = graph.getDefaultParent();
		model = graph.getModel();
		model.beginUpdate();
		Object v1 = graph.insertVertex(parent, null, "Hello", 400, 20, 80,
				30);
		Object v2 = graph.insertVertex(parent, null, "World!", 500, 500,
				80, 30);
		graph.insertEdge(parent, null, "Edge", v1, v2);
		model.endUpdate();

		
		//set graph size	
//		mxRectangle arg0 = new mxRectangle(0,0,500,400);
//		graph.setMinimumGraphSize(arg0);
//		graph.setMaximumGraphBounds(arg0);
		//graph.setMultigraph(true);
		//graph.setAllowDanglingEdges(false);			
			
		// define layout
	    layout = new mxOrganicLayout(graph);
//	    layout.setForceConstant(100); // the higher, the more separated
//		layout.setDisableEdgeStyle(true); 
//		layout.setMinDistanceLimit(25);
//		layout.setMaxDistanceLimit(35);  
	    layout.execute(parent);
	    
		graphComponent = new mxGraphComponent(graph);
		graphComponent.setConnectable(false);
		graphComponent.setToolTips(true);
			
		this.add(graphComponent);
		this.setVisible(true);
		//this.setLocation(0, 0);
	    //this.setSize(500,400);
	}
	
	public void updateGraph() {
		Object parent = graph.getDefaultParent();
		model.beginUpdate();
		//graph.getModel().remove(parent);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		
		LDModel ld_model = Controller.get().lhs;
		Model rdf_model = ld_model.getJenaModel();
//		Model rdf_model = ld_model.jena_model;
		NodeIterator it = rdf_model.listObjects();
		while (it.hasNext()) {
			RDFNode node = it.next();
			String str = rdf_model.qnameFor(node.toString());
			map.put(node.toString(), graph.insertVertex(parent, str, str, 0, 0, NODE_WIDTH, NODE_HEIGHT));
		}
		
		StmtIterator ito = rdf_model.listStatements();
		while (ito.hasNext()) {
			Statement st = ito.next();
			Object sub = map.get(st.getSubject().toString());
			Object obj = map.get(st.getObject().toString());
			graph.insertEdge(parent, null, st.getPredicate().toString(), sub, obj);
		}
		
		layout.execute(graph.getDefaultParent());
		model.endUpdate();
		
		
		//graphComponent.updateComponents();
		//graphComponent.refresh();
		this.repaint();

	}
	

}
