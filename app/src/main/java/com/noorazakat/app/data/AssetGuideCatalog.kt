package com.noorazakat.app.data

data class AssetGuideItem(
    val title: String,
    val explanation: String,
    val referenceIndex: Int
)

/**
 * Educational summaries transcribed from the user's supplied research document.
 * They are not executable eligibility decisions and require Mufti review before publication.
 */
object AssetGuideCatalog {
    private val hi = listOf(
        AssetGuideItem("रहने का घर", "आपके दिए हुए शोध-पत्र में अपने और परिवार के रहने के लिए रखा घर ज़कात योग्य व्यापारिक माल से अलग बताया गया है। किसी दूसरे इरादे या व्यवस्था में मुफ़्ती से पूछें।", 7),
        AssetGuideItem("किराये का मकान या दुकान", "आपके दिए हुए शोध-पत्र के अनुसार मूल जायदाद के बजाय हाथ में बची किराये की रकम को नक़दी के रूप में अलग जाँचना है।", 7),
        AssetGuideItem("बेचने की नीयत से खरीदा भूखंड", "खरीदते समय तिजारत और बेचने की पक्की नीयत का मसअला है। केवल बाद में बेचने का विचार होना अपने-आप वही हुक्म नहीं बनाता।", 7),
        AssetGuideItem("दुकान का माल", "बेचने के लिए रखा माल और कच्चा माल व्यापारिक संपत्ति के रूप में जाँचा जाता है; मूल्यांकन का आधार मुफ़्ती से तय कराएँ।", 7),
        AssetGuideItem("दुकान के औज़ार और सामान", "काउंटर, मशीन, अलमारियाँ, फर्नीचर और काम के औज़ार आपके शोध-पत्र में अलग रखे गए हैं; उपयोग और कारोबार की प्रकृति जाँचें।", 7),
        AssetGuideItem("कमेटी / बीसी", "जमा किस्तें, पहले उठाई गई रकम, अनुबंध और कब्ज़े की स्थिति अलग-अलग मसाइल पैदा कर सकती है। ऐप इसे अपने-आप संपत्ति या क़र्ज़ नहीं मानेगी।", 5),
        AssetGuideItem("लंबी अवधि का बैंक ऋण", "पूरे ऋण को एक साथ घटाने या केवल बारह मासिक किस्तें घटाने का निर्णय स्वतः नहीं किया गया है। ऋण के प्रकार और देय किस्तों की समीक्षा कराएँ।", 4),
        AssetGuideItem("खेती की ज़मीन और फ़सल", "ज़मीन और फ़सल के उस्र तथा दूसरे नियम अलग हैं। इस ऐप का ज़कात गणक खेती की उपज का हुक्म तय नहीं करता।", 7),
        AssetGuideItem("बैंक का ब्याज", "ब्याज को जायज़ बचत की तरह जोड़कर ज़कात में नहीं गिना गया है। उसके अलग शरई तसर्रुफ़ की रहनुमाई मुफ़्ती से लें।", 7)
    )

    private val ur = listOf(
        AssetGuideItem("رہائشی مکان", "آپ کی تحقیق میں اپنے اور اہلِ خانہ کے رہنے کا مکان اصل زکوٰۃ والے مال سے الگ بتایا گیا ہے۔ دوسری صورت میں مفتی سے پوچھیں۔", 7),
        AssetGuideItem("کرایہ کا مکان یا دکان", "تحقیق کے مطابق اصل جائیداد کے بجائے ہاتھ میں بچی ہوئی کرایہ کی رقم کو نقدی کے طور پر الگ جانچیں۔", 7),
        AssetGuideItem("فروخت کی نیت سے خریدا پلاٹ", "خریدتے وقت تجارت اور فروخت کی پختہ نیت کا مسئلہ ہے؛ بعد میں فروخت کا خیال آنا لازماً وہی حکم نہیں۔", 7),
        AssetGuideItem("دکان کا مال", "فروخت کے لیے رکھا ہوا مال اور خام مال تجارتی اثاثے کے طور پر جانچا جاتا ہے؛ قیمت کا معیار مفتی سے طے کرائیں۔", 7),
        AssetGuideItem("دکان کے آلات", "کاؤنٹر، مشین، ریک، فرنیچر اور کام کے اوزار کو تحقیق میں الگ رکھا گیا ہے؛ استعمال کی صورت دیکھیں۔", 7),
        AssetGuideItem("کمیٹی / بی سی", "جمع شدہ قسطیں، پہلے اٹھائی گئی رقم، معاہدہ اور قبضہ الگ مسائل پیدا کر سکتے ہیں۔ ایپ خودکار فیصلہ نہیں کرے گی۔", 5),
        AssetGuideItem("طویل مدتی بینک قرض", "پورا قرض یا صرف بارہ قسطیں منہا کرنے کا فیصلہ خودکار نہیں کیا گیا؛ قرض کی نوعیت اور واجب الادا قسطوں کی جانچ کرائیں۔", 4),
        AssetGuideItem("زرعی زمین اور فصل", "زمین اور فصل کے عشر/دیگر احکام الگ ہیں۔ ایپ کا زکوٰۃ کیلکولیٹر فصل کا حکم طے نہیں کرتا۔", 7),
        AssetGuideItem("بینک کا سود", "سود کو عام حلال بچت سمجھ کر زکوٰۃ میں شامل نہیں کیا گیا۔ اس کے شرعی تصرف کی رہنمائی مفتی سے لیں۔", 7)
    )

    private val en = listOf(
        AssetGuideItem("Personal residence", "Your supplied specification separates a home kept for personal/family residence from zakatable trade assets. Ask about unusual ownership or use cases.", 7),
        AssetGuideItem("Rental property", "The specification says to review the rent retained as cash separately from the property itself. The exact treatment depends on the case.", 7),
        AssetGuideItem("Plot bought for resale", "The purchase-time intention to trade is material. Merely thinking about selling later is not automatically the same intention.", 7),
        AssetGuideItem("Shop inventory", "Goods and raw materials held for sale are discussed as trade assets in the supplied specification. Have the valuation basis checked by a Mufti.", 7),
        AssetGuideItem("Shop equipment", "Counters, machinery, racks, furniture and work tools are treated separately in the supplied specification; use and business facts matter.", 7),
        AssetGuideItem("Committee / BC", "Saved instalments, an amount already received, contract terms and possession can change the analysis. The app does not auto-label it as asset or debt.", 5),
        AssetGuideItem("Long-term bank loan", "The app does not automatically deduct the entire loan or a fixed 12-EMI amount. Have the loan type and due instalments reviewed.", 4),
        AssetGuideItem("Agricultural land and crops", "Land and crop-ushr rules are distinct. This Zakat calculator does not determine crop dues.", 7),
        AssetGuideItem("Bank interest", "Interest is not treated as ordinary permissible savings in this calculator. Ask a Mufti about its separate disposal.", 7)
    )

    private val ar = listOf(
        AssetGuideItem("مسكن السكن", "تفصل المواصفات المقدمة بين البيت المعد للسكن الشخصي أو العائلي وبين أموال التجارة. اسأل عن حالات الملكية أو الاستعمال غير المعتادة.", 7),
        AssetGuideItem("العقار المؤجر", "تذكر المواصفات مراجعة المال المتبقي من الإيجار كنقد بشكل منفصل عن أصل العقار. ويتوقف الحكم الدقيق على الحالة.", 7),
        AssetGuideItem("أرض اشتريت بقصد البيع", "تعتبر نية التجارة عند الشراء أمراً مهماً؛ ومجرد التفكير في البيع لاحقاً ليس بالضرورة النية نفسها.", 7),
        AssetGuideItem("بضاعة المتجر", "تبحث البضاعة والمواد الخام المعدة للبيع باعتبارها أموال تجارة. راجع أساس التقييم مع مفتٍ.", 7),
        AssetGuideItem("معدات المتجر", "تذكر المواصفات العداد والآلات والرفوف والأثاث وأدوات العمل بشكل منفصل؛ وتهم طبيعة الاستخدام والعمل.", 7),
        AssetGuideItem("الجمعية / اللجنة", "قد تختلف المسألة بحسب الأقساط المدفوعة والمبلغ المستلم والعقد والقبض. لا تصنفها الأداة تلقائياً مالاً أو ديناً.", 5),
        AssetGuideItem("قرض مصرفي طويل الأجل", "لا تخصم الأداة القرض كله ولا اثنتي عشرة قسطاً تلقائياً؛ راجع نوع القرض والأقساط المستحقة.", 4),
        AssetGuideItem("الأرض الزراعية والمحاصيل", "أحكام الأرض وعشر المحصول مختلفة. لا يحدد حاسب الزكاة هذا حكم المحصول.", 7),
        AssetGuideItem("فوائد البنك", "لا تعامل هذه الحاسبة الفائدة كمدخرات مباحة عادية. اسأل مفتياً عن كيفية التصرف فيها.", 7)
    )

    fun items(language: String): List<AssetGuideItem> = when (language) {
        "ur" -> ur
        "en" -> en
        "ar" -> ar
        else -> hi
    }
}
