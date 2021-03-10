Java GUI实用小工具集 Common Gui Tools
=====================================

### 简介 ###
<a href="https://github.com/baishui2004/common_gui_tools" target="_blank">Common Gui Tools</a> 是用java编写，GUI界面的实用小工具集，1.5版分六个类别22个小工具。

### 安装 ###
1. Require Java 6.0+
2. 下载压缩包<a href="https://github.com/baishui2004/common_gui_tools/blob/master/dest/common_gui_tools-1.5.zip?raw=true">common_gui_tools-1.5.zip</a>，解压，Windows下双击start.bat运行，Mac或Linux下双击start.sh运行
3. 软件打开后通过菜单栏Character Tools、File Tools等选择要打开的工具

### 工具集索引 ###
#### 字符集工具集 ####
1. <a href="#Character Converter">编码转换：Character Converter</a>
2. <a href="#Encrypt And Decrypt">加密解密：Encrypt And Decrypt</a>
3. <a href="#Escape Character Tool">字符转义工具：Escape Character Tool</a>
4. <a href="#JUniversal Chardet">文本编码识别：JUniversal Chardet</a>
#### 文件工具集 ####
1. <a href="#File Digital Signature">计算文件数字签名：File Digital Signature</a>
2. <a href="#Folder And File Operate">文件(夹)查找操作：Folder And File Operate</a>
3. <a href="#JNotify Visual">文件(夹)变化监控：JNotify Visual</a>
4. <a href="#JODConverter Visual">OpenOffice文档转换：JODConverter Visual</a>
5. <a href="#Text File Split">文本文件切分：Text File Split</a>
#### 常用工具集 ####
1. <a href="#Code Formatter">Code格式化：Code Formatter</a>
2. <a href="#Password Generator">随机密码生成：Password Generator</a>
3. <a href="#QrCode Converter">二维码转换：QrCode Converter</a>
4. <a href="#Regex Tester">正则表达式验证：Regex Tester</a>
5. <a href="#Run Script">执行Script脚本：Run Script</a>
6. <a href="#Time Tool">时间工具：Time Tool</a>
7. <a href="#ZHConverter Visual">中文简体繁体互转：ZHConverter Visual</a>
#### 金融工具集 ####
1. <a href="#Interest Calculator">利息利率计算器：Interest Calculator</a>
#### 其他工具集 ####
1. <a href="#Color Tool">颜色工具：Color Tool</a>
2. <a href="#System Information">运行环境信息：System Information</a>
#### 过时工具集 ####
1. <a href="#Ant Script Auto Build">Ant脚本自动build：Ant Script Auto Build</a>
2. <a href="#Class Finder">Java类查找：Class Finder</a>
3. <a href="#Simple Webview">简易Webview：Simple Webview</a>

### 配置说明 ### 
```
1，本软件采用插件方式，22个小工具即是22个插件，插件配置文件夹conf，
   配置文件有：common_gui_tools.properties、tools.properties以及多个插件的配置；
2，相关配置说明参看各配置文件，可通过修改文件common_gui_tools.properties中属性CommonUseTools修改常用插件;
   默认加载插件Encrypt And Decrypt、Folder And File Operate、Time Tool、Code Formatter； 
3，可通过GUISkin属性修改软件皮肤，fontStyles系列属性修改显示字体。
```

### 开发说明 ### 
```
1，启动类：bs.tool.commongui.GuiMain，参见：src/main/MANIFEST.MF；
2，本地开发运行前先执行mvn clean package，以将src/main/sources下资源拷贝到target，否则运行时会找不到配置文件及图片；
```

### 捐助本项目 ### 
支持长远发展，感谢您的认可！
<br /><b>微信</b>  
![Donate weixin](./documention/images/donate/weixin.png)
<br /><b>支付宝</b>  
![Donate alipay](./documention/images/donate/alipay.jpg)

### 详细介绍 ### 
以下详细简绍每个小工具：
#### 字符集工具集 ####
<b>1. <a id="Character Converter">编码转换：Character Converter</a></b>  
(1)，编码：Encode String表单输入字符，点击右侧对应的Encode按钮，对输入字符进行编码，注意此时的编码类别是“二进制”、“八进制”、“十进制”、“十六进制”，对应的可以使用下面的Decode进行解码  
![Character Converter](./documention/images/Character%20Converter-1.png)  
(2)，乱码解码：选择编码类别“乱码解码”，比如在Big5表单中输入“趼睫”，点击右侧对应Decode按钮，GBK表单中解码出字符“字符”  
![Character Converter](./documention/images/Character%20Converter-2.png)

<b>2. <a id="Encrypt And Decrypt">加密解密：Encrypt And Decrypt</a></b>  
加密解密：默认字符集UTF-8，另可选其他常用字符集，前五种算法可解密，后五种算法不可逆  
![Encrypt And Decrypt](./documention/images/Encrypt%20And%20Decrypt.png)

<b>3. <a id="Escape Character Tool">字符转义工具：Escape Character Tool</a></b>  
HTML、XML、JAVA、JavaScript、CSV字符转义字符及还原，使用Apache Commons-lang的StringEscapeUtils类  
![Escape Character Tool](./documention/images/Escape%20Character%20Tool.png)

<b>4. <a id="JUniversal Chardet">文本编码识别：JUniversal Chardet</a></b>  
检测文件编码，识别准确率高（有一定的误差）  
![JUniversal Chardet](./documention/images/JUniversal%20Chardet.png)  
项目：<a href="https://code.google.com/p/juniversalchardet/" target="_blank">juniversalchardet</a>  
文档：<a href="http://www-archive.mozilla.org/projects/intl/UniversalCharsetDetection.html" target="_blank">Mozilla UniversalCharsetDetection</a>  
其他编码识别项目：<a href="http://sourceforge.net/projects/jchardet/" target="_blank">jchardet</a>&emsp;&nbsp;<a href="http://sourceforge.net/projects/cpdetector/" target="_blank">cpdetector</a>&emsp;&nbsp;<a href="http://wing.comp.nus.edu.sg/~tanyeefa/downloads/charsetdetectstreamreader/" target="_blank">Charset Detect Stream Reader</a>

#### 文件工具集 ####
<b>1. <a id="File Digital Signature">计算文件数字签名：File Digital Signature</a></b>   
计算文件/文件夹子文件的MD5、SHA1值，支持计算大文件，支持对文件名的正则过滤，对满足条件的文件进行计算  
![File Digital Signature](./documention/images/File%20Digital%20Signature.png)

<b>2. <a id="Folder And File Operate">文件(夹)查找操作Folder And File Operate</a></b>  
（1）此工具功能丰富，可通过多种条件(名称，类型，时间，大小，其中名称包括后缀名且不区分大小写)查找文件(夹)   
（2）不仅包括文件(夹)查找，也包括复制、剪切、删除文件及删除空文件夹  
（3）通过工具界面可了解其详细功能，注意操作类型非“默认查找”时，需谨慎操作，以防误删除文件  
（4）文件类型配置文件conf\FolderAndFileOperate\filetype.properties  
![Folder And File Operate](./documention/images/Folder%20And%20File%20Operate.png)

<b>3. <a id="JNotify Visual">文件(夹)变化监控：JNotify Visual</a></b>  
使用JNotify监控文件(夹)增删改及重命名  
![JNotify Visual](./documention/images/JNotify%20Visual.png)  
关于JNotify Visual使用的类包<a href="http://improve-lgpl-jars.googlecode.com/files/jnotify-0.94_improve-1.0.jar">jnotify-0.94_improve-1.0.jar</a>，改进自<a href="http://sourceforge.net/projects/jnotify/" target="_blank">jnotify-0.94</a>，项目地址：<a href="http://code.google.com/p/improve-lgpl-jars/" target="_blank">http://code.google.com/p/improve-lgpl-jars/</a>

<b>4. <a id="JODConverter Visual">OpenOffice文档转换：JODConverter Visual</a></b>  
可视化文档转换，支持常见文档的相互转换，如doc/docx转pdf、rtf、text、html，xls/xlsx转pdf、csv、tsv、html，支持的详细转换类型具体参见插件，需Openoffice后台服务支持  
![JODConverter Visual](./documention/images/JODConverter%20Visual.png)
测试OpenOffice版本：3.4  
配置文件：conf/JODConverterVisual/converter.properties  
转换文档：<a href="http://www.artofsolving.com/opensource/jodconverter/guide/supportedformats" target="_blank">Supported Formats</a>&emsp;&nbsp;<a href="http://www.liferay.com/zh/community/wiki/-/wiki/Main/Document+Conversion+with+OpenOffice" target="_blank">Document Conversion with OpenOffice</a>

<b>5. <a id="Text File Split">文本文件切分：Text File Split</a></b>  
按大小或行数切分文件  
![Text File Split](./documention/images/Text%20File%20Split.png)

#### 常用工具集 ####
<b>1. <a id="Code Formatter">Code格式化：Code Formatter</a></b>  
代码格式化，目前仅支持Json  
![Code Formatter](./documention/images/Code%20Formatter.png)  
项目：<a href="https://github.com/google/gson" target="_blank">gson</a>

<b>2. <a id="Password Generator">随机密码生成：Password Generator</a></b>  
使用类库：https://github.com/vt-middleware/passay  
![Password Generator](./documention/images/Password%20Generator.jpg)

<b>3. <a id="QrCode Converter">二维码转换：QrCode Converter</a></b>  
二维码生成、解析  
![QrCode Converter](./documention/images/QrCode%20Converter.png)  
项目：<a href="https://github.com/zxing/zxing" target="_blank">zxing</a>

<b>4. <a id="Regex Tester">正则表达式验证：Regex Tester</a></b>   
常用正则表达式文件conf\RegexTester\expression.properties  
![Regex Tester](./documention/images/Regex%20Tester.png)  
![Regex Tester help](./documention/images/Regex%20Tester-help.png)

<b>5. <a id="Run Script">执行Script脚本：Run Script</a></b>  
此工具简单目前仅可进行简单的计算  
![Run Script](./documention/images/Run%20Script.png)

<b>6. <a id="Time Tool">时间工具：Time Tool</a></b>  
时间字符串与时间戳的相互转换  
![Time Tool](./documention/images/Time%20Tool.png)

<b>7. <a id="ZHConverter Visual">中文简体繁体互转：ZHConverter Visual</a></b>  
![ZHConverter Visual](./documention/images/ZHConverter%20Visual.png)  
项目：<a href="http://code.google.com/p/java-zhconverter/" target="_blank">java-zhconverter</a>

#### 金融工具集 ####
<b>1. <a id="Interest Calculator">利息利率计算器：Interest Calculator</a></b>  
包括：贷款利率计算、等额本金与等额本息比较计算、提前还款计算、分期名义利率真实利率计算  
![Interest Calculator](./documention/images/Interest%20Calculator.jpg)

#### 其他工具集 ####
<b>1. <a id="Color Tool">颜色工具：Color Tool</a></b>  
此工具可进行颜色RGB码与HTML码的相互转换，调色板显示所填写颜色的效果以及选择颜色  
![Color Tool](./documention/images/Color%20Tool.png)

<b>2. <a id="System Information">运行环境信息：System Information</a></b>  
此工具可查看运行机器的Overview(基础信息)、Running Status、System Properties、支持的字符集、字体  
![System Information](./documention/images/System%20Information.png)

#### 过时工具集 ####
<b>1. <a id="Ant Script Auto Build">Ant脚本自动build: Ant Script Auto Build</a></b>  
通过解析Eclipse的Java Project、Dynamic Web Project或者MyEclipse的Web Project的相关配置文件，自动构建者这三类Project的Ant脚本  
![Ant Script Auto Build](./documention/images/Ant%20Script%20Auto%20Build.png)  
Ant Script Auto Build也提供独立版本，下载文件：<a href="https://github.com/baishui2004/common_gui_tools/blob/master/dest/antScriptAutoBuild-1.21.zip?raw=true">antScriptAutoBuild-1.21.zip</a>

<b>2. <a id="Class Finder">Java类查找：Class Finder</a></b>  
查找文件夹下的.class及.java文件，或者文件夹下压缩文件jar,war,aar,ear,zip内的.class及.java文件  
![Class Finder](./documention/images/Class%20Finder.png)  
可配置查找的文件类型（不限于.class及.java文件，可通过配置扩展用于查找其他类型的文件），压缩文件限于java.util.zip.ZipEntry类可解析的类型，配置文件参见conf\ClassFinder\conf.properties

<b>3. <a id="Simple Webview">简易Webview：Simple Webview</a></b>  
说明：Windows系统下可用，Mac及Linux系统下暂不可用  
![Simple Webview](./documention/images/Simple%20Webview.png)  
项目：<a href="https://sourceforge.net/projects/djproject/files/DJ Native Swing/" target="_blank">The DJ Project</a>