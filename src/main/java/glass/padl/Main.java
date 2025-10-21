package glass.padl;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import glass.ast.IMethod;
import glass.ast.IProject;
import glass.ast.IType;
import glass.lattice.builder.ILatticeBuilder;
import glass.lattice.builder.LatticeBuilder;
import glass.lattice.model.ILattice;
import glass.lattice.model.IRelation;
import glass.lattice.model.IRelationBuilder;
import glass.lattice.model.impl.ExtendedRIRBuilder;
import glass.lattice.model.impl.ReverseInheritanceRelationBuilder;
import glass.lattice.model.impl.UsualRelationBuilder;
import glass.lattice.visitor.impl.AdhocFeatureDetectorVisitor;
import glass.lattice.visitor.impl.AdhocValidationVisitor;
import glass.lattice.visitor.impl.ComplexPurgeExtentsVisitor;
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
		
    	// TO-DO: create test within this project
    	
    	
        //String filePath_test = "../../eclipse-workspace/testFeature2/bin/";
    	String filePath_padl = "../ptidej-Ptidej/PADL/target/classes/";
        String projectName = "aa";
        IProject project = new PADLProject(filePath_padl);
        
        /*
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
		IVisitor purgeVisitor = new ComplexPurgeExtentsVisitor((ExtendedRIRBuilder) relationBuilder);
		lattice.acceptTopVisitor(purgeVisitor);
		//System.out.println("Printing lattice after purging extents");
		//lattice.acceptTopVisitor(printer);
		//System.out.println("Done printing lattice!");
		
		
		// 6.2 Second, extract candidate features
		AdhocFeatureDetectorVisitor featureDetector = new AdhocFeatureDetectorVisitor(lattice);
		lattice.acceptTopVisitor(featureDetector);
		
		ILattice featureSemiLattice = featureDetector.getFeatureSemiLattice();
		System.out.println("Creating visualization for adhoc features");
		LatticePrinterGraphviz lpg3 = new LatticePrinterGraphviz("testFeature", true);
		featureSemiLattice.acceptTopVisitor(lpg3);
		lpg3.processResults();
		System.out.println("Visualization ready!");

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
