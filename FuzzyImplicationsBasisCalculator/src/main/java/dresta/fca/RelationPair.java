package dresta.fca;

public class RelationPair<T, U> {

	T firstElement;
	U secondElement;
	String relationName = "R";
	
	public RelationPair(T firstElement, U secondElement) {
		this.firstElement = firstElement;
		this.secondElement = secondElement;
	}
	
	public RelationPair(T firstElement, U secondElement, String relationName) {
		this.firstElement = firstElement;
		this.secondElement = secondElement;
		this.relationName = relationName;
	}
	
	public T getFirstElement() {
		return firstElement;
	}
	
	public U getSecondElement() {
		return secondElement;
	}
	
	public String getRelationName() {
		return relationName;
	}
	
	@Override
	public boolean equals(Object aThat) {
		if(this == aThat) return true;
	    if(aThat instanceof RelationPair<?,?>) {
	    	RelationPair that = (RelationPair)aThat;
	    	return this.firstElement.equals(that.firstElement) && this.secondElement.equals(that.secondElement);
	    }
	    else return false;
	 }
	
	@Override
	public int hashCode() {
	    return 2;
	}
	
	public String toString() {
		return firstElement.toString() + " " + relationName + " " + secondElement.toString();
	}
	
}
