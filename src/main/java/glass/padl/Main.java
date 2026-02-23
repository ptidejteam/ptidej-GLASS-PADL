package glass.padl;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import glass.ast.IMethod;
import glass.ast.IProject;
import glass.ast.IType;
import glass.lattice.builder.ILatticeBuilder;
import glass.lattice.builder.LatticeBuilder;
import glass.lattice.metrics.IMetricCalculator;
import glass.lattice.metrics.impl.ProximityMetric;
import glass.lattice.metrics.impl.RatioMetric;
import glass.lattice.model.ILattice;
import glass.lattice.model.ILatticeNode;
import glass.lattice.model.IRelation;
import glass.lattice.model.IRelationBuilder;
import glass.lattice.model.impl.ExtendedRIRBuilder;
import glass.lattice.model.impl.ReverseInheritanceRelationBuilder;
import glass.lattice.model.impl.UsualRelationBuilder;
import glass.lattice.visitor.impl.AdhocFeatureDetectorVisitor;
import glass.lattice.visitor.impl.AdhocValidationVisitor;
import glass.lattice.visitor.impl.ComplexPurgeExtentsVisitor;
import glass.lattice.visitor.impl.ConceptCounter;
import glass.lattice.visitor.impl.FeatureDetectorVisitor;
import glass.lattice.visitor.impl.InheritanceBuilderVisitor;
import glass.lattice.visitor.impl.LatticePrettyPrinter;
import glass.lattice.visitor.impl.LatticePrinterGraphviz;
import glass.lattice.visitor.impl.PrintCandidatesVisitor;
import glass.padl.ast.PADLProject;
import glass.lattice.visitor.IVisitor;

public class Main 
{
    public static void main( String[] args )
    {
    	/*
    	try {
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output.txt"))));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	
        String filePath_test = "/home/luca/coding/java/eclipse-workspace/TestForGLASS/bin";
    	String filePath_padl = "/home/luca/coding/java/ptidej/ptidej-Ptidej/PADL/target/classes/";
    	String filePath_freemind = "/home/luca/coding/java/glass/featurediscovery/qualitativeEvaluation/data/input projects/FreeMind0.7.1/src/";
    	String filePath_JHotDraw = "/home/luca/coding/java/glass/featurediscovery/qualitativeEvaluation/data/input projects/JHotDraw5.2/";
    	String filePath_JReversePro = "/home/luca/coding/java/glass/featurediscovery/qualitativeEvaluation/data/input projects/JreversePro";
    	String filePath_Lucene = "/home/luca/coding/java/glass/featurediscovery/qualitativeEvaluation/data/input projects/Lucene1.4/bin";
    	String filePath_JavaWebMail = "/home/luca/coding/java/glass/featurediscovery/qualitativeEvaluation/data/input projects/javawebmail-0.7/bin";
    	String filePath_JHD_5_1 = "/home/luca/coding/java/glass/JHotDraw-v5.1/bin";
    	String filePath_JHD_7_5_1 = "/home/luca/coding/java/glass/jhotdraw7";
    	String filePath_JHD_5_3 = "/home/luca/coding/java/glass/JHotDraw_5_3/JHotDraw/";
    	String filePath_JHD_5_4b1 = "/home/luca/coding/java/glass/JHotDraw54b1";
    	String filePath_JHD_5_4b2 = "/home/luca/coding/java/glass/jhotdraw54b2";
    	String filePath_JHD_6_0b1 = "/home/luca/coding/java/glass/jhotdraw60b1";
    	String testJava = "/home/luca/coding/java/ptidej/ptidej-Ptidej/PADL/src/";
    	String projectName = "";
        IProject project = new PADLProject(filePath_padl);
        
        /*5.3
        IRelationBuilder relationBuilder = new UsualRelationBuilder();
        IRelation relation = relationBuilder.buildRelationFrom(project);
        System.out.println("Built relation!");
        ILatticeBuilder latticeBuilder  = new LatticeBuilder();
        ILattice lattice = latticeBuilder.buildLattice(relation);
        System.out.println("Built lattice!");
        IVisitor inheritanceVisitor = new InheritanceBuilderVisitor();
        lattice.acceptBottomVisitor(inheritanceVisitor);
        lattice.acceptTopVisitor(inheritanceVisitor);
        System.out.println("Built inheritance lattice!");
		LatticePrettyPrinter printer = LatticePrettyPrinter.javaElementsLatticePrettyPrinter();
		lattice.acceptTopVisitor(printer);
		*/

		IRelationBuilder relationBuilder = new ExtendedRIRBuilder();
		IRelation relation = relationBuilder.buildRelationFrom(project);
		//System.out.println("Done building relation!");
		//System.out.println("Printing the relation");
		//System.out.println(relation.printString());
		
		ILatticeBuilder latticeBuilder = new LatticeBuilder();
		ILattice lattice = latticeBuilder.buildLattice(relation);
		System.out.println("Done building lattice!");
		
		System.out.println("Creating inheritance lattice");
		InheritanceBuilderVisitor inheritanceLatticeVisitor = new InheritanceBuilderVisitor(lattice);
		lattice.acceptTopVisitor(inheritanceLatticeVisitor);
		ILattice inheritanceLattice = inheritanceLatticeVisitor.getInheritanceLattice();
		Map<ILatticeNode, ILatticeNode> originalToSimplified = inheritanceLatticeVisitor.getOriginalToCloneMapping();
		
		IVisitor adhocValidation = new AdhocValidationVisitor();
		inheritanceLattice.acceptTopVisitor(adhocValidation);
		
		LatticePrinterGraphviz lpg = new LatticePrinterGraphviz("test", false);
		System.out.println("Creating visualization");
		lattice.acceptTopVisitor(lpg);
		lpg.processResults();
		System.out.println("Done!");
		
		System.out.println("Creating visualization for inheritance lattice");
		LatticePrinterGraphviz lpg2 = new LatticePrinterGraphviz("testInheritance", false);
		inheritanceLattice.acceptTopVisitor(lpg2);
		lpg2.processResults();
		System.out.println("Visualization ready!");

		//System.out.println("Using complex purge");
		IVisitor purgeVisitor = new ComplexPurgeExtentsVisitor((ExtendedRIRBuilder) relationBuilder, originalToSimplified);
		lattice.acceptTopVisitor(purgeVisitor);
		//System.out.println("Printing lattice after purging extents");
		//lattice.acceptTopVisitor(printer);
		//System.out.println("Done printing lattice!");
		
		
		// 6.2 Second, extract candidate features
		AdhocFeatureDetectorVisitor featureDetector = new AdhocFeatureDetectorVisitor(lattice, originalToSimplified);
		lattice.acceptTopVisitor(featureDetector);
		
		ILattice featureSemiLattice = featureDetector.getFeatureSemiLattice();
		IMetricCalculator metricCalculator = new ProximityMetric();
		featureSemiLattice.acceptTopVisitor(metricCalculator);
		System.out.println("Creating visualization for adhoc features");
		LatticePrinterGraphviz lpg3 = new LatticePrinterGraphviz("testFeature", true);
		featureSemiLattice.acceptTopVisitor(lpg3);
		lpg3.processResultsFeature();
		System.out.println("Visualization ready!");
		
		ConceptCounter counter = new ConceptCounter();
		featureSemiLattice.acceptTopVisitor(counter);
		System.out.println("Number of adhoc features: " + counter.getCount());
		System.out.println("Number of classes: " + project.getDefinedTypes().size());
		float ratio = ((float)counter.getCount())/project.getDefinedTypes().size();
		System.out.println("Adhoc features per classes: " + ratio);

		/*
		// 6.3 Third, print candidate feature nodes
		PrintCandidatesVisitor printCandidatesVisitor = new PrintCandidatesVisitor(
				featureDetector.getCandidateFeatureNodes());
		System.out.println("Printing candidate nodes");
		lattice.acceptTopVisitor(printCandidatesVisitor);
		System.out.println("Done printing candidate nodes!");
		*/
		
		/*
		FileOutputStream fos;
		ObjectOutputStream oos;
		try {
			fos = new FileOutputStream(filePath_test.toString()+"/"+projectName+"_"+"lattice.ltc");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(printCandidatesVisitor.getNodes());
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
    }
    
}
