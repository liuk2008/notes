## CSS样式 ##

**引入方式**

* 内联样式
	* 使用style设置行内式
		* <标签名 style="属性:属性值;">内容</标签名>
* 内嵌样式
	* 一般在head标签中设置样式，在h5页面中type="text/css"可以省略
	
			<style type="text/css">
		        选择器（选择标签） {
		            属性: 属性值
		        }
		    </style>

* 外部样式 
	* link rel="stylesheet" href="mystyle.css"

**CSS选择器**

* 基础选择器
	* 标签选择器
		* 标签 { 属性: 属性值 }
	* 类选择器：一个标签内部只能有一个class
		* .类名 { 属性: 属性值 }
	* id选择器
		* #id { 属性: 属性值 }
	* 通配符选择器
		* * { 属性: 属性值 }
	* 类选择&id选择器
		* 类选择器可以多次重复使用
		* id选择器不得重复，只能使用一次
* 复合选择器
	* 后代选择器
		* 父级 子级标签 { 属性: 属性值 }
		* .grey strong { color: grey; }
	* 子元素选择器
		* 父级>子级标签 { 属性: 属性值 }
		* .grey>strong { color: pink; }
	* 交集选择器：标签之间不能有空格
		* 标签类选择器 { 属性: 属性值 }
		* strong.grey { color: gold; }	
	* 并集选择器
		* 标签，标签 { 属性: 属性值 }
		* p,span { color: gold; }	 
	* 伪类选择器
		* 链接伪类选择器
			* a:link，未访问的链接
			* a:visited，已访问的链接
			* a:hover，鼠标移动到链接上
			* a:active，选定的链接
			 
**样式属性**

* font字体
	* font-size：字体大小
	* font-family：字体，可设置多个
	* font-weight：字体粗细
	* font-style：字体风格
	* 综合写法：不能更改顺序，font-size和font-family属性必须保留
		* 选择器 { font: font-style font-weight font-size font-family }
* 外观属性
	* color：文本颜色
	* text-align：文本水平对齐方式
	* text-indent：首行缩进
	* text-decoration：文本装饰
	* line-height：行间距，上距离+内容高度+下距离
* background属性
	* background-color：背景颜色
	* background-image:url()，背景图片
	* background-repeat：背景平铺
	* background-position：背景位置
		* 存在x、y坐标值，若指定x坐标，y默认垂直居中
	* background-attachment：背景附着，背景是固定或者滚动的
	* background:rgba(0,0,0,0.3)，背景半透明
	
**标签显示模式**

* 块级元素（block-level）
	* 独占一行
	* 可以设置宽高 
	* 默认宽度是父级的100%
	* 可以放行内或块级元素
* 行内元素（inline-level）
	* 一行可以显示多个
	* 无法设置宽高
	* 默认宽度是本身内容的宽度
	* 行内元素只能容纳文本或其他行内元素
* 行内块元素（inline-block）
	* 一行可以显示多个，但是有空白缝隙
	* 默认宽度是本身内容的宽度
	* 可以设置宽高  
* 模式装换display
	* 块转行内 display : inline
	* 行内转块 display : block
	* 块、行内元素转行内块 display : inline-block 

**CSS特征**

* 层叠性
	* 多种CSS样式进行叠加
	* 根据“就近原则”选择CSS样式 
	* 样式不冲突，不会叠加
* 继承性
	* 子标签会继承父标签一些样式，text-，line-，font-，color可以被继承
	* 简化代码，降低CSS代码复杂性
* 优先级
	* 选择器相同，执行层叠性 
	* 选择器不同，执行
		* 继承 或 * -> 0,0,0,0
		* 标签选择器 -> 0,0,0,1
		* 类、伪类选择器 -> 0,0,1,0
		* id选择器 -> 0,1,0,0
		* 行内样式 -> 1,0,0,0
		* !important -> ∞，color: pink!important;
	* 权重叠加	
		* div ul li -> 0,0,0,3
		* .nav ul li -> 0,0,1,2
		* a:hover -> 0,0,1,1
		* .nav a -> 0,0,1,1
	* 继承的权重是0
		* 如果标签选中了，按照公式计算权重
		* 如果标签没选中，那么权重为0
* 布局的三种机制
	* 普通流：块级元素，行内元素
	* 浮动：让多个块级盒子一行显示
	* 定位：将盒子定位在浏览器某一个位置

**盒子模型**

* 内容
* 边框
	* border-width：边框宽度
	* border-style：边框样式，solid，dashed，dotted
	* border-color：边框颜色
	* border-collapse: collapse;合并相邻边框
	* border:1px solid pink
	* border-radius：圆角
	* box-shadow:水平阴影，垂直阴影，模糊距离，阴影尺寸，阴影颜色，内/外阴影
* 内边距
	* 设置内边距后，盒子会变大
	* 设置顺序：上 -> 右 -> 下 -> 左
	* 盒子距离 + 内边距 + 边框宽度 
	* 盒子没有宽度，则padding不会撑开盒子
* 外边距
	* 水平居中：
		* 必须设置宽度，margin-left、margin-right设置为auto 
	* 清除默认内外边距：
		* { margin: 0; padding: 0; }
	* 行内元素：
		* 部分浏览器设置上下边距无效果，为了兼容性，尽量只设置左右内外边距，不设置上下内外边距
	* 外边距合并：
		* 相邻块元素垂直外边距合并：取margin-bottom、margin-top中最大值
		* 嵌套块元素垂直外边距合并：如果父标签没有设置边框以及上内边距，取父标签、子标签中margin-top中最大值，并且margin-top只在父标签中生效。解决办法：1、父标签设置边框，2、父标签设置上内边距，3、父标签设置 overflow: hidden  

**浮动（float）**

* 使用背景
	* 标准流不能满足网页布局的要求，需要使用浮动来完成，让多个块级元素一行内显示
	* 设定浮动属性后，元素脱离标准流控制，移动到指定位置
	* 只有none，left，right属性，选择器 { float: 属性值 }
* 特性
	* 加了浮动的盒子在普通流的盒子上面，但是普通流盒子的文字不会被覆盖
	* 加了浮动的盒子是不占位置的，宽度自适应
	* float属性会改变元素的display属性，类似转成inline-block，一行显示
	* 浮动元素之间没有缝隙
	* 浮动元素的总宽度超过父级宽度，多出的盒子在另一行对齐
	* 浮动元素与父盒子：子盒子的浮动参照父盒子对齐，不会与父盒子的边框重叠，也不会超过父盒子的内边距
	* 浮动元素与兄弟盒子：浮动只会影响当前或后面的盒子，不会影响前面的标准流盒子
* 用法
	* 浮动和标准流的父盒子搭配  
	* 导航栏做法：ul li a
* 清除浮动
	* 主要是为了解决父级元素因为子级元素浮动引起内部高度为0的问题，清除浮动之后，父级元素根据浮动的子盒子自动检测高度，父级元素有了高度就不会影响下面的标准流。
	* 方法：
		* 1、额外标签法
			* 在浮动元素末位添加一个空标签，div style="clear: both;" 
			* 在只有both，left，right属性，缺点造成结构复杂
		* 2、父级添加overflow属性
			* 父标签设置 overflow: hidden，影响页面显示 
		* 3、使用after伪元素清除

			    .clearfix:after {
					content: "";
			        display: block;
			        height: 0;
			        visibility: hidden;
			        clear: both;
			    }
				.clearfix {
					*zoom: 1; /*用于ie6，7清除浮动样式*/
				}

		* 4、使用双伪元素清除

				.clearfix:before,
				.clearfix:after {
					content: "";
			        display: table;
			    }
				.clearfix:after {
			        clear: both;
			    }
				.clearfix {
					*zoom: 1; /*用于ie6，7清除浮动样式*/
				}

**定位**
