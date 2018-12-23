package javaTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class JavaTest {
	
	public ExcelOperation excelOp;
	public static Properties prop;
	public static InputStream input;

	public static void readPropertiesFile() {
		prop = new Properties();
		try {
			input = new FileInputStream("C://Users//sagar//eclipse oxygen workspace//Rest-Assured-full-version//Settings.properties");
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String args[]) throws IOException {
		
		readPropertiesFile();
		String filePath = prop.getProperty("filePath");
		String fileName = prop.getProperty("fileName");;
		String sheetName = prop.getProperty("sheetName");;
		String reportName = prop.getProperty("ReportName");;
		String ReportName = "myfirstReoprttt";
		
		ExcelOperation.readExcel(filePath, fileName, sheetName,ReportName);
		
	}
}
