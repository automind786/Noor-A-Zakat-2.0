package com.noorazakat.app.data

data class ReferenceItem(
    val title: String,
    val summary: String,
    val printedReference: String,
    val onlineSource: String,
    val url: String,
    val reviewNote: String
)

object ReferenceCatalog {
    val items = listOf(
        ReferenceItem(
            title = "ज़कात का निसाब और 2.5%",
            summary = "अहल-ए-सुन्नत के प्रकाशित स्रोत में सोने का निसाब 7.5 तोला, चाँदी का निसाब 52.5 तोला और चालीसवाँ हिस्सा (2.5%) दिया गया है।",
            printedReference = "बहार-ए-शरीअत, जिल्द 1, हिस्सा 5, पृ. 902; फ़ैज़ान-ए-ज़कात, पृष्ठ 13; फ़तावा अमजदिया, जिल्द 1, पृ. 378",
            onlineSource = "दावत-ए-इस्लामी — फ़ैज़ान-ए-ज़कात, पृष्ठ 13",
            url = "https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-13",
            reviewNote = "इस पृष्ठ पर निसाब से ऊपर की बढ़ोतरी के लिए ख़ुम्स (पाँचवाँ हिस्सा) की अलग शर्त भी दी गई है। इसलिए केवल कुल वज़न × 2.5% वाला साधारण नियम हर मामले में लागू नहीं किया गया है।"
        ),
        ReferenceItem(
            title = "निसाब से ऊपर की बढ़ोतरी (ख़ुम्स)",
            summary = "फ़ैज़ान-ए-ज़कात के प्रकाशित पाठ में निसाब से ऊपर की मात्रा के लिए ख़ुम्स की सीमा का उदाहरण दिया गया है।",
            printedReference = "फ़तावा رضویہ مُخرّجہ, जिल्द 10, पृ. 85; फ़ैज़ान-ए-ज़कात, पृष्ठ 13",
            onlineSource = "दावत-ए-इस्लामी — फ़ैज़ान-ए-ज़कात, पृष्ठ 13",
            url = "https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-13",
            reviewNote = "मौजूदा engine इस प्रकाशित ख़ुम्स सीमा को केवल indicative calculation के रूप में लागू करता है। हर metal/wealth category में इसके इस्तेमाल की मुफ़्ती से पुष्टि ज़रूरी है; page 15 पर पूरी क़ीमत के 2.5% का आसान भुगतान-तरीका और नफ़्ल अतिरिक्त का ज़िक्र भी है।"
        ),
        ReferenceItem(
            title = "सदक़ा-ए-फ़ित्र की पात्रता और किसकी ओर से अदा करें",
            summary = "स्रोत में फ़ित्रा के लिए निसाब, हौल/माल-ए-नामी की शर्त न होना, ईद की सुबह की स्थिति और पारिवारिक जिम्मेदारियों के अलग नियम दिये गए हैं।",
            printedReference = "फ़राइज़-ए-उलूम सीखिए, सबक 43; बहार-ए-शरीअत, जिल्द 1, हिस्सा 5, पृ. 938; फ़तावा-ए-हिन्दिया, 1/192",
            onlineSource = "दावत-ए-इस्लामी — फ़राइज़-ए-उलूम सीखिए, पृष्ठ 22",
            url = "https://www.dawateislami.net/bookslibrary/ur/farz-uloom-sikhiye/page_22",
            reviewNote = "फ़ित्रा को ज़कात का केवल 'हर व्यक्ति के लिए एक निश्चित रकम' वाला नियम नहीं माना गया है। यह हिसाब-पत्र मात्रा का हिसाब करता है; किस पर वाजिब है और किसकी तरफ़ से कौन दे, वह अलग जाँच का विषय है।"
        ),
        ReferenceItem(
            title = "फ़ित्रा की खाद्य मात्रा",
            summary = "स्रोत में गेहूँ/आटा/सत्तू के लिए आधा साअ (लगभग 1.920 किलो) और जौ/खजूर/मुनक़्का के लिए एक साअ (लगभग 3.840 किलो) की मात्रा दर्ज है।",
            printedReference = "बहार-ए-शरीअत, जिल्द 1, हिस्सा 5, पृ. 938–939; फ़तावा رضویہ, जिल्द 10, पृ. 295; दुर्र-उल-मुख़्तार, 3/372–376",
            onlineSource = "दावत-ए-इस्लामी — फ़ैज़ान-ए-ज़कात, पृष्ठ 49",
            url = "https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-49",
            reviewNote = "पुराने साल की रुपये वाली रकम को मौजूदा भाव नहीं बनाया गया है। ऐप में उपयोगकर्ता स्थानीय भाव दर्ज करता है।"
        ),
        ReferenceItem(
            title = "क़र्ज़ और ज़कात",
            summary = "क़र्ज़ की हर किस्म एक जैसी नहीं; ज़कात के स्रोत में दَین, उधार, मَدیُون और अलग-अलग हालात के लिए विस्तृत अध्याय सूचीबद्ध हैं।",
            printedReference = "फ़ैज़ान-ए-ज़कात, पृष्ठ 13 के विषय-सूची में 'क़र्ज़ और ज़कात' तथा संबंधित उप-विषय; फ़तावा رضویہ और बहार-ए-शरीअत के संदर्भ",
            onlineSource = "दावत-ए-इस्लामी — फ़ैज़ान-ए-ज़कात, पृष्ठ 13",
            url = "https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-13",
            reviewNote = "पुराने ऐप का 'सारा क़र्ज़ cash फिर silver फिर gold से घटाओ' implementation इस source से पर्याप्त रूप से प्रमाणित नहीं है; इसे बंद रखा गया है।"
        ),
        ReferenceItem(
            title = "कमेटी / बीसी के शरई अहकाम",
            summary = "दावत-ए-इस्लामी के दारुल इफ्ता का अलग विस्तृत दस्तावेज़ कमेटी की संरचना और जमा रकम को अलग-अलग अनुबंध की अलग-अलग स्थितियों में देखता है।",
            printedReference = "कमेटी के शरई अहکام، दारالافتاء اہلسنت، 2026 का प्रकाशन",
            onlineSource = "दावत-ए-इस्लामी — कमेटी के शरई अहکام (PDF)",
            url = "https://data2.dawateislami.net/Data/Books/Download/ur/pdf/2026/3679-1.pdf?fn=committee-ke-shari-ahkam",
            reviewNote = "कमेटी/बीसी को बिना अनुबंध और कब्ज़े की स्थिति जाने सीधे संपत्ति या क़र्ज़ का दर्जा नहीं किया जाएगा।"
        ),
        ReferenceItem(
            title = "ज़कात के मसारिफ़ और तमलीक",
            summary = "ज़कात के वितरण में तमलीक और पात्र व्यक्ति/संस्था की जाँच आवश्यक है।",
            printedReference = "क़ुरआन, सूरह अत-तौबा 9:60; बहार-ए-शरीअत, जिल्द 1, हिस्सा 5 — मसारिफ़-ए-ज़कात",
            onlineSource = "क़ुरआन — सूरह अत-तौबा 9:60",
            url = "https://quran.com/9/60",
            reviewNote = "इस प्रारंभिक संस्करण में रिश्तेदारों और संस्थाओं की पात्रता के जटिल मामलों को स्वतः मंज़ूर नहीं किया जाता।"
        ),
        ReferenceItem(
            title = "आपकी दी हुई मूल research",
            summary = "Noor-A-Zakat: Shar'i Tahqeeq va Fiqhi Dastavez — आपके द्वारा दी गई चार-पृष्ठीय निर्देश-पत्र।",
            printedReference = "Noor-A-Zakat Research Document.pdf, पृष्ठ 1–4",
            onlineSource = "User-supplied research document",
            url = "",
            reviewNote = "यह निर्देश-पत्र परियोजना का आधार है, किसी दारुल इफ्ता की अंतिम फ़तवा-प्रमाणन नहीं। इसीलिए जटिल शरई नियम पर source verification जारी रहना चाहिए।"
        ),
        ReferenceItem(
            title = "हौल की तारीख़ का भाव और पूरी क़ीमत पर भुगतान",
            summary = "फ़ैज़ान-ए-ज़कात पृष्ठ 15 के अनुसार बाज़ार भाव माल की जगह का और हौल पूरा होने के दिन का लिया जाता है। नक़द भुगतान का आसान तरीका पूरी क़ीमत का 2.5% है; कुछ अतिरिक्त रकम नफ़्ल हो सकती है।",
            printedReference = "फ़ैज़ान-ए-ज़कात, पृष्ठ 15; बहार-ए-शरीअत, जिल्द 1, हिस्सा 5, मसअला 18, पृ. 908",
            onlineSource = "दावत-ए-इस्लामी — फ़ैज़ान-ए-ज़कात, पृष्ठ 15",
            url = "https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-15",
            reviewNote = "इंटरनेट spot rate स्थानीय/हौल तारीख़ का भाव नहीं है। उपयोगकर्ता को माल की जगह का स्थानीय भाव दर्ज करना और चुने हुए ख़ुम्स/पूरी-क़ीमत तरीके की मुफ़्ती से पुष्टि करना ज़रूरी है।"
        ),
        ReferenceItem(
            title = "मिले-जुले सोने, चाँदी और दूसरे माल का हिसाब",
            summary = "फ़ैज़ान-ए-ज़कात पृष्ठ 14 अलग-अलग माल को जोड़ने, हर श्रेणी के निसाब तक पहुँचने/न पहुँचने और जिस निसाब से फ़क़ीरों को अधिक लाभ हो उसके अनुसार हिसाब लगाने के अलग हालात बताता है।",
            printedReference = "फ़ैज़ान-ए-ज़कात, पृष्ठ 14; रद्द-उल-मुहतार, किताबुज़-ज़कात, 3/271; फ़तावा رضویہ, जिल्द 10, पृ. 115–116",
            onlineSource = "दावत-ए-इस्लामी — फ़ैज़ान-ए-ज़कात, पृष्ठ 14",
            url = "https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-14",
            reviewNote = "एक category अपना निसाब पूरा कर चुकी हो तो केवल net-total वाला generic formula न लगाएँ। मौजूदा engine ऐसे cases को मुफ़्ती review में भेजता है।"
        )
    )

    private data class LocalizedCopy(
        val title: String,
        val summary: String,
        val reviewNote: String,
        val onlineSource: String
    )

    private val translatedCopies: Map<String, List<LocalizedCopy>> = mapOf(
        "en" to listOf(
            LocalizedCopy("Zakat nisab and 2.5%", "Published Ahl-e-Sunnat material lists the gold nisab as 7.5 tola, the silver nisab as 52.5 tola and the fortieth (2.5%) as the standard rate.", "The same source also discusses a khums threshold for excess above nisab in a gold-only example. A simple total-weight × 2.5% rule is not treated as universally applicable.", "Dawat-e-Islami — Faizan-e-Zakat, page 13"),
            LocalizedCopy("Surplus above nisab (khums)", "The cited publication gives an example in which the surplus above nisab is subject to a separate khums threshold.", "The current engine applies this published khums threshold only as an indicative branch. Confirm its use for each metal/wealth category with a Mufti; page 15 also describes a full-value 2.5% payment method with a possible nafl excess.", "Dawat-e-Islami — Faizan-e-Zakat, page 13"),
            LocalizedCopy("Fitrah obligation and who pays for whom", "The source describes separate Fitrah conditions, including nisab, the dawn of Eid and family responsibility; it also notes that Hawl and growth are not conditions in the same way as Zakat.", "Fitrah is not merely a fixed price multiplied by every person in a household. This screen estimates commodity value only; it does not decide obligation or payer responsibility.", "Dawat-e-Islami — Farz Uloom Sikhiye, page 22"),
            LocalizedCopy("Fitrah commodity quantities", "The cited source gives approximately half a Sa' for wheat/flour/sattu (about 1.920 kg) and one Sa' for barley, dates and raisins (about 3.840 kg).", "An old rupee amount is not reused as a मौजूदा भाव. The user enters a local market price; exact local measurement should be confirmed for the relevant jurisdiction.", "Dawat-e-Islami — Faizan-e-Zakat, page 49"),
            LocalizedCopy("Debt and Zakat", "Debts are not all the same. The cited Zakat material treats debt and related cases in separate detailed discussions.", "The old rule 'subtract every debt from cash, then silver, then gold' is not sufficiently established by this reference alone and is disabled.", "Dawat-e-Islami — Faizan-e-Zakat, page 13"),
            LocalizedCopy("Shariah rulings for committee / BC", "A separate Dar-ul-Ifta document discusses committee arrangements and their different contractual circumstances.", "A committee/BC entry cannot automatically be classified as an asset or debt without examining the contract, payment and possession situation.", "Dawat-e-Islami — Committee ke Sharai Ahkam (PDF)"),
            LocalizedCopy("Zakat recipients and tamleek", "Tamleek and recipient eligibility matter in Zakat distribution. Qur'an 9:60 lists the categories of Zakat recipients.", "Complex family and institutional cases are not automatically approved by this starter app. Review the detailed ruling and consult a qualified Mufti where needed.", "क़ुरआन — सूरह अत-तौबा 9:60"),
            LocalizedCopy("Your supplied research document", "Noor-A-Zakat: Shar'i Tahqeeq va Fiqhi Dastavez — the four-page specification supplied for this project.", "This document is a product specification input, not a final fatwa certification by a Dar-ul-Ifta. Complex rules remain subject to source review.", "User-provided research document"),
            LocalizedCopy("Hawl-date market value and full-value payment", "Faizan-e-Zakat page 15 says the market rate is for the asset's location on the date the Hawl completes. Its simple cash-payment method is 2.5% of the full value; the excess may be nafl.", "An internet spot quote is not the local Hawl-date rate. Enter the local rate for the asset location and ask a Mufti to confirm the khums/full-value method.", "Dawat-e-Islami — Faizan-e-Zakat, page 15"),
            LocalizedCopy("Mixed gold, silver and other wealth", "Faizan-e-Zakat page 14 details how categories may be combined when below their individual nisab, and which nisab valuation benefits the poor more.", "Do not apply a generic net-total formula when a category already reaches its own nisab. The current engine sends those cases for Mufti review.", "Dawat-e-Islami — Faizan-e-Zakat, page 14")
        ),
        "ur" to listOf(
            LocalizedCopy("زکوٰۃ کا نصاب اور 2.5٪", "اہلِ سنت کے شائع شدہ مواد میں سونے کا نصاب ساڑھے سات تولہ، چاندی کا نصاب ساڑھے باون تولہ اور چالیسواں حصہ (2.5٪) درج ہے۔", "اسی ماخذ میں نصاب سے زائد مقدار پر خمس کی شرط کی مثال بھی ہے؛ ہر صورت میں کل وزن پر سیدھا 2.5٪ لگانا درست نہیں سمجھا گیا۔", "دعوتِ اسلامی — فیضانِ زکوٰۃ، صفحہ 13"),
            LocalizedCopy("نصاب سے زائد مقدار (خمس)", "مذکورہ مطبوعہ کتاب میں نصاب سے زائد مقدار کے لیے خمس کی الگ حد کی مثال دی گئی ہے۔", "موجودہ انجن اس شائع شدہ خمس کی حد کو صرف ابتدائی حساب کے طور پر لاگو کرتا ہے۔ ہر قسم کے مال میں اس کے اطلاق کی مفتی صاحب سے تصدیق کریں؛ صفحہ 15 پر پوری قیمت کے 2.5٪ اور نفلی اضافے کا آسان طریقہ بھی موجود ہے۔", "دعوتِ اسلامی — فیضانِ زکوٰۃ، صفحہ 13"),
            LocalizedCopy("فطرہ کب واجب ہے اور کس کی طرف سے؟", "ماخذ میں فطرہ کے الگ شرائط بیان ہیں، جن میں نصاب، عید کی صبح اور افرادِ خانہ کی ذمہ داری شامل ہے؛ حول اور مالِ نامی کی شرط زکوٰۃ جیسی نہیں۔", "فطرہ صرف ہر فرد کی تعداد کو ایک مقررہ رقم سے ضرب دینے کا مسئلہ نہیں۔ یہ صفحہ صرف غلے کی مقدار اور مقامی قیمت کا اندازہ دیتا ہے۔", "دعوتِ اسلامی — فرائضِ علوم سیکھیے، صفحہ 22"),
            LocalizedCopy("فطرہ کی غذائی مقدار", "ماخذ میں گیہوں/آٹا/ستو کے لیے تقریباً نصف صاع (1.920 کلو) اور جو، کھجور، منقیٰ کے لیے ایک صاع (3.840 کلو) درج ہے۔", "پچھلے سال کی روپے والی رقم کو موجودہ شرح نہیں بنایا گیا۔ مقامی بازار کا تازہ بھاؤ درج کریں؛ مقامی پیمانے کی تصدیق بھی ضروری ہے۔", "دعوتِ اسلامی — فیضانِ زکوٰۃ، صفحہ 49"),
            LocalizedCopy("قرض اور زکوٰۃ", "ہر قرض ایک جیسا نہیں ہوتا۔ مذکورہ زکوٰۃ کے ماخذ میں قرض اور متعلقہ صورتوں کے الگ تفصیلی ابواب موجود ہیں۔", "پرانا اصول کہ ہر قرض پہلے نقدی، پھر چاندی، پھر سونے سے منہا ہو، اس ماخذ سے اکیلا ثابت نہیں؛ اسے غیر فعال رکھا گیا ہے۔", "دعوتِ اسلامی — فیضانِ زکوٰۃ، صفحہ 13"),
            LocalizedCopy("کمیٹی / بی سی کے شرعی احکام", "دارالافتاء کا الگ دستاویز کمیٹی کے مختلف معاہداتی حالات اور ان کے احکام بیان کرتا ہے۔", "معاہدہ، ادائیگی اور قبضہ دیکھے بغیر کمیٹی کو خودکار طور پر اثاثہ یا قرض قرار نہیں دیا جائے گا۔", "دعوتِ اسلامی — کمیٹی کے شرعی احکام (PDF)"),
            LocalizedCopy("مصارفِ زکوٰۃ اور تملیک", "زکوٰۃ کی ادائیگی میں تملیک اور مستحق کی اہلیت اہم ہیں۔ سورۃ التوبہ آیت 60 میں مصارفِ زکوٰۃ کی اقسام بیان ہوئی ہیں۔", "خاندانی اور ادارہ جاتی پیچیدہ صورتوں کو یہ ابتدائی ایپ خودکار طور پر درست قرار نہیں دیتی۔", "قرآن — سورۃ التوبہ 9:60"),
            LocalizedCopy("آپ کی فراہم کردہ تحقیق", "نورِ زکوٰۃ: شرعی تحقیق و فقہی دستاویز — اس منصوبے کے لیے فراہم کردہ چار صفحات کی specification۔", "یہ دستاویز منصوبے کی ضروریات کا ماخذ ہے، کسی دارالافتاء کی حتمی فتویٰ تصدیق نہیں۔ پیچیدہ احکام مزید جانچ کے محتاج ہیں۔", "صارف کی فراہم کردہ تحقیق"),
            LocalizedCopy("حول کی تاریخ کا بھاؤ اور پوری قیمت پر ادائیگی", "فیضانِ زکوٰۃ صفحہ 15 کے مطابق مال کی جگہ اور حول مکمل ہونے کے دن کا مقامی بازار بھاؤ معتبر ہے۔ نقد ادائیگی کا آسان طریقہ پوری قیمت کا 2.5٪ ہے؛ زائد حصہ نفلی ہو سکتا ہے۔", "انٹرنیٹ کا فوری بھاؤ مقامی حول کی تاریخ کا بھاؤ نہیں۔ مال کی جگہ کا مقامی بھاؤ درج کریں اور خمس/پوری قیمت کے طریقے کی مفتی صاحب سے تصدیق کریں۔", "دعوتِ اسلامی — فیضانِ زکوٰۃ، صفحہ 15"),
            LocalizedCopy("مخلوط سونا، چاندی اور دوسرے اموال", "فیضانِ زکوٰۃ صفحہ 14 میں مختلف مال کے الگ نصاب سے کم یا پورا ہونے کی صورتوں اور فقراء کے زیادہ فائدے والے نصاب کے مطابق حساب کی تفصیل ہے۔", "اگر کوئی قسم اپنا نصاب پورا کر چکی ہو تو عمومی net-total فارمولا نہ لگائیں؛ موجودہ انجن ایسے معاملات مفتی صاحب کے جائزے میں بھیجتا ہے۔", "دعوتِ اسلامی — فیضانِ زکوٰۃ، صفحہ 14")
        ),
        "ar" to listOf(
            LocalizedCopy("نصاب الزكاة ونسبة 2.5٪", "تذكر مواد أهل السنة المنشور نصاب الذهب بسبعة أتوْل ونصف، ونصاب الفضة باثنين وخمسين توْلاً ونصف، والجزء الأربعين (واحد من أربعين، 2.5٪).", "يذكر المصدر نفسه أيضاً شرط الخمس في الزيادة على النصاب في مثال الذهب وحده؛ لذلك لا يُعمَّم ضرب كل الوزن في 2.5٪.", "دعوة إسلامي — فيضان الزكاة، صفحة 13"),
            LocalizedCopy("الزيادة على النصاب (الخمس)", "يعرض المصدر المذكور مثالاً لحدّ مستقل للخمس في المقدار الزائد على النصاب.", "يطبق المحرك الحالي حدّ الخمس المنشور كفرع حسابي إرشادي فقط. يجب تأكيد تطبيقه على كل نوع من المال مع مفتٍ؛ كما تذكر الصفحة 15 طريقة 2.5٪ من كامل القيمة مع احتمال الزيادة النافلة.", "دعوة إسلامي — فيضان الزكاة، صفحة 13"),
            LocalizedCopy("وجوب الفطرة وعمن تُؤدّى", "يذكر المصدر شروطاً خاصة للفطرة، منها النصاب وطلوع فجر العيد ومسؤولية الإنفاق على أفراد الأسرة؛ ولا يشترط فيها الحول والنماء بالطريقة نفسها في الزكاة.", "ليست الفطرة مجرد ضرب عدد أفراد المنزل في مبلغ ثابت. هذه الشاشة تقدّر مقدار الطعام وقيمته فقط ولا تفصل في الوجوب أو مسؤولية الأداء.", "دعوة إسلامي — فرائض العلوم، صفحة 22"),
            LocalizedCopy("مقادير طعام صدقة الفطر", "يذكر المصدر نصف صاع تقريباً من القمح/الدقيق (نحو 1.920 كغ)، وصاعاً من الشعير والتمر والزبيب (نحو 3.840 كغ).", "لا يُعاد استخدام مبلغ نقدي من سنة سابقة كسعر حالي. أدخل سعر السوق المحلي وتحقق من معيار الوزن المعتمد محلياً.", "دعوة إسلامي — فيضان الزكاة، صفحة 49"),
            LocalizedCopy("الدين والزكاة", "ليست الديون كلها في حكم واحد؛ والمصدر المذكور يعرض مسائل الدين وصوره في أبواب مفصلة.", "القاعدة القديمة التي تخصم كل دين من النقد ثم الفضة ثم الذهب لا يثبتها هذا المرجع وحده؛ لذا أُوقف تطبيقها الآلي.", "دعوة إسلامي — فيضان الزكاة، صفحة 13"),
            LocalizedCopy("الأحكام الشرعية للجمعية / اللجنة", "توجد وثيقة مستقلة لدار الإفتاء تشرح صور الجمعيات واختلاف عقودها وأحكامها.", "لا تصنف الجمعية آلياً على أنها مال أو دين قبل فحص العقد والدفع والقبض.", "دعوة إسلامي — أحكام الجمعية (PDF)"),
            LocalizedCopy("مصارف الزكاة والتمليك", "التمليك وأهلية المستحق مهمان في صرف الزكاة. وتذكر الآية 60 من سورة التوبة أصناف مصارف الزكاة.", "لا تعتمد هذه النسخة الحالات العائلية والمؤسسية المعقدة آلياً؛ راجع المسألة واستفت مفتياً مؤهلاً.", "القرآن — سورة التوبة 9:60"),
            LocalizedCopy("البحث الذي قدمه المستخدم", "نور الزكاة: تحقيق شرعي ووثيقة فقهية — المواصفات ذات الصفحات الأربع المقدمة لهذا المشروع.", "هذه الوثيقة مدخل لمتطلبات التطبيق وليست تصديقاً نهائياً بفتوى من دار الإفتاء. تبقى المسائل المعقدة بحاجة إلى مراجعة.", "البحث المقدم من المستخدم"),
            LocalizedCopy("سعر يوم اكتمال الحول والدفع من كامل القيمة", "تذكر فيضان الزكاة ص. 15 أن السعر المحلي المعتبر هو سعر مكان وجود المال يوم اكتمال الحول. وطريقة الدفع النقدي السهلة هي 2.5٪ من كامل القيمة وقد تكون الزيادة نافلة.", "السعر العالمي الفوري عبر الإنترنت ليس سعر السوق المحلي يوم الحول. أدخل السعر المحلي في مكان وجود المال واستشر مفتياً في طريقة الخمس أو كامل القيمة.", "دعوة إسلامي — فيضان الزكاة، صفحة 15"),
            LocalizedCopy("اختلاط الذهب والفضة والأموال الأخرى", "تفصل فيضان الزكاة ص. 14 كيفية ضم الأموال عند كونها دون نصاب كل نوع، واختيار النصاب الذي يحقق نفعاً أكبر للفقراء.", "لا تستخدم قاعدة إجمالي موحدة إذا بلغ نوع من المال نصابه الخاص؛ يحيل المحرك الحالي هذه الحالات إلى مراجعة مفتٍ.", "دعوة إسلامي — فيضان الزكاة، صفحة 14")
        )
    )

    fun itemsFor(language: String): List<ReferenceItem> {
        val translations = translatedCopies[language] ?: return items
        return items.mapIndexed { index, item ->
            val translated = translations.getOrNull(index) ?: return@mapIndexed item
            item.copy(
                title = translated.title,
                summary = translated.summary,
                reviewNote = translated.reviewNote,
                onlineSource = translated.onlineSource
            )
        }
    }

    fun item(index: Int, language: String): ReferenceItem =
        itemsFor(language).getOrElse(index) { items.getOrElse(index) { items.last() } }

}
