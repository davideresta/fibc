package dresta.fibc.view;

import dresta.fca.FuzzyDecimal;
import dresta.fibc.controller.MainController;
import dresta.fibc.controller.MainTableRecord;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class FuzzyTableCell extends TextFieldTableCell<MainTableRecord, String> {

	private MainController mainController;
	private String attribute;
	
	public FuzzyTableCell(MainController mainController, String attribute) {
		super(new DefaultStringConverter());
		
		this.mainController = mainController;
		this.attribute = attribute;
	}
	
	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		
		try {
			mainController.updateRelationDegree(getTableRow().getItem().getItem(), attribute, item);
		} catch(Exception e) {
			
		}
	}
	
}

class FuzzyDecimalStringConverter extends StringConverter<FuzzyDecimal> {
	
	@Override
	public FuzzyDecimal fromString(String arg0) {
		return new FuzzyDecimal(arg0);
	}

	@Override
	public String toString(FuzzyDecimal arg0) {
		return arg0.toString();
	}
	
}
