package dresta.fibc.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;

import dresta.fca.FuzzyDecimal;
import dresta.fca.FuzzyFormalContext;
import dresta.fca.FuzzySet;
import dresta.fibc.view.FuzzyTableCell;
import dresta.fibc.view.MainApp;

public class MainController {
    
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private TableView<MainTableRecord> tableView;
    @FXML
    private TextFlow implicationSets;
    @FXML
    private ComboBox<String> cbMode;
    
    private FuzzyFormalContext ffc;
    
    public MainController() {
    	
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	cbMode.setItems(FXCollections.observableArrayList("BK algorithm", "DS algorithm", "KI algorithm (approx)", "KA algorithm (approx)"));
    	cbMode.setValue("BK algorithm");
    	
    	tableView.getSelectionModel().setCellSelectionEnabled(true);
        initFfc();
        refreshTableView();
    }
    
    public void initFfc() {
        ffc = new FuzzyFormalContext();
        ffc.addItem("item1");
        ffc.addItem("item2");
        ffc.addItem("item3");
        ffc.addAttribute("attr1");
        ffc.addAttribute("attr2");
        ffc.addAttribute("attr3");
    }
    
    public void newFfc() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Wait");
        alert.setHeaderText("Are you sure you want to create a new table?\n"
                + "The current table will be lost.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeSave, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == buttonTypeYes) {
            initFfc();
            refreshTableView();
        } else if (result.get() == buttonTypeSave) {
            saveAs();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    
    public void saveAs() {
        Stage stage = (Stage)mainAnchorPane.getScene().getWindow();
        
        FileChooser.ExtensionFilter filter;
        filter = new FileChooser.ExtensionFilter("File Excel (*.xlsx)", "*.xlsx");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialFileName("Prova");
        fileChooser.setInitialDirectory(new File("."));
        
        File file = fileChooser.showSaveDialog(stage);
        
        if(file == null)
            return;
        
        ExcelManager.write(ffc, file.getAbsolutePath());
    }
    
    public void tableFromFile() {
        Stage stage = (Stage)mainAnchorPane.getScene().getWindow();
        
        FileChooser.ExtensionFilter filter;
        filter = new FileChooser.ExtensionFilter("File Excel (*.xlsx)", "*.xlsx");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialDirectory(new File("."));
        
        File file = fileChooser.showOpenDialog(stage);
        
        if (file == null)
            return;
        
        ffc = new FuzzyFormalContext();
        ExcelManager.read(ffc, file.getAbsolutePath());
        
        refreshTableView();
    }
    
    public void closeApp() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Wait");
        alert.setHeaderText("Are you sure you want to exit?\n"
                + "The current table will be lost.");

        ButtonType buttonTypeExit = new ButtonType("Exit");
        ButtonType buttonTypeSave = new ButtonType("Save and exit");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeExit, buttonTypeSave, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == buttonTypeExit) {
            Platform.exit();
            System.exit(0);
        } else if (result.get() == buttonTypeSave) {
            saveAs();
            Platform.exit();
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    
    public void addItem() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New item");
        dialog.setHeaderText("Insert item name");
        dialog.setContentText("Item name:");
        
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            String item = result.get().trim();
            
            if(item.isEmpty()) {
                displayError("Please insert a valid name!");
                return;
            } else if(ffc.containsItem(item)) {
                displayError("Item " + item + " already exists");
                return;
            }
            
            MainTableRecord r = new MainTableRecord(item);
        
            for(int i=0; i<ffc.getNumberOfAttributes()-1; ++i)
                r.addValue(" ");

            ffc.addItem(item);
            refreshTableView();
        }
    }
    
    public void addAttribute() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New attribute");
        dialog.setHeaderText("Insert attribute name");
        dialog.setContentText("Attribute name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            String attribute = result.get().trim();
            
            if(attribute.isEmpty()) {
                displayError("Pleas insert a valid name!");
                return;
            }
            else if(ffc.containsAttribute(attribute)) {
                displayError("Attribute " + attribute + " already exists");
                return;
            }
            
            ffc.addAttribute(attribute);
            refreshTableView();
        }
    }
    
    public void removeItem() {
        if(ffc.getNumberOfItems() == 1) {
            displayError("There is only this item left, you cannot remove it.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Remove item");
        dialog.setHeaderText("Insert item name");
        dialog.setContentText("Item name:");

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            String item = result.get().trim();
            
            if(!ffc.containsItem(item)) {
                displayError("Item " + item + " not found!");
                return;
            }
            
            ffc.removeItem(item);
            refreshTableView();
        }
    }
    
    public void removeAttribute() {
        if(ffc.getNumberOfAttributes() == 1) {
            displayError("There is only this attribute left, you cannot remove it.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Remove attribute");
        dialog.setHeaderText("Insert attribute name");
        dialog.setContentText("Attribute name:");

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            String attribute = result.get().trim();
            
            if(!ffc.containsAttribute(attribute)) {
                displayError("Attribute " + attribute + " not found!");
                return;
            }
            
            ffc.removeAttribute(attribute);
            refreshTableView();
        }
    }
    
    public void updateRelationDegree(String item, String attribute, String newDegreeString) {
    	try {
    		FuzzyDecimal oldDegree = ffc.getRelationDegree(item, attribute);
    		FuzzyDecimal newDegree = new FuzzyDecimal(newDegreeString);
    		if(!oldDegree.equals(newDegree)) {
    			if(newDegree.compareTo(FuzzyDecimal.ONE)<=0 && newDegree.compareTo(FuzzyDecimal.ZERO)>=0) {
    				ffc.setRelationDegree(item, attribute, newDegree);
    			} else {
    				displayError("Invalid value: " + newDegree);
    			}
        		refreshTableView();
        	}
    	} catch(Exception e) {
    		refreshTableView();
    	}
    	
    }
    
    private void displayError(String desc) {
        Alert dialog = new Alert(AlertType.ERROR);
        dialog.setTitle("Error");
        dialog.setHeaderText("Error");
        dialog.setContentText(desc);
        dialog.showAndWait();
    }
    
    private void refreshTableView() {
    	tableView.getColumns().clear();
        tableView.getItems().clear();
        
        addItemsColumn("Items");
        ffc.getAttributes().forEach(a -> addAttributeColumn(a));
        
        Iterable<String> items = ffc.getItems();
        Iterable<String> attributes = ffc.getAttributes();
        MainTableRecord row;
        
        for(String item: items) {
        	row = new MainTableRecord(item);
        	for(String attribute: attributes) {
        		row.addValue(ffc.getRelationDegree(item, attribute).toString());
            }
        	
        	tableView.getItems().add(row);
        }
        
    }
    
    private void addItemsColumn(String colName) {
        TableColumn<MainTableRecord, String> col = new TableColumn<>(colName);
        
        col.setCellFactory(cell -> new TableCell<MainTableRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                this.setText(item);
                this.setAlignment(Pos.CENTER);
            }
        });
        
        col.setCellValueFactory(new PropertyValueFactory<>("item"));
        col.setSortable(false);
        col.setStyle("-fx-font-weight: bold");
        col.setReorderable(false);
        
        tableView.getColumns().add(col);
    }
    
    private void addAttributeColumn(String colName) {
        TableColumn<MainTableRecord, String> col = new TableColumn<>(colName);
        int i = tableView.getColumns().size()-1;//columns.size() - 1;
        
        col.setSortable(false);
        col.setStyle("-fx-alignment: CENTER;");
        
        col.setCellFactory(f -> new FuzzyTableCell(this, colName));
        col.setCellValueFactory(data -> data.getValue().getValue(i));

        col.setReorderable(false);
        
        tableView.getColumns().add(col);
    }
    
    @FXML
    private void findGuiguesDuquenneBasisInANewThread() {
    	final Set<String> GDBasis = new HashSet<String>();
    	
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Operation in progress",
                ButtonType.CANCEL
        );
        alert.setTitle("Operation in progress");
        alert.setHeaderText("Please wait...");
        
        Task<Void> task = new Task<Void>() {
            {
                setOnFailed(a -> {
                    alert.close();
                });
                setOnSucceeded(a -> {
                    alert.close();
                });
                setOnCancelled(a -> {
                    alert.close();
                });
            }

            @Override
            protected Void call() throws Exception {
            	try {
            		Set<String> tempGDBasis = null;
            		if(cbMode.getValue().equals("BK algorithm")) {
    	            	tempGDBasis = ffc.getGuiguesDuquenneBasis(FuzzyFormalContext.BK_ALGORITHM);
            		} else if(cbMode.getValue().equals("DS algorithm")) {
    	            	tempGDBasis = ffc.getGuiguesDuquenneBasis(FuzzyFormalContext.DS_ALGORITHM);
            		} else if(cbMode.getValue().equals("KI algorithm (approx)")) {
    	            	tempGDBasis = ffc.getGuiguesDuquenneBasis(FuzzyFormalContext.KI_ALGORITHM);
            		} else if(cbMode.getValue().equals("KA algorithm (approx)")) {
    	            	tempGDBasis = ffc.getGuiguesDuquenneBasis(FuzzyFormalContext.KA_ALGORITHM);
            		}
            		
            		for(String s : tempGDBasis) {
	            		GDBasis.add(s);
	            	}
	            	
            	} catch(Exception e) {
            		System.out.println(e);
            	}
            	
                return null;
            }
        };
        
        Thread t = new Thread(task);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        
        alert.initOwner(mainAnchorPane.getScene().getWindow());
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.CANCEL && task.isRunning()) {
            task.cancel(true);
            System.out.println("Operation aborted");
        }
        
        // After the end of the task
    	
    	implicationSets.getChildren().clear();
    	if(!GDBasis.isEmpty()) {
    		System.out.println("GD Basis:");
    		for(String implication : GDBasis) {
        		Text text = new Text(implication + "\n");
        		System.out.println(implication);
        		implicationSets.getChildren().add(text);
        	}
    	} else {
    		System.out.println("No result found");
    		Text text = new Text("No result found\n");
    		implicationSets.getChildren().add(text);
    	}
    	System.out.println();
    }
    
    
    
    
    
    
    
    
    @FXML
	private void runDsRandomTestsInANewThread() {
		
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Operation in progress",
                ButtonType.CANCEL
        );
        alert.setTitle("Operation in progress");
        alert.setHeaderText("Please wait...");
        
        Task<Void> task = new Task<Void>() {
            {
                setOnFailed(a -> {
                    alert.close();
                });
                setOnSucceeded(a -> {
                    alert.close();
                });
                setOnCancelled(a -> {
                    alert.close();
                });
            }

            @Override
            protected Void call() throws Exception {
            	try {
	            	runDsRandomTests();
            	} catch(Exception e) {
            		System.out.println(e);
            	}
            	
                return null;
            }
        };
        
        Thread t = new Thread(task);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        
        alert.initOwner(mainAnchorPane.getScene().getWindow());
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.CANCEL && task.isRunning()) {
            task.cancel(true);
            System.out.println("Operation aborted");
        }
    	
	}

    private void runDsRandomTests() {
    	System.out.println("Ds multiple test running");
    	
    	//test parameters
    	FuzzyDecimal step = new FuzzyDecimal("0.125");
    	int numItems = 3;
    	int numAttributes = 2;
    	int numTests = 40;
    	
    	int successfulTests = 0;
    	
    	for(int t=0; t<numTests; t++) {
    		FuzzyFormalContext ffcTest = new FuzzyFormalContext();
    		
    		for(int i=1; i<=numItems; i++) {
    			ffcTest.addItem("item" + i);
    		}
    		for(int a=1; a<=numAttributes; a++) {
    			ffcTest.addAttribute("attr" + a);
    		}
    		
    		for(String item: ffcTest.getItems()) {
    			for(String attribute: ffcTest.getAttributes()) {
    				FuzzyDecimal relationDegree = step.multiply(new FuzzyDecimal((new Random()).nextInt( FuzzyDecimal.ONE.divide(step).add(FuzzyDecimal.ONE).intValue() )));
    				
    				//mi assicuro che lo step sia effettivamente step
    				if(item.equals("item1") && attribute.equals("attr1")) {
    					relationDegree = step;
    				}
    				
    				ffcTest.setRelationDegree(item, attribute, relationDegree);
    			}
    		}
    		System.out.println("Perform test on FuzzyFormalContext: " + ffcTest);
    		
    		//trovo una base di questo ffc con DS algorithm
    		//se la trovo, incremento i casi di successo
    		try {
    			Set<String> GDBasis = ffcTest.getGuiguesDuquenneBasis(FuzzyFormalContext.DS_ALGORITHM);
    			if(GDBasis != null) successfulTests++;
    			else System.out.println("This test failed");
    		} catch(NullPointerException e) {
    			System.out.println("This test failed");
    		}
    		
    		System.out.println();
    	}
    	
    	System.out.println();
    	System.out.println("DS algorithm was tested on " + numTests + " random FuzzyFormalContexts");
    	System.out.println("These FuzzyFormalContexts had " + numItems + " items, " + numAttributes + " attributes and step " + step);
    	System.out.println("Successful tests (GD Basis found): " + successfulTests);
    	System.out.println("Success rate: " + ((double)(successfulTests) / numTests));
    	System.out.println();
    	
    	String resultMessage = "DS algorithm was tested on " + numTests + " random Fuzzy Formal Contexts.\n";
    	resultMessage += "These Fuzzy Formal Contexts had " + numItems + " items, " + numAttributes + " attributes and step " + step + ".\n";
    	resultMessage += "Successful tests (GD Basis found):\t" + successfulTests + "\n";
    	resultMessage += "Success rate:\t" + ((double)(successfulTests) / numTests);
    	
    	final String finalResultMessage = resultMessage;
        
        Platform.runLater(() -> {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Operation complete",
                    ButtonType.FINISH
            );
            alert.setTitle("Tests result");
            alert.setHeaderText(finalResultMessage);
            alert.initOwner(mainAnchorPane.getScene().getWindow());
        	alert.showAndWait();
        });
    }
    
}
