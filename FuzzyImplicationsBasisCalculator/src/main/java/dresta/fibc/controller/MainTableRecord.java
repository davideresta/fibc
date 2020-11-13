package dresta.fibc.controller;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;

public class MainTableRecord {
    private final SimpleStringProperty item;
    private final ArrayList<SimpleStringProperty> values;
    
    public MainTableRecord(String item) {
        this.item = new SimpleStringProperty(item);
        this.values = new ArrayList<>();
    }
    
    public String getItem() { return item.get(); }
    
    public void addValue(String val) {
        values.add(new SimpleStringProperty(val));
    }
    
    public SimpleStringProperty getValue(int index) { return values.get(index); }
    
    public void setValue(int index, String newVal) {
        values.get(index).set(newVal);
    }
}
