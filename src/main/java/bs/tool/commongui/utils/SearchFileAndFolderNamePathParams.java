package bs.tool.commongui.utils;

import bs.tool.commongui.GuiUtils;

import java.util.Map;
import java.util.regex.Pattern;

public class SearchFileAndFolderNamePathParams {

    public Boolean type_repeatSearch;
    public Boolean type_sameNameSearch;
    public Boolean type_blankSearch;
    public String fileType;
    public boolean containsFile;
    public boolean containsFolder;
    public boolean containsHidden;
    public boolean containsNotHidden;
    public Double sizeFrom;
    public Double sizeTo;
    public Long modifyTimeFrom;
    public Long modifyTimeTo;
    public String filePathCsText;
    public String filePathNCsText;
    public boolean filePathSRegex;
    public String fileNameCsText;
    public String fileNameNCsText;
    public boolean fileNameSRegex;
    public String folderPathCsText;
    public String folderPathNCsText;
    public boolean folderPathSRegex;

    public Pattern filePathCsPattern;
    public Pattern filePathNCsPattern;
    public Pattern fileNameCsPattern;
    public Pattern fileNameNCsPattern;
    public Pattern folderPathCsPattern;
    public Pattern folderPathNCsPattern;

    public SearchFileAndFolderNamePathParams(Map<String, Object> paramsMap) {
        type_repeatSearch = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("type_repeatSearch")));
        type_sameNameSearch = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("type_sameNameSearch")));
        type_blankSearch = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("type_blankSearch")));
        fileType = GuiUtils.toString(paramsMap.get("searchFileType"));
        containsFile = GuiUtils.parseFalse(paramsMap.get("containsFile"));
        containsFolder = GuiUtils.parseFalse(paramsMap.get("containsFolder"));
        containsHidden = GuiUtils.parseFalse(paramsMap.get("containsHidden"));
        containsNotHidden = GuiUtils.parseFalse(paramsMap.get("containsNotHidden"));
        sizeFrom = (Double) paramsMap.get("fileSizeFrom");
        sizeTo = (Double) paramsMap.get("fileSizeTo");
        modifyTimeFrom = (Long) paramsMap.get("modifyTimeFrom");
        modifyTimeTo = (Long) paramsMap.get("modifyTimeTo");
        filePathCsText = GuiUtils.toString(paramsMap.get("filePathContainsText"));
        filePathNCsText = GuiUtils.toString(paramsMap.get("filePathNotContainsText"));
        filePathSRegex = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("filePathSupportRegex")));
        fileNameCsText = GuiUtils.toString(paramsMap.get("fileNameContainsText"));
        fileNameNCsText = GuiUtils.toString(paramsMap.get("fileNameNotContainsText"));
        fileNameSRegex = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("fileNameSupportRegex")));
        folderPathCsText = GuiUtils.toString(paramsMap.get("folderPathContainsText"));
        folderPathNCsText = GuiUtils.toString(paramsMap.get("folderPathNotContainsText"));
        folderPathSRegex = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("folderPathSupportRegex")));

        if (filePathSRegex) {
            filePathCsPattern = Pattern.compile(filePathCsText, Pattern.CASE_INSENSITIVE);
            filePathNCsPattern = Pattern.compile(filePathNCsText, Pattern.CASE_INSENSITIVE);
        }
        if (fileNameSRegex) {
            fileNameCsPattern = Pattern.compile(fileNameCsText, Pattern.CASE_INSENSITIVE);
            fileNameNCsPattern = Pattern.compile(fileNameNCsText, Pattern.CASE_INSENSITIVE);
        }
        if (folderPathSRegex) {
            folderPathCsPattern = Pattern.compile(folderPathCsText, Pattern.CASE_INSENSITIVE);
            folderPathNCsPattern = Pattern.compile(folderPathNCsText, Pattern.CASE_INSENSITIVE);
        }
    }

}
