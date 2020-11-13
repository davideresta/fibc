package dresta.fca.tests;

import java.util.HashSet;
import java.util.Set;

import dresta.fca.FuzzyDecimal;
import dresta.fca.FuzzyFormalContext;
import dresta.fca.FuzzySet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for FuzzyImplicationsBasisCalculator.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(AppTest.class);
    }

    /**
     * Some simple GD basis tests
     */
    public void testApp() {
        
    	Set<FuzzySet<String>> correctPis;
    	FuzzySet<String> fs1;
    	FuzzySet<String> fs2;
    	
    	FuzzyFormalContext ffcTest = new FuzzyFormalContext();
    	ffcTest.addItem("item1");
    	ffcTest.addItem("item2");
    	ffcTest.addItem("item3");
    	ffcTest.addItem("item4");
    	ffcTest.addAttribute("attr1");
    	ffcTest.addAttribute("attr2");
    	ffcTest.setRelationDegree("item1", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item1", "attr2", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item2", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item2", "attr2", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item3", "attr1", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item3", "attr2", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item4", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item4", "attr2", new FuzzyDecimal("0"));
    	correctPis = new HashSet<FuzzySet<String>>();
    	fs1 = new FuzzySet<String>();
    	fs1.add("attr1", FuzzyDecimal.ZERO);
    	fs1.add("attr2", FuzzyDecimal.ONE);
    	correctPis.add(fs1);
    	assertTrue(ffcTest.getPseudoIntentsSystemByBKAlgorithm().equals(correctPis));
    	
    	ffcTest = new FuzzyFormalContext();
    	ffcTest.addItem("item1");
    	ffcTest.addItem("item2");
    	ffcTest.addItem("item3");
    	ffcTest.addItem("item4");
    	ffcTest.addAttribute("attr1");
    	ffcTest.addAttribute("attr2");
    	ffcTest.setRelationDegree("item1", "attr1", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item1", "attr2", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item2", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item2", "attr2", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item3", "attr1", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item3", "attr2", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item4", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item4", "attr2", new FuzzyDecimal("1"));
    	correctPis = new HashSet<FuzzySet<String>>();
    	fs1 = new FuzzySet<String>();
    	fs1.add("attr1", FuzzyDecimal.ZERO);
    	fs1.add("attr2", FuzzyDecimal.ZERO);
    	correctPis.add(fs1);
    	assertTrue(ffcTest.getPseudoIntentsSystemByBKAlgorithm().equals(correctPis));
    	
    	ffcTest = new FuzzyFormalContext();
    	ffcTest.addItem("item1");
    	ffcTest.addItem("item2");
    	ffcTest.addItem("item3");
    	ffcTest.addItem("item4");
    	ffcTest.addAttribute("attr1");
    	ffcTest.addAttribute("attr2");
    	ffcTest.setRelationDegree("item1", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item1", "attr2", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item2", "attr1", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item2", "attr2", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item3", "attr1", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item3", "attr2", new FuzzyDecimal("0"));
    	ffcTest.setRelationDegree("item4", "attr1", new FuzzyDecimal("1"));
    	ffcTest.setRelationDegree("item4", "attr2", new FuzzyDecimal("1"));
    	correctPis = new HashSet<FuzzySet<String>>();
    	fs1 = new FuzzySet<String>();
    	fs1.add("attr1", FuzzyDecimal.ZERO);
    	fs1.add("attr2", FuzzyDecimal.ONE);
    	correctPis.add(fs1);
    	fs2 = new FuzzySet<String>();
    	fs2.add("attr1", FuzzyDecimal.ONE);
    	fs2.add("attr2", FuzzyDecimal.ZERO);
    	correctPis.add(fs2);
    	assertTrue(ffcTest.getPseudoIntentsSystemByBKAlgorithm().equals(correctPis));
    	
    }
}
