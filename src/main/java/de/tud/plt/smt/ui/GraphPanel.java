package de.tud.plt.smt.ui;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class GraphPanel extends mxGraphComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6686987563806502720L;

	protected mxGraph graph;
	protected mxFastOrganicLayout layout;
	protected mxGraphModel model;
	
	
	public GraphPanel() {
		super(new mxGraph());
		graph = this.getGraph();
		graph.setMultigraph(true);
		graph.setAllowDanglingEdges(false);
		graph.setCellsEditable(true);
		graph.setAutoSizeCells(true);
		graph.setEdgeLabelsMovable(false);
		graph.setGridEnabled(true);
		graph.setExtendParentsOnAdd(true);
		
		
		
		model = (mxGraphModel) graph.getModel();
			
		// define layout
	    layout = new mxFastOrganicLayout(graph);
	    layout.setForceConstant(100); // the higher, the more separated
//		layout.setDisableEdgeStyle(true); 
//		layout.setMinDistanceLimit(100);
//		layout.setMaxDistanceLimit(35);  
		
		
		mxStylesheet smt_stylesheet =  new mxStylesheet();
		graph.setStylesheet(smt_stylesheet);
		
		Map<String, Object> style_literal = new HashMap<String, Object>();
		style_literal.put("shape", "rectangle");
		style_literal.put("fontBold", true);
//		style_literal.put("rounded", true);
//		style_literal.put("fillColor", "#999999");
//		style_literal.put("fontColor", "red");
		smt_stylesheet.putCellStyle("literal", style_literal);
		
		Map<String, Object> style_node = new HashMap<String, Object>();
		style_node.put("shape", "ellipse");
//		style_node.put("fillColor", "#128949");
//		style_node.put("fontColor", "black");
		smt_stylesheet.putCellStyle("resource", style_node);

		
		Map<String, Object> style_named_graph = new HashMap<String, Object>();
		style_named_graph.put("fillColor", "white");
		style_named_graph.put("fontColor", "black");
		smt_stylesheet.putCellStyle("named_graph", style_named_graph);
		
		
		this.setConnectable(false);
		this.setToolTips(false);
		this.setLocation(0, 0);	
		this.setVisible(true);
		
		this.setAutoExtend(true);
	}

	public void clear() {
		model.beginUpdate();
		model.clear();
		model.endUpdate();
		this.updateUI();
	}
	
}