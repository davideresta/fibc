package dresta.fibc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dresta.fca.FuzzyDecimal;
import dresta.fca.FuzzyFormalContext;

public class ExcelManager {
    
	public static void read(FuzzyFormalContext ffc, String filename) {
		FileInputStream fis = null;
        
        try {
            fis = new FileInputStream(new File(filename));
            XSSFSheet sheet = new XSSFWorkbook(fis).getSheetAt(0);
            
            Iterator<Row> rowIt = sheet.iterator();

            int columnsCount = getColumnsCount(sheet.iterator());
            int rowsCount = sheet.getPhysicalNumberOfRows();
            
            boolean headerRow = true;
            //int rowsCount = 0;
            
            String[][] table = new String[rowsCount][columnsCount];
            for(int i=0; i<rowsCount; i++) {
            	Row row = rowIt.next();
            	
            	if(rowIsEmpty(row, columnsCount)) 
                    break;
            	
            	for(int j=0; j<columnsCount; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                    
                    if(cell != null)
                        cell.setCellType(CellType.STRING);
                    
                    String cellContent = cell == null ? "" : cell.toString();
                    table[i][j] = cellContent;
            	}
            }
            
            for(int i=0; i<table.length; i++) {
            	for(int j=0; j<table[0].length; j++) {
            		if(i==0 && j>0) {
            			ffc.addAttribute(table[i][j]);
            		} else if(i>0 && j==0) {
            			ffc.addItem(table[i][j]);
            		}
            		else if(i>0 && j>0) {
            			ffc.setRelationDegree(table[i][0], table[0][j], new FuzzyDecimal(table[i][j]));
            		}
            	}
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis != null)
                    fis.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void write(FuzzyFormalContext ffc, String filename) {
        FileOutputStream fos = null;
        
        System.out.println("Scrivo");
        
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Table");
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            
            
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
            Font dataCellFont = workbook.createFont();
            dataCellFont.setFontHeightInPoints((short)14);
            dataCellStyle.setFont(dataCellFont);
            
            Row header = sheet.createRow(0);
            
            Cell cell = header.createCell(0);
            cell.setCellValue("Items");//table.getName());
            cell.setCellStyle(headerCellStyle);
            
            sheet.autoSizeColumn(0);
            
            int i = 0;
            for(String attribute: ffc.getAttributes()) {
                cell = header.createCell(i+1);
                cell.setCellValue(attribute);
                cell.setCellStyle(headerCellStyle);
                sheet.autoSizeColumn(i+1);
                i++;
            }
            
            int r=1;
            for(String item: ffc.getItems()) {
                Row row = sheet.createRow(r);
                
                cell = row.createCell(0);
                cell.setCellValue(item);
                cell.setCellStyle(headerCellStyle);
                sheet.autoSizeColumn(0);
                
                int j=0;
                for(String attribute: ffc.getAttributes()) {
                    cell = row.createCell(j+1);
                    cell.setCellValue(ffc.getRelationDegree(item, attribute).toString());
                    cell.setCellStyle(dataCellStyle);
                    j++;
                }
                
                r++;
            }
        
            fos = new FileOutputStream(filename);
            workbook.write(fos);
            fos.close();
            
            System.out.println(filename);

            // Closing the workbook
            workbook.close();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null)
                    fos.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static boolean rowIsEmpty(Row row, int cols) {
        if (row == null) 
            return true;
        
        if (row.getLastCellNum() <= 0) 
            return true;
        
        for (int i = 0; i < cols; ++i) {
            Cell cell = row.getCell(i);
            
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        
        return true;
    }
    
    private static int getColumnsCount(Iterator<Row> rowIt) {
        int result = 1;
        
        try {
            if(rowIt.hasNext()) {
                Row row = rowIt.next();

                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell;
                
                cellIterator.next();
                while(cellIterator.hasNext() ) {
                    cell = cellIterator.next();
                    
                    if(cell.getStringCellValue().isEmpty())
                        break;
                    
                    ++result;
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
}
