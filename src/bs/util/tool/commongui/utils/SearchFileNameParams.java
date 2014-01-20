package bs.util.tool.commongui.utils;

import java.util.Map;
import java.util.regex.Pattern;

import bs.util.tool.commongui.GuiUtils;

public class SearchFileNameParams {

	public String fileNameCsText;
	public String fileNameNCsText;
	public boolean fileNameSRegex;

	public Pattern fileNameCsPattern;
	public Pattern fileNameNCsPattern;

	public SearchFileNameParams(Map<String, Object> paramsMap) {
		fileNameCsText = GuiUtils.toString(paramsMap.get("fileNameContainsText"));
		fileNameNCsText = GuiUtils.toString(paramsMap.get("fileNameNotContainsText"));
		fileNameSRegex = Boolean.parseBoolean(GuiUtils.toString(paramsMap.get("fileNameSupportRegex")));
		if (fileNameSRegex) {
			fileNameCsPattern = Pattern.compile(fileNameCsText, Pattern.CASE_INSENSITIVE);
			fileNameNCsPattern = Pattern.compile(fileNameNCsText, Pattern.CASE_INSENSITIVE);
		}
	}

}
