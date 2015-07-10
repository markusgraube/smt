/**
 * 
 */
package de.tud.plt.smt;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;

import de.tud.plt.smt.controller.DeclarativeRuleTransformator;
import junit.framework.TestCase;

/**
 * @author mgraube
 *
 */
public class TestDeclarativeRuleTransformator extends TestCase {

	private static DeclarativeRuleTransformator a;
	
	@BeforeClass
	public static void setUpBeforeClass(){
		
	}
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		File f =  new File("src/main/resources/examples/Class-Table/rules/declarative/01_Class2Table.rq");
		a = new DeclarativeRuleTransformator(f);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link de.tud.plt.smt.controller.DeclarativeRuleTransformator#DeclarativeRuleTransformator(java.lang.String)}.
	 * @throws IOException 
	 */
	public final void testDeclarativeRuleTransformator() throws IOException {
		a.generateOperationalRules("test/");
	}

	/**
	 * Test method for {@link de.tud.plt.smt.controller.DeclarativeRuleTransformator#transformToLeft2Right()}.
	 */
	public final void testTransformToLeft2Right() {
		a.transformToLeft2Right();
		
	}

	/**
	 * Test method for {@link de.tud.plt.smt.controller.DeclarativeRuleTransformator#transformToRight2Left()}.
	 */
	public final void testTransformToRight2Left() {
		a.transformToRight2Left();
	}

	/**
	 * Test method for {@link de.tud.plt.smt.controller.DeclarativeRuleTransformator#transformToCorrespondenceCheck()}.
	 */
	public final void testTransformToCorrespondenceCheck() {
		a.transformToCorrespondenceCheck();
	}

	/**
	 * Test method for {@link de.tud.plt.smt.controller.DeclarativeRuleTransformator#transformToEvolveBoth()}.
	 */
	public final void testTransformToEvolveBoth() {
		fail("Not yet implemented"); // TODO
	}

}
