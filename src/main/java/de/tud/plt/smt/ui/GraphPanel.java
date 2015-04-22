package de.tud.plt.smt.ui;

import javax.swing.JPanel;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class GraphPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4099161978657241049L;
	mxGraph graph;
	
	public GraphPanel(){
		graph = new mxGraph();
		
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try
		{
			Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
					30);
			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
					80, 30);
			graph.insertEdge(parent, null, "Edge", v1, v2);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		
		//set graph size	
		mxRectangle arg0 = new mxRectangle(0,0,620,655);
		graph.setMinimumGraphSize(arg0);
		graph.setMaximumGraphBounds(arg0);
			
			
		// define layout
	    mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
	    //layout.setForceConstant(45); // the higher, the more separated
	    //layout.setDisableEdgeStyle( false); // true transforms the edges and makes them direct lines

	    // layout graph
	      
	    layout.execute(graph.getDefaultParent());
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
			
			
		//graph.setMultigraph(false);
		graph.setAllowDanglingEdges(false);
		//graphComponent.setConnectable(true);
		//graphComponent.setToolTips(true);
			
		this.add(graphComponent);
		
		this.setVisible(true);
		this.setLocation(0, 0);
	    this.setSize(620,655);
		
	}
	
	public void updateGraph() {
		
	}
	

}
