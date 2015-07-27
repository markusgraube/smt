package de.tud.plt.smt.ui;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.tud.plt.smt.model.TransformationModel;

public class RDFGraphPanel extends GraphPanel {

	/** The logger */
	private static Logger logger = Logger.getLogger(RDFGraphPanel.class);
	
	private static final int NODE_HEIGHT = 20;
	private static final int NODE_WIDTH = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4099161978657241049L;

	
	public RDFGraphPanel(){
		super();
	}
		
	public void updateGraph(TransformationModel transformation_model) {
		clear();
		
		Model model_lhs = transformation_model.get_lhs();
		Model model_cs = transformation_model.get_cs();
		Model model_rhs = transformation_model.get_rhs();
		
		model.beginUpdate();
		
		// Create LHS
		Object lhs = graph.insertVertex(graph.getDefaultParent(), "lhs", "LHS", 5, 5, 500, 400, "named_graph");
		generateGraph(transformation_model.pm, model_lhs, lhs);
		
		// Create RHS
		Object rhs = graph.insertVertex(graph.getDefaultParent(), "rhs", "RHS", 510, 5, 300, 200, "named_graph");
		generateGraph(transformation_model.pm, model_rhs, rhs);
		
		
		// Create CS
		Object cs = graph.insertVertex(graph.getDefaultParent(), "cs", "CS", 510, 205, 300, 200, "named_graph");
		generateGraph(transformation_model.pm, model_cs, cs);
		
		
		layout.execute(graph.getDefaultParent());
		model.endUpdate();
	}

	/**
	 * @param transformation_model
	 * @param rdf_model
	 * @param rhs
	 * @param parent
	 */
	private void generateGraph(PrefixMapping pm, Model rdf_model, Object parent) {
		StmtIterator ito = rdf_model.listStatements();
		while (ito.hasNext()) {
			Statement st = ito.next();
			Resource r_sub = st.getSubject();
			RDFNode r_obj = st.getObject();
			Object sub = model.getCell(r_sub.toString());
			Object obj = model.getCell(r_obj.toString());			
			if (sub==null){
				sub = createVertex(parent, r_sub, pm);
			}
			if (obj==null){
				obj = createVertex(parent, r_obj, pm);
			}
			createEdge(parent, st, sub, obj, pm);
		}
		layout.execute(parent);
		graph.extendParent(parent);
	}

	/**
	 * @param parent
	 * @param st
	 * @param sub
	 * @param obj
	 */
	private void createEdge(Object parent, Statement st, Object sub, Object obj, PrefixMapping pm) {
		String label = getNodeLabel(st.getPredicate(), pm);
		graph.insertEdge(parent, st.toString(), label, sub, obj);
		logger.debug("New Edge: " + sub + "->" + obj + " ("+label+")");
	}

	/**
	 * @param model
	 * @param parent
	 * @param node
	 * @return
	 */
	private Object createVertex(final Object parent, RDFNode node, PrefixMapping pm) {
		Object sub;
		String label = getNodeLabel(node, pm);
		String id = node.toString();
		if (node.isLiteral())
			sub = graph.insertVertex(parent, id, label, 0, 0, NODE_WIDTH, NODE_HEIGHT, "literal");
		else
			sub = graph.insertVertex(parent, id, label, 0, 0, NODE_WIDTH, NODE_HEIGHT, "resource");
		graph.resizeCell(sub, graph.getPreferredSizeForCell(sub));
		logger.debug("New Vertex: " + id + " (" + label +")");
		return sub;
	}

	/**
	 * @param rdf_model
	 * @param r_obj
	 * @return
	 */
	private String getNodeLabel(RDFNode r_obj, PrefixMapping pm) {
		if (r_obj.isAnon())
			return r_obj.toString();
		if (r_obj.isResource())
			return pm.qnameFor(r_obj.toString());
		else if (r_obj.isLiteral())
			return r_obj.asLiteral().getString();
		else
			return "none";
	}
	

}
