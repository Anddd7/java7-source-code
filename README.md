## Java7 核心类库源码解析

请直接查看`.java` : 通过`JavaDoc+Test`书写 ,方便链接到源码


# Tracker
### 20180806
* **Java Concurrency in Practice** 读书笔记和示例代码
  * 包括 线程基础/同步sync/并发concurrent/容器/线程池
  * 示例代码
* JUC容器的源码解析
  * Synchronized 同步容器
  * Concurrent 并发容器: Map/List/Queue
  * 阻塞队列
* JUC锁: 
  * CountDownLatch 闭锁
  * AQS 锁的公共类


### 20180514
* String, 部分Character


### 20180508
* 除 Set 外, 常用的 Collection 都已经分析完毕
* 简化语言描述, 增加测试用例(实践用法)
* 接触到新的类再看源码(不能脱离实际场景瞎看, 容易迷茫没有方向)

### 20180420
* 对每一个类梳理一个 Tests 文件 ,并提供 Doc 注释或者测试案例
* Collection/AbstractCollection/List 等接口和类的源码分析(基于方法)

### 20180419
复习除 J.U.C 之外的其他包 ，适当重写
* 使用 Junit/Mock 书写测试
* 对原有组件进行分类