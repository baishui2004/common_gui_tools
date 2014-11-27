Java GUI实用小工具集 Common Gui Tools
=====================================

<a href="https://github.com/baishui2004/common_gui_tools" target="_blank">Common Gui Tools</a> 是用java编写，GUI界面的实用小工具集，1.21版有16个小工具： 

<pre>
1，<a href="#Character Converter">编码转换：Character Converter</a>
2，<a href="#Encrypt And Decrypt">加密解密：Encrypt And Decrypt</a>
3，<a href="#File Digital Signature">计算文件数字签名：File Digital Signature，支持大文件</a>
4，<a href="#Regex Tester">正则表达式验证：Regex Tester</a>
5，<a href="#Run Script">执行Script脚本：Run Script</a>
6，<a href="#Folder And File Operate">文件(夹)查找操作：Folder And File Operate</a>
7，<a href="#Ant Script Auto Build">Ant脚本自动build：Ant Script Auto Build</a>
8，<a href="#Class Finder">Java类查找：Class Finder，查找本地的*.class或*.java</a>
9，<a href="#Color Tool">颜色工具：Color Tool</a>
10，<a href="#System Information">运行环境信息：System Information</a>
11，<a href="#JODConverter Visual">OpenOffice文档转换JODConverter Visual，需Openoffice后台服务支持</a>
12，<a href="#JNotify Visual">文件(夹)变化监控：JNotify Visual</a>
13，<a href="#JUniversal Chardet">文本编码识别：JUniversal Chardet</a>
14，<a href="#ZHConverter Visual">中文简体繁体互转：ZHConverter Visual</a>
15，<a href="#Escape Character Tool">字符转义工具：Escape Character Tool</a>
16，<a href="#Short URL">短网址转换：Short URL</a>
</pre>

   欢迎您使用并提供宝贵意见！


<br /><b>下载及使用说明：</b>
<br />Require Java 6.0+
<br />下载压缩包<a href="https://github.com/baishui2004/common_gui_tools/blob/master/dest/common_gui_tools-1.21.zip?raw=true">common_gui_tools-1.21.zip</a>，解压，Windows下，用start.bat运行，Linux下使用start.sh运行。
<!-- more -->

<br /><b>配置说明：</b> 
<pre>
1，本软件采用插件方式，13个小工具即是13个插件，插件配置文件夹conf，
   配置文件有：common_gui_tools.properties、more_tools.properties以及多个插件的配置；
2，相关配置说明参看各配置文件，可通过修改文件common_gui_tools.properties中属性CommonUseTools修改常用插件;
   默认加载插件Encrypt And Decrypt、Folder And File Operate及Class Finder； 
3，可通过GUISkin属性修改软件皮肤，fontStyles系列属性修改显示字体。
</pre>



<br /><br /><b>下面逐个简单简绍每个小工具：</b> 打开软件后可通过菜单栏Tools、More Tools选择工具显示，通过各工具的关闭按钮或菜单栏再次点击工具关闭显示。
<br />
<br /><b>1，<a id="Character Converter">编码转换：Character Converter</a></b>
<br />(1)，编码：Encode String表单输入字符，点击右侧对应的Encode按钮，对输入字符进行编码，注意此时的编码类别是“二进制”、“八进制”、“十进制”、“十六进制”。对应的可以使用下面的Decode进行解码。
<br />
![Character Converter](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Character%20Converter-1.png)
<br />(2)，乱码解码：选择编码类别“乱码解码”，比如在Big5表单中输入“趼睫”，点击右侧对应Decode按钮，GBK表单中解码出字符“字符”。
<br />
![Character Converter](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Character%20Converter-2.png)

<b>2，<a id="Encrypt And Decrypt">加密解密：Encrypt And Decrypt</a></b> 
<br />加密解密：默认字符集UTF-8，另可选其他常用字符集，前五种算法可解密，后五种算法不可逆。
<br />
![Encrypt And Decrypt](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Encrypt%20And%20Decrypt.png)

<b>3，<a id="File Digital Signature">计算文件数字签名：File Digital Signature，支持大文件</a></b> 
<br />计算文件/文件夹子文件的MD5、SHA1值，支持计算大文件，支持对文件名的正则过滤，对满足条件的文件进行计算。
<br />
![File Digital Signature](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/File%20Digital%20Signature.png)

<b>4，<a id="Regex Tester">正则表达式验证：Regex Tester</a></b> 
<br />常用正则表达式文件conf\RegexTester\expression.properties。
<br />
![Regex Tester](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Regex%20Tester.png)
<br />
![Regex Tester help](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Regex%20Tester-help.png)

<b>5，<a id="Run Script">执行Script脚本：Run Script</a></b>
<br />此工具简单目前仅可进行简单的计算。
<br />
![Run Script](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Run%20Script.png)

<b>6，<a id="Folder And File Operate">文件(夹)查找操作Folder And File Operate</a></b>
<br />此工具功能丰富，可通过多种条件(名称，类型，时间，大小，其中名称包括后缀名且不区分大小写)查找文件(夹)； 
<br />不仅包括文件(夹)查找，也包括复制、剪切、删除文件及删除空文件夹；
<br />通过工具界面可了解其详细功能，注意操作类型非“默认查找”时，需谨慎操作，以防误删除文件；
<br />文件类型配置文件conf\FolderAndFileOperate\filetype.properties。
<br />
![Folder And File Operate](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Folder%20And%20File%20Operate.png)

<b>7，<a id="Ant Script Auto Build">Ant脚本自动build: Ant Script Auto Build</a></b>
<br />通过解析Eclipse的Java Project、Dynamic Web Project或者MyEclipse的Web Project的相关配置文件，自动构建者这三类Project的Ant脚本。
<br />
![Ant Script Auto Build](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Ant%20Script%20Auto%20Build.png)
<br />Ant Script Auto Build也提供独立版本，下载文件：<a href="https://github.com/baishui2004/common_gui_tools/blob/master/dest/antScriptAutoBuild-1.21.zip?raw=true">antScriptAutoBuild-1.21.zip</a>

<b>8，<a id="Class Finder">Java类查找：Class Finder，查找本机的.class或.java</a></b>
<br />查找文件夹下的.class及.java文件，或者文件夹下压缩文件jar,war,aar,ear,zip内的.class及.java文件。
<br />
![Class Finder](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Class%20Finder.png)
<br />可配置查找的文件类型（不限于.class及.java文件，可通过配置扩展用于查找其他类型的文件），压缩文件限于java.util.zip.ZipEntry类可解析的类型，配置文件参见conf\ClassFinder\conf.properties。

<b>9，<a id="Color Tool">颜色工具：Color Tool</a></b>
<br />此工具可进行颜色RGB码与HTML码的相互转换，调色板显示所填写颜色的效果以及选择颜色。
<br />
![Color Tool](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Color%20Tool.png)

<b>10，<a id="System Information">运行环境信息：System Information</a></b>
<br />此工具可查看运行机器的Overview(基础信息)、Running Status、System Properties、支持的字符集、字体。
<br />
![System Information](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/System%20Information.png)

<b>11，<a id="JODConverter Visual">OpenOffice文档转换JODConverter Visual，需Openoffice后台服务支持</a></b>
<br />可视化文档转换，支持常见文档的相互转换，如doc/docx转pdf、rtf、text、html，xls/xlsx转pdf、csv、tsv、html，支持的详细转换类型具体参见插件。
<br />
![JODConverter Visual](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/JODConverter%20Visual.png)
<br />测试OpenOffice版本：3.4
<br />配置文件：conf/JODConverterVisual/converter.properties
<br />转换文档：<a href="http://www.artofsolving.com/opensource/jodconverter/guide/supportedformats" target="_blank">Supported Formats</a>&emsp;&nbsp;<a href="http://www.liferay.com/zh/community/wiki/-/wiki/Main/Document+Conversion+with+OpenOffice" target="_blank">Document Conversion with OpenOffice</a>

<b>12，<a id="JNotify Visual">文件(夹)变化监控：JNotify Visual</a></b>
<br />使用JNotify监控文件(夹)增删改及重命名。
<br />
![JNotify Visual](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/JNotify%20Visual.png)
<br />关于JNotify Visual使用的类包<a href="http://improve-lgpl-jars.googlecode.com/files/jnotify-0.94_improve-1.0.jar">jnotify-0.94_improve-1.0.jar</a>，改进自<a href="http://sourceforge.net/projects/jnotify/" target="_blank">jnotify-0.94</a>，项目地址<a href="http://code.google.com/p/improve-lgpl-jars/" target="_blank">http://code.google.com/p/improve-lgpl-jars/</a>

<b>13，<a id="JUniversal Chardet">文本编码识别：JUniversal Chardet</a></b>
<br />检测文件编码，识别准确率高（有一定的误差）。
<br />
![JUniversal Chardet](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/JUniversal%20Chardet.png)
<br />项目：<a href="https://code.google.com/p/juniversalchardet/" target="_blank">juniversalchardet</a>
<br />文档：<a href="http://www-archive.mozilla.org/projects/intl/UniversalCharsetDetection.html" target="_blank">Mozilla UniversalCharsetDetection</a>
<br />其他编码识别项目：<a href="http://sourceforge.net/projects/jchardet/" target="_blank">jchardet</a>&emsp;&nbsp;<a href="http://sourceforge.net/projects/cpdetector/" target="_blank">cpdetector</a>&emsp;&nbsp;<a href="http://wing.comp.nus.edu.sg/~tanyeefa/downloads/charsetdetectstreamreader/" target="_blank">Charset Detect Stream Reader</a>

<b>14，<a id="ZHConverter Visual">中文简体繁体互转：ZHConverter Visual</a></b>
<br />
![ZHConverter Visual](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/ZHConverter%20Visual.png)
<br />项目：<a href="http://code.google.com/p/java-zhconverter/" target="_blank">java-zhconverter</a>

<b>15，<a id="Escape Character Tool">字符转义工具：Escape Character Tool</a></b>
<br />HTML、XML、JAVA、JavaScript、CSV字符转义字符及还原，使用Apache Commons-lang的StringEscapeUtils类。
<br />
![Escape Character Tool](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Escape%20Character%20Tool.png)

<b>16，<a id="Short URL">短网址转换：Short URL</a></b>
<br />当前支持将URL转换为百度提供的短网址，以及可将百度的短网址还原为原网址。
<br />
![Short URL](https://raw.githubusercontent.com/baishui2004/common_gui_tools/master/documention/images/Short%20URL.png)
