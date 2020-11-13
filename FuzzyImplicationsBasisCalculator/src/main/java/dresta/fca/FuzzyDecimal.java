package dresta.fca;

import java.math.BigDecimal;

public class FuzzyDecimal extends BigDecimal {
	
	private static final long serialVersionUID = 3982022745206622205L;
	
	public static FuzzyDecimal ZERO = new FuzzyDecimal("0");
	public static FuzzyDecimal ONE = new FuzzyDecimal("1");
	
	public FuzzyDecimal(String value) {
		super(value);
	}
	
	public FuzzyDecimal(int value) {
		super(value);
	}
	
	@Override
	public int hashCode() {
		return 3;
	}
	
	@Override
	public boolean equals(Object aThat) {
		if(!(aThat instanceof FuzzyDecimal)) {
			return false;
		}
		FuzzyDecimal that = (FuzzyDecimal)aThat;
		return this.compareTo(that)==0;
	}
	
	public FuzzyDecimal add(FuzzyDecimal other) {
		return new FuzzyDecimal(super.add(other).toString());
	}
	
	public FuzzyDecimal subtract(FuzzyDecimal other) {
		return new FuzzyDecimal(super.subtract(other).toString());
	}
	
	public FuzzyDecimal multiply(FuzzyDecimal other) {
		return new FuzzyDecimal(super.multiply(other).toString());
	}
	
	public FuzzyDecimal negate() {
		return new FuzzyDecimal(super.negate().toString());
	}
	
	public int getNumberOfDecimalPlaces() {
	    return Math.max(0, stripTrailingZeros().scale());
	}
	
}
