package bs.tool.commongui.utils;

import bs.tool.commongui.GuiUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileUtils {

    /**
     * java.io.tmpdir(临时目录).
     */
    public final static String JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir");

    /**
     * 重复文件查找时用的Map（存储的是所有大小的文件，并非仅是同一个大小大于1的才存储）.
     * <p>
     * <pre>
     * key的格式："文件大小".
     * </pre>
     */
    public static Map<String, List<File>> searchRepeatSizeFilesMap;
    /**
     * 重复文件查找时用的Map（存储的是同一个大小大于1的）.
     * <p>
     * <pre>
     * key的格式："文件大小:MD5".
     */
    public static Map<String, List<File>> searchRepeatSameSizeFilesMap;
    /**
     * 重复文件查找时用的Set.
     * <p>
     * <pre>
     * 元素String的格式："文件大小:MD5".
     * 判断repeatFilesMap的value List<File>.size()>1，则加入repeatFilesSet中.
     * </pre>
     */
    public static Set<String> repeatFilesSet;

    /**
     * 同名文件查找时用的Map（存储的是所有文件名的文件，并非仅是同一个文件名大于1的才存储）.
     * <p>
     * <pre>
     * key的格式："文件名".
     * </pre>
     */
    public static Map<String, List<File>> searchSameNameFilesMap;
    /**
     * 同名文件查找时用的Set.
     * <p>
     * <pre>
     * 元素String的格式："文件名".
     * 判断sameNameFilesMap的value List<File>.size()>1，则加入sameNameFilesSet中.
     * </pre>
     */
    public static Set<String> sameNameFilesSet;

    /**
     * 迭代获取文件夹目录下所有文件(夹).
     *
     * @param path      文件夹目录地址
     * @param paramsMap 参数
     * @return <code>List<File></code> 文(夹)件集合
     */
    public static List<File> getAllSubFiles(String path, Map<String, Object> paramsMap) {
        File file = new File(path);
        List<File> fileList = new ArrayList<File>();
        SearchFileAndFolderNamePathParams searchFileAndFolderNamePathParams = new SearchFileAndFolderNamePathParams(
                paramsMap);
        if (file.isDirectory()) {
            loopDirectory(file, fileList, searchFileAndFolderNamePathParams, 0);
        }
        // 重复文件查找 计算相同大小文件的MD5值
        if (searchFileAndFolderNamePathParams.typeRepeatSearch) {
            for (String fileSize : searchRepeatSizeFilesMap.keySet()) {
                List<File> sizeFiles = searchRepeatSizeFilesMap.get(fileSize);
                // 小于2个的无需判断重复
                if (sizeFiles.size() < 2) {
                    continue;
                }
                for (File sameSizeFile : sizeFiles) {
                    try {
                        byte[] bytes = new byte[2048];
                        new FileInputStream(sameSizeFile).read(bytes);
                        String prop = fileSize + ":" + DigestUtils.md5Hex(bytes);
                        List<File> repeatFiles = searchRepeatSameSizeFilesMap.get(prop);
                        if (repeatFiles == null) {
                            repeatFiles = new ArrayList<File>();
                            searchRepeatSameSizeFilesMap.put(prop, repeatFiles);
                        } else if (repeatFiles.size() > 0) {
                            repeatFilesSet.add(prop);
                        }
                        repeatFiles.add(sameSizeFile);
                    } catch (FileNotFoundException e) {
                        GuiUtils.log(e);
                    } catch (IOException e) {
                        GuiUtils.log(e);
                    }
                }
            }
        }
        // 同名文件查找
        else if (searchFileAndFolderNamePathParams.typeSameNameSearch) {
            for (String fileName : searchSameNameFilesMap.keySet()) {
                List<File> nameFiles = searchSameNameFilesMap.get(fileName);
                if (nameFiles.size() > 1) {
                    sameNameFilesSet.add(fileName);
                }
            }
        }
        return fileList;
    }

    /**
     * 迭代目录，将符合条件的文件(不包括文件夹)添加到fileList中.
     *
     * @param directory 目录
     * @param fileList  file集合
     * @param sps       参数
     */
    public static void loopDirectory(File directory, List<File> fileList, SearchFileNameParams sps) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loopDirectory(file, fileList, sps);
            } else {
                ifAddFile(file, fileList, sps);
            }
        }
    }

    /**
     * 迭代目录，将符合条件的子文件或子文件夹添加到fileList中.
     *
     * @param directory 目录
     * @param fileList  file集合
     * @param sps       参数
     * @param hierarchy 目录层级 与sps.folderHierarchy做对比进行目录层级遍历深度使用，传0不对遍历层级进行限制
     */
    private static void loopDirectory(File directory, List<File> fileList, SearchFileAndFolderNamePathParams sps, int hierarchy) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        hierarchy += 1;
        for (File file : files) {
            ifAddFile(file, fileList, sps);
            if (sps.folderHierarchy != 0 && hierarchy >= sps.folderHierarchy) {
                continue;
            }
            if (file.isDirectory()) {
                loopDirectory(file, fileList, sps, hierarchy);
            }
        }
    }

    /**
     * 迭代获取地址所有文件(夹)，包括file本身.
     *
     * @param file 文件(夹)
     * @return <code>List<File></code> 文(夹)件集合
     */
    public static List<File> getAllFile(File file) {
        List<File> fileList = new ArrayList<File>();
        if (file.exists()) {
            fileList.add(file);
        }
        if (file.isDirectory()) {
            loopDirectory(file, fileList);
        }
        return fileList;
    }

    /**
     * 迭代目录，将所有子文件或者子文件夹添加到fileList中.
     *
     * @param directory 目录
     * @param fileList  file集合
     */
    public static void loopDirectory(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            fileList.add(file);
            if (file.isDirectory()) {
                loopDirectory(file, fileList);
            }
        }
    }

    /**
     * 根据条件判断是否将文件add到file集合.
     *
     * @param file     文件
     * @param fileList file集合
     * @param sps      参数
     */
    private static void ifAddFile(File file, List<File> fileList, SearchFileNameParams sps) {
        // 判断文件名是否满足匹配
        if (FileUtils.ifMatchText(file.getName(), sps.fileNameCsText, sps.fileNameNCsText, sps.fileNameSRegex,
                sps.fileNameCsPattern, sps.fileNameNCsPattern)) {
            fileList.add(file);
        }
    }

    /**
     * 根据条件判断是否将文件(夹)add到file集合.
     *
     * @param file     文件(夹)
     * @param fileList file集合
     * @param sps      参数
     */
    private static void ifAddFile(File file, List<File> fileList, SearchFileAndFolderNamePathParams sps) {
        boolean hidden = file.isHidden();
        boolean isDir = file.isDirectory();

        // 是否是查找空文件/文件夹
        boolean blankDirOrFile = (isDir && (file.listFiles() == null || file.listFiles().length != 0)) || (!isDir && file.length() != 0);
        if (sps.typeBlankSearch && blankDirOrFile) {
            return;
        }
        // 判断是否包括隐藏文件、非隐藏文件、文件、文件夹
        boolean ifAdd = ifAddFileFolderHidden(hidden, isDir, sps);
        if (ifAdd) {
            // 比较修改时间
            ifAdd = ifInSideTime(file.lastModified(), sps.modifyTimeFrom, sps.modifyTimeTo);
        }
        if (ifAdd) {
            // 文件(夹)路径包含(不包含)字符
            ifAdd = ifMatchText(file.getAbsolutePath(), sps.filePathCsText, sps.filePathNCsText, sps.filePathSRegex,
                    sps.filePathCsPattern, sps.filePathNCsPattern);
        }
        if (ifAdd) {
            if (isDir) {
                if (!sps.typeRepeatSearch && !sps.typeSameNameSearch) {
                    // 文件夹路径包含(不包含)字符
                    ifAdd = ifMatchText(file.getAbsolutePath(), sps.folderPathCsText, sps.folderPathNCsText,
                            sps.folderPathSRegex, sps.folderPathCsPattern, sps.folderPathNCsPattern);
                }
            } else {
                long fileSize = file.length();
                // 比较文件大小
                ifAdd = ifInSideSize(fileSize, sps.sizeFrom, sps.sizeTo);
                if (ifAdd) {
                    String fileName = file.getName();
                    String fileType = getFileType(fileName);
                    // 比较文件类型
                    ifAdd = sps.fileType.length() == 0 || ("," + sps.fileType + ",").contains("," + fileType + ",");
                    if (ifAdd) {
                        // 文件名包含(不包含)字符，比较名称/路径是否匹配
                        ifAdd = ifMatchText(fileName, sps.fileNameCsText, sps.fileNameNCsText, sps.fileNameSRegex,
                                sps.fileNameCsPattern, sps.fileNameNCsPattern);
                    }
                    if (ifAdd) {
                        // 重复文件查找 先找到大小相同的文件
                        if (sps.typeRepeatSearch && !isDir) {
                            String prop = Long.toString(fileSize);
                            if (sps.repeatSameSuffix) {
                                prop = fileType + ":" + prop;
                            }
                            List<File> sizeFiles = searchRepeatSizeFilesMap.get(prop);
                            if (sizeFiles == null) {
                                sizeFiles = new ArrayList<File>();
                                searchRepeatSizeFilesMap.put(prop, sizeFiles);
                            }
                            sizeFiles.add(file);
                        } else if (sps.typeSameNameSearch) {
                            // 同名文件查找
                            String prop = "";
                            if (sps.repeatSameSuffix) {
                                prop = fileName;
                            } else {
                                prop = fileName.substring(0, fileName.length() - fileType.length() - 1);
                            }
                            List<File> nameFiles = searchSameNameFilesMap.get(prop);
                            if (nameFiles == null) {
                                nameFiles = new ArrayList<File>();
                                searchSameNameFilesMap.put(prop, nameFiles);
                            }
                            nameFiles.add(file);
                        }
                    }
                }
            }
        }
        // 当查找类型为'重复文件查找'，files最后长度为0，结果保存在FileUtils.repeatFilesSet及FileUtils.repeatFilesMap中
        // 当查找类型为'同名文件查找'，files最后长度为0，结果保存在FileUtils.sameNameFilesSet及FileUtils.sameNameFilesMap中
        if (ifAdd && !sps.typeRepeatSearch && !sps.typeSameNameSearch) {
            fileList.add(file);
        }
    }

    /**
     * 判断是否包括隐藏文件、非隐藏文件、文件、文件夹.
     */
    public static boolean ifAddFileFolderHidden(boolean hidden, boolean isDir, SearchFileAndFolderNamePathParams sps) {
        boolean add = true;
        if (!isDir && !sps.containsFile) {
            add = false;
        } else if (isDir && !sps.containsFolder) {
            add = false;
        }
        if (!hidden && !sps.containsNotHidden) {
            add = false;
        } else if (hidden && !sps.containsHidden) {
            add = false;
        }
        return add;
    }

    /**
     * 比较是否在时间之间.
     */
    public static boolean ifInSideTime(long side, Long from, Long to) {
        boolean inSide = true;
        if (from != null && to == null) {
            inSide = side >= from;
        } else if (from == null && to != null) {
            inSide = side <= to;
        } else if (from != null && to != null) {
            inSide = side >= from && side <= to;
        }
        return inSide;
    }

    /**
     * 比较是否在大小之间.
     */
    public static boolean ifInSideSize(long side, Double from, Double to) {
        boolean inSide = true;
        if (from != null && to == null) {
            inSide = side >= from;
        } else if (from == null && to != null) {
            inSide = side <= to;
        } else if (from != null && to != null) {
            inSide = side >= from && side <= to;
        }
        return inSide;
    }

    /**
     * 获取文件后缀名，小写.
     */
    public static String getFileType(String fileName) {
        String fileType = "";
        int lIndex = fileName.lastIndexOf(".");
        if (lIndex > 0) {
            fileType = fileName.substring(lIndex + 1, fileName.length()).toLowerCase();
        }
        return fileType;
    }

    /**
     * 判断文件(夹)名是否满足匹配.
     */
    public static boolean ifMatchText(String fileName, String csText, String ncsText, boolean sRegex,
                                      Pattern csPattern, Pattern ncsPattern) {
        boolean match = true;
        String lFileName = fileName.toLowerCase();
        String lcsText = csText.toLowerCase();
        String lncsText = ncsText.toLowerCase();
        if (sRegex) {
            if (csText.length() != 0) {
                Matcher m = csPattern.matcher(fileName);
                match = m.find();
            }
            if (match && ncsText.length() != 0) {
                Matcher m = ncsPattern.matcher(fileName);
                match = !m.find();
            }
        } else {
            if (csText.length() != 0) {
                match = lFileName.contains(lcsText);
            }
            if (match && ncsText.length() != 0) {
                match = !lFileName.contains(lncsText);
            }
        }
        return match;
    }

}
