package de.tud.plt.smt.ui;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.modify.request.UpdateModify;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateRequest;

import de.tud.plt.smt.model.Rule;

public class RuleGraphPanel extends GraphPanel {
	
	private static final int NODE_HEIGHT = 20;
	private static final int NODE_WIDTH = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4099161978657241049L;

	private PrefixMapping pm;
	
	public RuleGraphPanel(){
		super();
	}
		
	public void updateGraph(Rule rule) {
		clear();
		UpdateRequest upd = rule.upd;
		pm = rule.pm;
		model.beginUpdate();
		Object g_lhs = graph.insertVertex(graph.getDefaultParent(), "graph://LHS", "graph://LHS", 5, 5, 300, 500, "named_graph");
		Object g_cs = graph.insertVertex(graph.getDefaultParent(), "graph://CS", "graph://CS", 310, 5, 300, 500, "named_graph");
		Object g_rhs = graph.insertVertex(graph.getDefaultParent(), "graph://RHS", "graph://RHS", 615, 5, 300, 500, "named_graph");
		
		List<Update> list = upd.getOperations();
		for (Update update : list) {
			UpdateModify m = (UpdateModify) update;
			generateVisualisation_LHS(m.getWherePattern(), graph.getDefaultParent(), "graph://LHS");
			generateVisualisation_LHS(m.getWherePattern(), graph.getDefaultParent(), "graph://RHS");
			generateVisualisation_LHS(m.getWherePattern(), graph.getDefaultParent(), "graph://CS");
			generateVisualisation_RHS(m.getInsertQuads(), "graph://LHS");
			generateVisualisation_RHS(m.getInsertQuads(), "graph://RHS");
			generateVisualisation_RHS(m.getInsertQuads(), "graph://CS");
		}
		model.endUpdate();
		
		layout.execute(g_lhs);
		layout.execute(g_cs);
		layout.execute(g_rhs);
	}
	
	
	private void generateVisualisation_RHS(List<Quad> insertQuads, final String graph_c) {
		for (Quad quad : insertQuads) {
			Node g = quad.getGraph();
			if (g.toString().equals(graph_c)){
				Node s = quad.getSubject();
				Node p = quad.getPredicate();
				Node o = quad.getObject();
				
				String id_g = g.toString();
				String id_s = s.toString();
				String id_o = o.toString();
				
				Object gra = model.getCell(id_g);
				Object sub = model.getCell(id_s);
				Object obj = model.getCell(id_o);			
								
				if (sub==null){
					String label = "+"+s.toString(pm);
					sub = createVertex(gra, s,  label, "added");
				}
				if (obj==null){
					String label = "+"+o.toString(pm);
					obj = createVertex(gra, o,  label, "added");
				}
				String label = "+"+p.toString(pm);
				graph.insertEdge(gra, quad.toString(), label, sub, obj, "added");	
			}
		}
		layout.execute(graph.getDefaultParent());
		
	}

	/**
	 * @param el
	 */
	private void generateVisualisation_LHS(final Element el, final Object parent, final String graph_c) {
		if (el.getClass().equals(ElementNamedGraph.class)) {
			ElementNamedGraph ng_original = (ElementNamedGraph) el;
			ElementGroup eg_original = (ElementGroup) ng_original.getElement();
			
			Node g = ng_original.getGraphNameNode();
			String id_g = g.toString();
			if (id_g.equals(graph_c)){
				Object gra = model.getCell(id_g);
				generateVisualisation_LHS(eg_original, gra, graph_c);
			}
		} 
		else if (el.getClass().equals(ElementGroup.class)) {
			ElementGroup elementgroup = (ElementGroup) el;
			for (Element el_i : elementgroup.getElements()) {
				generateVisualisation_LHS(el_i, parent, graph_c);	
			}
		} 
		else if (el.getClass().equals(ElementMinus.class)) {
//			ElementMinus elementMinus = (ElementMinus) el;
//			ElementGroup elementgroup = (ElementGroup) elementMinus.getMinusElement();
//			generateVisualisation(elementgroup);
//			graph.insertVertex(parent, el.toString(), "Minus", 0, 0, NODE_WIDTH, NODE_HEIGHT);
		}
		else if (el.getClass().equals(ElementUnion.class)) {
//			ElementUnion elementUnion = (ElementUnion) el;
//			List<Element> elements = elementUnion.getElements();
//			for (Element el_i : elements){
//				generateVisualisation(el_i);
//			}
		}
		else if (el.getClass().equals(ElementPathBlock.class)){
			ElementPathBlock epb = (ElementPathBlock) el;
			Iterator<TriplePath> itPatternElts = epb.patternElts();
			while (itPatternElts.hasNext()) {
				TriplePath triplePath = itPatternElts.next();
				
				Node s = triplePath.getSubject();
				Node p = triplePath.getPredicate();
				Node o = triplePath.getObject();
				
				Object sub = model.getCell(s.toString());
				Object obj = model.getCell(o.toString());			
				if (sub==null){
					String label = s.toString(pm);
					sub = createVertex(parent, s,  label, "");
				}
				if (obj==null){
					String label = o.toString(pm);
					obj = createVertex(parent, o,  label, "");
				}
				String label = p.toString(pm);
				graph.insertEdge(parent, triplePath.toString(), label, sub, obj);
			}
		}
	}
	
	
	/**
	 * @param model
	 * @param parent
	 * @param node
	 * @return
	 */
	private Object createVertex(final Object parent, Node node, String label, String style) {
		Object sub;
		String additional_style = (style!="") ? ";"+style : "";
		
		if (node.isLiteral())
			sub = graph.insertVertex(parent, node.toString(), label, 0, 0, NODE_WIDTH, NODE_HEIGHT, "literal" + additional_style);
		else
			sub = graph.insertVertex(parent, node.toString(), label, 0, 0, NODE_WIDTH, NODE_HEIGHT, "resource" + additional_style);
		graph.resizeCell(sub, graph.getPreferredSizeForCell(sub));
		return sub;
	}

}

