# 📱 SmartBudget – 安卓记账助手

> 一个基于 Android Studio 和 Java 开发的本地记账 App，支持账单管理、图表统计、预算设置与分类维护。

---

## 🧩 项目简介

**SmartBudget** 是一款轻量级的 Android 应用，旨在帮助用户便捷记录日常支出，掌握财务状况，合理控制预算。支持账单的添加、浏览、编辑、删除，内置分类管理系统与支出图表统计功能，数据本地持久化，离线可用。

---

## 🏗️ 项目结构

```
├── java/
│   ├── MainActivity.java                 // 首页主界面
│   ├── AddRecordActivity.java           // 添加账单页
│   ├── EditRecordActivity.java          // 编辑账单页
│   ├── HistoryActivity.java             // 历史账单浏览页
│   ├── CategoryManageActivity.java      // 类别管理页
│   ├── ChartActivity.java               // 支出图表页
│   ├── UserInfoActivity.java            // 用户设置页
│   ├── RecordAdapter.java               // 历史账单适配器
│   ├── DBHelper.java                    // SQLite 数据库助手
│   └── Record.java                      // 账单数据模型
├── res/layout/
│   ├── activity_main.xml
│   ├── activity_add_record.xml
│   ├── activity_edit_record.xml
│   ├── activity_history.xml
│   ├── activity_category_manage.xml
│   ├── activity_chart.xml
│   ├── activity_user_info.xml
│   └── item_record.xml                  // 单条账单项布局
├── res/drawable/                        // 卡片背景、图标等
└── AndroidManifest.xml
```

---

## 🔑 核心功能

* 📋 添加账单：支持金额、类型、备注与日期
* 📖 历史记录：按关键字或时间筛选账单
* 📊 图表分析：以饼图展示支出结构（支持按月/年/全部）
* 🗂️ 类别管理：自定义添加或删除支出类型
* 🧑 用户信息：设置姓名与月预算，并同步至首页展示
* 📦 数据持久化：使用 SQLite 本地存储账单数据

---

## 🛠️ 开发技术

| 类别     | 技术方案                      |
| ------ | ------------------------- |
| 编程语言   | Java                      |
| 开发平台   | Android Studio            |
| UI 布局  | XML + ConstraintLayout    |
| 图表库    | MPAndroidChart            |
| 数据存储   | SQLite + SQLiteOpenHelper |
| 最低支持版本 | Android 5.0 (API 21)      |

---

## 🖼️ 部分界面截图

（请上传截图后补充此部分）

---

## 📌 使用说明

1. 下载或 Clone 本仓库
2. 使用 Android Studio 打开项目
3. 运行在 Android 模拟器或真机上
4. 初次使用请进入“个人中心”设置用户与预算
5. 开始添加你的第一条账单吧！

---

## 🧱 后续功能可拓展方向

* 云端同步与备份
* 超支提醒与分析建议
* 折线图/柱状图等多种图表支持
* 导出为 CSV/Excel
* 登录/多用户支持

---

## 📃 许可证（License）

本项目仅用于学习与教学示范目的，禁止商用。

---
