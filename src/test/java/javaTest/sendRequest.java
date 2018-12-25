package javaTest;

import static io.restassured.RestAssured.get;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class sendRequest {

	@Test
	public static int testResponseCode(String reqUrl, String methodName, double expectedCode1, String jsonBody,
			int expectedTime, ExtentReports extent) {
		
		int expectedCode = Integer.valueOf((int) Math.round(expectedCode1));
		
		if (!jsonBody.contains("NA")) {
			JSONObject jsonobj = new JSONObject(jsonBody);
		}
		
		Response resp = null;
		int code = 0;
		RestAssured.baseURI = reqUrl;
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		ExtentTest logger2 = extent.createTest(methodName + " " + reqUrl);

		if (methodName.equalsIgnoreCase("get")) {
			resp = get(reqUrl);
		} else if (methodName.equalsIgnoreCase("post")) {
			request.body(jsonBody);
			resp = request.post();
		} else if (methodName.equalsIgnoreCase("put")) {
			request.body(jsonBody);
			resp = request.put();
		} else if (methodName.equalsIgnoreCase("delete")) {
			resp = request.delete();
		}
		if (resp != null) {
			code = resp.getStatusCode();
		}
		System.out.println(methodName + " method :" + code);

		if (code == Math.round(expectedCode)) {
			if (resp.getTime() > expectedTime) {
				logger2.log(Status.FAIL,"<span style='color:red;'>"+
								" <b>Faild reason: </b>Response Time is greater than Expected Time.<br />"
								+"</span>"
								+ "<b>Expected Response code:</b> " + expectedCode 
								+ "<br /><b>Actual response code:</b> " + code
								+ "<br /><b>Response time: </b>" + resp.getTime()
								+ "<br /><b>Expected time: </b>"+ expectedTime
								+ "<br /><b>Requested Body: </b>"+ jsonBody
								+ "<br><b>Response Body: </b>" + resp.asString()
								+"</span>");
				return -1;
			}
			logger2.log(Status.PASS,
								" <b>Expected Response code:</b> " + expectedCode
								+ "<br /><b>Actual response code:</b> " + code
								+ "<br /><b>Response time: </b>" + resp.getTime() 
								+ "<br /><b>Expected time: </b>" + expectedTime
								+ "<br /><b>Requested Body: </b>"+ jsonBody
								+ "<br /><b>Response Body: </b>" + resp.asString()
								);
			return 0;
		} else {
			logger2.log(Status.FAIL,"<span style='color:red;'>"+
								" <b>Faild reason: </b>Response code is not equal to Expected code.<br />"
								+"</span>"
								+ "<b>Expected Response code:</b> " + expectedCode
								+ "<br /><b>Actual response code:</b> " + code
								+ "<br /><b>Response time: </b> " + resp.getTime() 
								+ "<br /><b>Requested Body: </b>"+ jsonBody
								+ "<br /><b>Response Body: </b>"+ resp.asString()
								);
			return -1;
		}

	}

	public static String hitUserAuthAPI(String userAuthAPIUrl, String methodName, String JSONBody) {

		if (!JSONBody.contains(null)) {
			JSONObject jsonobj = new JSONObject(JSONBody);
		
			Response resp = null;
			int code = 0;
			RestAssured.baseURI = userAuthAPIUrl;
			RequestSpecification request = RestAssured.given();
			request.header("Content-Type", "application/json");

			request.body(JSONBody);
			resp = request.post();
			if (resp != null) {
				code = resp.getStatusCode();
				if(code == 200) {
					
					/*read here respose and in header or in body bearer token will be provided just fetch and use it for further admin api calls*/
					return resp.getBody().toString();
				}
			}
		}
		
		return "EUBearerToken";
	}

}
