
## HTML基础元素 ##

* <!DOCTYPE html> 文档类型，声明为 HTML5 文档
* <lang="en"> 声明html页面内容使用的语言
* charset 告诉浏览器网页的编码格式是UTF-8，lang 告诉浏览器这是一张中文网页

**head元素**

* meta：用来描述网页属性的一种语言

		charset属性："UTF-8"，让html文件以utf-8编码形式保存，浏览器根据编码解析内容
		name属性：主要用于描述网页，属性值：author、description、keywords、generator、viewport
		http-equiv属性：相当于http的头文件作用，可以向浏览器返回一些有用的信息。属性值：content-type、expires、refresh
* title：描述文档的标题
* link：定义了文档与外部资源之间的关系
* style：定义了HTML文档的样式文件引用地址
* base：描述了基本的链接地址/链接目标，该标签作为HTML文档中所有的链接标签的默认链接
	* base href="http://www.runoob.com" target="_blank" 	
* script：定义了客户端的脚本文件


**body元素**

* 排版标签
	* h：定义一个标题
	* p：定义一个段落
	* div：定义了文档的区域，块级 (block-level)，一行只能放一个div
	* span：用来组合文档中的行内元素，内联元素(inline)，一行可以放多个span
	* br：换行
	* hr：水平线
* 格式化标签
	* b、strong：定义粗体
	* i、em：定义斜体
	* s、del：定义删除线
	* u、ins：定义下划线
	* pre：预格式化文本
* image标签 
	* src="xxx"：定义一个图像
	* alt="logo"：图片不能显示时的替换文本
	* title="logo"：鼠标悬停时显示的文本
* 链接标签 
	* a href="xxx"：定义一个链接
	* target="_blank"：默认self在当前窗口打开，_blank在新窗口打开
	* a href="#id名"：定义一个锚点
* 表格标签
	* table：定义一个表格
		* tr：定义一行
		* td：定义单元格
		* th：定义表头单元格
	* caption：定义表格标题
	* cellspacing：单元格之间的距离，默认2像素（h5默认不支持）
	* cellpadding：单元格内容与边框之间的距离，默认1像素（h5默认不支持）
	* colspan：跨列合并
	* rowspan：跨行合并
	* thead、tbody、tfoot：表格划分结构
* 列表标签
	* ul：定义一个无序列表
		* li：定义一个列表项目  
	* ol：定义一个有序列表
		* li：定义一个列表项目  
	* dl：自定义列表。每个列表有标题（由 <dt> 标签定义），内容（由 <dd> 标签定义）
* 表单标签
	* input：type属性，value属性，name属性，checked='checked'属性 
		* text
		* password
		* radio
		* checkbox
		* button
		* submit
		* reset
		* image
		* file
	* label：用于绑定一个表单元素，当点击label标签时，绑定的表单元素获取焦点
		* <label>用户名：<input type="text"/></label>
		* <label for='username'>用户名：</label><input id='username' type="text"/>
	* select：selected='selected'属性 
		* option 
		* 缺点：在不同的浏览器中显示的效果可能不一样
	* textarea：文本域控件 	
	* form：定义一个表单
* 特殊字符
	* 空格：&nbsp;
	* <：&lt;
	* >：&gt;
