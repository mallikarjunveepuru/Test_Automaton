package magical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import common.XmltoJava;

public class Suite implements ITestListener {

	static WebDriver driver = new FirefoxDriver();
	static boolean status = true;
	static String projectHome = System.getProperty("user.dir");
	public static File file1;
	public static HSSFWorkbook workbook;
	public static HSSFSheet sheet;

	List<XmlSuite> xmlSuites;

	public Suite() {
		xmlSuites = new ArrayList<XmlSuite>();
	}

	public WebDriver newDriver() {
		if (driver == null) {
			return new FirefoxDriver();
		}
		return driver;
	}

	public void method(boolean stat) {
		status = stat;
	}

	private void runTests(TestNG tng) throws JAXBException, Exception {

		// file1.createNewFile();
		XmltoJava xmltojava = (XmltoJava) common.GenericClass.unmarshallClass(
				projectHome + "/config/" + "config.xml", XmltoJava.class);

		for (int k = 0; k < xmltojava.getSuite().length; k++) {

			XmlSuite suite = new XmlSuite();
			ArrayList<XmlTest> tests = new ArrayList<XmlTest>();

			for (int l = 0; l < xmltojava.getSuite()[k].getParameter().length; l++) {

				Map<String, String> parameters = new HashMap<String, String>();
				suite.setName(xmltojava.getSuite()[k].getSuitename());
				List<XmlTest> xmlTest = new ArrayList<XmlTest>();

				XmlTest test = new XmlTest(suite);
				test.setName(xmltojava.getSuite()[k].getParameter()[l]
						.getTestcase());

				parameters.put("suiteName",
						xmltojava.getSuite()[k].getParameter()[l].getName());
				parameters
						.put("Testcase",
								xmltojava.getSuite()[k].getParameter()[l]
										.getTestcase());
				test.setParameters(parameters);

				ArrayList<XmlInclude> methodsToRun = new ArrayList<XmlInclude>();
				ArrayList<XmlClass> classes1 = new ArrayList<XmlClass>();
				XmlClass classes = new XmlClass();

				if (xmltojava.getSuite()[k].getClassname().equals(
						"Public_Catalog")) {
					classes.setName("magical.Public_Catalog");
				} else {
					classes.setName("magical."
							+ xmltojava.getSuite()[k].getSuitename());
				}

				methodsToRun.add(new XmlInclude(xmltojava.getSuite()[k]
						.getParameter()[l].getTestcase()));
				classes.setIncludedMethods(methodsToRun);
				classes1.add(classes);
				test.setXmlClasses(classes1);
				xmlTest.add(test);
				tests.addAll(xmlTest);
			}
			suite.setTests(tests);
			xmlSuites.add(suite);
		}
		tng.setXmlSuites(xmlSuites);
		tng.run();
	}

	public static void main(String[] args) throws JAXBException, Exception {

		Suite rtest = new Suite();
		//login();
		TestNG tng = new TestNG();
		tng.addListener(rtest);
		rtest.runTests(tng);
		driver.quit();

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
