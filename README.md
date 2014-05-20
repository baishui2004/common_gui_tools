Common Gui Tools
================

Common Gui Tools 是用java编写，GUI界面的实用小工具集，1.1版有13个小工具： 

<pre>
<br /><b>1</b>，编码转换：Character Converter 
<br /><b>2</b>，加密解密：Encrypt And Decrypt 
<br /><b>3</b>，计算文件数字签名：File Digital Signature，支持大文件
<br /><b>4</b>，正则表达式验证：Regex Tester 
<br /><b>5</b>，执行Script脚本：Run Script 
<br /><b>6</b>，文件(夹)查找操作：Folder And File Operate 
<br /><b>7</b>，Ant脚本自动build：Ant Script Auto Build 
<br /><b>8</b>，运行环境信息：System Information 
<br /><b>9</b>，Java类查找：Class Finder，查找本地的*.class或*.java
<br /><b>10</b>，OpenOffice文档转换JODConverter Visual，需Openoffice后台服务支持
<br /><b>11</b>，文件(夹)变化监控：JNotify Visual 
<br /><b>12</b>，文本编码识别：JUniversal Chardet 
<br /><b>13</b>，中文简体繁体互转：ZHConverter Visual
</pre>

   欢迎您使用并提供宝贵意见！


<br /><b>下载及使用说明：</b> 
<br />下载压缩包<a href="https://github.com/baishui2004/common_gui_tools/blob/master/dest/common_gui_tools-1.1.zip">common_gui_tools-1.1.zip</a>，解压，Windows下使用start.bat运行，Linux下使用start.sh运行。

<br /><b>配置说明：</b> 
<pre>
1，本软件采用插件方式，13个小工具即是13个插件，插件配置文件夹conf，
   配置文件有：common_gui_tools.properties、more_tools.properties以及多个插件的配置；
2，相关配置说明参看各配置文件，可通过修改文件common_gui_tools.properties中属性CommonUseTools修改常用插件;
   默认加载插件Encrypt And Decrypt、Folder And File Operate及Class Finder； 
3，可通过GUISkin属性修改软件皮肤，fontStyles系列属性修改显示字体。
</pre>



<br /><br /><b>下面逐个简单简绍每个小工具：</b> 打开软件后可通过菜单栏Tools、More Tools选择展现
工具，通过关闭按钮关闭。
<br /><b>1，编码转换：Character Converter</b>
<br />(1)，编码：Encode String表单输入字符，点击右侧对应的Encode按钮，对输入字符进行编码，注意此时的编码类别是“二进制”、“八进制”、“十进制”、“十六进制”。对应的可以使用下面的Decode进行解码。 
   [http://common-gui-tools.googlecode.com/files/Character%20Converter-1.png]
<br />(2)，乱码解码：选择编码类别“乱码解码”，比如在Big5表单中输入“趼睫”，点击右侧对应Decode按钮，GBK表单中解码出字符“字符”。 
  [http://common-gui-tools.googlecode.com/files/Character%20Converter-2.png]

<b>2，加密解密：Encrypt And Decrypt</b> 
<br />加密解密：默认字符集UTF-8，另可选其他常用字符集，前五种算法可解密，后五种算法不可逆。
   [http://common-gui-tools.googlecode.com/files/Encrypt%20And%20Decrypt-1.png] 

<b>4，正则表达式验证：Regex Tester</b>    
<br />常用正则表达式文件conf\RegexTester\expression.properties。
   [http://common-gui-tools.googlecode.com/files/Regex%20Tester-1.png]

<b>5，执行Script脚本：Run Script</b>
<br />此功能简单以后待完善。
  [http://common-gui-tools.googlecode.com/files/Run%20Script-1.png]

<b>6，文件(夹)查找操作Folder And File Operate</b>
<br />此工具功能比较丰富，可通过多种条件(名称，类型，时间，大小，其中名称包括后缀名且不区分大小写)查找文件(夹)； 
<br />且不仅包括文件(夹)查找，也包括复制、剪切、删除文件及删除空文件夹；
<br />通过界面文字可了解其功能，注意操作类型不为“默认查找”时，需谨慎操作；
<br />文件类型配置文件conf\FolderAndFileOperate\filetype.properties。
  [http://common-gui-tools.googlecode.com/files/Folder%20And%20File%20Operate-1.png]

<b>7，Ant脚本自动build: Ant Script Auto Build</b>
<br />通过解析Eclipse的Java Project、Dynamic Web Project或者MyEclipse的Web Project的相关配置文件，自动构建者这三类Project的Ant脚本。
  [http://common-gui-tools.googlecode.com/files/Ant%20Script%20Auto%20Build-1.png]
<br />Ant Script Auto Build也提供独立版本，参见Downloads的下载文件antScriptAutoBuild-1.1.zip

<b>8，运行环境信息：System Information</b>
<br />此工具可查看运行机器上支持的字符集、字体及Java System.getProperties()相关属性。
  [http://common-gui-tools.googlecode.com/files/System%20Information-1.png]

<b>11，文件(夹)变化监控：JNotify Visual</b>
<br />使用JNotify监控文件(夹)增删改及重命名。
  [http://common-gui-tools.googlecode.com/files/JNotify%20Visual-1.png]
<br />关于JNotify Visual使用的类包jnotify-0.94_improve-1.0.jar，改进自jnotify-0.94，项目地址http://code.google.com/p/improve-lgpl-jars/
 
