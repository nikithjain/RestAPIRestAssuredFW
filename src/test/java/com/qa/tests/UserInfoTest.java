package com.qa.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qa.util.TestBase;
import com.qa.util.TestUtil;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserInfoTest extends TestBase {
	
	@BeforeMethod
	public void setup() {
		TestBase.init();
	}
	
	@DataProvider(name="getData")
	public Object[][] getData() {
		Object testdata[][]= TestUtil.getDataFromSheet(TestUtil.UserSheetName);
		return testdata;
	}
	
	@Test(dataProvider="getData")
	public void getUserDetailswithCorrectIdTest(String user,String	HTTPMethod,String first_name,String	last_name, String avatar) {
		
		test = extent.startTest("UserInfo Test");
		//1. Define the Base URL
		RestAssured.baseURI = prop.getProperty("serviceurl");
		
		
		//2. Define the HTTP Request
		RequestSpecification httpRequest = RestAssured.given();
		test.log(LogStatus.INFO, "Trying to Hit the request");
		
		
		//3. Make the request/Execute the request
		Response response = httpRequest.request(Method.GET,"/"+user);
		test.log(LogStatus.INFO, "Executed the request");
		
		//4. Get the Response body
		String responseBody = response.getBody().asString();
		System.out.println("Response Body is :"+responseBody);
		
		//Validate the Key or Value 
		Assert.assertEquals(responseBody.contains(first_name), true);
		
		//5.Get the Status code
		int statusCode = response.getStatusCode();
		System.out.println("Status Code is :"+statusCode);
		
		Assert.assertEquals(statusCode, TestUtil.RESPONSE_CODE_200);
		
		System.out.println("Status Line is "+response.getStatusLine());
		
		//6. Get Headers
		Headers headers = response.getHeaders();
		System.out.println(headers);
		String contentType = response.getHeader("Content-Type");
		System.out.println("Value of Content Type is :"+contentType);
		
		//7. Get the Key value using Json Path
		JsonPath jsonPathValue = response.jsonPath();
		String firstNameVal = jsonPathValue.get("data.first_name");
		System.out.println("Value of First Name  is "+firstNameVal);
		Assert.assertEquals(firstNameVal,first_name);
		
		String lastNameValue = jsonPathValue.get("data.last_name");
		System.out.println("Value of Last Name  is "+lastNameValue);
		Assert.assertEquals(lastNameValue,last_name);
		
		String avatarValue = jsonPathValue.get("data.avatar");
		System.out.println("Value of Avatar is "+avatarValue);
		Assert.assertEquals(avatarValue,avatar);
		
		
	}
	
	@AfterMethod
	public void quit() {
		if(extent!=null) {
			extent.endTest(test);
			extent.flush();
		}
	}

}
