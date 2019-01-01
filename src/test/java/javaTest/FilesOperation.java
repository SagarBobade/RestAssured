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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.restassured.response.Response;

public class FilesOperation {
	
	public static String propertiesFilePath = "C://API-Test-Excel//Settings.properties";
	public static String globalToken = null;
	public static Boolean globalTestAccessibilityTesting = false;
	public static Boolean globalTestOnlyFailedScenario = false;
	
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
		Double expectedCode = null;
		Workbook workbook = null;
		HSSFCell cell = null;

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
					if(globalTestOnlyFailedScenario.equals(true)) {
						if(sheet.getRow(i).getCell(j + 1).getStringCellValue().toString().equalsIgnoreCase("PASS")) {
					//	if(!((j + 1) > sheet.getRow(i).getLastCellNum()-1))
					//		System.out.println(sheet.getRow(i).getCell(j + 1).getStringCellValue().toString());
						continue;
						}
					}

					if (sendRequest.testResponseCode(
							reqUrl, 
							method,
							Double.parseDouble(sheet.getRow(i).getCell(3).toString()),
							sheet.getRow(i).getCell(j).toString(),
							Integer.valueOf(
									(int) Math.round(Double.parseDouble(sheet.getRow(i).getCell(4).toString()))),
							extent) == 0) {
						totalAPIToTesting++;
						if(globalTestOnlyFailedScenario.equals(true)) {
							 cell = sheet.getRow(i).getCell(j + 1);							
						}
						else {
						 cell = sheet.getRow(i).createCell(j + 1);
						}
						cell.setCellValue("PASS");
				        CellStyle backgroundStyle = workbook.createCellStyle();
						backgroundStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
						 
						workbook.write(f2);

					} else {
						System.out.println("fail :"+ Double.parseDouble(sheet.getRow(i).getCell(3).toString()));
						totalAPIToTesting++;
						if(globalTestOnlyFailedScenario.equals(true)) {
							 sheet.getRow(i).getCell(j + 1).setCellValue("FAIL");						
						}
						else {
						 sheet.getRow(i).createCell(j + 1).setCellValue("FAIL");
						}
						//cell.setCellValue("FAIL");
				        CellStyle backgroundStyle = workbook.createCellStyle();
						backgroundStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
						workbook.write(f2);
					}
					if(globalTestAccessibilityTesting.equals(true)) {
						break;
					}
				}
				extent.flush();
			}
			System.out.println("Total APIs tested: " + sheet.getLastRowNum());
			System.out.println("Total APIs calls were:" + totalAPIToTesting);
		}
	}
	
	public FilesOperation excelOp;
	public static Properties prop;
	public static InputStream input;
	
	public static void readPropertiesFile() {
		prop = new Properties();
		try {
			input = new FileInputStream(propertiesFilePath);
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAPIs() throws IOException {
		
		readPropertiesFile();
		String TestAccessibilityTesting = prop.getProperty("TestAccessibilityTesting"); 
		String testDataExcelPath = prop.getProperty("TestDataExcelPath");
		String testDataExcel = prop.getProperty("TestDataExcelName");;
		String sheetName = prop.getProperty("SheetName");;
		String reportName = prop.getProperty("ReportName");;
		String mailShoot = prop.getProperty("MailShoot");
		String display = prop.getProperty("orgCode");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		String testOnlyFailedScenario = prop.getProperty("testOnlyFailedScenario");
		if(testOnlyFailedScenario.equalsIgnoreCase("true")) {
			globalTestOnlyFailedScenario = true;
		}
		
		if(TestAccessibilityTesting.equalsIgnoreCase("true")) {
		String userAuthAPIUrl = prop.getProperty("AuthenticationAPIUrl");
		
			String token = sendRequest.hitUserAuthAPI(userAuthAPIUrl, display, username, password );
			if(!token.equals("fail")) {
			globalToken = token;
			globalTestAccessibilityTesting = true;
			}
			else {
				System.out.println("sendrequest token is failed");
			}
		}

		readWriteExcel(testDataExcelPath, testDataExcel, sheetName, reportName);

		if(mailShoot.equalsIgnoreCase("true")) {
		String from = prop.getProperty("From");
		String toCommaSeperated = prop.getProperty("To");
        List<String> toList = Arrays.asList(toCommaSeperated.split("\\s*,\\s*"));
        String []to = toList.toArray(new String[toList.size()]);
        String MailPassword = prop.getProperty("MailPassword");
        String ccCommaSeperated = prop.getProperty("Cc");
        List<String> ccList = Arrays.asList(ccCommaSeperated.split("\\s*,\\s*"));
        String []cc = toList.toArray(new String[ccList.size()]);

        sendMail.sendMail(from, MailPassword, to, cc, reportName, testDataExcelPath, testDataExcel);
		}
	}
}