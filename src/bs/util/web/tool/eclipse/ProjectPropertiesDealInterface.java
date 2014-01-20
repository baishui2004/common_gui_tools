package bs.util.web.tool.eclipse;

import java.io.IOException;

import javax.swing.JTextArea;

/**
 * Eclipse Project Properties Deal Interface.
 * 
 * @author baishui2004
 * @version 1.1
 * @date 2013-4-5
 */
public interface ProjectPropertiesDealInterface {

	/**
	 * 解析属性文件获得Project相关属性.
	 */
	public void deal(String projectPath) throws IOException;

	/**
	 * 是否是Eclipse 的Java Project、Dynamic Web Project或者MyEclipse的Web Project.
	 */
	public boolean isJavaOrJavaWebEclipseProject(String projectPath);

	/**
	 * 是否是Java Web Project.
	 */
	public boolean isJavaWebProject();

	/**
	 * 设置项目绝对路径.
	 */
	public void setProjectPath(String projectPath);

	public void setRunLogTextArea(JTextArea runLogTextArea);

	/**
	 * 获取项目绝对路径.
	 */
	public String getProjectPath();

	/**
	 * Java Compile Source.
	 */
	public String getCompileSource();

	/**
	 * Java Compile Target.
	 */
	public String getCompileTarget();

	/**
	 * 获取项目名称.
	 */
	public String getProjectName();

	/**
	 * 获取项目Java源码目录(可能多个).
	 */
	public String[] getJavaSourcesPath();

	/**
	 * 获取项目Java源码编译目录.
	 */
	public String getOutputPath();

	/**
	 * 获取项目Webapp目录.
	 */
	public String getWebappPath();

}
