package bs.util.tool.commongui.utils;

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

import org.apache.commons.codec.digest.DigestUtils;

import bs.util.tool.commongui.GuiUtils;

public final class FileUtils {

	/**
	 * 相同大小文件查找时用的Map.
	 * 
	 * <pre>
	 * key的格式："文件大小".
	 * </pre>
	 */
	public static Map<String, List<File>> sameSizeFilesMap;
	/**
	 * 重复文件查找时用的Map.
	 * 
	 * <pre>
	 * key的格式："文件大小:MD5".
	 */
	public static Map<String, List<File>> repeatFilesMap;
	/**
	 * 重复文件查找时用的Set.
	 * 
	 * <pre>
	 * 元素String的格式："文件大小:MD5".
	 * 判断repeatFilesMap的value List<File>.size()>1，则加入repeatFilesProp中.
	 * </pre>
	 */
	public static Set<String> repeatFilesProp;

	/**
	 * 迭代获取文件夹目录下所有文件(夹).
	 * 
	 * @param path
	 *            文件夹目录地址
	 * @param paramsMap
	 *            参数
	 * @return <code>List<File></code> 文(夹)件集合
	 */
	public static List<File> getAllSubFiles(String path, Map<String, Object> paramsMap) {
		File file = new File(path);
		List<File> fileList = new ArrayList<File>();
		SearchFileAndFolderNamePathParams searchFileAndFolderNamePathParams = new SearchFileAndFolderNamePathParams(
				paramsMap);
		if (file.isDirectory()) {
			loopDirectory(file, fileList, searchFileAndFolderNamePathParams);
		}
		// 重复文件查找 计算相同大小文件的MD5值
		if (searchFileAndFolderNamePathParams.type_repeatSearch) {
			for (String fileSize : sameSizeFilesMap.keySet()) {
				for (File sameSizeFile : sameSizeFilesMap.get(fileSize)) {
					try {
						byte[] bytes = new byte[2048];
						new FileInputStream(sameSizeFile).read(bytes);
						String prop = fileSize + ":" + DigestUtils.md5Hex(bytes);
						List<File> repeatFiles = repeatFilesMap.get(prop);
						if (repeatFiles == null) {
							repeatFiles = new ArrayList<File>();
							repeatFilesMap.put(prop, repeatFiles);
						} else if (repeatFiles.size() > 0) {
							repeatFilesProp.add(prop);
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
		return fileList;
	}

	/**
	 * 迭代目录，将符合条件的文件(不包括文件夹)添加到fileList中.
	 * 
	 * @param directory
	 *            目录
	 * @param fileList
	 *            file集合
	 * @param sps
	 *            参数
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
	 * @param directory
	 *            目录
	 * @param fileList
	 *            file集合
	 * @param sps
	 *            参数
	 */
	private static void loopDirectory(File directory, List<File> fileList, SearchFileAndFolderNamePathParams sps) {
		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			ifAddFile(file, fileList, sps);
			if (file.isDirectory()) {
				loopDirectory(file, fileList, sps);
			}
		}
	}

	/**
	 * 迭代获取地址所有文件(夹)，包括file本身.
	 * 
	 * @param file
	 *            文件(夹)
	 * @param paramsMap
	 *            参数
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
	 * @param directory
	 *            目录
	 * @param fileList
	 *            file集合
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
	 * @param file
	 *            文件
	 * @param fileList
	 *            file集合
	 * @param sps
	 *            参数
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
	 * @param file
	 *            文件(夹)
	 * @param fileList
	 *            file集合
	 * @param sps
	 *            参数
	 */
	private static void ifAddFile(File file, List<File> fileList, SearchFileAndFolderNamePathParams sps) {
		boolean hidden = file.isHidden();
		boolean isDir = file.isDirectory();

		// 是否是查找空文件/文件夹
		if (sps.type_blankSearch
				&& ((isDir && (file.listFiles() == null || file.listFiles().length != 0)) || (!isDir && file.length() != 0))) {
			return;
		}
		boolean ifAdd = ifAddFileFolderHidden(hidden, isDir, sps); // 判断是否包括隐藏文件、非隐藏文件、文件、文件夹
		if (ifAdd) {
			ifAdd = ifInSideTime(file.lastModified(), sps.modifyTimeFrom, sps.modifyTimeTo); // 比较修改时间
		}
		if (ifAdd) {
			// 文件(夹)路径包含(不包含)字符
			ifAdd = ifMatchText(file.getAbsolutePath(), sps.filePathCsText, sps.filePathNCsText, sps.filePathSRegex,
					sps.filePathCsPattern, sps.filePathNCsPattern);
		}
		if (ifAdd) {
			if (isDir) {
				if (!sps.type_repeatSearch) {
					// 文件夹路径包含(不包含)字符
					ifAdd = ifMatchText(file.getAbsolutePath(), sps.folderPathCsText, sps.folderPathNCsText,
							sps.folderPathSRegex, sps.folderPathCsPattern, sps.folderPathNCsPattern);
				}
			} else {
				long fileSize = file.length();
				ifAdd = ifInSideSize(fileSize, sps.sizeFrom, sps.sizeTo); // 比较文件大小
				if (ifAdd) {
					String fileName = file.getName();
					String fileType = getFileType(fileName);
					ifAdd = sps.fileType.length() == 0 || ("," + sps.fileType + ",").contains("," + fileType + ","); // 比较文件类型
					if (ifAdd) {
						// 文件名包含(不包含)字符
						ifAdd = ifMatchText(fileName, sps.fileNameCsText, sps.fileNameNCsText, sps.fileNameSRegex,
								sps.fileNameCsPattern, sps.fileNameNCsPattern); // 比较名称/路径是否匹配
					}
					// 重复文件查找 先找到大小相同的文件
					if (ifAdd && sps.type_repeatSearch) {
						String prop = Long.toString(fileSize);
						List<File> sameSizeFiles = sameSizeFilesMap.get(prop);
						if (sameSizeFiles == null) {
							sameSizeFiles = new ArrayList<File>();
							sameSizeFilesMap.put(prop, sameSizeFiles);
						}
						sameSizeFiles.add(file);
					}
				}
			}
		}
		// 当查找类型为'重复文件查找'，files最后长度为0，结果保存在FileUtils.repeatFilesProp及FileUtils.repeatFilesMap中
		if (ifAdd && !sps.type_repeatSearch) {
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
