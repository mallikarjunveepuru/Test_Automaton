package common;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import common.GenericClass;

@XmlRootElement(name = "XmltoJava")
public class XmltoJava extends GenericClass {

	private Suite[] suite;
	private String name;

	@XmlElement
	public Suite[] getSuite() {
		return suite;
	}

	public void setSuite(Suite[] suite) {
		this.suite = suite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlRootElement
	public static class Suite {
		private Parameter[] parameter;

		private String suitename;
		private String classname;

		@XmlAttribute
		public String getClassname() {
			return classname;
		}

		public void setClassname(String classname) {
			this.classname = classname;
		}

		@XmlAttribute
		public String getSuitename() {
			return suitename;
		}

		public void setSuitename(String suitename) {
			this.suitename = suitename;
		}

		@XmlElement
		public Parameter[] getParameter() {
			return parameter;
		}

		public void setParameter(Parameter[] parameter) {
			this.parameter = parameter;
		}

		@Override
		public String toString() {
			return "Suite [parameter=" + Arrays.toString(parameter) + ", suitename=" + suitename + "]";
		}

	}

	@XmlRootElement
	public static class Parameter {

		private String name;
		private String testcase;

		@XmlAttribute
		public String getTestcase() {
			return testcase;
		}

		public void setTestcase(String testcase) {
			this.testcase = testcase;
		}

		@XmlAttribute
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Parameter [name=" + name + ", testcase=" + testcase + "]";
		}

	}

	@Override
	public String toString() {
		return "XmltoJava [suite=" + Arrays.toString(suite) + ", name=" + name + "]";
	}

}
