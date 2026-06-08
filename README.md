# 万象 Everyphants

## 0xFF 绪

此项目灵感来源于 `Wox` 和 `PowerToy` 的 `命令面板`。

先前在还没有多少项目经验积累的时候写过一回，当时的项目结构十分混乱且脆弱，而且后来还因为各种原因烂尾了。

正好近来要做 `面向对象程序设计` 这门课的大作业，同时也有了不少项目经验积累，于是就又把这个 idea 拿出来做了。不过这一次，我们打算推翻重来，用更健壮也更现代的代码重写一遍。

## 0x00 概述

项目的核心就是即时的命令执行。所以项目的主窗口仅由一个输入框构成，并根据输入内容，分别按命令与内容触发不同的行为。

## 0x10 功能

### 0x11 加解密/编解码

- [x] URL 编码
- [x] URL 解码
- [x] Base64 编码
- [x] Base64 解码
- [x] UTF-8 编码
- [x] UTF-8 解码
- [x] 凯撒加密
- [x] 栅栏加密
- [x] 莫斯密码编码
- [ ] 莫斯密码解码

### 0x12 数学

- [x] 进制转换
- [x] 质因分解
- [ ] 数字性质
- [x] 随机数

### 0x13 文本

- [x] 翻译
- [x] 数字大写

### 0x14 查询

- [x] 当前时间
- [x] 颜色代码
- [x] 答案之书
- [ ] 网络用语

### 0x15 信息类

- [x] 倒计时

## 0x20 技术实现

从需求看，插件分为：

- 输入无关 内容固定
- 输入无关 内容刷新
- 输入有关 内容固定
- 输入有关 内容刷新

故实现给出一个接口 Refreshable 和两个基类 PersistentPlugin 和 ReactivePlugin

从实现来看，PersistentPlugin 仅在应用启动时注册一次其给出的 Result，而 ReactivePlugin 则是在输入变化时，触发 Result 的重建。
// 原先是有 ProactivePlugin 的，但是因为本身就是小项目，故弃用

### UML

``` mermaid
---
config:
  theme: base
  themeCSS: |
    .node rect,
    .node circle,
    .node ellipse,
    .node polygon,
    .node path,
    .classGroup rect,
    .cluster rect {
      rx: 8px !important;
      ry: 8px !important;
    }
    .classGroup line {
      stroke: #a0a0a0 !important;
    }
    .classGroup .classTitle {
      font-weight: 600 !important;
    }
    .classGroup text {
      font-size: 13px !important;
    }
  themeVariables:
    darkMode: true                           # 🔑 开启暗色模式

    # ===== 基础背景 =====
    background: "#202020"                    # WinUI 暗色背景
    mainBkg: "#2d2d2d"                       # 主节点背景
    secondBkg: "#383838"                     # 次节点背景

    # ===== 主色调（暗色模式下略微调整）=====
    primaryColor: "#60cdff"                  # WinUI 暗色强调蓝
    primaryBorderColor: "#3aa0d4"            # 强调色边框
    primaryTextColor: "#000000"              # 强调色节点上的黑色文字

    # ===== 文字颜色 =====
    textColor: "#ffffff"                     # WinUI 暗色主文本色
    lineColor: "#a0a0a0"                     # 连接线颜色

    # ===== 辅助节点配色 =====
    secondaryColor: "#3a3a3a"                # 次要节点背景
    tertiaryColor: "#333333"                 # 第三级节点背景

    # ===== 簇/分组 =====
    clusterBkg: "#2a2a2a"                    # 分组背景
    clusterBorder: "#4a4a4a"                 # 分组边框

    # ===== 注释框 =====
    noteBkgColor: "#3d3520"                  # 注释背景
    noteBorderColor: "#5c4e2e"               # 注释边框
    noteTextColor: "#ffffff"                 # 注释文字

    # ===== 时序图专用变量 =====
    actorBkg: "#3a3a3a"                      # Actor 背景
    actorBorder: "#5a5a5a"                   # Actor 边框
    actorTextColor: "#ffffff"                # Actor 文字
    actorLineColor: "#a0a0a0"                # Actor 生命线

    signalColor: "#a0a0a0"                   # 信号箭头颜色
    signalTextColor: "#ffffff"               # 信号文字颜色

    labelBoxBkgColor: "#3a3a3a"              # 标签框背景
    labelBoxBorderColor: "#5a5a5a"           # 标签框边框
    labelTextColor: "#ffffff"                # 标签文字

    loopTextColor: "#ffffff"                 # 循环文字

    activationBorderColor: "#60cdff"         # 激活框边框
    activationBkgColor: "#1a3a4a"            # 激活框背景

    sequenceNumberColor: "#000000"           # 序列号颜色

    # ===== 类图专用 =====
    classText: "#ffffff"                     # 类文字

    # ===== 甘特图 =====
    sectionBkgColor: "#3a3a3a"               # 节背景
    altSectionBkgColor: "#333333"            # 交替节背景
    sectionBkgColor2: "#3a3a3a"              # 第二层节背景
    taskBkgColor: "#60cdff"                  # 任务条背景
    taskBorderColor: "#3aa0d4"               # 任务条边框
    taskTextColor: "#000000"                 # 任务条文字
    taskTextLightColor: "#000000"            # 任务条浅色文字
    taskTextOutsideColor: "#ffffff"          # 任务条外部文字
    taskTextClickableColor: "#60cdff"        # 可点击任务文字
    activeTaskBkgColor: "#60cdff"            # 活动任务背景
    activeTaskBorderColor: "#3aa0d4"         # 活动任务边框
    gridColor: "#4a4a4a"                     # 网格线颜色
    doneTaskBkgColor: "#5a5a5a"              # 已完成任务背景
    doneTaskBorderColor: "#3a3a3a"           # 已完成任务边框
    critBkgColor: "#ff99a4"                  # 关键任务背景（暗色错误红）
    critBorderColor: "#cc7780"               # 关键任务边框
    todayLineColor: "#ff99a4"                # 今日线颜色

    # ===== 饼图 =====
    pie1: "#60cdff"                          # 强调蓝
    pie2: "#8fd7ff"                          # 浅强调蓝
    pie3: "#a0a0a0"                          # 中性灰
    pie4: "#5a5a5a"                          # 浅灰
    pie5: "#3aa0d4"                          # 深蓝
    pie6: "#3a3a3a"                          # 背景灰
    pie7: "#4a4a4a"                          # 边框灰
    pie8: "#333333"                          # 浅背景灰
    pie9: "#ffffff"                          # 深文本色
    pie10: "#ff99a4"                         # 错误红
    pie11: "#6ccb8b"                         # 成功绿
    pie12: "#f7b064"                         # 警告橙

    # ===== 节点样式 =====
    nodeBorder: "#a0a0a0"                    # 节点边框
    nodeTextColor: "#ffffff"                 # 节点文字

    # ===== 其他 =====
    fontFamily: "'Cascadia Code', Consolas, '霞鹜文楷', '仓耳今楷01'"
    fontSize: "14px"
---
classDiagram
    class Result {
        +title: String
        +displayText: String
        +score: double
        +getTitle() String
    }
    
    class RefreshableResult {
        +withRefresh(action: Runnable) RefreshableResult
        +refresh() void
    }
    
    class LoadingResult {
        +finish(title: String, displayText: String) void
    }
    
    class Plugin {
        <<abstract>>
        #name: String
        #iconFile: String
        +query(input: String)* Result
    }
    
    class ProactivePlugin {
        <<abstract>>
        +build()* Result
    }
    
    class ReactivePlugin~T~ {
        <<abstract>>
        +parse(query: String)* T
        +build(t: T)* Result
    }
    
    class EncodePlugin {
        <<abstract>>
        +parse(query: String) String
    }
    class DecodePlugin {
        <<abstract>>
        +build(s: String) Result
    }
    class EncryptionPlugin {
        <<abstract>>
        +parse(s: String) String
    }
    
    class TimePlugin
    class CountdownPlugin
    
    class Base64EncodePlugin
    class UrlEncodePlugin
    class Utf8EncodePlugin
    
    class Base64DecodePlugin
    class UrlDecodePlugin
    class Utf8DecodePlugin
    
    class CaesarEncryptionPlugin
    class FenceEncryptionPlugin
    class MorseEncryptionPlugin
    
    class ColorPlugin
    class BaseConversionPlugin
    class RandomPlugin
    class TranslatePlugin
    class NumberToChinesePlugin
    class PrimeFactorizationPlugin
    class AnswerBookPlugin
    
    class PluginManager {
        -plugins: List~Plugin~
        +getPlugins() List~Plugin~
    }
    
    class MainController {
        +init(stage: Stage) void
    }
    class ResultItem
    class InputThrottle
    class WindowDragHandler
    
    class WindowsAcrylicUtil {
        +applyRoundedCorners(stage: Stage, radius: int)$ void
    }
    
    class App {
        +start(stage: Stage) void
    }
    class Application {
        <<JavaFX>>
    }
    
    RefreshableResult --|> Result
    LoadingResult --|> RefreshableResult
    
    ProactivePlugin --|> Plugin
    ReactivePlugin --|> Plugin
    
    EncodePlugin --|> ReactivePlugin
    DecodePlugin --|> ReactivePlugin
    EncryptionPlugin --|> ReactivePlugin
    
    TimePlugin --|> ProactivePlugin
    CountdownPlugin --|> ProactivePlugin
    
    Base64EncodePlugin --|> EncodePlugin
    UrlEncodePlugin --|> EncodePlugin
    Utf8EncodePlugin --|> EncodePlugin
    
    Base64DecodePlugin --|> DecodePlugin
    UrlDecodePlugin --|> DecodePlugin
    Utf8DecodePlugin --|> DecodePlugin
    
    CaesarEncryptionPlugin --|> EncryptionPlugin
    FenceEncryptionPlugin --|> EncryptionPlugin
    MorseEncryptionPlugin --|> EncryptionPlugin
    
    ColorPlugin --|> ReactivePlugin
    BaseConversionPlugin --|> ReactivePlugin
    RandomPlugin --|> ReactivePlugin
    TranslatePlugin --|> ReactivePlugin
    NumberToChinesePlugin --|> ReactivePlugin
    PrimeFactorizationPlugin --|> ReactivePlugin
    AnswerBookPlugin --|> ReactivePlugin
    
    App --|> Application
    
    PluginManager *--> Plugin
    Plugin ..> Result
    MainController --> PluginManager
    MainController --> InputThrottle
    MainController --> WindowDragHandler
    MainController ..> Result
    App --> MainController
    App ..> WindowsAcrylicUtil 
```
