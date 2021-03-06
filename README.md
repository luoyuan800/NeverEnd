# NeverEnd
## Game NeverEnd
You can clone and modify the code. You should mark you changes in the code and pulic those codes. Every thing are under BSD licens. Unless you got my permission，you shouldn't use any code to earn profit even you modify it.

在以非盈利为目、保留所有copyright声明的基础下，可以对源码进行任意的修改、删除和重新发布。但必须保证在**未**获得本人授权的情况下，修改后的项目也必须开源并且注明修改的部分，同时禁止添加任何收费的内容。

# Game Struct（项目介绍）

![](structs.png)

# 数据模型
cn.luo.yuan.maz.model目录下的类都是用来存储数据bean。
# 逻辑模型
cn.luo.yuan.maz.service包下的类是游戏逻辑流程相关的类
# 存储机制
cn.luo.yuan.maz.persistence包下的类是关于数据存储（存档）的类

其中有有部分数据存入sqlite数据库，主要是Hero相关的数据（加密后存储），便于在选择存档的时候有个直观的查看，并且根据存档编号进行区分，实现多存档的功能

另外还有部分数据是存入代类的系列化文件中，这种存储方式可以方便读档的是直接加载类，并且可以防止用户修改存档。

# 显示模块
显示相关的代码在cn.luo.yuan.maz.display包下。

# 关于数据加密
### 内存数据加密
使用位加密的方式加密了数据

# 一些大改动（持续变化）
1. 装备系统去除了打造的功能，也就移除了材料之类的东西了，装备只能通过随机商店购买（或者后续增加打怪掉落）。装备的属性只能通过升级提升，高阶装备（颜色）只能通过升级随机获取。
2. 所有装备的属性都需要五行激活（另外一件装备）
3. 宠物也取消加点和自动升级的功能，只能同升级提升属性和改变颜色（阶位）。
4. 装备和宠物的颜色代表升级时候的成长属性

# 联网结构
 ## 交换宠物、物品、装备
 ![](exchange_object.png)

 ## 取回自己上传的（或者被别人交换后的）
 ![](getbackmyobject.png)
 
 # 第三方资源链接：
 ## http://opengameart.org/
 ## http://k-after.at.webry.info/
 ## https://yande.re/
 ## http://www.aj.undo.jp/
 ## http://ichimedou.sakura.ne.jp/free/freemate.htm
 ## http://commons.nicovideo.jp/search/material/image
