package javaTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.restassured.response.Response;

public class ExcelOperation {

	public static void readExcel(String filePath, String fileName, String sheetName, String reportName)
			throws IOException {
		File file = new File(filePath+"\\"+fileName);
		FileInputStream inputStream = new FileInputStream(file);

		 File directory = new File("Reports");
		    if (! directory.exists()){
		        directory.mkdir();
		    }
		    
		ExtentHtmlReporter reporter = new ExtentHtmlReporter("./"+directory+"/"+reportName+".html");
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
			//		System.out.println("working body is :- row: " + i + ") body: " + sheet.getRow(i).getCell(j));

					if (sendRequest.testResponseCode(reqUrl, sheet.getRow(i).getCell(2).toString(),
							Double.parseDouble(sheet.getRow(i).getCell(3).toString()),
							sheet.getRow(i).getCell(j).toString(),
							Integer.valueOf(
									(int) Math.round(Double.parseDouble(sheet.getRow(i).getCell(4).toString()))),
							extent) == 0) {
						totalAPIToTesting++;
		//				System.out.println("Pass");

						HSSFCell cell = sheet.getRow(i).createCell(j + 1);
						cell.setCellValue("PASS");
						workbook.write(f2);

					} else {
						totalAPIToTesting++;
		//				System.out.println("Fail");
						HSSFCell cell = sheet.getRow(i).createCell(j + 1);
						cell.setCellValue("FAIL");
						workbook.write(f2);
					}
		//			System.out.println("*************************************");
				}
				extent.flush();
			}
			System.out.println("Total APIs tested: " + sheet.getLastRowNum());
			System.out.println("Total APIs calls were:" + totalAPIToTesting);
		}
	}
}