package com.example

import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.util.concurrent.TimeUnit

data class GameRound(
    val categoryAr: String,
    val categoryEn: String,
    val secretWordAr: String,
    val secretWordEn: String,
    val decoysAr: List<String>,
    val decoysEn: List<String>
)

object GameData {
    val PreloadedRounds = listOf(
        // 1. Daily Habits & Routines (عادات وتفاصيل يومية)
        GameRound(
            categoryAr = "عادات وتفاصيل يومية",
            categoryEn = "Daily Habits & Routines",
            secretWordAr = "شرب الشاي بعد الغداء",
            secretWordEn = "Drinking Tea After Lunch",
            decoysAr = listOf("الاستيقاظ مبكراً", "غسيل الأسنان", "تصفح الهاتف قبل النوم"),
            decoysEn = listOf("Waking up early", "Brushing teeth", "Checking phone before sleep")
        ),
        GameRound(
            categoryAr = "عادات وتفاصيل يومية",
            categoryEn = "Daily Habits & Routines",
            secretWordAr = "البحث عن مفتاح السيارة",
            secretWordEn = "Searching for car keys",
            decoysAr = listOf("شرب القهوة الصباحية", "ترتيب السرير", "ري النباتات"),
            decoysEn = listOf("Drinking morning coffee", "Making the bed", "Watering plants")
        ),
        GameRound(
            categoryAr = "عادات وتفاصيل يومية",
            categoryEn = "Daily Habits & Routines",
            secretWordAr = "غفوة خمس دقائق إضافية",
            secretWordEn = "Snoozing for 5 more minutes",
            decoysAr = listOf("ممارسة الرياضة", "تحضير الفطور", "مراجعة قائمة المهام"),
            decoysEn = listOf("Doing exercise", "Preparing breakfast", "Checking to-do list")
        ),

        // 2. Viral Trends & Internet (التريندات والإنترنت)
        GameRound(
            categoryAr = "التريندات والإنترنت",
            categoryEn = "Viral Trends & Internet",
            secretWordAr = "تريند رقصة المشاهير",
            secretWordEn = "Viral Celebrity Dance Trend",
            decoysAr = listOf("مقطع طبخ سريع", "تحدي سكب الماء البارد", "مقطع فك صندوق منتج جديد"),
            decoysEn = listOf("Quick cooking video", "Ice bucket challenge", "Product unboxing video")
        ),
        GameRound(
            categoryAr = "التريندات والإنترنت",
            categoryEn = "Viral Trends & Internet",
            secretWordAr = "ميم القطة الضاحكة",
            secretWordEn = "Laughing Cat Meme",
            decoysAr = listOf("فلتر تغيير الملامح", "فيديو قطة لطيفة", "هاشتاق متصدر"),
            decoysEn = listOf("Face-altering filter", "Cute cat video", "Trending hashtag")
        ),
        GameRound(
            categoryAr = "التريندات والإنترنت",
            categoryEn = "Viral Trends & Internet",
            secretWordAr = "تصوير تحدي التيك توك",
            secretWordEn = "Shooting a TikTok Challenge",
            decoysAr = listOf("بث مباشر للألعاب", "تسجيل بودكاست", "كتابة تغريدة مثيرة للجدل"),
            decoysEn = listOf("Gaming livestream", "Recording a podcast", "Writing a controversial tweet")
        ),

        // 3. Retro Tech & Nostalgia (مقتنيات وتكنولوجيا قديمة)
        GameRound(
            categoryAr = "مقتنيات وتكنولوجيا قديمة",
            categoryEn = "Retro Tech & Nostalgia",
            secretWordAr = "شريط الكاسيت",
            secretWordEn = "Cassette Tape",
            decoysAr = listOf("القرص المرن", "جهاز البيجر", "الراديو الخشبي الكبير"),
            decoysEn = listOf("Floppy Disk", "Pager Beeper", "Large wooden radio")
        ),
        GameRound(
            categoryAr = "مقتنيات وتكنولوجيا قديمة",
            categoryEn = "Retro Tech & Nostalgia",
            secretWordAr = "أتاري العائلة",
            secretWordEn = "Atari / Famicom Console",
            decoysAr = listOf("كاميرا التحميض الفوري", "تلفزيون الصندوق الضخم", "كمبيوتر العائلة بشاشة سميكة"),
            decoysEn = listOf("Instant film camera", "Huge box television", "CRT monitor PC")
        ),
        GameRound(
            categoryAr = "مقتنيات وتكنولوجيا قديمة",
            categoryEn = "Retro Tech & Nostalgia",
            secretWordAr = "نوكيا أبو كشاف",
            secretWordEn = "Nokia Lantern Phone",
            decoysAr = listOf("جهاز الوكمان", "الة كاتبة يدوية", "شريط فيديو في إتش إس"),
            decoysEn = listOf("Walkman player", "Manual typewriter", "VHS Video Tape")
        ),

        // 4. Superpowers & Fantasy (قوى خارقة وخيال)
        GameRound(
            categoryAr = "قوى خارقة وخيال",
            categoryEn = "Superpowers & Fantasy",
            secretWordAr = "الاختفاء عن الأنظار",
            secretWordEn = "Invisibility",
            decoysAr = listOf("الطيران في الهواء", "قراءة الأفكار", "التخاطر الذهني"),
            decoysEn = listOf("Flying", "Mind reading", "Telepathy")
        ),
        GameRound(
            categoryAr = "قوى خارقة وخيال",
            categoryEn = "Superpowers & Fantasy",
            secretWordAr = "السفر عبر الزمن",
            secretWordEn = "Time Travel",
            decoysAr = listOf("التخاطب مع الحيوانات", "الشفاء السريع للجروح", "التحكم في الجاذبية"),
            decoysEn = listOf("Talking to animals", "Rapid healing", "Gravity control")
        ),
        GameRound(
            categoryAr = "قوى خارقة وخيال",
            categoryEn = "Superpowers & Fantasy",
            secretWordAr = "إيقاف الوقت والزمن",
            secretWordEn = "Stopping Time",
            decoysAr = listOf("المشي على الماء", "رؤية الأشياء خلف الجدران", "التحول لحيوان آخر"),
            decoysEn = listOf("Walking on water", "X-ray vision", "Shape-shifting")
        ),

        // 5. Unusual Jobs & Professions (وظائف ومهن غريبة)
        GameRound(
            categoryAr = "وظائف ومهن غريبة",
            categoryEn = "Unusual Jobs & Professions",
            secretWordAr = "متذوق طعام الحيوانات",
            secretWordEn = "Pet Food Tester",
            decoysAr = listOf("حارس جزيرة خاصة", "منقح اللؤلؤ الطبيعي", "مصمم ألعاب نارية"),
            decoysEn = listOf("Private island caretaker", "Natural pearl sorter", "Fireworks designer")
        ),
        GameRound(
            categoryAr = "وظائف ومهن غريبة",
            categoryEn = "Unusual Jobs & Professions",
            secretWordAr = "مختبر الألعاب المائية",
            secretWordEn = "Water Slide Tester",
            decoysAr = listOf("طبيب أشجار ونباتات", "مصنف بيض الدجاج", "غواص كرات الغولف"),
            decoysEn = listOf("Tree doctor", "Egg grader", "Golf ball diver")
        ),
        GameRound(
            categoryAr = "وظائف ومهن غريبة",
            categoryEn = "Unusual Jobs & Professions",
            secretWordAr = "مشتم رائحة الإبط",
            secretWordEn = "Deodorant Tester",
            decoysAr = listOf("منوم حيوانات مغناطيسي", "دافع ركاب القطارات", "نائب طابور احترافي"),
            decoysEn = listOf("Animal hypnotist", "Train passenger pusher", "Professional queue stand-in")
        ),

        // 6. Emotions & Feelings (مشاعر وأحاسيس)
        GameRound(
            categoryAr = "مشاعر وأحاسيس",
            categoryEn = "Emotions & Feelings",
            secretWordAr = "تأنيب الضمير بعد أكل الوجبة السريعة",
            secretWordEn = "Guilt After Eating Fast Food",
            decoysAr = listOf("الحماس لرحلة مفاجئة", "الخوف من الغرباء", "الشوق لصديق قديم"),
            decoysEn = listOf("Excitement for a surprise trip", "Fear of strangers", "Longing for an old friend")
        ),
        GameRound(
            categoryAr = "مشاعر وأحاسيس",
            categoryEn = "Emotions & Feelings",
            secretWordAr = "فرحة العثور على ورقة نقدية في الجيب القديم",
            secretWordEn = "Joy of Finding Cash in an Old Pocket",
            decoysAr = listOf("الارتياح عند إلغاء موعد", "الحزن لضياع مفتاح المنزل", "الغضب من زحمة السير"),
            decoysEn = listOf("Relief of cancelling a meeting", "Sadness of losing house keys", "Anger from traffic jam")
        ),
        GameRound(
            categoryAr = "مشاعر وأحاسيس",
            categoryEn = "Emotions & Feelings",
            secretWordAr = "الإحراج عند نسيان اسم شخص يرحب بك",
            secretWordEn = "Embarrassment of Forgetting Someone's Name",
            decoysAr = listOf("الفخر بتحقيق هدف صعب", "الدهشة من مفاجأة جميلة", "التردد قبل اتخاذ قرار مصيري"),
            decoysEn = listOf("Pride in achieving a hard goal", "Surprise from a beautiful gift", "Hesitation before a big decision")
        ),

        // 7. Household Disasters (كوارث منزلية)
        GameRound(
            categoryAr = "كوارث منزلية",
            categoryEn = "Household Disasters",
            secretWordAr = "احتراق الطبخة بالكامل",
            secretWordEn = "Burning the Entire Meal",
            decoysAr = listOf("انكسار طبق الزجاج المفضل", "انسكاب القهوة على السجادة", "ضياع ريموت التلفزيون"),
            decoysEn = listOf("Breaking the favorite glass plate", "Spilling coffee on the carpet", "Losing the TV remote")
        ),
        GameRound(
            categoryAr = "كوارث منزلية",
            categoryEn = "Household Disasters",
            secretWordAr = "تسرب المياه من سقف الحمام",
            secretWordEn = "Water Leaking from Bathroom Ceiling",
            decoysAr = listOf("انقطاع مفاجئ للكهرباء", "توقف فلتر الغسالة", "خراب مقبض الباب الخارجي"),
            decoysEn = listOf("Sudden power outage", "Washing machine filter blockage", "Broken outer door handle")
        ),
        GameRound(
            categoryAr = "كوارث منزلية",
            categoryEn = "Household Disasters",
            secretWordAr = "هروب قطة العائلة خارج المنزل",
            secretWordEn = "Family Cat Escaping Outside",
            decoysAr = listOf("تعطل مكيف الهواء في الصيف", "احتراق مصباح الغرفة الرئيسي", "سقوط الستارة بالكامل"),
            decoysEn = listOf("AC breakdown in summer", "Main room bulb burning out", "Curtain falling down completely")
        ),

        // 8. Places Inside the House (أماكن في المنزل)
        GameRound(
            categoryAr = "أماكن في المنزل",
            categoryEn = "Places Inside the House",
            secretWordAr = "مخزن الكراكيب تحت الدرج",
            secretWordEn = "Clutter Storage Under the Stairs",
            decoysAr = listOf("شرفة المنزل المطلة", "زاوية القراءة الهادئة", "ممر المطبخ الضيق"),
            decoysEn = listOf("The balcony with a view", "Quiet reading corner", "Narrow kitchen corridor")
        ),
        GameRound(
            categoryAr = "أماكن في المنزل",
            categoryEn = "Places Inside the House",
            secretWordAr = "السطح لمشاهدة الغروب",
            secretWordEn = "The Roof for Sunset Watching",
            decoysAr = listOf("غرفة غسيل الملابس", "حديقة المنزل الخلفية", "صالة المعيشة الرئيسية"),
            decoysEn = listOf("The laundry room", "The backyard garden", "The main living room")
        ),
        GameRound(
            categoryAr = "أماكن في المنزل",
            categoryEn = "Places Inside the House",
            secretWordAr = "القبو المظلم القديم",
            secretWordEn = "The Old Dark Basement",
            decoysAr = listOf("مدخل المنزل الأمامي", "ممر غرف النوم", "زاوية طاولة الطعام"),
            decoysEn = listOf("The front entrance foyer", "Bedroom hallway", "Dining table corner")
        ),

        // 9. Things We Always Lose (أشياء نضيعها دائماً)
        GameRound(
            categoryAr = "أشياء نضيعها دائماً",
            categoryEn = "Things We Always Lose",
            secretWordAr = "فردة الجورب الثانية",
            secretWordEn = "The Missing Sock",
            decoysAr = listOf("قلم حبر أزرق", "نظارة القراءة اليومية", "سماعات الأذن اللاسلكية"),
            decoysEn = listOf("Blue ink pen", "Daily reading glasses", "Wireless earbuds")
        ),
        GameRound(
            categoryAr = "أشياء نضيعها دائماً",
            categoryEn = "Things We Always Lose",
            secretWordAr = "ريموت مكيف الهواء",
            secretWordEn = "Air Conditioner Remote",
            decoysAr = listOf("مفتاح السيارة الاحتياطي", "شاحن الهاتف السريع", "مقص الأظافر الصغير"),
            decoysEn = listOf("Spare car key", "Fast phone charger", "Small nail clipper")
        ),
        GameRound(
            categoryAr = "أشياء نضيعها دائماً",
            categoryEn = "Things We Always Lose",
            secretWordAr = "بطاقة الهوية الوطنية",
            secretWordEn = "National ID Card",
            decoysAr = listOf("ربطة الشعر المطاطية", "مظلة المطر", "فلاش ميموري يو إس بي"),
            decoysEn = listOf("Elastic hair tie", "Rain umbrella", "USB Flash Drive")
        ),

        // 10. Common Phobias & Nightmares (كوابيس ومخاوف شائعة)
        GameRound(
            categoryAr = "كوابيس ومخاوف شائعة",
            categoryEn = "Common Phobias & Nightmares",
            secretWordAr = "السقوط من مكان مرتفع في الحلم",
            secretWordEn = "Falling from a High Place in a Dream",
            decoysAr = listOf("نسيان مذاكرة الامتحان النهائي", "التأخر عن رحلة الطائرة", "المشي حافياً في مكان عام"),
            decoysEn = listOf("Forgetting to study for final exam", "Being late for a flight", "Walking barefoot in public")
        ),
        GameRound(
            categoryAr = "كوابيس ومخاوف شائعة",
            categoryEn = "Common Phobias & Nightmares",
            secretWordAr = "الخوف من التحدث أمام الجمهور",
            secretWordEn = "Fear of Public Speaking",
            decoysAr = listOf("فوبيا الأماكن الضيقة", "فوبيا الحشرات الطائرة", "الخوف من طبيب الأسنان"),
            decoysEn = listOf("Claustrophobia / Fear of enclosed spaces", "Fear of flying insects", "Fear of the dentist")
        ),
        GameRound(
            categoryAr = "كوابيس ومخاوف شائعة",
            categoryEn = "Common Phobias & Nightmares",
            secretWordAr = "نفاد شحن الهاتف بمنتصف الطريق",
            secretWordEn = "Phone Battery Dying Mid-Journey",
            decoysAr = listOf("تساقط الأسنان في المنام", "الضياع بمدينة مجهولة ليلاً", "عدم توقف المصعد الكهربائي"),
            decoysEn = listOf("Teeth falling out in a dream", "Getting lost in an unknown city at night", "Elevator not stopping")
        ),

        // 11. Childhood Games (ألعاب طفولة)
        GameRound(
            categoryAr = "ألعاب طفولة",
            categoryEn = "Childhood Games",
            secretWordAr = "لعبة الغميمة",
            secretWordEn = "Hide and Seek",
            decoysAr = listOf("لعبة طاق طاق طاقية", "لعبة صيد السمك المغناطيسية", "لعبة سباق الجري"),
            decoysEn = listOf("Taq Taq Taqia", "Magnetic fishing game", "Running race game")
        ),
        GameRound(
            categoryAr = "ألعاب طفولة",
            categoryEn = "Childhood Games",
            secretWordAr = "لعبة الحجلة والتربيع",
            secretWordEn = "Hopscotch",
            decoysAr = listOf("لعبة شد الحبل", "لعبة الكراسي الموسيقية", "لعبة الكرات الزجاجية"),
            decoysEn = listOf("Tug of war", "Musical chairs", "Marbles game")
        ),
        GameRound(
            categoryAr = "ألعاب طفولة",
            categoryEn = "Childhood Games",
            secretWordAr = "لعبة إكس أو الورقية",
            secretWordEn = "Tic-Tac-Toe on Paper",
            decoysAr = listOf("لعبة البلبل الدوار", "لعبة السلم والثعبان", "لعبة الشرطي والحرامي"),
            decoysEn = listOf("Spinning top toy", "Snakes and Ladders", "Cops and Robbers")
        ),

        // 12. Street Food (مأكولات الشارع)
        GameRound(
            categoryAr = "مأكولات الشارع",
            categoryEn = "Street Food",
            secretWordAr = "كشري مصري بخل وثوم",
            secretWordEn = "Egyptian Koshary",
            decoysAr = listOf("شاورما دجاج صاروخ", "فلافل ساخنة بالسمسم", "بطاطس مقلية بالبهارات"),
            decoysEn = listOf("Chicken shawarma wrap", "Hot falafel with sesame", "Spiced French fries")
        ),
        GameRound(
            categoryAr = "مأكولات الشارع",
            categoryEn = "Street Food",
            secretWordAr = "بليلة ساخنة بالحجازي",
            secretWordEn = "Hijazi Hot Balilah",
            decoysAr = listOf("عرنوس ذرة مشوي", "ساندوتش كبدة بالليمون", "مطبق مالح باللحم"),
            decoysEn = listOf("Grilled corn on the cob", "Liver sandwich with lemon", "Savory meat Mutabbaq")
        ),
        GameRound(
            categoryAr = "مأكولات الشارع",
            categoryEn = "Street Food",
            secretWordAr = "فخفخينا فواكه مشكلة",
            secretWordEn = "Fakhfakhina Mixed Fruits",
            decoysAr = listOf("غزل البنات وردي", "ترمس مملح بالكمون", "كنافة نابلسية عالفحم"),
            decoysEn = listOf("Pink cotton candy", "Salted lupin with cumin", "Nabulsi Kunafa on charcoal")
        ),

        // 13. Things in a Backpack (أدوات في الحقيبة)
        GameRound(
            categoryAr = "أدوات في الحقيبة",
            categoryEn = "Things in a Backpack",
            secretWordAr = "كابل شاحن متشابك",
            secretWordEn = "Tangled Charger Cable",
            decoysAr = listOf("زجاجة ماء بارد", "دفتر ملاحظات صغير", "معقم يدين طبي"),
            decoysEn = listOf("Cold water bottle", "Small notebook", "Hand sanitizer gel")
        ),
        GameRound(
            categoryAr = "أدوات في الحقيبة",
            categoryEn = "Things in a Backpack",
            secretWordAr = "علكة بنكهة النعناع القوي",
            secretWordEn = "Strong Mint Chewing Gum",
            decoysAr = listOf("مظلة جيب قابلة للطي", "سماعات رأس سلكية", "مناديل ورقية معطرة"),
            decoysEn = listOf("Foldable pocket umbrella", "Wired headphones", "Scented wet wipes")
        ),
        GameRound(
            categoryAr = "أدوات في الحقيبة",
            categoryEn = "Things in a Backpack",
            secretWordAr = "مفاتيح المنزل بميدالية مميزة",
            secretWordEn = "House Keys with Keychain",
            decoysAr = listOf("قلم تظليل أصفر", "نظارة شمسية سوداء", "وجبة خفيفة مقرمشة"),
            decoysEn = listOf("Yellow highlighter pen", "Black sunglasses", "Crunchy snack bar")
        ),

        // 14. Weather & Vibes (طقس وأجواء)
        GameRound(
            categoryAr = "طقس وأجواء",
            categoryEn = "Weather & Vibes",
            secretWordAr = "المطر الغزير مع رائحة التراب",
            secretWordEn = "Heavy Rain with Earth Smell",
            decoysAr = listOf("الضباب الكثيف في الصباح", "رعد وبرق يخترق السماء", "غبار وعاصفة رملية مفاجئة"),
            decoysEn = listOf("Thick morning fog", "Thunder and lightning", "Sudden sandstorm")
        ),
        GameRound(
            categoryAr = "طقس وأجواء",
            categoryEn = "Weather & Vibes",
            secretWordAr = "نسيم البحر البارد عند العصر",
            secretWordEn = "Cool Sea Breeze at Afternoon",
            decoysAr = listOf("شمس الصيف اللاهبة", "البرد القارس والثلوج", "رطوبة خانقة على الساحل"),
            decoysEn = listOf("Scorching summer sun", "Freezing cold and snow", "Suffocating coastal humidity")
        ),
        GameRound(
            categoryAr = "طقس وأجواء",
            categoryEn = "Weather & Vibes",
            secretWordAr = "ليلة شتوية دافئة حول الحطب",
            secretWordEn = "Warm Winter Night Campfire",
            decoysAr = listOf("فجر ربيعي هادئ ولطيف", "غروب شمس خريفي مائل للأصفر", "نهار غائم يبشر بالخير"),
            decoysEn = listOf("Quiet spring dawn", "Autumn sunset", "Cloudy day forecasting rain")
        ),

        // 15. Summer Activities (نشاطات صيفية)
        GameRound(
            categoryAr = "نشاطات صيفية",
            categoryEn = "Summer Activities",
            secretWordAr = "بناء قلعة رملية على الشاطئ",
            secretWordEn = "Building a Sandcastle on Beach",
            decoysAr = listOf("السباحة في المسبح الأولمبي", "تناول مثلجات المانجو الباردة", "ركوب القوارب المطاطية"),
            decoysEn = listOf("Swimming in the Olympic pool", "Eating cold mango ice cream", "Riding rubber boats")
        ),
        GameRound(
            categoryAr = "نشاطات صيفية",
            categoryEn = "Summer Activities",
            secretWordAr = "ركوب الدراجات النارية المائية",
            secretWordEn = "Riding a Jet Ski",
            decoysAr = listOf("نزهة برية عند المساء", "السفر لبلد بارد وممطر", "الاسترخاء تحت المظلة الشاطئية"),
            decoysEn = listOf("Desert picnic at evening", "Traveling to a cold rainy country", "Relaxing under beach umbrella")
        ),
        GameRound(
            categoryAr = "نشاطات صيفية",
            categoryEn = "Summer Activities",
            secretWordAr = "الغوص لمشاهدة الشعب المرجانية",
            secretWordEn = "Diving to Watch Coral Reefs",
            decoysAr = listOf("رحلة سفاري بالصحراء", "لعب كرة الطائرة الشاطئية", "حضور المهرجانات والصيفية"),
            decoysEn = listOf("Desert safari trip", "Playing beach volleyball", "Attending summer festivals")
        )
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateWithGemini(promptTheme: String, apiKey: String): GameRound = suspendCancellableCoroutine { continuation ->
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            continuation.resumeWithException(Exception("API Key is missing or invalid."))
            return@suspendCancellableCoroutine
        }

        // We will request a JSON schema from Gemini 3.5 Flash
        val model = "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val systemInstructions = """
            You are an expert game designer for the Arab party game "Secret Ink" (حبر سري).
            You generate a single new game round containing a "General Category" and a "Secret Word" belonging to it, which must be challenging but widely known, culturally relevant to Arab players, and easily translatable to English.
            You must also generate 3 plausible decoy words in both Arabic and English belonging to the same category.
            You must respond with raw JSON matching this schema:
            {
              "category_ar": "general category in Arabic",
              "category_en": "general category in English",
              "secret_word_ar": "specific secret word in Arabic",
              "secret_word_en": "specific secret word in English",
              "decoys_ar": ["decoy 1", "decoy 2", "decoy 3"],
              "decoys_en": ["decoy 1", "decoy 2", "decoy 3"]
            }
        """.trimIndent()

        val userPrompt = if (promptTheme.trim().isEmpty()) {
            "Generate a random fun and culturally relevant category and secret word for the game."
        } else {
            "Generate a category and secret word related to this theme: $promptTheme"
        }

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userPrompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstructions)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 1.0)
                put("responseMimeType", "application/json")
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        continuation.invokeOnCancellation { call.cancel() }

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    if (!resp.isSuccessful) {
                        continuation.resumeWithException(IOException("API Call Failed with code ${resp.code}: ${resp.message}"))
                        return
                    }
                    val bodyString = resp.body?.string() ?: ""
                    try {
                        val jsonResponse = JSONObject(bodyString)
                        val candidates = jsonResponse.getJSONArray("candidates")
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.getJSONObject("content")
                        val parts = content.getJSONArray("parts")
                        val rawText = parts.getJSONObject(0).getString("text").trim()

                        val parsedRound = JSONObject(rawText)
                        val decoysArJson = parsedRound.optJSONArray("decoys_ar")
                        val decoysAr = mutableListOf<String>()
                        if (decoysArJson != null) {
                            for (i in 0 until decoysArJson.length()) {
                                decoysAr.add(decoysArJson.getString(i))
                            }
                        }
                        if (decoysAr.size < 3) {
                            decoysAr.addAll(listOf("بسبوسة", "شاي كرك", "الرياضة"))
                        }

                        val decoysEnJson = parsedRound.optJSONArray("decoys_en")
                        val decoysEn = mutableListOf<String>()
                        if (decoysEnJson != null) {
                            for (i in 0 until decoysEnJson.length()) {
                                decoysEn.add(decoysEnJson.getString(i))
                            }
                        }
                        if (decoysEn.size < 3) {
                            decoysEn.addAll(listOf("Basbousa", "Karak Tea", "Sports"))
                        }

                        val round = GameRound(
                            categoryAr = parsedRound.getString("category_ar"),
                            categoryEn = parsedRound.getString("category_en"),
                            secretWordAr = parsedRound.getString("secret_word_ar"),
                            secretWordEn = parsedRound.getString("secret_word_en"),
                            decoysAr = decoysAr.take(3),
                            decoysEn = decoysEn.take(3)
                        )
                        continuation.resume(round)
                    } catch (e: Exception) {
                        continuation.resumeWithException(Exception("Failed to parse AI output: ${e.message}\nRaw: $bodyString"))
                    }
                }
            }
        })
    }
}
