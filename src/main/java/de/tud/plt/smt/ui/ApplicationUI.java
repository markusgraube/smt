package de.tud.plt.smt.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
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
import javax.swing.tree.TreePath;

import de.tud.plt.smt.controller.Controller;
import de.tud.plt.smt.model.LDModel;


public class ApplicationUI {
	
	
	public String path = System.getProperty("user.dir")+"/src/main/resources/examples/CAE-HMI/models/";
	
	
	private JFrame mainFrame;
	private JTextArea jta_rdf;
	private JTextArea jta_rule;
	private JEditorPane editorPane; 
	private Controller controller;
	private GraphPanel graphPanel;

	public DefaultMutableTreeNode model_tree_node;
	public DefaultMutableTreeNode tree_node_rules;
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
		
		
		JToolBar vertical = new JToolBar(SwingConstants.HORIZONTAL);
        JButton b_add_model = new JButton("Add Model");
        JButton b_save_model = new JButton("Save Model");
        JButton b_load_model = new JButton("Load Model");
        JButton b_transform = new JButton("Transform");
        JButton b_save_rule = new JButton("Save Rule");
		JButton b_load_rule = new JButton("Load Rule");

		
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
				LDModel model = controller.models.get(path);
				controller.loadModeltoLHS(model);
				jta_rdf.setText(model.getTurtleRepresentation());
				graphPanel.updateGraph();
			}
		});
        b_transform.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.transform();
				
			}
		});
        b_load_rule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File rdf_file = chooseSPARQLRuleFile();
				controller.addRule(rdf_file);
				jta_rule.setText(controller.current_rule.transformationQuery);
				editorPane.setText(controller.current_rule.transformationQuery);
				tree_rules.updateUI();
			}
		});
        
        vertical.add(Box.createHorizontalGlue());
        vertical.add(b_add_model);
        vertical.add(b_save_model);
        vertical.add(b_load_model);
        vertical.add(Box.createHorizontalGlue());
        vertical.add(b_transform);
        vertical.add(Box.createHorizontalGlue());
		vertical.add(b_save_rule);
		vertical.add(b_load_rule);
		vertical.add(Box.createHorizontalGlue());

        
		addMenuBar();
		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		mainFrame.getContentPane().add(vertical, BorderLayout.NORTH);
		
		
		JPanel panel_main = new JPanel();
		mainFrame.getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel_model = new JPanel();
		panel_model.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_main.add(panel_model);
		
		
		JLabel lblNewLabel_1 = new JLabel("Model");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		tree_model = new JTree();
		model_tree_node = new DefaultMutableTreeNode("Models");
		tree_model.setModel(new DefaultTreeModel(model_tree_node));		
		tree_model.setExpandsSelectedPaths(true);
		
		panel_model.setLayout(new BorderLayout(0, 0));
		panel_model.add(lblNewLabel_1, BorderLayout.NORTH);
		panel_model.add(tree_model, BorderLayout.WEST);
		
		JPanel panel_current_model = new JPanel();
		panel_model.add(panel_current_model);
		panel_current_model.setLayout(new GridLayout(0, 1, 0, 0));
		
		graphPanel = new GraphPanel();
		panel_current_model.add(graphPanel);
		
		
		jta_rdf = new JTextArea();
		jta_rdf.setEditable(false);
		jta_rdf.setLineWrap(true);
		jta_rdf.setText("# RDF Output Area");
		
		JScrollPane scrp = new JScrollPane(jta_rdf,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel_current_model.add(scrp);
		scrp.setPreferredSize(new Dimension(300, 150));
		
		JPanel panel_rule = new JPanel();
		panel_main.add(panel_rule);
		panel_rule.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_2 = new JLabel("Rule");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_rule.add(lblNewLabel_2, BorderLayout.NORTH);
		
		tree_rules = new JTree();
		tree_rules.setSelectionRow(1);
		tree_node_rules = new DefaultMutableTreeNode("Rulesets");
		tree_rules.setModel(new DefaultTreeModel(tree_node_rules));
		tree_rules.expandPath(new TreePath(tree_node_rules.getPath()));
		panel_rule.add(tree_rules, BorderLayout.EAST);
		
		JPanel panel_current_rule = new JPanel();
		panel_rule.add(panel_current_rule, BorderLayout.CENTER);
		panel_current_rule.setLayout(new GridLayout(0, 1, 0, 0));
		
		editorPane = new JEditorPane();
		panel_current_rule.add(editorPane);
		
		jta_rule = new JTextArea();
		panel_current_rule.add(jta_rule);
		jta_rule.setEditable(false);
		jta_rule.setLineWrap(true);
		jta_rule.setText("# Rule Output Area");
		
		JPanel panel_statusbar = new JPanel();
		panel_statusbar.setBorder(new LineBorder(new Color(0, 0, 0)));
		mainFrame.getContentPane().add(panel_statusbar, BorderLayout.SOUTH);
		panel_statusbar.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNewLabel = new JLabel("Status: xyz");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel_statusbar.add(lblNewLabel);
		
		
		mainFrame.setVisible(true);
	}
	
	
	private void addMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		mnMenu.setMnemonic('M');
		menuBar.add(mnMenu);
		JMenuItem mntmLoadRDF = new JMenuItem("Load RDF Model");
		JMenuItem mntmSaveRDF = new JMenuItem("Save RDF Model");
		JMenuItem mntmExit = new JMenuItem("Exit");
		JMenuItem mntmLoadRule = new JMenuItem("Load Rule(s)");
		
		mntmLoadRDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File rdf_file = useFileChooser();
				controller.addModel(rdf_file);
				jta_rdf.setText(controller.lhs.getTurtleRepresentation());
				graphPanel.updateGraph();
			}
		});
		mntmLoadRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseSPARQLRuleFile();
			}
		});
		mntmSaveRDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.saveLHS("model_lhs.ttl");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		mnMenu.add(mntmLoadRDF);
		mnMenu.add(mntmSaveRDF);
		mnMenu.add(mntmLoadRule);
		mnMenu.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmProperties = new JMenuItem("Properties");
		mnEdit.add(mntmProperties);
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
}
