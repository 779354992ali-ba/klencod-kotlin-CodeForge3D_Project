# CodeForge 3D

مشروع Android أصلي مبني باستخدام Kotlin وJetpack Compose.

## فتح المشروع
1. فك ضغط الأرشيف.
2. افتح مجلد `CodeForge3D` في Android Studio.
3. استخدم JDK 17.
4. نفّذ Gradle Sync ثم Build > Build APK(s).

## هيكل المصدر
- `model/Languages.kt`: تعريف اللغات المدعومة.
- `logic/SyntaxHighlighter.kt`: تلوين الصياغة.
- `logic/CodeError.kt` و`ErrorAnalyzer.kt`: تشخيصات أساسية.
- `ui/CodeEditorView.kt`: حقل تحرير الكود.
- `ui/PreviewWebView.kt`: معاينة الويب وPython عبر Pyodide.
- `ui/EditorScreen.kt`: الشاشة الرئيسية.

## ملاحظات مهمة
- `ErrorAnalyzer` محلل إرشادي خفيف وليس بديلاً عن مترجمات اللغات.
- معاينة JavaScript وHTML/CSS وJSON محلية داخل WebView.
- تشغيل Python يعتمد على تنزيل Pyodide من الإنترنت أول مرة.
- TypeScript يعرض حالياً كـ JavaScript دون خطوة تحويل TypeScript كاملة.
- لغات Java/Kotlin/C/C++/Go/Rust لديها تلوين وتشخيص أقواس فقط، ولا يوجد مترجم مدمج لها.
- لم يتم تنفيذ بناء APK في بيئة الإنشاء الحالية؛ يلزم التحقق بالبناء داخل Android Studio.
