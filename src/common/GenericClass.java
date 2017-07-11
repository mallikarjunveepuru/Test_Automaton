package common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class GenericClass
{
	public static GenericClass unmarshallClass(String file, @SuppressWarnings("rawtypes") Class c) throws JAXBException, IOException 
	{
		JAXBContext context = JAXBContext.newInstance(c);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		String fileContent = readFileAsString(file);
		fileContent = fileContent.replace("\r\n", "").replace("\n", "");
		GenericClass unMarshalledClass = (GenericClass) unmarshaller.unmarshal(new StringReader(fileContent));
		return unMarshalledClass;
	}

	static String readFileAsString(String filePath) throws java.io.IOException
	{
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try 
		{
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		}
		finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		return new String(buffer);
	}
}