# -*- coding: utf-8 -*-
"""生成《APP开发技术》期末试卷答案 Word 文档（校园生活助手 APP）。"""
import os
from docx import Document
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml.ns import qn

BASE = os.path.dirname(os.path.abspath(__file__))
SHOTS = os.path.join(BASE, "docs", "screenshots")

doc = Document()

# 默认中文字体
style = doc.styles["Normal"]
style.font.name = "宋体"
style.font.size = Pt(11)
style.element.rPr.rFonts.set(qn("w:eastAsia"), "宋体")


def set_cn(run, font="宋体"):
    run.font.name = font
    r = run._element
    r.rPr.rFonts.set(qn("w:eastAsia"), font)


def heading(text, level=1):
    h = doc.add_heading(level=level)
    run = h.add_run(text)
    set_cn(run, "黑体")
    return h


def para(text="", bold=False, size=11, italic=False, align=None, color=None):
    p = doc.add_paragraph()
    if align:
        p.alignment = align
    run = p.add_run(text)
    run.bold = bold
    run.italic = italic
    run.font.size = Pt(size)
    if color:
        run.font.color.rgb = color
    set_cn(run)
    return p


def code_block(code):
    p = doc.add_paragraph()
    p.paragraph_format.left_indent = Pt(6)
    run = p.add_run(code)
    run.font.name = "Consolas"
    run.font.size = Pt(9)
    run._element.rPr.rFonts.set(qn("w:eastAsia"), "Consolas")
    # 浅灰底纹
    pPr = p._p.get_or_add_pPr()
    shd = pPr.makeelement(qn("w:shd"), {qn("w:val"): "clear", qn("w:fill"): "F2F2F2"})
    pPr.append(shd)
    return p


def bullet(text):
    p = doc.add_paragraph(style="List Bullet")
    run = p.add_run(text)
    set_cn(run)
    return p


def add_img(path, width=2.3):
    if os.path.exists(path):
        doc.add_picture(path, width=Inches(width))
        doc.paragraphs[-1].alignment = WD_ALIGN_PARAGRAPH.CENTER


# ============ 封面 ============
t = doc.add_paragraph()
t.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = t.add_run("丽江文化旅游学院 2025—2026 学年第二学期")
r.bold = True
r.font.size = Pt(16)
set_cn(r, "黑体")

t = doc.add_paragraph()
t.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = t.add_run("《APP开发技术》期末考试试卷（考查）——项目答卷")
r.bold = True
r.font.size = Pt(15)
set_cn(r, "黑体")

doc.add_paragraph()
for line in [
    "年级：2023    专业：计算机科学与技术    班级：______",
    "姓名：______    学号：______    任课教师：李秋平 等",
    "项目名称：校园生活助手 APP（CampusLife）",
    "技术栈：Java + Android（AGP 8.5.2 / Gradle 8.7，compileSdk 34，minSdk 26）",
]:
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run(line)
    run.font.size = Pt(11)
    set_cn(run)

para()
para("说明：本答卷围绕自选主题“校园生活”实现一款完整可运行的 Android 应用，"
     "按试卷六个部分（一~六）逐条作答，并附关键代码与运行截图。", italic=True, size=10,
     color=RGBColor(0x66, 0x66, 0x66))

doc.add_page_break()

# ============ 一、项目文档与总体设计 ============
heading("一、项目文档与总体设计（20分）", 1)

heading("（1）APP 名称、项目背景、设计目标与用户需求（5分）", 2)
para("APP 名称：校园生活助手 APP（英文项目名 CampusLife）。")
para("项目背景：在校学生日常需要查看校园公告、活动报名、课程/活动提醒，并登记个人信息以便接收"
     "对应服务。现有信息分散在多个平台，缺少一个统一入口。为此设计一款“一站式校园服务”应用，"
     "把公告浏览、个人信息登记与本地保存、课程/活动提醒等高频功能聚合在一个 APP 中。")
para("设计目标：")
bullet("界面以白色为主色调，简洁清爽、层次清楚、布局合理。")
bullet("首页聚合常用功能入口与最新公告；底部导航在“首页/公告/我的”之间快速切换。")
bullet("个人信息可本地保存（SharedPreferences），重开 APP 自动回显。")
bullet("公告数据由本地 JSON 读取解析后列表展示，并可“设为提醒”触发系统通知。")
para("主要用户需求：①浏览校园公告/活动/课程提醒等信息；②登记并长期保存个人信息；"
     "③对感兴趣的公告设置提醒，及时收到通知。")

heading("（2）系统结构与技术路线（5分）", 2)
para("应用采用 Android 原生四大组件 + 单 Activity（底部导航）多 Fragment 的经典结构。"
     "系统结构图（技术路线）如下：")
code_block(
    "┌──────────────────────────────────────────────────────────┐\n"
    "│                    用户交互层 (UI)                          │\n"
    "│  MainActivity + BottomNavigationView                       │\n"
    "│   ├ HomeFragment(首页)                                     │\n"
    "│   ├ NoticeFragment(公告)                                   │\n"
    "│   └ ProfileFragment(我的)                                  │\n"
    "│  布局: ConstraintLayout/LinearLayout/GridLayout/CardView   │\n"
    "└───────────────┬───────────────────────┬──────────────────┘\n"
    "                │                       │\n"
    "      ┌─────────▼────────┐    ┌─────────▼──────────┐\n"
    "      │  本地数据保存层    │    │   资源/数据读取层    │\n"
    "      │ SharedPreferences │    │ assets/notices.json │\n"
    "      │  (多字段持久化)    │    │  NoticeRepository   │\n"
    "      └───────────────────┘    └─────────┬──────────┘\n"
    "                                         │\n"
    "                           ┌─────────────▼─────────────┐\n"
    "                           │       服务/提醒层           │\n"
    "                           │ ReminderService (Service)  │\n"
    "                           │     → sendBroadcast()      │\n"
    "                           │ ReminderReceiver(Receiver) │\n"
    "                           │     → Notification(通知)    │\n"
    "                           └────────────────────────────┘"
)
para("技术路线：用户通过底部导航在 Fragment 间切换 → Fragment 处理交互 → 通过 SharedPreferences "
     "保存/回显数据、通过 NoticeRepository 读取并解析本地 JSON → RecyclerView 列表展示 → "
     "点击“设为提醒”触发 Service → Service 读取数据后 sendBroadcast → BroadcastReceiver "
     "构建并弹出系统 Notification。各模块职责单一、低耦合。")

heading("（3）功能模块图与核心模块说明（5分）", 2)
code_block(
    "校园生活助手 APP\n"
    "├── 主框架 (MainActivity + 底部导航 BottomNavigationView)\n"
    "│     └── 首页 / 公告 / 我的 三个标签页切换\n"
    "├── 首页模块 (HomeFragment)\n"
    "│     ├── 标题 / 头像 / 副标题 / 主题 Banner 图\n"
    "│     ├── 功能图标区（公告 / 登记 / 提醒 / 关于）\n"
    "│     └── 最新公告卡片（主题服务信息展示）\n"
    "├── 信息登记模块 (ProfileFragment)\n"
    "│     ├── 多字段输入（姓名/班级/电话/服务类型/备注）\n"
    "│     ├── 保存 / 查看 / 清空 交互\n"
    "│     └── SharedPreferences 持久化 + 自动回显\n"
    "├── 校园公告模块 (NoticeFragment)\n"
    "│     ├── 本地 JSON 读取与解析 (NoticeRepository)\n"
    "│     ├── RecyclerView 列表展示 (NoticeAdapter)\n"
    "│     └── “设为提醒”入口\n"
    "└── 提醒服务模块\n"
    "      ├── ReminderService（后台读取数据）\n"
    "      ├── ReminderReceiver（接收广播）\n"
    "      └── Notification（系统通知提醒）"
)
para("核心模块说明：")
bullet("主框架 MainActivity：承载 BottomNavigationView，用 FragmentTransaction 切换三个 Fragment；启动即创建通知渠道并申请通知权限。")
bullet("首页 HomeFragment：聚合功能入口与最新公告，公告/登记入口会切换到对应底部标签。")
bullet("信息登记 ProfileFragment：演示输入、交互、本地保存与回显，用 SharedPreferences 保存多字段。")
bullet("公告列表 NoticeFragment + NoticeRepository + NoticeAdapter：读取 assets/notices.json，解析为 Notice 列表并用 RecyclerView 展示。")
bullet("提醒服务 ReminderService / ReminderReceiver / NotificationHelper：完整体现 Service → 广播 → 通知的提醒链路。")

heading("（4）使用说明与开发说明（5分）", 2)
para("使用说明（操作流程）：", bold=True)
bullet("打开 APP 进入首页，可看到标题、Banner 图、四个功能入口与“最新公告”卡片；底部导航可在 首页/公告/我的 之间切换。")
bullet("点击底部“公告”进入公告列表，浏览教务通知、活动报名、课程提醒、失物招领、便民服务等信息；点击某条公告的“设为提醒”，通知栏弹出提醒。")
bullet("点击底部“我的”进入登记页，填写姓名、班级、联系电话、选择服务类型、填写备注，点击“保存”提示“保存成功”并在下方回显；再次打开 APP 会自动回显上次保存的信息。“查看已保存信息”可随时查看，“清空”删除本地数据。")
bullet("首页点击“课程提醒”，后台 Service 读取本地课程数据并通过通知栏推送提醒；点击“关于我们”查看应用信息。")
para("Android 13 及以上首次运行会申请“通知”权限，授予后可正常收到提醒。", italic=True, size=10)
para("开发说明：", bold=True)
bullet("运行环境：Android Studio（Giraffe/Hedgehog 及以上）、JDK 17、AGP 8.5.2 / Gradle 8.7、compileSdk/targetSdk 34、minSdk 26。")
bullet("关键代码说明详见第三~五部分；项目文件结构详见第六部分。")

doc.add_page_break()

# ============ 二、界面设计与布局实现 ============
heading("二、界面设计与布局实现（25分）", 1)
para("界面以白色为主色调（白底卡片 + 浅绿点缀 + 浅色状态栏），整体美观、控件排列整齐，"
     "无重叠/错位/显示不完整问题。综合使用 ConstraintLayout、LinearLayout、GridLayout、"
     "CardView、ScrollView 等多种布局。")

heading("（1）首页显示标题（5分）", 2)
para("首页顶部以 ConstraintLayout 显示头像 + 主标题“校园生活助手 APP”+ 副标题“一站式校园服务·通知·报名·提醒”。")

heading("（2）显示主题图片、功能图标、头像图片（5分）", 2)
para("首页包含：圆形头像（ic_avatar）、主题 Banner 图（banner，圆角 CardView 承载）、"
     "2×2 功能图标区（公告/登记/提醒/关于，均为矢量图标 + 圆形底色）。")

heading("（3）显示主题服务信息（5分）", 2)
para("首页“最新公告”卡片展示分类标签、通知标题、地点、时间；公告列表中每条展示分类、标题、"
     "内容摘要、地点、时间，并带“设为提醒”按钮，均为与校园主题相关的服务信息。")

heading("（4）至少 3 个输入控件且有提示文字（5分）", 2)
para("“我的”登记页共 5 个输入控件（超过 3 个要求），均带 hint 提示文字：")
bullet("姓名 EditText（hint：请输入姓名）")
bullet("班级 EditText（hint：请输入班级）")
bullet("联系电话 EditText（inputType=phone，hint：请输入联系电话）")
bullet("服务类型 Spinner（下拉选择，entries=service_types）")
bullet("备注信息 EditText（多行 textMultiLine，hint：请输入备注信息）")

heading("（5）功能按钮与数据回显区域（5分）", 2)
para("登记页含“保存 / 查看已保存信息 / 清空”三个按钮，下方 CardView 为“已保存信息”数据回显区域。"
     "公告页每条含“设为提醒”按钮。所有按钮均有响应，无无效按钮。")
para("首页布局关键代码（节选，体现多种布局组合）：", bold=True)
code_block(
    "<ScrollView ...>\n"
    "  <LinearLayout orientation=\"vertical\">\n"
    "    <androidx.constraintlayout.widget.ConstraintLayout ...>  <!-- 顶部标题栏 -->\n"
    "      <ImageView id=ivAvatar .../>\n"
    "      <TextView id=tvTitle text=\"校园生活助手 APP\" .../>\n"
    "      <TextView id=tvSubtitle .../>\n"
    "    </ConstraintLayout>\n"
    "    <androidx.cardview.widget.CardView> <ImageView src=@drawable/banner/> </CardView>\n"
    "    <GridLayout columnCount=\"2\">       <!-- 2x2 功能图标 -->\n"
    "      <LinearLayout id=menuNotice.../> <LinearLayout id=menuProfile.../>\n"
    "      <LinearLayout id=menuRemind.../> <LinearLayout id=menuAbout.../>\n"
    "    </GridLayout>\n"
    "    <CardView> <LinearLayout id=cardLatestNotice>...最新公告...</LinearLayout> </CardView>\n"
    "  </LinearLayout>\n"
    "</ScrollView>"
)
para("底部导航宿主布局 activity_main.xml：", bold=True)
code_block(
    "<LinearLayout orientation=\"vertical\">\n"
    "  <FrameLayout id=\"fragmentContainer\" layout_height=\"0dp\" layout_weight=\"1\"/>\n"
    "  <com.google.android.material.bottomnavigation.BottomNavigationView\n"
    "      id=\"bottomNav\"\n"
    "      app:itemIconTint=\"@color/bottom_nav_color\"\n"
    "      app:itemTextColor=\"@color/bottom_nav_color\"\n"
    "      app:menu=\"@menu/menu_bottom_nav\"/>\n"
    "</LinearLayout>"
)

doc.add_page_break()

# ============ 三、信息输入与交互功能 ============
heading("三、信息输入与交互功能（15分）", 1)
heading("（1）输入框可输入修改且有提示文字（5分）", 2)
para("各 EditText 可正常输入与修改，并设置了 hint 提示（请输入姓名/电话等），"
     "Spinner 提供服务类型下拉选择。")
heading("（2）按钮正确响应（5分）", 2)
para("在 ProfileFragment 中为按钮注册点击监听，点击后立即响应，无按钮无反应问题：")
code_block(
    "btnSave.setOnClickListener(v -> saveProfile());\n"
    "btnQuery.setOnClickListener(v -> showSavedProfile());\n"
    "btnClear.setOnClickListener(v -> clearProfile());"
)
heading("（3）正确获取输入内容并显示结果（5分）", 2)
para("保存时校验姓名非空，读取各控件内容；保存成功后 Toast 提示并把完整信息显示在回显区域：")
code_block(
    "private void saveProfile() {\n"
    "    String name = etName.getText().toString().trim();\n"
    "    if (TextUtils.isEmpty(name)) { etName.setError(\"请输入姓名\"); return; }\n"
    "    SharedPreferences.Editor editor = prefs.edit();\n"
    "    editor.putString(KEY_NAME, name);\n"
    "    editor.putString(KEY_CLASS, etClass.getText().toString().trim());\n"
    "    editor.putString(KEY_PHONE, etPhone.getText().toString().trim());\n"
    "    editor.putString(KEY_SERVICE, getSelectedServiceType());\n"
    "    editor.putString(KEY_NOTE, etNote.getText().toString().trim());\n"
    "    editor.apply();\n"
    "    Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_SHORT).show();\n"
    "    showSavedProfile();   // 在页面回显已保存信息\n"
    "}"
)

doc.add_page_break()

# ============ 四、本地数据保存与读取回显 ============
heading("四、本地数据保存与读取回显功能（20分）", 1)
heading("（1）使用 SharedPreferences 保存用户输入（5分）", 2)
para("通过 getSharedPreferences(\"campus_profile\", MODE_PRIVATE) 获取实例，使用 Editor 写入。")
heading("（2）保存多个字段且与主题相关（5分）", 2)
para("共保存 5 个与校园服务相关字段：姓名、班级、联系电话、服务类型、备注，非单一数据。")
code_block(
    "private static final String PREF_NAME = \"campus_profile\";\n"
    "private static final String KEY_NAME=\"name\", KEY_CLASS=\"class\",\n"
    "        KEY_PHONE=\"phone\", KEY_SERVICE=\"service_type\", KEY_NOTE=\"note\";"
)
heading("（3）保存后提示“保存成功”（5分）", 2)
para("保存成功后通过 Toast 弹出 R.string.save_success（“保存成功”）并在回显区显示结果（见上节代码）。")
heading("（4）重开 APP 自动回显、无数据不闪退（5分）", 2)
para("Fragment 创建时调用 restoreProfile() 自动回填；未保存数据时显示默认文案“暂无保存记录”，"
     "不会闪退：")
code_block(
    "private void restoreProfile() {\n"
    "    if (!prefs.contains(KEY_NAME)) {        // 无数据：显示默认页面，不闪退\n"
    "        tvResult.setText(R.string.no_record);\n"
    "        return;\n"
    "    }\n"
    "    etName.setText(prefs.getString(KEY_NAME, \"\"));\n"
    "    etClass.setText(prefs.getString(KEY_CLASS, \"\"));\n"
    "    etPhone.setText(prefs.getString(KEY_PHONE, \"\"));\n"
    "    etNote.setText(prefs.getString(KEY_NOTE, \"\"));\n"
    "    selectServiceType(prefs.getString(KEY_SERVICE, \"\"));\n"
    "    showSavedProfile();                     // 回显到信息展示区域\n"
    "}"
)

doc.add_page_break()

# ============ 五、服务类扩展功能 ============
heading("五、服务类扩展功能（10分）", 1)
heading("（1）主题内容展示（校园公告列表）（5分）", 2)
para("公告页用 RecyclerView 展示与主题相关的校园公告（教务通知、活动报名、课程提醒、失物招领、"
     "便民服务等），数据来自本地 JSON。")
heading("（2）本地 JSON 读取 + Service/广播/通知（5分）", 2)
para("NoticeRepository 从 assets 读取 notices.json 并用 org.json 解析为 Notice 列表，解析失败"
     "安全返回空列表，避免闪退：")
code_block(
    "public static List<Notice> loadNotices(Context context) {\n"
    "    List<Notice> result = new ArrayList<>();\n"
    "    String json = readAssetFile(context, FILE_NAME);\n"
    "    if (json == null) return result;\n"
    "    try {\n"
    "        JSONObject root = new JSONObject(json);\n"
    "        JSONArray array = root.getJSONArray(\"notices\");\n"
    "        for (int i = 0; i < array.length(); i++) {\n"
    "            JSONObject o = array.getJSONObject(i);\n"
    "            result.add(new Notice(o.optInt(\"id\", i), o.optString(\"category\"),\n"
    "                    o.optString(\"title\"), o.optString(\"location\"),\n"
    "                    o.optString(\"time\"), o.optString(\"content\")));\n"
    "        }\n"
    "    } catch (Exception e) { Log.e(TAG, \"解析公告 JSON 失败\", e); }\n"
    "    return result;\n"
    "}"
)
para("“设为提醒”体现 Service → 广播 → 通知 完整链路：")
code_block(
    "// 1) Fragment：启动 Service\n"
    "Intent i = new Intent(requireContext(), ReminderService.class);\n"
    "i.putExtra(ReminderService.EXTRA_TITLE, notice.getTitle());\n"
    "requireContext().startService(i);\n\n"
    "// 2) ReminderService：读取/封装数据后发广播\n"
    "Intent b = new Intent(this, ReminderReceiver.class);\n"
    "b.setAction(ReminderReceiver.ACTION_REMIND);\n"
    "b.putExtra(ReminderReceiver.EXTRA_TITLE, title);\n"
    "b.putExtra(ReminderReceiver.EXTRA_CONTENT, content);\n"
    "sendBroadcast(b);\n\n"
    "// 3) ReminderReceiver：构建并弹出系统通知 NotificationCompat"
)

doc.add_page_break()

# ============ 六、源代码打包与代码质量 ============
heading("六、源代码打包与代码质量（10分）", 1)
heading("（1）完整可导入运行的 Android 项目（5分）", 2)
para("提交标准 Android Gradle 工程，可直接用 Android Studio 导入运行。"
     "./gradlew assembleDebug 与 lintDebug 均通过。项目文件结构：")
code_block(
    "better02/\n"
    "├── app/\n"
    "│   ├── build.gradle                 # 模块构建脚本与依赖\n"
    "│   └── src/main/\n"
    "│       ├── AndroidManifest.xml      # 组件与权限声明\n"
    "│       ├── assets/notices.json      # 本地公告数据\n"
    "│       ├── java/com/campuslife/app/\n"
    "│       │   ├── MainActivity.java        # 主框架 + 底部导航\n"
    "│       │   ├── HomeFragment.java        # 首页\n"
    "│       │   ├── NoticeFragment.java      # 公告列表\n"
    "│       │   ├── ProfileFragment.java     # 信息登记 + 本地保存/回显\n"
    "│       │   ├── Notice.java              # 数据模型\n"
    "│       │   ├── NoticeRepository.java    # 本地 JSON 读取解析\n"
    "│       │   ├── NoticeAdapter.java       # 列表适配器\n"
    "│       │   ├── ReminderService.java     # 提醒服务\n"
    "│       │   ├── ReminderReceiver.java    # 提醒广播接收者\n"
    "│       │   └── NotificationHelper.java  # 通知渠道\n"
    "│       └── res/                     # 布局/图标/颜色/主题/菜单等资源\n"
    "├── build.gradle / settings.gradle   # 项目级构建脚本（含国内镜像）\n"
    "├── docs/screenshots/                # 运行截图\n"
    "└── README.md                        # 项目文档"
)
heading("（2）命名规范、关键注释、运行截图（5分）", 2)
para("源代码包名 com.campuslife.app，类/方法命名规范、结构清晰，关键方法均有中文注释。"
     "程序运行稳定，无闪退、卡死或按钮无响应。运行截图如下。")

# 运行截图
heading("运行截图", 2)
shots = [
    ("01_home.png", "图1 首页（含底部导航：首页/公告/我的）"),
    ("02_notice_list.png", "图2 校园公告列表（本地 JSON + RecyclerView）"),
    ("03_profile.png", "图3 信息登记页（多输入控件 + 数据回显）"),
    ("04_profile_saved.png", "图4 点击保存提示“保存成功”"),
    ("05_profile_echo.png", "图5 重新打开后数据自动回显"),
    ("06_notification.png", "图6 服务/广播触发的系统通知提醒"),
]
for fn, cap in shots:
    add_img(os.path.join(SHOTS, fn), width=2.3)
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run(cap)
    run.font.size = Pt(9)
    run.italic = True
    set_cn(run)
    doc.add_paragraph()

out = os.path.join(BASE, "docs", "校园生活助手APP_项目答卷.docx")
doc.save(out)
print("saved:", out)
