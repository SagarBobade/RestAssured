package javaTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.restassured.response.Response;

public class FilesOperation {
	

	public static void readWriteExcel(String filePath, String fileName, String sheetName, String reportName)
			throws IOException {
		File file = new File(filePath+"//"+fileName);
		FileInputStream inputStream = new FileInputStream(file);

		 File directory = new File("C://API-Test-Excel//Reports");
		    if (! directory.exists()){
		        directory.mkdir();
		    }
		    
		ExtentHtmlReporter reporter = new ExtentHtmlReporter(directory+"//"+reportName+".html");
		ExtentReports extent = new ExtentReports();
		extent.attachReporter(reporter);
		
		int totalAPIToTesting = 0;
		String reqUrl = null;
		String method = null;
		Workbook workbook = null;
		Response resp;

		String fileExtensionName = fileName.substring(fileName.indexOf("."));
		if (fileExtensionName.equals(".xls")) {
			workbook = new HSSFWorkbook(inputStream);
			HSSFSheet sheet = (HSSFSheet) workbook.getSheetAt(0);

			System.out.println("******************Total APIs are : " + sheet.getLastRowNum() + " ******************");

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				FileOutputStream f2 = new FileOutputStream(file);

				reqUrl = sheet.getRow(i).getCell(1).toString();
				method = sheet.getRow(i).getCell(2).toString();

				for (int j = 6; j <= sheet.getRow(i).getLastCellNum(); j = j + 2) {

					if (j >= sheet.getRow(i).getLastCellNum()) {
						break;
					}

					if (sendRequest.testResponseCode(reqUrl, sheet.getRow(i).getCell(2).toString(),
							Double.parseDouble(sheet.getRow(i).getCell(3).toString()),
							sheet.getRow(i).getCell(j).toString(),
							Integer.valueOf(
									(int) Math.round(Double.parseDouble(sheet.getRow(i).getCell(4).toString()))),
							extent) == 0) {
						totalAPIToTesting++;

						HSSFCell cell = sheet.getRow(i).createCell(j + 1);
						cell.setCellValue("PASS");
						workbook.write(f2);

					} else {
						totalAPIToTesting++;
						HSSFCell cell = sheet.getRow(i).createCell(j + 1);
						cell.setCellValue("FAIL");
						workbook.write(f2);
					}
				}
				extent.flush();
			}
			System.out.println("Total APIs tested: " + sheet.getLastRowNum());
			System.out.println("Total APIs calls were:" + totalAPIToTesting);
		}
	}
	
	/*####################################################### below code is copied from main class#######################################*/
	public FilesOperation excelOp;
	public static Properties prop;
	public static InputStream input;
	
	public static void readPropertiesFile() {
		prop = new Properties();
		try {
			input = new FileInputStream("C://API-Test-Excel//Settings.properties");
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAPIs() throws IOException {
		
		readPropertiesFile();
		String testDataExcelPath = prop.getProperty("TestDataExcelPath");
		String testDataExcel = prop.getProperty("TestDataExcelName");;
		String sheetName = prop.getProperty("SheetName");;
		String reportName = prop.getProperty("ReportName");;
		String mailShoot = prop.getProperty("MailShoot");
		String TestAccessibilityTesting = prop.getProperty("TestAccessibilityTesting");   
				
		if(TestAccessibilityTesting.equalsIgnoreCase("true")) {
		String userAuthAPIUrl = prop.getProperty("AuthenticationAPIUrl");
		String JSONBody = prop.getProperty("JSONBody");
		String method = prop.getProperty("Method");
		
			String token = sendRequest.hitUserAuthAPI(userAuthAPIUrl, method, JSONBody );
			System.out.println("token :- "+token);
		}

		readWriteExcel(testDataExcelPath, testDataExcel, sheetName, reportName);

		if(mailShoot.equalsIgnoreCase("true")) {
		String from = prop.getProperty("From");
		String toCommaSeperated = prop.getProperty("To");
        List<String> toList = Arrays.asList(toCommaSeperated.split("\\s*,\\s*"));
        String []to = toList.toArray(new String[toList.size()]);
        String password = prop.getProperty("Password");
        String ccCommaSeperated = prop.getProperty("Cc");
        List<String> ccList = Arrays.asList(ccCommaSeperated.split("\\s*,\\s*"));
        String []cc = toList.toArray(new String[ccList.size()]);

        sendMail.sendMail(from, password, to, cc, reportName, testDataExcelPath, testDataExcel);
		}
	}

}