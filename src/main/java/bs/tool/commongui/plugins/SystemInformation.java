package bs.tool.commongui.plugins;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.management.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

/**
 * 系统信息.
 */
public class SystemInformation extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 查询类别.
     */
    private String[] searchTypes = new String[]{"Overview", "Running Status", "System Properties", "Support Charsets",
            "Support Fonts"};

    /**
     * 当前查询类别.
     */
    private String curSearchType = searchTypes[0];

    /**
     * 输出文本域.
     */
    private JTextArea textArea = createJTextArea(GuiUtils.font14_cn);

    public SystemInformation() {

        // 主面板：边界布局，分North、Center两部分，North用于放置条件控件，Center是放置输出
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // North面板
        addJLabel(topPanel, "Search Type:", GuiUtils.font14b);
        // 查询类别下拉框
        addJComboBox(topPanel, searchTypes, GuiUtils.font13, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                curSearchType = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        addJLabel(topPanel, " ", GuiUtils.font14b);
        addJButton(topPanel, " Search ", "", GuiUtils.font14b, new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                int typeLen = searchTypes.length;
                String text = "";
                if (searchTypes[typeLen - 5].equals(curSearchType)) {
                    text = getSystemOverview();
                } else if (searchTypes[typeLen - 4].equals(curSearchType)) {
                    text = getRunningStatus();
                } else if (searchTypes[typeLen - 3].equals(curSearchType)) {
                    text = getSystemProperties();
                } else if (searchTypes[typeLen - 2].equals(curSearchType)) {
                    text = getAvailableCharsets();
                } else if (searchTypes[typeLen - 1].equals(curSearchType)) {
                    text = getAvailableFonts();
                }
                textArea.append(text);
            }

            public void mousePressed(MouseEvent e) {
                // clear文本域
                textArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    /**
     * System Information Overview.
     */
    private String getSystemOverview() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(
                getPropertiesString(new String[]{"java.runtime.name", "java.runtime.version", "java.vm.name"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"os.name", "os.version", "sun.os.patch.level", "os.arch",
                        "sun.arch.data.model"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"user.home", "user.dir", "user.name", "user.timezone",
                        "user.language", "user.country", "user.variant"}));
        sb.append("\n").append(getPropertiesString(new String[]{"java.home", "java.io.tmpdir"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"path.separator", "file.separator", "line.separator"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"file.encoding", "file.encoding.pkg", "sun.jnu.encoding",
                        "sun.cpu.endian", "sun.io.unicode.encoding", "sun.cpu.isalist"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"sun.boot.library.path", "sun.boot.class.path", "java.ext.dirs",
                        "java.endorsed.dirs", "java.library.path", "java.class.path"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"sun.desktop", "awt.toolkit", "java.awt.graphicsenv",
                        "java.awt.printerjob"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"java.version", "java.vm.info", "java.vm.version",
                        "java.class.version", "sun.java.launcher", "sun.management.compiler"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"java.specification.name", "java.specification.vendor",
                        "java.specification.version", "java.vendor", "java.vendor.url", "java.vendor.url.bug"}));
        sb.append("\n").append(
                getPropertiesString(new String[]{"java.vm.specification.name", "java.vm.specification.vendor",
                        "java.vm.specification.version", "java.vm.vendor"}));
        return sb.substring(1);
    }

    private String getPropertiesString(String[] keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append("\n").append(GuiUtils.getFillUpString(key, 32));
            String value = System.getProperty(key);
            if ("line.separator".equals(key)) {
                sb.append(value.replace("\n", "\\n").replace("\r", "\\r"));
            } else {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    /**
     * 虚拟机运行情况.
     */
    private String getRunningStatus() {
        StringBuilder sb = new StringBuilder();

        // 虚拟机开始及总运行时间
        RuntimeMXBean runBean = ManagementFactory.getRuntimeMXBean();
        sb.append("\n").append(GuiUtils.getFillUpString("Current Time", 32)).append(new Date());
        sb.append("\n").append(GuiUtils.getFillUpString("Start Time", 32)).append(new Date(runBean.getStartTime()));
        sb.append("\n").append(GuiUtils.getFillUpString("Run Time", 32))
                .append(GuiUtils.getCountTime(runBean.getUptime()));

        sb.append("\n");
        // 操作系统可用处理器数目
        OperatingSystemMXBean operateBean = ManagementFactory.getOperatingSystemMXBean();
        sb.append("\n").append(GuiUtils.getFillUpString("System Processors", 32))
                .append(operateBean.getAvailableProcessors());

        // 内存使用情况
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMem = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMem = memBean.getNonHeapMemoryUsage();

        sb.append("\n");
        sb.append("\n").append(GuiUtils.getFillUpString("Heap Used", 32)).append(getMemoey(heapMem.getUsed()));
        sb.append("\n").append(GuiUtils.getFillUpString("Heap Committed", 32))
                .append(getMemoey(heapMem.getCommitted()));
        sb.append("\n").append(GuiUtils.getFillUpString("Heap Init", 32)).append(getMemoey(heapMem.getInit()));
        sb.append("\n").append(GuiUtils.getFillUpString("Heap Max", 32)).append(getMemoey(heapMem.getMax()));
        sb.append("\n");
        sb.append("\n").append(GuiUtils.getFillUpString("NonHeap Used", 32)).append(getMemoey(nonHeapMem.getUsed()));
        sb.append("\n").append(GuiUtils.getFillUpString("NonHeap Committed", 32))
                .append(getMemoey(nonHeapMem.getCommitted()));
        sb.append("\n").append(GuiUtils.getFillUpString("NonHeap Init", 32)).append(getMemoey(nonHeapMem.getInit()));
        sb.append("\n").append(GuiUtils.getFillUpString("NonHeap Max", 32)).append(getMemoey(nonHeapMem.getMax()));

        sb.append("\n");
        Runtime curRuntime = Runtime.getRuntime();
        sb.append("\n").append(GuiUtils.getFillUpString("Free Memory", 32))
                .append(getMemoey(curRuntime.freeMemory()));
        sb.append("\n").append(GuiUtils.getFillUpString("Total Memory", 32))
                .append(getMemoey(curRuntime.totalMemory()));
        sb.append("\n").append(GuiUtils.getFillUpString("Max Memory", 32)).append(getMemoey(curRuntime.maxMemory()));

        // 当前运行线程
        sb.append("\n");
        sb.append("\n").append(GuiUtils.getFillUpString("  Thread Name", 32)).append("Thread ID");
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] threads = threadBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threads);
        for (int i = threadInfos.length - 1; i > -1; i--) {
            sb.append("\n").append(GuiUtils.getFillUpString(threadInfos[i].getThreadName(), 32))
                    .append(threadInfos[i].getThreadId());
        }
        return sb.toString();
    }

    private String getMemoey(long mem) {
        return (double) mem / (1024.0 * 1024.0) + "M";
    }

    /**
     * System.getProperties().
     */
    private String getSystemProperties() {
        StringBuilder sb = new StringBuilder();
        Properties properties = System.getProperties();
        Set<Object> sets = properties.keySet();
        List<String> keys = new ArrayList<String>();
        for (Object key : sets) {
            keys.add((String) key);
        }
        Collections.sort(keys);
        for (String key : keys) {
            sb.append("\n").append(GuiUtils.getFillUpString(key, 32));
            String value = properties.getProperty(key);
            if ("line.separator".equals(key)) {
                sb.append(value.replace("\n", "\\n").replace("\r", "\\r"));
            } else {
                sb.append(value);
            }
        }
        sb.append("\n\nCount " + sets.size() + " Properties.\n");
        return sb.substring(1);
    }

    /**
     * JDK 1.6 中文 API Charset类说明： Java 编程语言的本机字符编码方案是 UTF-16。因此 Java 平台的 charset 定义了 16 位 UTF-16 代码单元序列和字节序列之间的映射关系.
     */
    private String getAvailableCharsets() {
        int interval = 5;
        StringBuilder sb = new StringBuilder();
        SortedMap<String, Charset> map = GuiUtils.availableCharsets();
        int i = 0; // 首字母小写对应ASCII的十进制值
        int i_o = 0; // 上一个首字母小写对应ASCII的十进制值
        int l = 0; // 同一字母开头的字符集数目过多, 则10个分一行
        for (String key : map.keySet()) {
            i = (key.substring(0, 1).toLowerCase()).toCharArray()[0];
            if (i_o != 0 && i_o != i) {
                i_o = i;
                if (l % interval != 0) {
                    sb.append("\n");
                }
                l = 0;
            }
            sb.append(GuiUtils.getFillUpString(key, 22));
            l++;
            if (i_o == i && l % interval == 0) {
                sb.append("\n");
            }
            if (i_o == 0) {
                i_o = i;
            }
        }
        sb.append("\n\nCount " + map.size() + " Charsets.\n");
        return sb.toString();
    }

    /**
     * 支持字体.
     */
    private String getAvailableFonts() {
        StringBuilder sb = new StringBuilder();
        Font[] fonts = GuiUtils.availableFonts();
        List<String> names = new ArrayList<String>();
        for (Font font : fonts) {
            names.add(font.getFontName());
        }
        Collections.sort(names); // 排序
        int i = 0;
        for (String name : names) {
            sb.append(GuiUtils.getFillUpString(name, 42));
            i++;
            if (i % 3 == 0) {
                sb.append("\n");
            }
        }
        sb.append("\n\nCount " + fonts.length + " Fonts.\n");
        return sb.toString();
    }

}
