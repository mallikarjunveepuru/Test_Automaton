package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import magical.Suite;

public class Utility {

	
	Suite suite = new Suite();
	WebDriver driver=suite.newDriver();
	String workspace = System.getProperty("user.dir");
	String screenload = System.getProperty("user.dir") + "/test-output/html/";
	String screenget = "";
	String bluprintnames = "Drupal";
	String[] name = bluprintnames.split(",");
	static Logger l = Logger.getLogger(Utility.class.getName());
	HSSFWorkbook workbook = Suite.workbook;
	HSSFSheet sheet = Suite.sheet;
	File file1 = Suite.file1;
	Map<Integer, Object[]> testresultdata = new TreeMap<Integer, Object[]>();
	
	public String timestamp(){
		String timeStamp = new SimpleDateFormat("mm.ss").format(new Timestamp(System.currentTimeMillis()));
		return timeStamp;		
	}

	public String screenshot(String screenshot) throws IOException {
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screen, new File(screenload + screenshot + ".png"));
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		String image = screenget + screenshot + ".png";
		Reporter.log("<a title= \"title\" href=\"" + image + "\">" + "<img width=\"700\" height=\"550\" src=\""
				+ image + "\"></a>");
		return image;

	}

	public void jobvalidation(String jobName) throws InterruptedException, IOException {
		Properties prop = new Properties();
		WebDriverWait wait=new WebDriverWait(driver, 200);
		FileInputStream fs = new FileInputStream(workspace + "/src/property.Properties");
		prop.load(fs);
		
		  Layout l1 = new SimpleLayout();
		  Appender a;
		  a = new FileAppender(l1,"my.txt", true);	 
		  // 3rd parameter is true by default
		  // true = Appends the data into my.txt
		  // false = delete previous data and re-write
		  l.addAppender(a);
		
		System.out.println("Job Validation Started");
		l.info("Job Validation Started");
		Reporter.log("Job Validation Started");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("jobspage"))));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.findElement(By.xpath(prop.getProperty("jobspage"))).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElement(By.xpath(prop.getProperty("jobspage-1"))).click();
		driver.findElement(By.xpath(prop.getProperty("jobname"))).sendKeys(jobName);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Keyboard kb = ((HasInputDevices) driver).getKeyboard();
		kb.sendKeys(Keys.TAB, Keys.ARROW_DOWN);
		kb.pressKey("I");
		kb.sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		boolean jobcomplete = false;
		String endtime="" ,nomatching= "";
		while(!jobcomplete){
			if(driver.findElements(By.xpath(prop.getProperty("jobendtime"))).size()>0){
				endtime = driver.findElement(By.xpath(prop.getProperty("jobendtime"))).getText();
				
				driver.findElement(By.xpath(prop.getProperty("jobrowoperation"))).click();
				String errorMsg = driver.findElement(By.xpath(prop.getProperty("joberrormessage"))).getText();
				System.out.println("****outcome****"+errorMsg);
				l.error("\nJob Inprogress outcome : " + errorMsg.toString());
				Reporter.log("\nJob Inprogress outcome : " + errorMsg.toString());
			}
			else{
				endtime = "";
			}
			
			if(driver.findElements(By.xpath(prop.getProperty("nomatchingrecordswerefound"))).size()>0){
				nomatching = driver.findElement(By.xpath(prop.getProperty("nomatchingrecordswerefound"))).getText();
			}
			else{
				nomatching = "";
			}
			if(endtime.equals("-") ){
				driver.findElement(By.xpath(prop.getProperty("jobrefreshbutton"))).click();
				Thread.sleep(500);
			}else{	
				nomatching = driver.findElement(By.xpath(prop.getProperty("nomatchingrecordswerefound"))).getText();
				if(nomatching.equals("No matching records were found")){
					jobcomplete = true;
				}
			}
		}
		driver.findElement(By.xpath(prop.getProperty("jobstatusfilter"))).click();
		driver.findElement(By.xpath(prop.getProperty("jobname"))).sendKeys(jobName);
		kb.sendKeys(Keys.TAB, Keys.ARROW_DOWN);
		kb.pressKey("C");
		kb.sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		if(driver.findElements(By.xpath(prop.getProperty("jobendtime"))).size()>0){
			endtime = driver.findElement(By.xpath(prop.getProperty("jobendtime"))).getText();
			
			driver.findElement(By.xpath(prop.getProperty("jobrowoperation"))).click();
			String errorMsg = driver.findElement(By.xpath(prop.getProperty("joberrormessage"))).getText();
			System.out.println("****outcome****"+errorMsg);
			l.error("\nJob completed outcome : " + errorMsg.toString());
			Reporter.log("\nJob completed outcome : " + errorMsg.toString());
		} 
		else{
			endtime = "";
		}
		if(driver.findElements(By.xpath(prop.getProperty("nomatchingrecordswerefound"))).size()>0){
			nomatching = driver.findElement(By.xpath(prop.getProperty("nomatchingrecordswerefound"))).getText();
		}
		else{
			nomatching = "";
		}
		if(nomatching.equals("No matching records were found")){
			driver.findElement(By.xpath(prop.getProperty("jobstatusfilter"))).click();
			driver.findElement(By.xpath(prop.getProperty("jobname"))).sendKeys(jobName);
			kb.sendKeys(Keys.TAB, Keys.ARROW_DOWN);
			kb.pressKey("F");
			kb.sendKeys(Keys.ENTER);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);	
			if(driver.findElements(By.xpath(prop.getProperty("jobfailurestatusdanger"))).size()>0){
				driver.findElement(By.xpath(prop.getProperty("jobrowoperation"))).click();
				String errorMsg = driver.findElement(By.xpath(prop.getProperty("joberrormessage"))).getText();
				System.out.println("****error****"+errorMsg);
				l.error("\nJob Exception : " + errorMsg.toString());
				Reporter.log("\nJob Exception : " + errorMsg.toString());
				Assert.fail(errorMsg);
			}
		}
	}

	public void catalogpage() throws IOException {

		try {
			Properties prop = new Properties();
			FileInputStream fs = new FileInputStream(workspace + "/src/property.Properties");
			prop.load(fs);
			WebDriverWait wait = new WebDriverWait(driver, 200L);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("catalog"))));
			driver.findElement((By.xpath(prop.getProperty("catalog")))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("managecatalog"))));
			driver.findElement(By.xpath(prop.getProperty("managecatalog"))).click();
			// Thread.sleep(15000);
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath(prop.getProperty("blueprintstablefirstrow"))));
			Keyboard kb = ((HasInputDevices) driver).getKeyboard();
			kb.sendKeys(Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_DOWN, Keys.ARROW_DOWN,
					Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ENTER);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void launch(String stacknames) throws IOException, InterruptedException {

		try {
			String name[] = stacknames.split(",");
			Properties prop = new Properties();
			FileInputStream fs = new FileInputStream(workspace + "/src/property.Properties");
			prop.load(fs);
			WebDriverWait wait = new WebDriverWait(driver, 200L);
			Keyboard kb = ((HasInputDevices) driver).getKeyboard();
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("scroll(250, 0)");
			for (String blueprint : name) {
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath(prop.getProperty("blueprintstablefirstrow"))));
				jse.executeScript("window.scrollBy(0,250)", "");
				List<WebElement> ls = driver.findElement(By.xpath(prop.getProperty("blueprintstablexpath")))
						.findElements(By.tagName("tr"));
				for (WebElement rows : ls) {
					List<WebElement> ls1 = rows.findElements(By.tagName("td"));
					for (WebElement col : ls1) {
						if (col.getText().equals(blueprint)) {
							col.click();

						}
					}
				}
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				jse.executeScript("window.scrollBy(0,-500)", "");
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath(prop.getProperty("blueprintvesriontablefirstrow"))));
				List<WebElement> list = driver.findElement(By.xpath(prop.getProperty("blueprintversiontablexpath")))
						.findElements(By.tagName("tr"));
				for (WebElement versionrows : list) {
					List<WebElement> list1 = versionrows.findElements(By.tagName("td"));
					for (WebElement verioncol : list1) {
						if (verioncol.getText().equals(blueprint)) {
							verioncol.click();
						}
					}
				}
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath(prop.getProperty("blueprintversionlaunch"))));
				driver.findElement(By.xpath(prop.getProperty("blueprintversionlaunch"))).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(prop.getProperty("servername"))));

				WebElement servername = driver.findElement(By.id(prop.getProperty("servername")));
				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					servername.sendKeys(prop.getProperty("startwithname") + "loadbalancer");
				} else if (blueprint.equals("Template for Docker Hub")) {
					servername.sendKeys(prop.getProperty("startwithname") + "dockerhub");
				} else {
					servername.sendKeys(prop.getProperty("startwithname") + blueprint.toLowerCase());
				}
				kb.sendKeys(Keys.TAB, Keys.TAB, Keys.TAB, Keys.ENTER);
				Thread.sleep(3000);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("fieldinput")))).sendKeys(prop.getProperty("budget"));
				kb.sendKeys(Keys.ENTER);
				Thread.sleep(3000);
				if (blueprint.equals("Template for Docker Hub")) {
					driver.findElement(By.name("image_name")).sendKeys("tutum/wordpress");
					kb.sendKeys(Keys.TAB);
				}
				kb.sendKeys(Keys.TAB, Keys.ENTER);
				driver.findElement(By.xpath(prop.getProperty("fieldinput")))
						.sendKeys(prop.getProperty("cloud"));
				kb.sendKeys(Keys.ENTER);
				kb.sendKeys(Keys.TAB, Keys.ENTER);
				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("fieldinput")))).sendKeys("us-east-1");
				} else {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("fieldinput")))).sendKeys(prop.getProperty("region"));
					
				}System.out.println("1");
				kb.sendKeys(Keys.ENTER);
				System.out.println("2");
				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					WebElement source = driver.findElement(By.cssSelector(".dcm-slider-drag-handle"));
					WebElement target = driver.findElement(By.cssSelector(".dcm-slider-mark.first"));
					Actions act = new Actions(driver);
					act.dragAndDrop(source, target).build();
				} else if (blueprint.equals("Template for Docker Hub")) {
					driver.findElement(By.xpath("html/body/div[1]/div/div[2]/div/div/div[1]/ng-"
							+ "include/div[5]/div[5]/div/div/div[1]/div/div/table/t"
							+ "body/tr/td[1]/table/tbody/tr[2]/td/dcm-slider/div/a/a[1]/div")).click();
				} else {
					driver.findElement(By.xpath(prop.getProperty("productselector"))).click();
				}
				// stack validation
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("stackvalidation")))).click();
				// stack launch
				Thread.sleep(3000);
				kb.sendKeys(Keys.TAB, Keys.TAB, Keys.TAB, Keys.ENTER);
				Thread.sleep(5000);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("catalog"))));
				driver.findElement((By.xpath(prop.getProperty("catalog")))).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("managecatalog"))));
				driver.findElement(By.xpath(prop.getProperty("managecatalog"))).click();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void rowclick(String xpath, String resourcename, String buttonname)
			throws IOException, InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 2000L);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath
				+ "/table/tbody")));
		WebElement mytable = driver.findElement(By
				.xpath(xpath + "/table/tbody"));
		System.out.println(mytable.getText());
		List<WebElement> rows1 = mytable.findElements(By.tagName("tr"));
		for (int i = 0; i < rows1.size(); i++)
		{
			List<WebElement> ls1 = rows1.get(i).findElements(By.tagName("td"));

			for (int j = 0; j < ls1.size(); j++)
			{
				System.out.println(ls1.get(j).getText());
				if (ls1.get(j).getText().equals(resourcename)) {
					ls1.get(j).click();
					Thread.sleep(4000);
				}
			}
		}
		WebElement val = driver.findElement(By
				.tagName("dcm-grid-row-action-buttons"));
		WebElement val1 = val.findElement(By.tagName("div"));
		List<WebElement> val2 = val1.findElements(By.tagName("div"));
		for (int i = 0; i < val2.size(); i++) {
			System.out.println(val2.get(i).getText());
			if (val2.get(i).getText().equals(buttonname)) {
				val2.get(i).click();
			}
		}
	}

	public LinkedHashMap<String, String> viewmore(String xpath,
			String resourcename, String tabname) throws IOException,
			InterruptedException {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		WebDriverWait wait = new WebDriverWait(driver, 2000L);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath
				+ "/table/tbody")));
		WebElement mytable = driver.findElement(By
				.xpath(xpath + "/table/tbody"));
		List<WebElement> rows1 = mytable.findElements(By.tagName("tr"));
		for (int i = 0; i < rows1.size(); i++)// WebElement rows : rows1) {
		{
			List<WebElement> ls1 = rows1.get(i).findElements(By.tagName("td"));

			for (int j = 0; j < ls1.size(); j++)// WebElement rows : rows1) {
			{
				if (ls1.get(j).getText().equals(resourcename)) {
					ls1.get(j).click();
					Thread.sleep(4000);
				}
			}
		}boolean tab =  driver.findElements(By.linkText(tabname)).size()>0;
		if(tab){
		driver.findElement(By.linkText(tabname)).click();
		if (!tabname.equals("Networking")) {
			WebElement mytables4 = driver.findElement(By.xpath("//div[h3="
					+ "'" + tabname + "'" + "]"));
			WebElement mytables5 = mytables4.findElement(By.tagName("table"));
			WebElement mybody1 = mytables5.findElement(By.tagName("tbody"));
			List<WebElement> rows = mybody1.findElements(By.tagName("tr"));
			for (int i = 0; i < rows.size(); i++) {
				List<WebElement> th1 = rows.get(i).findElements(
						By.tagName("th"));
				List<WebElement> td1 = rows.get(i).findElements(
						By.tagName("td"));
				for (int j = 0; j < th1.size(); j++) {
					System.out.println(th1.get(j).getText() + "----"
							+ td1.get(j).getText());
					hashMap.put(th1.get(j).getText(), td1.get(j).getText());
				}
			}}
		 else {
			String name1 = "Firewalls";
			String name2 = "Endpoints";
			boolean boo=driver.findElements(By.xpath("//div[h3=" + "'" + name2  + "'" + "]")).size()>0;
			if (boo) {
		      WebElement mytables4 = driver.findElement(By.xpath("//div[h3="
						+ "'" + name2 + "'" + "]"));
				WebElement mytables5 = mytables4.findElement(By
						.tagName("table"));
				WebElement mybody1 = mytables5.findElement(By.tagName("tbody"));
				List<WebElement> rows = mybody1.findElements(By.tagName("tr"));
				List<WebElement> th1 = rows.get(0).findElements(
						By.tagName("th"));
				List<WebElement> td1 = rows.get(1).findElements(
						By.tagName("td"));
				for (int k = 0; k < th1.size(); k++) {
					System.out.println(th1.get(k).getText() + 
							 td1.get(k).getText());
					hashMap.put(th1.get(k).getText(), td1.get(k).getText());
				} boolean boo1 =  driver.findElements(By.xpath("//div/div[@class='tab-pane ng-scope active']/table")).size()>0;
				if(boo1){
			WebElement mytab5 = driver.findElement(By.xpath("//div/div[@class='tab-pane ng-scope active']/table"));
			System.out.println(mytab5.getText());
			WebElement mytabbody1 = mytab5.findElement(By.tagName("tbody"));
			List<WebElement> rowstab = mytabbody1.findElements(By.tagName("tr"));
			for (int i = 0; i < rowstab.size(); i++) {
				List<WebElement> thr1 = rowstab.get(i).findElements(
						By.tagName("th"));
				List<WebElement> tdr1 = rowstab.get(i).findElements(
						By.tagName("td"));
				for (int j = 0; j < thr1.size(); j++) {
			System.out.println(thr1.get(j).getText() + "----" + tdr1.get(j).getText());
			hashMap.put(thr1.get(j).getText(), tdr1.get(j).getText());
				 } 
			}} else {System.out.println("No Network tables exists for this server");}
				boolean boo2 =  driver.findElements(By.xpath("//div/div[@class='tab-pane ng-scope active']/table[@ng-if='details.subnet']")).size()>0;
				if(boo2){
			WebElement mytab6 = driver.findElement(By.xpath("//div/div[@class='tab-pane ng-scope active']/table[@ng-if='details.subnet']"));
			System.out.println(mytab6.getText());
			WebElement mytabbody2 = mytab6.findElement(By.tagName("tbody"));
			List<WebElement> rowstab2 = mytabbody2.findElements(By.tagName("tr"));
			for (int i = 0; i < rowstab2.size(); i++) {
				List<WebElement> thr2 = rowstab2.get(i).findElements(
						By.tagName("th"));
				List<WebElement> tdr2 = rowstab2.get(i).findElements(
						By.tagName("td"));
				for (int j = 0; j < thr2.size(); j++) {
			System.out.println(thr2.get(j).getText() + "----" + tdr2.get(j).getText());
			hashMap.put(thr2.get(j).getText(), tdr2.get(j).getText());
			}}}
				else{System.out.println("No Subnet table exists for this server");}
				}
		 else {	
			boolean boo1=driver.findElements(By.xpath("//div/div[h3=" + "'" + name1  + "'" + "]")).size()>0;
			if (boo1) {
		      WebElement mytables4 = driver.findElement(By.xpath("//div/div[h3="
						+ "'" + name1 + "'" + "]"));
				WebElement mytables5 = mytables4.findElement(By
						.tagName("table"));
				WebElement mybody1 = mytables5.findElement(By.tagName("tbody"));
				List<WebElement> rows = mybody1.findElements(By.tagName("tr"));
				List<WebElement> th1 = rows.get(0).findElements(
						By.tagName("th"));
				List<WebElement> td1 = rows.get(1).findElements(
						By.tagName("td"));
				for (int k = 0; k < th1.size(); k++) {
					System.out.println(th1.get(k).getText() +
							 td1.get(k).getText());
					hashMap.put(th1.get(k).getText(), td1.get(k).getText());
				}
				boolean boo2=driver.findElements(By.xpath("//div/div[@class='tab-pane ng-scope active']/table")).size()>0;
				if(boo2){
			WebElement mytab5 = driver.findElement(By.xpath("//div/div[@class='tab-pane ng-scope active']/table"));
			System.out.println(mytab5.getText());
			WebElement mytabbody1 = mytab5.findElement(By.tagName("tbody"));
			List<WebElement> rowstab = mytabbody1.findElements(By.tagName("tr"));
			for (int i = 0; i < rowstab.size(); i++) {
				List<WebElement> thr1 = rowstab.get(i).findElements(
						By.tagName("th"));
				List<WebElement> tdr1 = rowstab.get(i).findElements(
						By.tagName("td"));
				for (int j = 0; j < thr1.size(); j++) {
			System.out.println(thr1.get(j).getText() + "----" + tdr1.get(j).getText());
			hashMap.put(thr1.get(j).getText(), tdr1.get(j).getText());
				 } 
			}}}else {System.out.println("No Network tables exists for this server");}
			boolean boo3=driver.findElements(By.xpath("//div/div[@class='tab-pane ng-scope active']/table[@ng-if='details.subnet']")).size()>0;
			if(boo3){
			WebElement mytab6 = driver.findElement(By.xpath("//div/div[@class='tab-pane ng-scope active']/table[@ng-if='details.subnet']"));
			System.out.println(mytab6.getText());
			WebElement mytabbody2 = mytab6.findElement(By.tagName("tbody"));
			List<WebElement> rowstab2 = mytabbody2.findElements(By.tagName("tr"));
			for (int i = 0; i < rowstab2.size(); i++) {
				List<WebElement> thr2 = rowstab2.get(i).findElements(
						By.tagName("th"));
				List<WebElement> tdr2 = rowstab2.get(i).findElements(
						By.tagName("td"));
				for (int j = 0; j < thr2.size(); j++) {
			System.out.println(thr2.get(j).getText() + "----" + tdr2.get(j).getText());
			hashMap.put(thr2.get(j).getText(), tdr2.get(j).getText());
			}
			}
			} else{System.out.println("No subnet table exists for this server");}
		 }}}
		else {
			System.out.println("No tabname exists");
			WebElement mytables8 = driver.findElement(By.tagName("table"));
			WebElement mybody1 = mytables8.findElement(By.tagName("tbody"));
			List<WebElement> rows = mybody1.findElements(By.tagName("tr"));
			for (int i = 0; i < rows.size(); i++) {
				List<WebElement> th1 = rows.get(i).findElements(
						By.tagName("th"));
				List<WebElement> td1 = rows.get(i).findElements(
						By.tagName("td"));
				for (int j = 0; j < th1.size(); j++) {
					System.out.println(th1.get(j).getText() + "----"
							+ td1.get(j).getText());
					hashMap.put(th1.get(j).getText(), td1.get(j).getText());
				}
			}
		}
		  return hashMap; 
	}
	
	public void writeexl(ITestResult arg0)  throws IOException { 
         try {
			int res = arg0.getStatus();
			String result;
			if (res == 1) {
				result = "PASS";
			} else if(res == 2){
				result = "FAIL"; } else result = "SKIP";
			int i = 0;
			testresultdata.put(i, new Object[] { arg0.getName(), result });
			i++;
			// write excel file and file name is TestResult.xls
			Set<Integer> keyset = testresultdata.keySet();
			int rownum = sheet.getPhysicalNumberOfRows();
			for (int key : keyset) {
				Row row = sheet.createRow(rownum++);
				Object[] objArr = testresultdata.get(key);
				int cellnum = 0;
				for (Object obj : objArr) {
					System.out.println("Object values" + obj);
					if (cellnum == 0) {
						sheet.setColumnWidth(cellnum, 12000);
					}
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof String)
						cell.setCellValue((String) obj);
					FileOutputStream out = new FileOutputStream(Suite.file1);
					workbook.write(out);
					out.close();
					System.out.println("Excel written successfully..");
				}
			}
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public void status(String stacknames) throws IOException {

		String name[] = stacknames.split(",");
		Properties prop = new Properties();
		FileInputStream fs = new FileInputStream(workspace + "/src/property.Properties");
		prop.load(fs);
		try {
			for (String blueprint : name) {

				WebDriverWait wait = new WebDriverWait(driver, 200L);
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					jobvalidation("Launch stack "+prop.getProperty("startwithname") + "loadbalancer");
				} else if (blueprint.equals("Template for Docker Hub")) {
					jobvalidation("Launch stack "+prop.getProperty("startwithname") + "dockerhub");
				} else {
					jobvalidation("Launch stack "+prop.getProperty("startwithname") + blueprint.toLowerCase());
				}
				driver.findElement(By.xpath(prop.getProperty("stacks"))).click();
				driver.findElement(By.xpath(prop.getProperty("stackclearfilter"))).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("resourcename"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + "loadbalancer");
				} else if (blueprint.equals("Template for Docker Hub")) {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + "dockerhub");
				} else {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + blueprint.toLowerCase());
				}

				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				driver.findElement(By.xpath(prop.getProperty("refreshbutton"))).click();
				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				System.out.println("***********Test*****************");
				Boolean test = wait.until(ExpectedConditions
						.textToBePresentInElementLocated(By.xpath(prop.getProperty("status")), "running"));

				if (test == true) {
					System.out.println(test);
	
					if (blueprint.equals("Sample Web Server with Load Balancer")) {
						driver.findElement(By.xpath(prop.getProperty("status"))).click();
						wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.xpath(prop.getProperty("stackterminatebutton-1"))));
						driver.findElement(By.xpath(prop.getProperty("stackterminatebutton-1"))).click();
						wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.xpath(prop.getProperty("stackterminatebutton-2"))));
						driver.findElement(By.xpath(prop.getProperty("stackterminatebutton-2"))).click();
					} else if (blueprint.equals("Template for Docker Hub")) {
						driver.findElement(By.xpath(prop.getProperty("status"))).click();
						driver.findElement(By
								.xpath("html/body/div[1]/div/div[2]/dcm-grid-with-bulk-actions/div/dcm-grid/table/tbody/tr[2]/td[2]/dcm-grid-row-action-buttons/div[1]/div[1]/button"))
								.click();
						wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.xpath(prop.getProperty("stackterminatebutton-2"))));
						driver.findElement(By.xpath(prop.getProperty("stackterminatebutton-2"))).click();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			String status = driver.findElement(By.xpath(prop.getProperty("status"))).getText();
			Assert.assertEquals(status, "running");
		}
	}

	public void browservalidation(String stacknames) throws SocketException, IOException, InterruptedException {

		String name[] = stacknames.split(",");
		Properties prop = new Properties();
		FileInputStream fs = new FileInputStream(workspace + "/src/property.Properties");
		prop.load(fs);
		try {
			for (String blueprint : name) {

				WebDriverWait wait = new WebDriverWait(driver, 200L);
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("stacks"))));
				driver.findElement(By.xpath(prop.getProperty("stacks"))).click();
				driver.findElement(By.xpath(prop.getProperty("stackclearfilter"))).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("resourcename"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				driver.findElement(By.xpath(prop.getProperty("resourcename")))
						.sendKeys(prop.getProperty("startwithname") + blueprint.toLowerCase());
				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				driver.findElement(By.xpath(prop.getProperty("refreshbutton"))).click();
				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				System.out.println("***********Test*****************");
				Boolean test = wait.until(ExpectedConditions
						.textToBePresentInElementLocated(By.xpath(prop.getProperty("status")), "running"));
				System.out.println(test);

				if (test == true) {

					driver.findElement(By.xpath(prop.getProperty("refreshbutton"))).click();
					wait.until(ExpectedConditions
							.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
					driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
					Thread.sleep(5000);

					List<WebElement> stacklist = driver.findElement(By.xpath(prop.getProperty("stacktablexpath")))
							.findElements(By.tagName("tr"));
					for (WebElement rows1 : stacklist) {
						List<WebElement> stacklist1 = rows1.findElements(By.tagName("td"));
						for (WebElement col1 : stacklist1) {
							if (col1.getText().equals("running")) {
								col1.click();
							}
						}
					}

					wait.until(ExpectedConditions
							.visibilityOfElementLocated(By.xpath(prop.getProperty("viewstacksdetailsbutton"))));
					driver.findElement(By.xpath(prop.getProperty("viewstacksdetailsbutton"))).click();

					wait.until(
							ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("resourcestab"))));
					driver.findElement(By.xpath(prop.getProperty("resourcestab"))).click();

					wait.until(ExpectedConditions
							.elementToBeClickable(By.xpath(prop.getProperty("stackserverefreshbutton"))));
					driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

					List<WebElement> serlist = driver.findElement(By.xpath(prop.getProperty("stackservertablexpath")))
							.findElements(By.tagName("tr"));
					for (WebElement serverrows : serlist) {
						List<WebElement> serlist1 = serverrows.findElements(By.tagName("td"));
						for (WebElement servercol1 : serlist1) {
							System.out.println(servercol1.getText());
							if (servercol1.getText()
									.equals(prop.getProperty("startwithname") + blueprint.toLowerCase())) {
								servercol1.click();
							} else if (servercol1.getText()
									.equals(prop.getProperty("startwithname") + "loadbalancer")) {
								servercol1.click();
							} else if (servercol1.getText().equals(prop.getProperty("startwithname") + "dockerhub")) {
								servercol1.click();
							}
						}
					}

					wait.until(
							ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("publicipxpath"))));
					WebElement IP = driver.findElement(By.xpath(prop.getProperty("publicipxpath")));
					String publicip = IP.getText();

					if (blueprint.equals("Memcached") || blueprint.equals("MongoDB") || blueprint.equals("MySQL")
							|| blueprint.equals("PostgreSQL") || blueprint.equals("RabbitMQ")
							|| blueprint.equals("Redis")) {

						TelnetClient telnet = new TelnetClient();

						if (blueprint.equals("Memcached")) {
							telnet.connect(publicip, 11211);
						} else if (blueprint.equals("MongoDB")) {
							telnet.connect(publicip, 27017);
						} else if (blueprint.equals("MySQL")) {
							telnet.connect(publicip, 3306);
						} else if (blueprint.equals("PostgreSQL")) {
							telnet.connect(publicip, 5432);
						} else if (blueprint.equals("RabbitMQ")) {
							telnet.connect(publicip, 5672);
						} else if (blueprint.equals("Redis")) {
							telnet.connect(publicip, 6379);
						} else {
							System.out.println("Not connected");
						}
						// telnet.setDefaultTimeout(100);
						System.out.println("Connected");
						Thread.sleep(1000);
						System.out.println("*****" + telnet);
						Assert.assertNotNull(telnet);
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						Thread.sleep(3000);
						
					} else if (blueprint.equals("WildFly")) {
						driver.get(publicip + ":8080");
						Thread.sleep(10000);

						String Titile = driver.getTitle();
						Assert.assertEquals("Welcome to WildFly Application Server 8", Titile);
						driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
						// driver.navigate().back();

					} else {
						driver.get(publicip + ":80");
						Thread.sleep(10000);
				
						String Titile = driver.getTitle();

						if (blueprint.equals("Drupal")) {
							Assert.assertEquals("Welcome to Site-Install | Site-Install", Titile);
						} else if (blueprint.equals("Joomla")) {
							Assert.assertEquals("Joomla! Web Installer", Titile);
						} else if (blueprint.equals("LAMP")) {
							Assert.assertEquals("Hello world!", Titile);
						} else if (blueprint.equals("Magento")) {
							Assert.assertEquals("Magento Installation Wizard", Titile);
						} else if (blueprint.equals("Nginx")) {
							Assert.assertEquals("Hello world!", Titile);
						} else if (blueprint.equals("ownCloud")) {
							Assert.assertEquals("ownCloud", Titile);
						} else if (blueprint.equals("phpBB")) {
							Assert.assertEquals("Index of /", Titile);
						} else if (blueprint.equals("Piwik")) {
							Assert.assertEquals("Piwik › Installation", Titile);
						} else if (blueprint.endsWith("Redmine")) {
							Assert.assertEquals("Redmine", Titile);
						} else if (blueprint.equals("Spree")) {
							Assert.assertEquals("Spree Demo Site", Titile);
						} else if (blueprint.equals("Varnish")) {
							Assert.assertEquals("503 Service Unavailable", Titile);
						} else if (blueprint.equals("WordPress")) {
							Assert.assertEquals("WordPress › Installation", Titile);
						} else {
							driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
							driver.navigate().back();
							wait.until(ExpectedConditions
									.visibilityOfElementLocated(By.xpath(prop.getProperty("stacks"))));

						}
						driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
						// driver.navigate().back();

					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			String status = driver.findElement(By.xpath(prop.getProperty("status"))).getText();
			Assert.assertEquals(status, "running");
		}
	}

	public void terminate(String stacknames) throws IOException, InterruptedException {

		String name[] = stacknames.split(",");
		Properties prop = new Properties();
		FileInputStream fs = new FileInputStream(workspace + "/src/property.Properties");
		prop.load(fs);
		Thread.sleep(5000);

		try {
			for (String blueprint : name) {

				WebDriverWait wait = new WebDriverWait(driver, 200L);
				driver.navigate().to(prop.getProperty("url"));
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("stacks"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				driver.findElement(By.xpath(prop.getProperty("stacks"))).click();
				driver.findElement(By.xpath(prop.getProperty("stackclearfilter"))).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("resourcename"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + "loadbalancer");
				} else if (blueprint.equals("Template for Docker Hub")) {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + "dockerhub");
				} else {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + blueprint.toLowerCase());
				}

				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				driver.findElement(By.xpath(prop.getProperty("refreshbutton"))).click();
				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	
				Thread.sleep(5000);
				List<WebElement> stacklist = driver.findElement(By.xpath(prop.getProperty("stacktablexpath")))
						.findElements(By.tagName("tr"));

				for (WebElement rows1 : stacklist) {
					List<WebElement> stacklist1 = rows1.findElements(By.tagName("td"));

					for (WebElement col1 : stacklist1) {
						if (col1.getText().equals("running") || col1.getText().equals("failed")) {

							col1.click();
						}

					}
				}
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath(prop.getProperty("stackterminatebutton-1"))));
				driver.findElement(By.xpath(prop.getProperty("stackterminatebutton-1"))).click();
				Thread.sleep(3000);
				Keyboard kb=((HasInputDevices)driver).getKeyboard();
				kb.sendKeys(Keys.TAB, Keys.TAB, Keys.TAB, Keys.ENTER);
				System.out.println("***********CleanUp***************");
				Thread.sleep(5000);
				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					jobvalidation("Terminate stack "+prop.getProperty("startwithname") + "loadbalancer");
				} else if (blueprint.equals("Template for Docker Hub")) {
					jobvalidation("Terminate stack "+prop.getProperty("startwithname") + "dockerhub");
				} else {
					jobvalidation("Terminate stack "+prop.getProperty("startwithname") + blueprint.toLowerCase());
				}
				driver.findElement(By.xpath(prop.getProperty("stacks"))).click();
				driver.findElement(By.xpath(prop.getProperty("stackclearfilter"))).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("resourcename"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				if (blueprint.equals("Sample Web Server with Load Balancer")) {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + "loadbalancer");
				} else if (blueprint.equals("Template for Docker Hub")) {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + "dockerhub");
				} else {
					driver.findElement(By.xpath(prop.getProperty("resourcename")))
							.sendKeys(prop.getProperty("startwithname") + blueprint.toLowerCase());
				}

				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				driver.findElement(By.xpath(prop.getProperty("refreshbutton"))).click();
				wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(prop.getProperty("refreshbutton"))));
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				Thread.sleep(3000);
				String nomacthfound=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("nomatchingrecordswerefound")))).getText();
				Assert.assertEquals(nomacthfound, "No matching records were found");

			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
