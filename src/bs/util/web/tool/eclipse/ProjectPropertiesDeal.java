package bs.util.web.tool.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Eclipse Project Properties Deal.
 * 
 * @author Baishui2004
 * @version 1.1
 * @date 2013-4-5
 */
public class ProjectPropertiesDeal implements ProjectPropertiesDealInterface {

	/**
	 * Eclipse的Java Project、Dynamic Web Project或者MyEclipse的Web Project绝对路径地址.
	 */
	private String projectPath;

	/**
	 * 解析文件'.project'以获取Project Name, 此种方法要求'.project'文件根节点的第一个name子节点即是Project Name.
	 */
	private String projectNameFile = "/.project";
	/**
	 * 解析文件'.settings/org.eclipse.jdt.core.prefs'以获取compileSource以及compileTarget.
	 */
	private String compilePropsFile = "/.settings/org.eclipse.jdt.core.prefs";
	/**
	 * 如果没有'.settings/org.eclipse.jdt.core.prefs'文件，则解析文件'.settings/org.eclipse.wst.common.project.facet.core.xml'以"installed java facet version".
	 */
	private String projectFacetCoreFile = "/.settings/org.eclipse.wst.common.project.facet.core.xml";
	/**
	 * 解析文件'.classpath'以获取javaSourcesPath以及outputPath.
	 */
	private String classpathFile = "/.classpath";
	/**
	 * 解析文件'.settings/.jsdtscope'以获取webappPath.
	 */
	private String webappPropsFile = "/.settings/.jsdtscope";

	/**
	 * Project Name.
	 */
	private String projectName;
	/**
	 * Java Compile Source.
	 */
	private String compileSource;
	/**
	 * Java Compile Target.
	 */
	private String compileTarget;
	/**
	 * Java Sources path.
	 */
	private String[] javaSourcesPath;
	/**
	 * Output classes path.
	 */
	private String outputPath;

	/**
	 * 是否是Java Web Project.
	 */
	private boolean javaWebProject;

	/**
	 * Project Webapp path.
	 */
	private String webappPath;

	/**
	 * Main入口.
	 * 
	 * <pre>
	 * 只接受传入一个参数, 即Eclipse的Java Project、Dynamic Web Project或者MyEclipse的Web Project绝对路径地址.
	 * </pre>
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Parameters error.");
		}
		ProjectPropertiesDealInterface propertiesDeal = new ProjectPropertiesDeal();
		propertiesDeal.deal(args[0]);
	}

	/**
	 * 解析属性文件获得Project相关属性.
	 */
	public void deal(String projectPath) throws IOException {

		setProjectPath(projectPath);
		if (!isJavaOrJavaWebEclipseProject(projectPath)) {
			throw new IllegalArgumentException("The Path: \'" + projectPath
					+ "\' not has a Eclipse Java Project, Dynamic Web Project or MyEclipse Web Project.");
		}

		dealProjectName();
		dealCompileSourceAndTarget();
		dealSourceAndOutput();
		if (isJavaWebProject()) {
			dealWebappPath();
		}

		print("************ Project properties Start ************");
		print("Project Path: " + projectPath);
		print("Project Name: " + getProjectName());
		print("Java Compile Source: " + getCompileSource());
		print("Java Compile Target: " + getCompileTarget());
		String[] javaSourcesPath = getJavaSourcesPath();
		if (javaSourcesPath != null) {
			for (int i = 0; i < javaSourcesPath.length; i++) {
				print("Sources Path: " + javaSourcesPath[i]);
			}
		}
		print("Output Path: " + getOutputPath());
		print("Is Java Web Project: " + isJavaWebProject());
		if (isJavaWebProject()) {
			print("Webapp Path: " + getWebappPath());
		}
		print("************ Project properties End ************\n");
	}

	/**
	 * 运行日志输出文本域.
	 */
	private JTextArea runLogTextArea;

	public void setRunLogTextArea(JTextArea runLogTextArea) {
		this.runLogTextArea = runLogTextArea;
	}

	/**
	 * 输出.
	 */
	private void print(String log) {
		if (runLogTextArea != null) {
			runLogTextArea.append(log + "\n");
		} else {
			System.out.print(log + "\n");
		}
	}

	/**
	 * 是否是Eclipse 的Java Project、Dynamic Web Project或者MyEclipse的Web Project.
	 */
	public boolean isJavaOrJavaWebEclipseProject(String projectPath) {
		if (!new File(projectPath + this.projectNameFile).exists()) {
			return false;
		} else if (!new File(projectPath + this.compilePropsFile).exists()
				&& !new File(projectPath + this.projectFacetCoreFile).exists()) {
			return false;
		} else if (!new File(projectPath + this.classpathFile).exists()) {
			return false;
		}
		if (!new File(this.projectPath + this.webappPropsFile).exists()) {
			javaWebProject = false;
		} else {
			javaWebProject = true;
		}
		return true;
	}

	public boolean isJavaWebProject() {
		return this.javaWebProject;
	}

	public String getProjectPath() {
		return this.projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public String getCompileSource() {
		return this.compileSource;
	}

	public String getCompileTarget() {
		return this.compileTarget;
	}

	public String[] getJavaSourcesPath() {
		return this.javaSourcesPath;
	}

	public String getOutputPath() {
		return this.outputPath;
	}

	public String getWebappPath() {
		return this.webappPath;
	}

	/**
	 * 解析文件'.project'以获取Project Name, 此种方法要求'.project'文件根节点的第一个name子节点即是Project Name.
	 */
	private void dealProjectName() throws FileNotFoundException {
		String xmlPath = this.projectPath + this.projectNameFile;
		parseXmlProperties(xmlPath, new XmlParse() {
			@Override
			public void parse(XMLStreamReader reader) throws XMLStreamException {
				boolean pdFlag = false;
				boolean pnFlag = false;
				while (reader.hasNext()) {
					int i = reader.next();
					if (i == XMLStreamConstants.START_ELEMENT) {
						String elementName = reader.getLocalName();
						if ("projectDescription".equals(elementName)) {
							pdFlag = true;
						}
						if (pdFlag && "name".equals(elementName)) {
							pnFlag = true;
						}
					}
					if (pnFlag && reader.hasText()) {
						projectName = reader.getText().trim();
						break;
					}
				}
			}
		});
	}

	/**
	 * 解析文件'.settings/org.eclipse.jdt.core.prefs'以获取compileSource以及compileTarget.
	 * 如果没有'.settings/org.eclipse.jdt.core.prefs'文件，则解析文件'.settings/org.eclipse.wst.common.project.facet.core.xml'以获取"installed java facet version".
	 */
	private void dealCompileSourceAndTarget() throws IOException {
		String filePath = this.projectPath + this.compilePropsFile;
		if (new File(filePath).exists()) {
			Properties properties = new Properties();
			InputStream in = null;
			try {
				in = new FileInputStream(new File(filePath));
				properties.load(in);
			} finally {
				if (in != null) {
					in.close();
				}
			}
			compileSource = properties.getProperty("org.eclipse.jdt.core.compiler.source");
			compileTarget = properties.getProperty("org.eclipse.jdt.core.compiler.compliance");
		} else {
			String xmlPath = this.projectPath + this.projectFacetCoreFile;
			parseXmlProperties(xmlPath, new XmlParse() {
				@Override
				public void parse(XMLStreamReader reader) throws XMLStreamException {
					while (reader.hasNext()) {
						int i = reader.next();
						if (i == XMLStreamConstants.START_ELEMENT) {
							String elementName = reader.getLocalName();
							if ("installed".equals(elementName)) {
								String kind = reader.getAttributeValue(null, "facet");
								if ("java".equals(kind)) {
									compileSource = reader.getAttributeValue(null, "version");
									compileTarget = compileSource;
								}
							}
						}
					}
				}
			});
		}
	}

	/**
	 * 解析文件'.classpath'以获取javaSourcesPath以及outputPath.
	 */
	private void dealSourceAndOutput() throws FileNotFoundException {
		String xmlPath = this.projectPath + this.classpathFile;
		parseXmlProperties(xmlPath, new XmlParse() {
			@Override
			public void parse(XMLStreamReader reader) throws XMLStreamException {
				List<String> javaSourcesPaths = new ArrayList<String>();
				while (reader.hasNext()) {
					int i = reader.next();
					if (i == XMLStreamConstants.START_ELEMENT) {
						String elementName = reader.getLocalName();
						if ("classpathentry".equals(elementName)) {
							String kind = reader.getAttributeValue(null, "kind");
							String path = reader.getAttributeValue(null, "path");
							if ("src".equals(kind)) {
								javaSourcesPaths.add(path);
							} else if ("output".equals(kind)) {
								outputPath = path;
							}
						}
					}
				}
				javaSourcesPath = new String[javaSourcesPaths.size()];
				for (int i = 0; i < javaSourcesPath.length; i++) {
					javaSourcesPath[i] = javaSourcesPaths.get(i);
				}
			}
		});
	}

	/**
	 * 解析文件'.settings/.jsdtscope'以获取webappPath.
	 */
	private void dealWebappPath() throws FileNotFoundException {
		String xmlPath = this.projectPath + this.webappPropsFile;
		parseXmlProperties(xmlPath, new XmlParse() {
			@Override
			public void parse(XMLStreamReader reader) throws XMLStreamException {
				while (reader.hasNext()) {
					int i = reader.next();
					if (i == XMLStreamConstants.START_ELEMENT) {
						String elementName = reader.getLocalName();
						if ("classpathentry".equals(elementName)) {
							String kind = reader.getAttributeValue(null, "kind");
							if ("src".equals(kind)) {
								webappPath = reader.getAttributeValue(null, "path");
							}
						}
					}
				}
			}
		});
	}

	/**
	 * 解析XML接口.
	 */
	private interface XmlParse {
		void parse(XMLStreamReader reader) throws XMLStreamException;
	}

	/**
	 * 解析XML.
	 */
	private static void parseXmlProperties(String xmlPath, XmlParse xmlParse) throws FileNotFoundException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream stream = null;
		XMLStreamReader reader = null;
		try {
			stream = new FileInputStream(new File(xmlPath));
			reader = factory.createXMLStreamReader(stream);
			xmlParse.parse(reader);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (XMLStreamException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
