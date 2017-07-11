package magical;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import util.Utility;


@Listeners(org.uncommons.reportng.HTMLReporter.class)
public class Edutech implements ITestListener{
	
	String workspace=System.getProperty("user.dir");
	Suite suite = new Suite();
	WebDriver driver = suite.newDriver();
	Utility utility=new Utility();
	String Timestamp=utility.timestamp().replace(".", "");
	
	@BeforeMethod
	protected void checkEnvironment() {
		System.out.println(Suite.status);
		if (!Suite.status) {
			throw new SkipException("Skipping tests because resource was not available.");
		}
	}
	
	@Test
	public void title() throws IOException, InterruptedException{
		Properties prop = new Properties();
		WebDriverWait wait = new WebDriverWait(driver, 200);
		driver.get("http://13.84.219.29:8000/admin/");
		Assert.assertEquals(driver.getTitle(), "Log in | Django site admin");
	}
	
	@Test
	public void login() throws IOException, InterruptedException{
		Properties prop = new Properties();
		WebDriverWait wait = new WebDriverWait(driver, 200);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_username"))).sendKeys("magical");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_password"))).sendKeys("Admin@1234");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".submit-row>input"))).click();
		Thread.sleep(4000);
		String SiteName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content>h1"))).getText();
		System.out.println(SiteName);
		Assert.assertEquals(SiteName, "Site administration");		
	}
	
	@Parameters({"suiteName"})
	@AfterMethod
	public void screenshot(ITestResult arg0,@Optional String suiteName) throws IOException {
		// TODO Auto-generated method stub
		String screenshotname =suiteName+arg0.getName().toString(); 
		System.out.println(screenshotname);
		
		     String screenshot =  utility.screenshot(screenshotname);;
			 System.setProperty("org.uncommons.reportng.escape-output", "false");
			   Reporter.setCurrentTestResult(arg0); //// output gets lost without this entry
				  Reporter.log("<a title= \"title\" href=\"" + screenshot + "\">" +
				 "<img width=\"700\" height=\"550\" src=\"" + screenshot +
				  "\"></a>");

	}
			

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

}

