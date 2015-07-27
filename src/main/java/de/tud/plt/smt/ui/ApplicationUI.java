package de.tud.plt.smt.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.tud.plt.smt.controller.Controller;
import de.tud.plt.smt.controller.DeclarativeRuleTransformator;
import de.tud.plt.smt.model.Rule;
import de.tud.plt.smt.model.TransformationModel;
import java.awt.Component;
import javax.swing.border.TitledBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.JDesktopPane;
import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import javax.swing.Action;


public class ApplicationUI {
	
	
	public String path = System.getProperty("user.dir")+"/src/main/resources/examples/CAE-HMI/models/";
	
	
	private JFrame mainFrame;
	private JTextArea jta_rdf;
	private JTextArea jta_rule;
	private RuleGraphPanel rule_graph_panel; 
	private Controller controller;
	private RDFGraphPanel rdf_graph_panel;
	private JLabel lbl_status;

	public DefaultMutableTreeNode model_tree_node;
	public DefaultMutableTreeNode rule_tree_node;
	public JTree tree_model;
	
	public JTree tree_rules;



	
	/**
	 * Create the application UI
	 */
	public ApplicationUI() {
		controller = Controller.get();
		mainFrame = new JFrame();
		mainFrame.setTitle("SPARQL Model Transformator");
		mainFrame.setBounds(100, 100, 1116, 862);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
		addMenuBar();
		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel desktopPane = new JPanel();
		mainFrame.getContentPane().add(desktopPane, BorderLayout.CENTER);
		desktopPane.setLayout(new GridLayout(1, 2));
		
		JToolBar panel_rule = new JToolBar();
		
		
		tree_rules = new JTree();
		tree_rules.setBorder(new TitledBorder(null, "Rulesets", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tree_rules.setEditable(true);
		rule_tree_node = new DefaultMutableTreeNode("Rulesets");
		tree_rules.setModel(new DefaultTreeModel(rule_tree_node));
		
		
		
		
		
		panel_rule.setLayout(new BorderLayout(0, 0));
		
		JPanel toolBar = new JPanel();
		panel_rule.add(toolBar, BorderLayout.NORTH);
		
		JLabel lblNewLabel_2 = new JLabel("Rule");
		toolBar.add(lblNewLabel_2);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_1);
		
		JButton b_add_rule = new JButton("Add Rule");
		toolBar.add(b_add_rule);
		JButton b_load_rule = new JButton("Load Rule");
		toolBar.add(b_load_rule);
		JButton b_save_rule = new JButton("Save Rule");
		toolBar.add(b_save_rule);
		JButton b_clear_rule_canvas = new JButton("Clear Canvas");
		toolBar.add(b_clear_rule_canvas);
		JButton b_generate_operational_rules = new JButton("Generate Operational Rules");
		toolBar.add(b_generate_operational_rules);
		
		panel_rule.add(tree_rules, BorderLayout.WEST);
		
		JPanel panel_current_rule = new JPanel();
		panel_rule.add(panel_current_rule, BorderLayout.CENTER);
		panel_current_rule.setLayout(new GridLayout(0, 1, 0, 0));
		
		rule_graph_panel = new RuleGraphPanel();
		panel_current_rule.add(rule_graph_panel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_current_rule.add(scrollPane_1);
		
		jta_rule = new JTextArea();
		scrollPane_1.setViewportView(jta_rule);
		jta_rule.setEditable(false);
		jta_rule.setLineWrap(true);
		jta_rule.setText("# Rule Output Area");
		
		
		b_add_rule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File rule_file = chooseSPARQLRuleFile();
				controller.addRule(rule_file);
				tree_rules.updateUI();
			}
		});
		b_generate_operational_rules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File rdf_file = chooseSPARQLRuleFile();
				DeclarativeRuleTransformator t =  new DeclarativeRuleTransformator(rdf_file);
				try {
					t.generateOperationalRules("test");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		b_load_rule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TreePath b = tree_rules.getSelectionPath();
				String path = b.getLastPathComponent().toString();
				Rule rule = controller.rules.get(path);
				controller.current_rule = rule;
				jta_rule.setText(rule.transformationQuery);
				RuleGraphPanel a = controller.form.rule_graph_panel;
				a.updateGraph(rule);
			}
		});
		
		JToolBar panel_model = new JToolBar();
		desktopPane.add(panel_model);
		desktopPane.add(panel_rule);
		
		tree_model = new JTree();
		tree_model.setEditable(true);
		tree_model.setBorder(new TitledBorder(null, "Models", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		model_tree_node = new DefaultMutableTreeNode("Models");
		tree_model.setModel(new DefaultTreeModel(model_tree_node));
		
		
		panel_model.setLayout(new BorderLayout(0, 0));
		
		JPanel toolBar_btn_model = new JPanel();
		panel_model.add(toolBar_btn_model, BorderLayout.NORTH);
		
		
		JLabel lblNewLabel_1 = new JLabel("Model");
		toolBar_btn_model.add(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		Component horizontalGlue = Box.createHorizontalGlue();
		toolBar_btn_model.add(horizontalGlue);
		
		JButton b_add_model = new JButton("Add Model");
		toolBar_btn_model.add(b_add_model);
		JButton b_load_model = new JButton("Load Model");
		toolBar_btn_model.add(b_load_model);
		JButton b_clear = new JButton("Clear Canvas");
		toolBar_btn_model.add(b_clear);
		JButton b_save_model = new JButton("Save Model");
		toolBar_btn_model.add(b_save_model);
		
		panel_model.add(tree_model, BorderLayout.WEST);
		
		JPanel panel_current_model = new JPanel();
		panel_model.add(panel_current_model);
		panel_current_model.setLayout(new GridLayout(0, 1, 0, 0));
		
		rdf_graph_panel = new RDFGraphPanel();
		panel_current_model.add(rdf_graph_panel);
		
		
		jta_rdf = new JTextArea();
		jta_rdf.setEditable(false);
		jta_rdf.setLineWrap(true);
		jta_rdf.setText("# RDF Output Area");
		
		JScrollPane scrp = new JScrollPane(jta_rdf,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel_current_model.add(scrp);
		scrp.setPreferredSize(new Dimension(300, 150));
		
		
		///////////// Action Listener
		b_add_model.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File rdf_file = useFileChooser();
				controller.addModel(rdf_file);
				tree_model.updateUI();
			}
		});
		b_load_model.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TreePath b = tree_model.getSelectionPath();
				String path = b.getLastPathComponent().toString();
				TransformationModel model = controller.models.get(path);
				controller.loadModel(model);
			}
		});
		b_save_model.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.saveLHS("model_lhs.ttl");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		b_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdf_graph_panel.clear();
			}
		});
		

		
		JPanel panel_statusbar = new JPanel();
		panel_statusbar.setBorder(new LineBorder(new Color(0, 0, 0)));
		mainFrame.getContentPane().add(panel_statusbar, BorderLayout.SOUTH);
		panel_statusbar.setLayout(new GridLayout(0, 1, 0, 0));
		
		lbl_status = new JLabel("Status: xyz");
		lbl_status.setHorizontalAlignment(SwingConstants.LEFT);
		panel_statusbar.add(lbl_status);
		
		mainFrame.setVisible(true);
	}
	
	
	private void addMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		mnMenu.setMnemonic('M');
		menuBar.add(mnMenu);
		JMenuItem mntmExit = new JMenuItem("Exit");
		
		mnMenu.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmProperties = new JMenuItem("Properties");
		mnEdit.add(mntmProperties);
		
		JMenuItem mntmTransform = new JMenuItem("Transform");
		mntmTransform.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					controller.transform();
					
				}
			});
		menuBar.add(mntmTransform);
	}
	
	private File useFileChooser() {
		JFileChooser chooser = new JFileChooser(path);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "RDF (Turtle, N3, TriG, TriX, RDF/XML)", "ttl", "rdf", "xml", "nt", "n3", "trig", "trix");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       return chooser.getSelectedFile();
	    }
	    else
	    	return null;
	}
	
	private File chooseSPARQLRuleFile() {
		JFileChooser chooser = new JFileChooser(path);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "SPARQL Rule", "rq", "query", "sparql");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       return chooser.getSelectedFile();
	    }
	    else
	    	return null;
	}


	public void update_model_visualisation() {
		jta_rdf.setText(controller.activeModel.get_trig());
		rdf_graph_panel.updateGraph(controller.activeModel);
	}


	public void update_rule_visualisation() {
		jta_rule.setText(controller.current_rule.transformationQuery);
		rule_graph_panel.updateGraph(controller.current_rule);
	}
	
}
