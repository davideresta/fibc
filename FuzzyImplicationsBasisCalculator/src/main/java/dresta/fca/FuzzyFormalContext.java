package dresta.fca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dresta.multithreading.MtUtils;

public class FuzzyFormalContext {
	
	public static final int BK_ALGORITHM = 0;
	public static final int DS_ALGORITHM = 1;
	public static final int KI_ALGORITHM = 2;
	public static final int KA_ALGORITHM = 3;
	
	private final ArrayList<String> items = new ArrayList<String>();
	private final ArrayList<String> attributes = new ArrayList<String>();
	private final FuzzySet<RelationPair<String, String>> relationDegrees = new FuzzySet<RelationPair<String,String>>();
	
	private Set<FuzzySet<String>> pseudoIntentsSystem = null;
	
	//These fields are created or updated by the preCompute() method and can be used only after this method has been called
	private final Set<FuzzySet<String>> V = new HashSet<FuzzySet<String>>();
	private HashMap<FuzzySet<String>, Set<FuzzySet<String>>> compGNeighbourhoodMapForElementsOfV = new HashMap<FuzzySet<String>, Set<FuzzySet<String>>>();
	private HashMap<FuzzySet<String>, Set<FuzzySet<String>>> predMapForElementsOfV = new HashMap<FuzzySet<String>, Set<FuzzySet<String>>>();
	private HashMap<FuzzySet<String>, FuzzySet<String>> intentClosuresMapForElementsOfV = new HashMap<FuzzySet<String>, FuzzySet<String>>();
	private FuzzySet<String> maxDegreeVertex = null;
	
	
	
	
		
	public FuzzyFormalContext() {
		
	}
	
	
	
	
	
	
	//section 1: methods that do not use precomputed fields. No problem
	
	synchronized public Iterable<String> getItems() {
		return items;
	}
	
	synchronized public void addItem(String item) {
		items.add(item);
		for(String attribute: attributes) {
			relationDegrees.add(new RelationPair<String, String>(item, attribute), FuzzyDecimal.ZERO);
		}
	}
	
	synchronized public void removeItem(String item) {
		items.remove(item);
		for(String attribute : attributes) {
			relationDegrees.remove(new RelationPair<String, String>(item, attribute));
		}
	}
	
	synchronized public Iterable<String> getAttributes() {
		return attributes;
	}
	
	synchronized public void addAttribute(String attribute) {
		attributes.add(attribute);
		for(String item: items) {
			relationDegrees.add(new RelationPair<String, String>(item, attribute), FuzzyDecimal.ZERO);
		}
	}
	
	synchronized public void removeAttribute(String attribute) {
		attributes.remove(attribute);
		for(String item : items) {
			relationDegrees.remove(new RelationPair<String, String>(item, attribute));
		}
	}
	
	synchronized public boolean containsItem(String item) {
		return items.contains(item);
	}
	
	synchronized public boolean containsAttribute(String attribute) {
		return attributes.contains(attribute);
	}
	
	synchronized public int getNumberOfItems() {
		return items.size();
	}
	
	synchronized public int getNumberOfAttributes() {
		return attributes.size();
	}
	
	synchronized public FuzzyDecimal getRelationDegree(String item, String attribute) {
		return relationDegrees.getDegree(new RelationPair<String, String>(item, attribute));
	}
	
	synchronized public void setRelationDegree(String item, String attribute, FuzzyDecimal degree) {
		if(items.contains(item) && attributes.contains(attribute)) {
			relationDegrees.setDegree(new RelationPair<String, String>(item, attribute), degree);
		}
	}
	
	
	synchronized public FuzzyDecimal getStep() {
		int maxExp = 0;
		for(String item : items) {
			for(String attribute : attributes) {
				FuzzyDecimal degree = relationDegrees.getDegree(new RelationPair<String, String>(item, attribute));
				if(degree.getNumberOfDecimalPlaces() > maxExp) {
					maxExp = degree.getNumberOfDecimalPlaces();
				}
			}
		}
		
		FuzzyDecimal gcd = new FuzzyDecimal(relationDegrees.getDegree(new RelationPair<String, String>(items.get(0), attributes.get(0))).multiply(new FuzzyDecimal("10").pow(maxExp)).toString());
		for(String item : items) {
			for(String attribute : attributes) {
				FuzzyDecimal degree = relationDegrees.getDegree(new RelationPair<String, String>(item, attribute));
				gcd = FuzzyUtils.gcd(gcd, new FuzzyDecimal(degree.multiply(new FuzzyDecimal("10").pow(maxExp)).toString()));
			}
		}
		gcd = FuzzyUtils.gcd(gcd, new FuzzyDecimal(FuzzyDecimal.ONE.multiply(new FuzzyDecimal("10").pow(maxExp)).toString()));
		
		return new FuzzyDecimal(gcd.divide(new FuzzyDecimal("10").pow(maxExp)).toString());
	}
	
	synchronized public String toString() {
		return relationDegrees.toString();
	}
	
	
	synchronized public FuzzyDecimal attrImplicationInM(FuzzySet<String> A, FuzzySet<String> B, FuzzySet<String> M) {
		return FuzzyUtils.imply(attrSubsethood(A,M), attrSubsethood(B,M));
	}
	
	synchronized public FuzzyDecimal attrSubsethood(FuzzySet<String> A, FuzzySet<String> B) {
		FuzzyDecimal min = FuzzyDecimal.ONE;
		for(int i=0; i<attributes.size(); i++) {
			FuzzyDecimal temp = FuzzyUtils.imply(A.getDegree(attributes.get(i)), B.getDegree(attributes.get(i)));
			min = FuzzyUtils.min(temp, min);
		}
		return min;
	}
	
	synchronized public FuzzySet<String> getIntentClosure(FuzzySet<String> A) {
		FuzzySet<String> A1 = new FuzzySet<String>();
		FuzzySet<String> A2 = new FuzzySet<String>();
		
		for(int r=0; r<items.size(); r++) {
			String item = items.get(r);
			FuzzyDecimal min = FuzzyDecimal.ONE;
			
			for(int c = 0; c < attributes.size(); c++) {
				String attribute = attributes.get(c);
				RelationPair<String, String> relationPair = new RelationPair<String, String>(item, attribute);
				FuzzyDecimal relationDegree = relationDegrees.getDegree(relationPair);
				FuzzyDecimal res = FuzzyUtils.imply(A.getDegree(attribute), relationDegree);
				min = FuzzyUtils.min(res, min);
			}
			
			A1.add(item, min);
		}
		
		for(int c = 0; c < attributes.size(); c++) {
			String attribute = attributes.get(c);
			FuzzyDecimal min = FuzzyDecimal.ONE;
			
			for(int r = 0; r < items.size(); r++) {
				String item = items.get(r);
				RelationPair<String, String> relationPair = new RelationPair<String, String>(item, attribute);
				FuzzyDecimal relationDegree = relationDegrees.getDegree(relationPair);
				
				FuzzyDecimal res = FuzzyUtils.imply(A1.getDegree(item), relationDegree);
				
				min = FuzzyUtils.min(res, min);
			}
			
			A2.add(attribute, min);
		}
		
		return A2;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// section 2: methods that build the precomputed fields (the set V, the preds of the elements in V, the intentClosures of the elements in V, the complementary graph of G)
	// methods that need to use the precomputed fields need to call the preCompute() method first
	
	synchronized private void preCompute() {
		MtUtils.mtPrintln("Step: " + getStep());
		
		MtUtils.mtPrintln("Computing V");
		
		//compute V
		V.clear();
		createVLoop(new FuzzySet<String>(), attributes.size(), getStep());
		
		MtUtils.mtPrintln("V computed. V size: " + V.size());
		MtUtils.mtPrintln("Computing compG, intentClosures and preds");
		
		//compute compG
		int maxDegree = 0;
		compGNeighbourhoodMapForElementsOfV.clear();
		for(FuzzySet<String> v: V) {
			if(Thread.currentThread().isInterrupted()) {
				return;
			}
			Set<FuzzySet<String>> VN = new HashSet<FuzzySet<String>>();
			for(FuzzySet<String> u: V) {
				if(!v.equals(u) && !(E(v,u) || E(u,v))) {
					VN.add(u);
				}
			}
			compGNeighbourhoodMapForElementsOfV.put(v, VN);
			
			if(VN.size() >= maxDegree) {
				maxDegreeVertex = v;
				maxDegree = VN.size();
			}
		}
		
		//compute intentClosures
		intentClosuresMapForElementsOfV.clear();
		for(FuzzySet<String> v: V) {
			if(Thread.currentThread().isInterrupted()) {
				return;
			}
			intentClosuresMapForElementsOfV.put(v, getIntentClosure(v));
		}
		
		//compute preds
		predMapForElementsOfV.clear();
		for(FuzzySet<String> v: V) {
			predMapForElementsOfV.put(v, predOfAnElementOfV(v));
		}
		
		//!!! Show precomputed infos
		/*if(!Thread.currentThread().isInterrupted()) {
			MtUtils.mtPrintln("V contains:");
			for(FuzzySet<String> v: V) {
				MtUtils.mtPrintln("\tv:" + v);
				MtUtils.mtPrintln("\t\tcompGNeighbourhood(v):" + compGNeighbourhoodMapForElementsOfV.get(v));
				MtUtils.mtPrintln("\t\tPred(v):" + predMapForElementsOfV.get(v));
				MtUtils.mtPrintln("\t\tintentClosure(v):" + intentClosuresMapForElementsOfV.get(v));
			}
		}*/
		
		MtUtils.mtPrintln("compG, intentClosures and preds computed");
	}
	
	synchronized private void createVLoop(FuzzySet<String> currentSet, int n, FuzzyDecimal step) {
		if(Thread.currentThread().isInterrupted()) {
			return;
		}
		for(FuzzyDecimal i=FuzzyDecimal.ZERO; i.compareTo(FuzzyDecimal.ONE)<=0; i=i.add(step)) {
			if(n>0) {
				FuzzySet<String> newCurrentSet = new FuzzySet<String>(currentSet);
				newCurrentSet.add(attributes.get(n-1), i);
				createVLoop(newCurrentSet, n-1, step);
			}
			else if(n==0) {
				FuzzySet<String> A2 = getIntentClosure(currentSet);
				if(!currentSet.equals(A2)) {
					V.add(currentSet);
				}
			}
		}
	}
	
	synchronized private boolean E(FuzzySet<String> P, FuzzySet<String> Q) {
		if(P.equals(Q) || !V.contains(P) || !V.contains(Q)) {
			return false;
		}
		else if(!attrImplicationInM(Q, getIntentClosure(Q), P).equals(FuzzyDecimal.ONE)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	synchronized private Set<FuzzySet<String>> predOfAnElementOfV(FuzzySet<String> Q) {
		if(!V.contains(Q)) {
			return null;
		}
		else {
			Set<FuzzySet<String>> result = new HashSet<FuzzySet<String>>();
			for(FuzzySet<String> P: V) {
				if(E(P,Q)) {
					result.add(P);
				}
			}
			return result;
		}
	}
	

	
	
	
	
	
	
	
	
	
	// section 3: methods that use precomputed fields
	// these methods can be called only after the preCompute() method has been called
	
	synchronized private Set<FuzzySet<String>> getPreComputedCompGNeighbourhoodOfAnElementOfV(FuzzySet<String> P) throws IllegalArgumentException {
		Set<FuzzySet<String>> compGNeighbourhoodOfP = compGNeighbourhoodMapForElementsOfV.get(P);
		if(compGNeighbourhoodOfP == null) {
			throw new IllegalArgumentException();
		}
		return compGNeighbourhoodOfP;
	}
	
	synchronized private Set<FuzzySet<String>> getPreComputedPredOfASubsetOfV(Set<FuzzySet<String>> P) throws IllegalArgumentException {
		Set<FuzzySet<String>> result = new HashSet<FuzzySet<String>>();
		for(FuzzySet<String> Q: P) {
			Set<FuzzySet<String>> predQ = predMapForElementsOfV.get(Q);
			if(predQ == null) {
				throw new IllegalArgumentException();
			}
			result.addAll(predQ);
		}
		return result;
	}
	
	synchronized private FuzzySet<String> getPreComputedIntentClosureOfAnElementOfV(FuzzySet<String> P) throws IllegalArgumentException {
		FuzzySet<String> intentClosureOfP = intentClosuresMapForElementsOfV.get(P);
		if(intentClosureOfP == null) {
			throw new IllegalArgumentException();
		}
		return intentClosureOfP;
	}
	
	synchronized private boolean isPseudoIntentsSystem(Set<FuzzySet<String>> set) {
		//weak test
		for(FuzzySet<String> P: set) {
			for(FuzzySet<String> Q: set) {
				if(!P.equals(Q)) {
					try {
						if(attrImplicationInM(Q, getPreComputedIntentClosureOfAnElementOfV(Q), P).compareTo(FuzzyDecimal.ONE)<0) {
							return false;
						}
					} catch(IllegalArgumentException iae) {
						return false;
					}
   				}
   			}
   		}
		
		//strong test
		Set<FuzzySet<String>> predSet;
		try {
			predSet = getPreComputedPredOfASubsetOfV(set);
		} catch(IllegalArgumentException iae) {
			return false;
		}
		for(FuzzySet<String> v: V) {
			if(!set.contains(v)) {
				//if v is in V-set
				if(!predSet.contains(v)) {
					return false;
				}
			} else {
				//if v is not in V-set
				if(predSet.contains(v)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	
	
	
	
	
	// section 4: methods that use the methods of section 3.
	// these methods have the responsibility to call the preCompute() method before using the section 3 methods.
	// these methods' purpose is essentially the computation of a pseudointents system and a Guigues-Duquenne basis.
	
	synchronized public Set<String> getGuiguesDuquenneBasis(int algorithm) {
		long startTime = System.currentTimeMillis();
		
		Set<FuzzySet<String>> pseudoIntentsSystem = null;
		
		if(algorithm == BK_ALGORITHM) {
			//BK algorithm
			System.out.println("Algorithm: BronKerbosch");
			pseudoIntentsSystem = getPseudoIntentsSystemByBKAlgorithm();
		} else if(algorithm == DS_ALGORITHM) {
			//DS algorithm
			System.out.println("Algorithm: DoubleStep");
			if(FuzzyUtils.isValidStep(new FuzzyDecimal(getStep().multiply(new FuzzyDecimal("2")).toString()))) {
				pseudoIntentsSystem = getPseudoIntentsSystemByDSAlgorithm();
			} else {
				System.out.println("DoubleStep algorithm is not applicable");
			}
		} else if(algorithm == KI_ALGORITHM) {
			//KI algorithm
			System.out.println("Algorithm: KillItems");
			pseudoIntentsSystem = getApproximatePseudoIntentsSystemByKIAlgorithm();
		} else if(algorithm == KA_ALGORITHM) {
			//KA algorithm
			System.out.println("Algorithm: KillAttributes");
			pseudoIntentsSystem = getApproximatePseudoIntentsSystemByKAAlgorithm();
		}
		
		long endTime = System.currentTimeMillis();
		MtUtils.mtPrintln("Operation completed in: " + (endTime - startTime) + " millisecs");
		
		Set<String> guiguesDuquenneBasis = new HashSet<String>();
		for(FuzzySet<String> P: pseudoIntentsSystem) {
			guiguesDuquenneBasis.add(P.toString() + " -> " + getIntentClosure(P));
		}
		return guiguesDuquenneBasis;
	}
	
	
	
	
	/**This method finds a pseudointents system using BronKerbosch algorithm.
	 * Specifically, BronKerbosch algorithm is used to find all the maximal cliques of the complementary graph of G.
	 * If a clique is found that is also a pseudointents system, the algorithm stops.
	 * @return a pseudointents system for this fuzzy formal context
	 */
	synchronized public Set<FuzzySet<String>> getPseudoIntentsSystemByBKAlgorithm() {
		preCompute();
		pseudoIntentsSystem = null;
		BronKerbosch(new HashSet<FuzzySet<String>>(), new HashSet<FuzzySet<String>>(V), new HashSet<FuzzySet<String>>(), 0);
		return pseudoIntentsSystem;
	}
	
	synchronized private void BronKerbosch(Set<FuzzySet<String>> R, Set<FuzzySet<String>> P, Set<FuzzySet<String>> X, int depth) {
		if(pseudoIntentsSystem != null || Thread.currentThread().isInterrupted()) {
			return;
		}
		if(P.isEmpty() && X.isEmpty()) {
			
			if(isPseudoIntentsSystem(R)) {
				pseudoIntentsSystem = R;
			}
			return;
		}
		
		Set<FuzzySet<String>> tempP = new HashSet<FuzzySet<String>>(P);
		tempP.addAll(X);
		FuzzySet<String> u = chooseU(tempP, P);
		
		Set<FuzzySet<String>> iterSet = new HashSet<FuzzySet<String>>(P);
		iterSet.removeAll(getPreComputedCompGNeighbourhoodOfAnElementOfV(u));
		int counter = 1;
		for(FuzzySet<String> v : iterSet) {
			if(depth==0) {
				MtUtils.mtPrintln("BK iteration " + counter++ + " of " + iterSet.size());
			}
			
			if(pseudoIntentsSystem != null) {
				return;
			}
			Set<FuzzySet<String>> arg1 = new HashSet<FuzzySet<String>>(R);
			arg1.add(v);
			
			Set<FuzzySet<String>> arg2 = new HashSet<FuzzySet<String>>(P);
			arg2.retainAll(getPreComputedCompGNeighbourhoodOfAnElementOfV(v));
			
			Set<FuzzySet<String>> arg3 = new HashSet<FuzzySet<String>>(X);
			arg3.retainAll(getPreComputedCompGNeighbourhoodOfAnElementOfV(v));
			BronKerbosch(arg1, arg2, arg3, depth+1);
			P.remove(v);
			X.add(v);
		}
	}
	
	synchronized private FuzzySet<String> chooseU(Set<FuzzySet<String>> tempP, Set<FuzzySet<String>> P) {
		if(tempP.equals(V) && P.equals(V)) {
			return maxDegreeVertex;
		}
		
		FuzzySet<String> u = null;
		int minSize = getPreComputedCompGNeighbourhoodOfAnElementOfV(maxDegreeVertex).size();
		for(FuzzySet<String> v : tempP) {
			Set<FuzzySet<String>> iterSet = new HashSet<FuzzySet<String>>(P);
			iterSet.removeAll(getPreComputedCompGNeighbourhoodOfAnElementOfV(v));
			if(iterSet.size()<=minSize) {
				minSize = iterSet.size();
				u = v;
			}
		}
		return u;
	}
	
	
	
	/** This method finds a pseudointents system using DoubleStep algorithm.
	 * Specifically, a new simplified fuzzy formal context is created by doubling the step of this context
	 * and adjusting the values properly if needed. BronKerbosch algorithm is used to find a
	 * pseudointents system of the simplified context, then all the sets of fuzzy sets of attributes that are "similar"
	 * to this simplified pseudointents system are considered in order to find a pseudointents system for this context.
	 * @return a pseudointents system for this fuzzy formal context, or null if the DS algorithm fails
	 */
	
	synchronized public Set<FuzzySet<String>> getPseudoIntentsSystemByDSAlgorithm() {
		
		preCompute();
		pseudoIntentsSystem = null;
		
		FuzzyDecimal step = getStep();
		FuzzyDecimal newStep = new FuzzyDecimal(step.multiply(new FuzzyDecimal("2")).toString());
		
		//creo newffc
		FuzzyFormalContext newFfc = new FuzzyFormalContext();
		for(String item: items) {
			newFfc.addItem(item);
		}
		for(String attribute: attributes) {
			newFfc.addAttribute(attribute);
		}
		for(String item: items) {
			for(String attribute: attributes) {
				FuzzyDecimal degree = getRelationDegree(item, attribute);
				FuzzyDecimal newDegree = null;
				if(FuzzyUtils.isValidDegreeIfStepIs(degree, newStep)) {
					newDegree = degree;
				} else {
					newDegree = degree.add(step);
				}
				newFfc.setRelationDegree(item, attribute, newDegree);
			}
		}
		
		MtUtils.mtPrintln("Looking for a pseudointents system of the simplified context");
		
		//compute a pseudoIntentsSystem for newFfc
		Set<FuzzySet<String>> newPis = newFfc.getPseudoIntentsSystemByBKAlgorithm();
		
		
		MtUtils.mtPrintln("Pseudointents system of the simplified context found");
		MtUtils.mtPrintln("Pseudointents system of the simplified context: " + newPis);
		MtUtils.mtPrintln("Checking the sets of fuzzy sets of attributes that are similar to this simplified pseudointents system");
		
		try {
			//check all the sets of fuzzy sets of attributes "similar to" newPis
			Set<RelationPair<FuzzySet<String>, String>> alreadyCheckedSet = new HashSet<RelationPair<FuzzySet<String>, String>>();
			checkedCounter = 0;
			checkPisCandidates(newPis, newPis, alreadyCheckedSet, step);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pseudoIntentsSystem;
	}
	
	long checkedCounter = 0;
	
	synchronized private void checkPisCandidates(Set<FuzzySet<String>> originalSet, Set<FuzzySet<String>> currentSet, Set<RelationPair<FuzzySet<String>, String>> alreadyCheckedSet, FuzzyDecimal step) {
		if(currentSet.size()*attributes.size() == alreadyCheckedSet.size()) {
			MtUtils.mtPrintln("Check DS pis candidate " + checkedCounter++ + " of " + Math.pow(3d, currentSet.size()*attributes.size()));
			if(isPseudoIntentsSystem(currentSet)) {
				MtUtils.mtPrintln("Pseudointents system found!!!  " + currentSet.toString());
				
				pseudoIntentsSystem = currentSet;
			}
			return;
		}
		for(FuzzySet<String> fs: currentSet) {
			for(String attribute: attributes) {
				if(!alreadyCheckedSet.contains(new RelationPair<FuzzySet<String>, String>(fs, attribute))) {
					for(int i=-1; i<=1; i++) {
						Set<RelationPair<FuzzySet<String>, String>> newAlreadyCheckedSet = new HashSet<RelationPair<FuzzySet<String>, String>>(alreadyCheckedSet);
						//create modified set newSet
						Set<FuzzySet<String>> newSet = new HashSet<FuzzySet<String>>();
						for(FuzzySet<String> fs2: currentSet) {
							if(fs2.equals(fs)) {
								FuzzySet<String> newFs = new FuzzySet<String>();
								for(String attribute2: attributes) {
									FuzzyDecimal degree = fs.getDegree(attribute2);
									if(attribute2.equals(attribute) && !(i==-1 && degree.equals(FuzzyDecimal.ZERO)) && !(i==1 && degree.equals(FuzzyDecimal.ONE))) {
										newFs.add(attribute2, new FuzzyDecimal(degree.add(step.multiply(new FuzzyDecimal(Integer.toString(i)))).toString()));
									} else {
										newFs.add(attribute2, degree);
									}
								}
								
								Set<RelationPair<FuzzySet<String>, String>> newAlreadyCheckedSetIter = new HashSet<RelationPair<FuzzySet<String>, String>>(newAlreadyCheckedSet);
								for(RelationPair<FuzzySet<String>, String> pair: newAlreadyCheckedSetIter) {
									if(pair.getFirstElement().equals(fs)) {
										newAlreadyCheckedSet.remove(pair);
										newAlreadyCheckedSet.add(new RelationPair<FuzzySet<String>, String>(newFs, pair.getSecondElement()));
									}
								}
								newAlreadyCheckedSet.add(new RelationPair<FuzzySet<String>, String>(newFs, attribute));
								
								newSet.add(newFs);
							} else {
								newSet.add(fs2);
							}
						}
						//recursion
						checkPisCandidates(originalSet, newSet, newAlreadyCheckedSet, step);
						if(pseudoIntentsSystem != null || Thread.currentThread().isInterrupted()) {
							return;
						}
					}
					return;
				}
			}
		}
	}
	
	
	
	/**
	 * This method finds an approximate pseudointents system using KillItems algorithm.
	 * Specifically, it computes a pseudointents system of the fuzzy formal context created by setting to zero the
	 * non maximum relation values relatively to each item. Depending on the context, the result can be very different
	 * from an actual pseudointents system of this fuzzy formal context.
	 * @return An approximate pseudointents system for this fuzzy formal context.
	 */
	synchronized public Set<FuzzySet<String>> getApproximatePseudoIntentsSystemByKIAlgorithm() {
		FuzzyFormalContext newFfc = new FuzzyFormalContext();
		for(String item: items) {
			newFfc.addItem(item);
		}
		for(String attribute: attributes) {
			newFfc.addAttribute(attribute);
		}
		
		for(String item: items) {
			FuzzyDecimal maxDegree = FuzzyDecimal.ZERO;
			for(String attribute: attributes) {
				if(getRelationDegree(item, attribute).compareTo(maxDegree)>0) {
					maxDegree = getRelationDegree(item, attribute);
				}
			}
			
			for(String attribute: attributes) {
				FuzzyDecimal degree = getRelationDegree(item, attribute);
				FuzzyDecimal newDegree = degree;
				
				if(!degree.equals(maxDegree)) {
					newDegree = FuzzyDecimal.ZERO;
				}
				
				newFfc.setRelationDegree(item, attribute, newDegree);
			}
		}
		return newFfc.getPseudoIntentsSystemByBKAlgorithm();
	}
	
	
	
	
	/**
	 * This method finds an approximate pseudointents system using KillAttributes algorithm.
	 * Specifically, it computes a pseudointents system of the fuzzy formal context created by setting to zero the
	 * non maximum relation values relatively to each attribute. Depending on the context, the result can be very different
	 * from an actual pseudointents system of this fuzzy formal context.
	 * @return An approximate pseudointents system for this fuzzy formal context.
	 */
	synchronized public Set<FuzzySet<String>> getApproximatePseudoIntentsSystemByKAAlgorithm() {
		FuzzyFormalContext newFfc = new FuzzyFormalContext();
		for(String item: items) {
			newFfc.addItem(item);
		}
		for(String attribute: attributes) {
			newFfc.addAttribute(attribute);
		}
		
		for(String attribute: attributes) {
			FuzzyDecimal maxDegree = FuzzyDecimal.ZERO;
			for(String item: items) {
				if(getRelationDegree(item, attribute).compareTo(maxDegree)>0) {
					maxDegree = getRelationDegree(item, attribute);
				}
			}
			
			for(String item: items) {
				FuzzyDecimal degree = getRelationDegree(item, attribute);
				FuzzyDecimal newDegree = degree;
				
				if(!degree.equals(maxDegree)) {
					newDegree = FuzzyDecimal.ZERO;
				}
				
				newFfc.setRelationDegree(item, attribute, newDegree);
			}
		}
		return newFfc.getPseudoIntentsSystemByBKAlgorithm();
	}
	
}
