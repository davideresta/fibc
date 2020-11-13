package dresta.fca;

public class FuzzyUtils {

	public static FuzzyDecimal globalize(FuzzyDecimal a) {
		if(a.equals(FuzzyDecimal.ONE)) {
			return FuzzyDecimal.ONE;
		}
		else {
			return FuzzyDecimal.ZERO;
		}
	}
	
	public static FuzzyDecimal min(FuzzyDecimal a, FuzzyDecimal b) {
		if(a.compareTo(b)<=0) {
			return a;
		}
		else {
			return b;
		}
	}
	
	public static FuzzyDecimal imply(FuzzyDecimal a, FuzzyDecimal b) {
		return min(FuzzyDecimal.ONE, (FuzzyDecimal.ONE).subtract(a).add(b));
    }
	
	public static FuzzyDecimal gcd(FuzzyDecimal a, FuzzyDecimal b) {
		if(b.equals(FuzzyDecimal.ZERO)) {
			return a;
		}
		return gcd(b, new FuzzyDecimal(a.divideAndRemainder(b)[1].toString()));
	}
	
	public static boolean isValidDegreeIfStepIs(FuzzyDecimal degree, FuzzyDecimal step) {
		for(FuzzyDecimal i=FuzzyDecimal.ZERO; i.compareTo(FuzzyDecimal.ONE)<=0; i=i.add(step)) {
			if(i.equals(degree)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidStep(FuzzyDecimal step) {
		if(step.compareTo(FuzzyDecimal.ZERO)<=0 || step.compareTo(FuzzyDecimal.ONE)>0) {
			return false;
		}
		
		FuzzyDecimal i;
		for(i=FuzzyDecimal.ZERO; i.compareTo(FuzzyDecimal.ONE)<0; i=i.add(step)) {
			
		}
		return i.equals(FuzzyDecimal.ONE);
	}
	
}
