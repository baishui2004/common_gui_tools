package bs.tool.ant;

import bs.tool.eclipse.ProjectPropertiesDeal;
import bs.tool.eclipse.ProjectPropertiesDealInterface;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Properties;

/**
 * Ant Script Auto Build.
 *
 * @author Baishui2004
 * @version 1.1
 * @date 2013-4-5
 */
public class AntScriptAutoBuild {

    /**
     * Eclipse的Java Project、Dynamic Web Project或者MyEclipse的Web Project绝对路径地址.
     */
    private String projectPath;

    /**
     * Project properties.
     */
    private ProjectPropertiesDealInterface projectProperties;

    /**
     * AntScriptAutoBuild conf properties.
     */
    private Properties confProperties;

    /**
     * 生成Ant 脚本属性文件路径.
     */
    private String antPropertiesFilePath;

    /**
     * 生成Ant 脚本文件路径.
     */
    private String antScriptFilePath;

    /**
     * 是否备份已存在的同名构建属性脚本及脚本.
     */
    private boolean isBak = true;

    public void setIsBak(boolean isBak) {
        this.isBak = isBak;
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
     * Main入口.
     * <p>
     * <pre>
     * 只接受传入一个参数, 即Eclipse的Dynamic Web Project或者MyEclipse的Web Project绝对路径地址.
     * </pre>
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length == 2) {
            AntScriptAutoBuild build = new AntScriptAutoBuild();
            ProjectPropertiesDealInterface propertiesDeal = new ProjectPropertiesDeal();
            propertiesDeal.deal(args[0]);
            build.autoBuild(args[0], propertiesDeal, args[1]);
        } else {
            throw new IllegalArgumentException("Parameters error.");
        }
    }

    /**
     * 根据Eclipse/MyEclipse Project属性创建Ant脚本.
     */
    public void autoBuild(String projectPath, ProjectPropertiesDealInterface properties, String taskNames)
            throws IOException, URISyntaxException {
        this.projectPath = projectPath;
        this.projectProperties = properties;
        this.antPropertiesFilePath = this.projectPath + "/build.properties";
        this.antScriptFilePath = this.projectPath + "/build.xml";

        this.confProperties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(new File(getActualPath("conf/AntScriptAutoBuild/conf.properties")));
            this.confProperties.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        String actualTask = getActualAndSortTaskNames(taskNames);
        String specialTask = getSpecialAndSortTaskNames(taskNames);
        String printTask = actualTask + "," + specialTask;
        if (printTask.startsWith(",")) {
            printTask = printTask.substring(1);
        }
        if (printTask.endsWith(",")) {
            printTask = printTask.substring(0, printTask.length() - 1);
        }
        print("Ant Task: " + printTask.replace(",", ", ") + ".\n");

        appendPropertiesAndScriptFile(actualTask, specialTask);
        autoReBuildPropertiesFile();

        print("\nAuto build Ant Script Done.");
    }

    /**
     * 重新排序Ant Task.
     */
    private String getActualAndSortTaskNames(String taskNames) {
        String baseTask = this.confProperties.getProperty("baseTask").trim();
        String javaTask = this.confProperties.getProperty("javaTask").trim();
        String javaWebTask = this.confProperties.getProperty("javaWebTask").trim();
        String generalTask = this.confProperties.getProperty("generalTask").trim();

        String allTask = baseTask + "," + javaTask + "," + javaWebTask + "," + generalTask + ",";
        String[] tasks = allTask.split(",");
        StringBuilder sortTaskName = new StringBuilder();
        for (String task : tasks) {
            if (("," + taskNames.toLowerCase() + ",").contains("," + task.toLowerCase() + ",")) {
                if (!(!this.projectProperties.isJavaWebProject() && ("," + javaWebTask.toLowerCase() + ",")
                        .contains("," + task.toLowerCase() + ","))) {
                    sortTaskName.append("," + task);
                }
            }
        }

        return sortTaskName.length() != 0 ? sortTaskName.substring(1) : sortTaskName.toString();
    }

    /**
     * 特殊Ant Task.
     */
    private String getSpecialAndSortTaskNames(String taskNames) {
        String specialTask = this.confProperties.getProperty("specialTask").trim();
        String[] tasks = specialTask.split(",");
        StringBuilder sortTaskName = new StringBuilder();
        for (String task : tasks) {
            if (("," + taskNames.toLowerCase() + ",").contains("," + task.toLowerCase() + ",")) {
                sortTaskName.append("," + task);
            }
        }
        return sortTaskName.length() != 0 ? sortTaskName.substring(1) : sortTaskName.toString();
    }

    /**
     * 组装build.properties及build.xml.
     */
    private void appendPropertiesAndScriptFile(String taskNames, String specialTaskNames) throws IOException,
            URISyntaxException {
        // build.properties
        appendFile(this.antPropertiesFilePath, getActualPath("conf/AntScriptAutoBuild/properties/"), taskNames,
                "#Auto build properties, " + new Date() + ".\n", "");

        // build.xml
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\n" + "<project name=\""
                + this.projectProperties.getProjectName() + "\" default=\"all\" basedir=\".\">" + "\n\n"
                + "	<!-- properties属性配置文件 -->\n" + "	<property file=\"build.properties\" />";
        String xmlEnd = "\n	<!-- 全部任务 -->\n" + "	<target name=\"all\" depends=\"";
        appendFile(this.antScriptFilePath, getActualPath("conf/AntScriptAutoBuild/script/"), taskNames, xmlHeader,
                xmlEnd + taskNames + "\" />" + "\n\n" + "</project>");

        // 特殊任务，此类任务每个任务单独生成一个构建脚本build-taskName.xml
        if (specialTaskNames.length() != 0) {
            String[] specialTasks = specialTaskNames.split(",");
            for (String task : specialTasks) {
                appendFile(this.antScriptFilePath.substring(0, this.antScriptFilePath.length() - 4) + "-" + task
                        + ".xml", getActualPath("conf/AntScriptAutoBuild/script/"), task, xmlHeader, xmlEnd + task
                        + "\" />" + "\n\n" + "</project>");
            }
        }
    }

    /**
     * 根据taskNames拼装文件.
     */
    private void appendFile(String mergerFilePath, String folderPath, String taskNames, String header, String end)
            throws IOException {
        File mergerFile = new File(mergerFilePath);

        if (mergerFile.exists()) {
            if (isBak) {
                String reNameMergerFilePath = mergerFilePath + "." + new Date().getTime() + "bak";
                File reNameMergerFile = new File(reNameMergerFilePath);
                mergerFile.renameTo(reNameMergerFile);
                print("old file \"" + mergerFile.getName() + "\" renamed to " + "\"" + reNameMergerFile.getName()
                        + "\".");
            } else {
                print("File \"" + mergerFile.getName() + "\" exist but be covered.");
            }
        }

        StringBuilder inSb = new StringBuilder(header + "\n");
        String[] tasks = taskNames.split(",");
        for (String task : tasks) {
            inSb.append("\n");
            appendFile(inSb, folderPath + task);
        }
        inSb.append("\n" + end);

        OutputStream fos = new FileOutputStream(mergerFile);
        // 不采用fos.write(bytes)是为了保证输出文件的存储格式为"UTF-8"
        // fos.write(inSb.toString().getBytes());
        // 使用下面的方式能够保证输出文件的存储格式为"UTF-8"
        Writer writer = new OutputStreamWriter(fos, "UTF-8");
        writer.write(inSb.toString());
        writer.flush();
        writer.close();
        fos.close();

        if (!System.getProperty("os.name").contains("Windows")) {
            try {
                Runtime.getRuntime().exec(new String[]{"chmod", "755", mergerFilePath}).waitFor();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拼装文件.
     */
    private void appendFile(StringBuilder inSb, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        InputStream in = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
        BufferedReader bfReader = new BufferedReader(reader);
        while (true) {
            String line = bfReader.readLine();
            if (line == null) {
                break;
            }
            inSb.append(line).append("\n");
        }
        in.close();
        reader.close();
        bfReader.close();
    }

    /**
     * 根据Eclipse/MyEclipse Project属性重设build.properties部分属性值.
     */
    private void autoReBuildPropertiesFile() throws IOException {
        File propertiesFile = new File(this.antPropertiesFilePath);
        InputStream in = new FileInputStream(propertiesFile);
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
        BufferedReader bfReader = new BufferedReader(reader);
        StringBuilder inSb = new StringBuilder();
        while (true) {
            String line = bfReader.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("project.name=")) {
                line = "project.name=" + this.projectProperties.getProjectName();
            } else if (line.startsWith("dir.lib=") && this.projectProperties.isJavaWebProject()) {
                line = "dir.lib=" + this.projectProperties.getWebappPath() + "/WEB-INF/lib";
            } else if (line.startsWith("dir.web=") && this.projectProperties.isJavaWebProject()) {
                line = "dir.web=" + this.projectProperties.getWebappPath();
            } else if (line.startsWith("project.compile.source=")) {
                line = "project.compile.source=" + this.projectProperties.getCompileSource();
            } else if (line.startsWith("project.compile.target=")) {
                line = "project.compile.target=" + this.projectProperties.getCompileTarget();
            } else if (line.startsWith("dir.src=")) {
                line = "dir.src=" + this.projectProperties.getJavaSourcesPath()[0];
            } else if (line.startsWith("dir.classes=")) {
                line = "dir.classes=" + this.projectProperties.getOutputPath();
            }
            inSb.append(line).append("\n");
        }
        OutputStream fos = new FileOutputStream(propertiesFile);
        // 不采用fos.write(bytes)是为了保证输出文件的存储格式为"UTF-8"
        // fos.write(inSb.toString().getBytes());
        // 使用下面的方式能够保证输出文件的存储格式为"UTF-8"
        Writer writer = new OutputStreamWriter(fos, "UTF-8");
        writer.write(inSb.toString());
        writer.flush();
        writer.close();

        in.close();
        reader.close();
        bfReader.close();
        fos.close();
    }

    /**
     * 获取与Jar lib目录同级的文件(夹)或同级文件夹的子文件(夹)的绝对路径.
     */
    public static String getActualPath(String path) {
        return LIB_PARENT_PATH + path;
    }

    /**
     * Jar lib目录的父路径.
     */
    private static String LIB_PARENT_PATH = getLibParentPath();

    /**
     * 获取Jar lib目录的父路径.
     */
    private static String getLibParentPath() {
        String classResource = AntScriptAutoBuild.class.getName().replace(".", "/") + ".class";
        String path = "";
        try {
            path = AntScriptAutoBuild.class.getClassLoader().getResource(classResource).getPath();
            path = path.replace("+", "%2b"); // "+"号decode后为空格" "，"%2b"号decode后为"+"号
            path = URLDecoder.decode(path, "UTF-8");
            path = path.substring(0, path.length() - classResource.length());
            if (path.contains(".jar!")) {
                path = path.substring(5, path.indexOf(".jar!") + 4);
                path = path.substring(0, path.lastIndexOf("/"));
                path = path.substring(0, path.length() - "lib".length());
            } else {
                path = path.substring(0, path.length() - "classes".length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

}
