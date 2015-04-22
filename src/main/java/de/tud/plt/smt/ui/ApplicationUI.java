package de.tud.plt.smt.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ApplicationUI {
	
	private JFrame mainFrame;

	/**
	 * Create the application UI
	 */
	public ApplicationUI() {
		mainFrame = new JFrame();
		mainFrame.setTitle("SPARQL Model Transformator");
		mainFrame.setBounds(100, 100, 1116, 862);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		addMenuBar();
		
		GraphPanel graphPanel = new GraphPanel();
		
		mainFrame.add(graphPanel);
		mainFrame.setVisible(true);
	}
	
	
	private void addMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		
		JMenuItem mntmLoadRule = new JMenuItem("Load Rule(s)");
		JMenuItem mntmLoadRDF = new JMenuItem("Load RDF Model");
		JMenuItem mntmSaveRDF = new JMenuItem("Save RDF Model");
		JMenuItem mntmExit = new JMenuItem("Exit");
		
		
		mntmLoadRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseSPARQLRuleFile();
			}
		});
		
		mntmLoadRDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				useFileChooser();
			}
		});
		
		
		mnMenu.add(mntmLoadRule);
		mnMenu.add(mntmLoadRDF);
		mnMenu.add(mntmSaveRDF);
		mnMenu.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmProperties = new JMenuItem("Properties");
		mnEdit.add(mntmProperties);
	}
	
	private File useFileChooser() {
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "RDF (Turtle, N3, TriG, TriX, RDF/XML)", "ttl", "rdf", "xml", "nt", "n3", "trig", "trix");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       return chooser.getSelectedFile();
	    }
	    else
	    	return null;
	}
	
	private File chooseSPARQLRuleFile() {
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "SPARQL Rule", "rq", "query", "sparql");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       return chooser.getSelectedFile();
	    }
	    else
	    	return null;
	}

}
