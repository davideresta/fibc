package dresta.fca;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class FuzzySet<T> {

	private HashMap<T, FuzzyDecimal> truthDegrees = new HashMap<T, FuzzyDecimal>();
	
	public FuzzySet() {
		
	}
	
	public FuzzySet(FuzzySet<T> oldFuzzySet) {
		truthDegrees = new HashMap<T, FuzzyDecimal>(oldFuzzySet.truthDegrees);
	}
	
	public void add(T element, FuzzyDecimal degree) {
		truthDegrees.put(element, degree);
	}
	
	public void remove(T element) {
		truthDegrees.remove(element);
	}
	
	public FuzzyDecimal getDegree(T element) {
		return truthDegrees.get(element);
	}
	
	public void setDegree(T element, FuzzyDecimal degree) {
		truthDegrees.put(element, degree);
	}
	
	public boolean contains(T element) {
		if(truthDegrees.containsKey(element) && truthDegrees.get(element).compareTo(FuzzyDecimal.ZERO) > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object aThat) {
		if(!(aThat instanceof FuzzySet<?>)) {
			return false;
		}
		FuzzySet<?> that = (FuzzySet<?>)aThat;
		return this.truthDegrees.equals(that.truthDegrees);
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	public String toString() {
		String result = "";
		
		ArrayList<String> sortedElementsStringList = new ArrayList<String>();
		ArrayList<String> elementsStringList = new ArrayList<String>();
		ArrayList<String> degreesStringList = new ArrayList<String>();
		for(T element: truthDegrees.keySet()) {
			sortedElementsStringList.add(element.toString());
			elementsStringList.add(element.toString());
			degreesStringList.add(truthDegrees.get(element).toString());
		}
		sortedElementsStringList.sort(Comparator.nullsFirst(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder())));
		for(String elementString: sortedElementsStringList) {
			if(result.equals("")) {
				result = degreesStringList.get(elementsStringList.indexOf(elementString)) + "/" + elementString;
			} else {
				result = result + ",  " + degreesStringList.get(elementsStringList.indexOf(elementString)) + "/" + elementString;
			}
		}
		result = "{" + result;
		result += "}";
		return result;
	}
	
}
