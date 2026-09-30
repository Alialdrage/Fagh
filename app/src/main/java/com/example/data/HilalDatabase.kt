package com.example.data

import com.example.model.HilalCityData
import com.example.model.HilalFiqhRule
import com.example.model.HilalMonthRecord
import com.example.model.HilalVisibilityStatus
import com.example.model.IslamicEvent

object HilalDatabase {

    val monthsData: List<HilalMonthRecord> = listOf(
        // 1. المحرم 1446
        HilalMonthRecord(
            id = "hilal_1446_01",
            monthIndex = 1,
            monthNameArabic = "المحرم الحرام",
            monthTitleFull = "هلال شهر المحرم الحرام 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الجمعة 5 تموز/يوليو الساعة 01:57 صباحاً بتوقيت النجف",
            observationEvening = "السبت 6 تموز/يوليو 2024 (29 ذو الحجة)",
            expectedFirstDay = "الأحد 7 تموز/يوليو 2024",
            moonAgeHours = 40.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "07:12 م",
                moonsetTime = "08:14 م",
                stayDurationMinutes = 62,
                altitudeDegrees = 11.8,
                illuminationPercent = 2.45,
                elongationAngle = 18.2,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "07:13 م", "08:15 م", 62, 11.9, 2.45, 18.2, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "07:15 م", "08:16 م", 61, 11.6, 2.43, 18.1, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "07:05 م", "08:04 م", 59, 12.5, 2.50, 18.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "07:23 م", "08:24 م", 61, 11.2, 2.40, 18.0, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بيروت", "07:54 م", "08:57 م", 63, 11.8, 2.46, 18.3, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("لندن", "09:20 م", "10:14 م", 54, 8.4, 2.30, 17.5, HilalVisibilityStatus.VISIBLE_IF_CLEAR)
            ),
            sistaniFiqhStatement = "يُتوقع أن يُرى الهلال مرتفعاً وواضحاً بالعين المجردة في مساء السبت في أفق مدينة النجف الأشرف ومعظم البلدان الإسلامية، وبناءً عليه فإن غرة شهر المحرم الحرام لعام 1446هـ تكون يوم الأحد.",
            events = listOf(
                IslamicEvent(1, "رأس السنة الهجرية الجديدة", "دخول عام 1446 للهجرة النبوية المباركة.", isMajor = true),
                IslamicEvent(2, "ورود الإمام الحسين (ع) كربلاء", "وصول ركب سيد الشهداء وأهل بيته إلى أرض الطف عام 61هـ.", isMajor = false),
                IslamicEvent(7, "منع الماء عن معسكر الحسين (ع)", "حصار معسكر الإمام الحسين ومنعهم من شرب ماء الفرات.", isMajor = false),
                IslamicEvent(10, "عاشوراء - استشهاد الإمام الحسين (ع)", "ذكرى استشهاد ريحانة رسول الله وأصحابه وأهل بيته الميامين.", isMajor = true),
                IslamicEvent(12, "دفن الشهداء الأبرار", "دفن الأجساد الطاهرة في كربلاء المقدسة.", isMajor = false),
                IslamicEvent(25, "شهادة الإمام علي بن الحسين السجاد (ع)", "ذكرى شهادة الإمام زين العابدين عليه السلام.", isMajor = true)
            )
        ),

        // 2. صفر 1446
        HilalMonthRecord(
            id = "hilal_1446_02",
            monthIndex = 2,
            monthNameArabic = "صفر الخير",
            monthTitleFull = "هلال شهر صفر الخير 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الأحد 4 آب/أغسطس الساعة 02:13 ظهراً بتوقيت النجف",
            observationEvening = "الاثنين 5 آب/أغسطس 2024 (29 محرم)",
            expectedFirstDay = "الثلاثاء 6 آب/أغسطس 2024",
            moonAgeHours = 29.0,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "06:58 م",
                moonsetTime = "07:44 م",
                stayDurationMinutes = 46,
                altitudeDegrees = 9.2,
                illuminationPercent = 1.62,
                elongationAngle = 14.5,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "06:59 م", "07:45 م", 46, 9.3, 1.62, 14.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "07:01 م", "07:46 م", 45, 9.0, 1.60, 14.4, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "06:56 م", "07:40 م", 44, 9.8, 1.68, 14.7, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "07:08 م", "07:53 م", 45, 8.8, 1.58, 14.3, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("بيروت", "07:38 م", "08:24 م", 46, 9.1, 1.63, 14.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("لندن", "08:44 م", "09:22 م", 38, 6.7, 1.45, 13.8, HilalVisibilityStatus.VISIBLE_IF_CLEAR)
            ),
            sistaniFiqhStatement = "يُتوقع رؤية الهلال بالعين المجردة بصورة واضحة في حال خلو الجو من العوالق، ويكون يوم الثلاثاء هو غرة شهر صفر الخير.",
            events = listOf(
                IslamicEvent(1, "ورود سبايا أهل البيت إلى الشام", "دخول حرم رسول الله (ص) وعلي بن الحسين (ع) إلى دمشق.", isMajor = false),
                IslamicEvent(7, "شهادة الإمام الحسن المجتبى (ع)", "على رواية، واستشهاد الإمام موسى الكاظم (ع) على رواية.", isMajor = true),
                IslamicEvent(20, "زيارة أربعينية الإمام الحسين (ع)", "المسيرة المليونية المباركة لزيارة مرقد سيد الشهداء بكربلاء.", isMajor = true),
                IslamicEvent(28, "وفاة النبي الأعظم محمد (ص)", "ذكرى رحيل خاتم الأنبياء والمرسلين رسول الإنسانية محمد (ص).", isMajor = true),
                IslamicEvent(29, "شهادة الإمام علي بن موسى الرضا (ع)", "ذكرى استشهاد ثامن أئمة الهدى عليه السلام في طوس خراسان.", isMajor = true)
            )
        ),

        // 3. ربيع الأول 1446
        HilalMonthRecord(
            id = "hilal_1446_03",
            monthIndex = 3,
            monthNameArabic = "ربيع الأول",
            monthTitleFull = "هلال شهر ربيع الأول 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الثلاثاء 3 أيلول/سبتمبر الساعة 04:55 فجراً بتوقيت النجف",
            observationEvening = "الأربعاء 4 أيلول/سبتمبر 2024 (29 صفر)",
            expectedFirstDay = "الخميس 5 أيلول/سبتمبر 2024",
            moonAgeHours = 37.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "06:24 م",
                moonsetTime = "07:06 م",
                stayDurationMinutes = 42,
                altitudeDegrees = 8.1,
                illuminationPercent = 1.35,
                elongationAngle = 13.4,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "06:25 م", "07:07 م", 42, 8.2, 1.35, 13.4, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "06:26 م", "07:07 م", 41, 7.9, 1.33, 13.3, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "06:33 م", "07:14 م", 41, 8.7, 1.40, 13.7, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "06:30 م", "07:10 م", 40, 7.6, 1.30, 13.1, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("بيروت", "07:01 م", "07:43 م", 42, 8.0, 1.34, 13.4, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "يُتوقع أن يُرى الهلال بالعين المجردة في حال صفاء الأفق في مساء الأربعاء، وبذلك يكون الخميس أول أيام شهر ربيع الأول المبارك.",
            events = listOf(
                IslamicEvent(1, "هجرة النبي (ص) ومبيت علي (ع) في فراشه", "ليلة المبيت وتضحية أمير المؤمنين عليه السلام.", isMajor = true),
                IslamicEvent(8, "شهادة الإمام الحسن العسكري (ع)", "ذكرى استشهاد الإمام الحادي عشر من أئمة أهل البيت.", isMajor = true),
                IslamicEvent(9, "تتويج الإمام الحجة بن الحسن (عج)", "بدء إمامة بقية الله الأعظم عجل الله فرجه الشريف.", isMajor = true),
                IslamicEvent(17, "المولد النبوي الشريف ومولد الإمام الصادق (ع)", "أعظم مناسبات الفرح الإسلامي بمولد سيد الكونين.", isMajor = true)
            )
        ),

        // 4. ربيع الآخر 1446
        HilalMonthRecord(
            id = "hilal_1446_04",
            monthIndex = 4,
            monthNameArabic = "ربيع الآخر",
            monthTitleFull = "هلال شهر ربيع الآخر 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الأربعاء 2 تشرين الأول/أكتوبر الساعة 09:49 مساءً بتوقيت النجف",
            observationEvening = "الخميس 3 تشرين الأول/أكتوبر 2024",
            expectedFirstDay = "السبت 5 تشرين الأول/أكتوبر 2024",
            moonAgeHours = 20.8,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "05:46 م",
                moonsetTime = "06:17 م",
                stayDurationMinutes = 31,
                altitudeDegrees = 5.7,
                illuminationPercent = 0.72,
                elongationAngle = 9.8,
                visibilityStatus = HilalVisibilityStatus.TELESCOPE_ONLY
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "05:47 م", "06:17 م", 30, 5.6, 0.71, 9.7, HilalVisibilityStatus.TELESCOPE_ONLY),
                HilalCityData("بغداد", "05:47 م", "06:17 م", 30, 5.4, 0.70, 9.6, HilalVisibilityStatus.TELESCOPE_ONLY),
                HilalCityData("مكة المكرمة", "06:05 م", "06:37 م", 32, 6.3, 0.78, 10.1, HilalVisibilityStatus.TELESCOPE_ONLY)
            ),
            sistaniFiqhStatement = "بما أن الهلال في مساء الخميس يغيب بعد غروب الشمس بمدة يسيرة ولا يمكن رؤيته بالعين المجردة، وبما أن الرؤية بالتلسكوب لا تثبت بها بداية الشهر عند سماحة السيد، فيكون الجمعة متمماً لربيع الأول، والسبت غرة ربيع الآخر.",
            events = listOf(
                IslamicEvent(8, "ولادة الإمام الحسن العسكري (ع)", "ذكرى ولادة الإمام الحادي عشر عليه السلام.", isMajor = true),
                IslamicEvent(10, "وفاة السيدة فاطمة المعصومة (ع)", "ذكرى وفاة كريمة أهل البيت المدفونة بقم المقدسة.", isMajor = true)
            )
        ),

        // 5. جمادى الأولى 1446
        HilalMonthRecord(
            id = "hilal_1446_05",
            monthIndex = 5,
            monthNameArabic = "جمادى الأولى",
            monthTitleFull = "هلال شهر جمادى الأولى 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الجمعة 1 تشرين الثاني/نوفمبر الساعة 03:47 عصراً",
            observationEvening = "السبت 2 تشرين الثاني/نوفمبر 2024",
            expectedFirstDay = "الاثنين 4 تشرين الثاني/نوفمبر 2024",
            moonAgeHours = 27.2,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "05:13 م",
                moonsetTime = "05:49 م",
                stayDurationMinutes = 36,
                altitudeDegrees = 6.8,
                illuminationPercent = 1.05,
                elongationAngle = 11.8,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_IF_CLEAR
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "05:13 م", "05:49 م", 36, 6.8, 1.05, 11.8, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("مكة المكرمة", "05:43 م", "06:21 م", 38, 7.8, 1.15, 12.3, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "تُتوقع الرؤية بالعين المجردة مع صفاء الجو التام وسلامة الأفق من الغبار والضباب.",
            events = listOf(
                IslamicEvent(5, "ولادة السيدة زينب الكبرى (ع)", "يوم الممرض وبطلة كربلاء عقيلة الهاشميين.", isMajor = true),
                IslamicEvent(13, "شهادة السيدة فاطمة الزهراء (ع)", "الرواية الأولى لاستشهاد بضعة الرسول الأكرم.", isMajor = true)
            )
        ),

        // 6. جمادى الآخرة 1446
        HilalMonthRecord(
            id = "hilal_1446_06",
            monthIndex = 6,
            monthNameArabic = "جمادى الآخرة",
            monthTitleFull = "هلال شهر جمادى الآخرة 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الأحد 1 كانون الأول/ديسمبر الساعة 09:21 صباحاً",
            observationEvening = "الاثنين 2 كانون الأول/ديسمبر 2024",
            expectedFirstDay = "الأربعاء 4 كانون الأول/ديسمبر 2024",
            moonAgeHours = 32.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "04:58 م",
                moonsetTime = "05:48 م",
                stayDurationMinutes = 50,
                altitudeDegrees = 9.8,
                illuminationPercent = 1.82,
                elongationAngle = 15.6,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "04:58 م", "05:48 م", 50, 9.8, 1.82, 15.6, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "04:57 م", "05:46 م", 49, 9.5, 1.80, 15.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "يُتوقع أن يُرى الهلال بالعين المجردة مرتفعاً وجلياً في مساء الاثنين، فيكون الأربعاء غرة جمادى الآخرة.",
            events = listOf(
                IslamicEvent(3, "شهادة سيدة نساء العالمين فاطمة الزهراء (ع)", "الرواية الثالثة المشهورة (الأيام الفاطمية الكبرى).", isMajor = true),
                IslamicEvent(13, "وفاة السيدة أم البنين (ع)", "ذكرى رحيل والدة أبي الفضل العباس وإخوته.", isMajor = true),
                IslamicEvent(20, "ولادة سيدة نساء العالمين فاطمة الزهراء (ع)", "يوم المرأة المسلمة والبر بالوالدة.", isMajor = true)
            )
        ),

        // 7. رجب الأصب 1446
        HilalMonthRecord(
            id = "hilal_1446_07",
            monthIndex = 7,
            monthNameArabic = "رجب الأصب",
            monthTitleFull = "هلال شهر رجب الأصب 1446هـ (الأشهر الحرم)",
            hijriYear = 1446,
            conjunctionDateTime = "الاثنين 30 كانون الأول/ديسمبر الساعة 01:27 بعد الظهر",
            observationEvening = "الثلاثاء 31 كانون الأول/ديسمبر 2024 (29 جمادى الآخرة)",
            expectedFirstDay = "الخميس 2 كانون الثاني/يناير 2025",
            moonAgeHours = 28.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "05:08 م",
                moonsetTime = "06:05 م",
                stayDurationMinutes = 57,
                altitudeDegrees = 11.2,
                illuminationPercent = 2.10,
                elongationAngle = 16.8,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "05:08 م", "06:05 م", 57, 11.2, 2.10, 16.8, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "05:48 م", "06:45 م", 57, 12.1, 2.25, 17.3, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "05:04 م", "06:00 م", 56, 10.6, 2.05, 16.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "الهلال مرتفع ومكثه قرابة ساعة بعد الغروب وتتحقق رؤيته بالعين المجردة بوضوح تام، ويكون الأربعاء متمماً أو الخميس بداية رجب الأصب.",
            events = listOf(
                IslamicEvent(1, "ولادة الإمام محمد الباقر (ع)", "باقر علم الأولين والآخرين خامس الأئمة.", isMajor = true),
                IslamicEvent(3, "شهادة الإمام علي الهادي (ع)", "ذكرى استشهاد الإمام العاشر بسامراء.", isMajor = true),
                IslamicEvent(10, "ولادة الإمام محمد الجواد (ع)", "باب المراد تاسع أئمة الهدى.", isMajor = true),
                IslamicEvent(13, "ولادة أمير المؤمنين علي بن أبي طالب (ع)", "وليد الكعبة المشرفة ومولى الموحدين عليه السلام.", isMajor = true),
                IslamicEvent(25, "شهادة الإمام موسى بن جعفر الكاظم (ع)", "راهب بني هاشم وباب الحوائج في بغداد الكاظمية.", isMajor = true),
                IslamicEvent(27, "المبعث النبوي الشريف", "نزول جبريل بالرسالة الإلهية الخاتمة على رسول الله (ص).", isMajor = true)
            )
        ),

        // 8. شعبان المعظم 1446
        HilalMonthRecord(
            id = "hilal_1446_08",
            monthIndex = 8,
            monthNameArabic = "شعبان المعظم",
            monthTitleFull = "هلال شهر شعبان المعظم 1446هـ",
            hijriYear = 1446,
            conjunctionDateTime = "الأربعاء 29 كانون الثاني/يناير 2025 الساعة 03:36 فجراً",
            observationEvening = "الخميس 30 كانون الثاني/يناير 2025 (29 رجب)",
            expectedFirstDay = "السبت 1 شباط/فبراير 2025",
            moonAgeHours = 39.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "05:32 م",
                moonsetTime = "06:44 م",
                stayDurationMinutes = 72,
                altitudeDegrees = 14.5,
                illuminationPercent = 3.30,
                elongationAngle = 21.0,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "05:32 م", "06:44 م", 72, 14.5, 3.30, 21.0, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "05:31 م", "06:42 م", 71, 14.2, 3.25, 20.8, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "06:09 م", "07:18 م", 69, 15.0, 3.40, 21.4, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "الهلال مرتفع جداً وواضح للعيان دون أدنى شك بالعين المجردة، وتثبت غرة شهر شعبان المبارك.",
            events = listOf(
                IslamicEvent(3, "ولادة الإمام الحسين بن علي (ع)", "سيد شباب أهل الجنة وسبط رسول الله.", isMajor = true),
                IslamicEvent(4, "ولادة أبي الفضل العباس (ع)", "قمر بني هاشم وساقي عطاشى كربلاء.", isMajor = true),
                IslamicEvent(5, "ولادة الإمام زين العابدين السجاد (ع)", "زين العباد وسيد الساجدين عليه السلام.", isMajor = true),
                IslamicEvent(11, "ولادة علي الأكبر (ع)", "شبيهاً برسول الله خَلقاً وخُلقاً ومنطقاً.", isMajor = true),
                IslamicEvent(15, "ولادة الإمام المهدي المنتظر (عج)", "النصف من شعبان، ليلة مباركة وذكرى ولادة منجي البشرية.", isMajor = true)
            )
        ),

        // 9. شهر رمضان المبارك 1446
        HilalMonthRecord(
            id = "hilal_1446_09",
            monthIndex = 9,
            monthNameArabic = "شهر رمضان المبارك",
            monthTitleFull = "هلال شهر رمضان المبارك 1446هـ (شهر الله وضيافته)",
            hijriYear = 1446,
            conjunctionDateTime = "الجمعة 28 شباط/فبراير 2025 الساعة 03:45 عصراً",
            observationEvening = "السبت 1 آذار/مارس 2025 (29 شعبان)",
            expectedFirstDay = "الاثنين 3 آذار/مارس 2025",
            moonAgeHours = 27.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "05:58 م",
                moonsetTime = "06:33 م",
                stayDurationMinutes = 35,
                altitudeDegrees = 6.9,
                illuminationPercent = 1.05,
                elongationAngle = 11.9,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_IF_CLEAR
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "05:58 م", "06:33 م", 35, 6.9, 1.05, 11.9, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("بغداد", "05:57 م", "06:31 م", 34, 6.6, 1.02, 11.7, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("مكة المكرمة", "06:27 م", "07:05 م", 38, 8.1, 1.18, 12.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "05:48 م", "06:21 م", 33, 6.3, 0.98, 11.4, HilalVisibilityStatus.TELESCOPE_ONLY),
                HilalCityData("بيروت", "06:26 م", "07:01 م", 35, 6.8, 1.04, 11.8, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("لندن", "06:40 م", "07:08 م", 28, 4.2, 0.80, 10.2, HilalVisibilityStatus.IMPOSSIBLE)
            ),
            sistaniFiqhStatement = "في مساء يوم السبت يُتوقع أن يُرى الهلال بالعين المجردة في حال صفاء الجو التام في أفق النجف الأشرف، وفي حال عدم التمكن من الرؤية بالعين المجردة لغبار أو غيم فتتم عدة شعبان ثلاثين يوماً ويكون أول شهر رمضان يوم الاثنين.",
            events = listOf(
                IslamicEvent(1, "أول أيام شهر الصيام المبارك", "دخول شهر الرحمة والمغفرة والعتق من النار.", isMajor = true),
                IslamicEvent(10, "وفاة السيدة خديجة الكبرى (ع)", "أم المؤمنين ووزيرة صدق رسول الله الأكرم.", isMajor = true),
                IslamicEvent(15, "ولادة الإمام الحسن المجتبى (ع)", "كريم أهل البيت وسبط النبي المصطفى الأول.", isMajor = true),
                IslamicEvent(19, "ضربة أمير المؤمنين الإمام علي (ع) في محراب الكوفة", "أولى ليالي القدر المباركة.", isMajor = true),
                IslamicEvent(21, "شهادة أمير المؤمنين علي بن أبي طالب (ع)", "ليلة القدر الثانية وذكرى الفاجعة العظمى.", isMajor = true),
                IslamicEvent(23, "ليلة القدر العظمى الثالثة", "أرجى ليالي القدر لنيل المغفرة وقدر الأرزاق والآجال.", isMajor = true)
            )
        ),

        // 10. شوال المكرم 1446 (عيد الفطر)
        HilalMonthRecord(
            id = "hilal_1446_10",
            monthIndex = 10,
            monthNameArabic = "شوال المكرم",
            monthTitleFull = "هلال شهر شوال المكرم 1446هـ (هلال عيد الفطر السعيد)",
            hijriYear = 1446,
            conjunctionDateTime = "السبت 29 آذار/مارس 2025 الساعة 01:58 بعد الظهر",
            observationEvening = "الأحد 30 آذار/مارس 2025 (29 شهر رمضان)",
            expectedFirstDay = "الاثنين 31 آذار/مارس 2025",
            moonAgeHours = 30.5,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "06:19 م",
                moonsetTime = "07:05 م",
                stayDurationMinutes = 46,
                altitudeDegrees = 9.4,
                illuminationPercent = 1.70,
                elongationAngle = 14.8,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "06:20 م", "07:06 م", 46, 9.4, 1.70, 14.8, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "06:20 م", "07:05 م", 45, 9.1, 1.68, 14.7, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "06:40 م", "07:27 م", 47, 10.2, 1.80, 15.2, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "06:14 م", "06:58 م", 44, 8.8, 1.62, 14.4, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بيروت", "06:51 م", "07:38 م", 47, 9.5, 1.72, 14.9, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("لندن", "07:31 م", "08:14 م", 43, 7.8, 1.50, 13.9, HilalVisibilityStatus.VISIBLE_IF_CLEAR)
            ),
            sistaniFiqhStatement = "يُتوقع أن يُرى الهلال بالعين المجردة واضحاً في مساء يوم الأحد 30 آذار في أفق النجف الأشرف ومعظم بلاد العالم الإسلامي، وعليه يكون يوم الاثنين هو يوم عيد الفطر السعيد وأول شوال المكرم.",
            events = listOf(
                IslamicEvent(1, "عيد الفطر السعيد", "يوم الجائزة، إخراج زكاة الفطرة وحرمة الصيام.", isMajor = true),
                IslamicEvent(8, "ذكرى هدم قبور أئمة البقيع", "فاجعة هدم الأضرحة الطاهرة في المدينة المنورة عام 1344هـ.", isMajor = true),
                IslamicEvent(25, "شهادة الإمام جعفر بن محمد الصادق (ع)", "مؤسس المذهب وناشر علوم أهل البيت عليهم السلام.", isMajor = true)
            )
        ),

        // 11. ذو القعدة الحرام 1446
        HilalMonthRecord(
            id = "hilal_1446_11",
            monthIndex = 11,
            monthNameArabic = "ذو القعدة الحرام",
            monthTitleFull = "هلال شهر ذو القعدة الحرام 1446هـ (الأشهر الحرم)",
            hijriYear = 1446,
            conjunctionDateTime = "الأحد 27 نيسان/أبريل 2025 الساعة 10:31 مساءً",
            observationEvening = "الاثنين 28 نيسان/أبريل 2025",
            expectedFirstDay = "الأربعاء 30 نيسان/أبريل 2025",
            moonAgeHours = 21.0,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "06:41 م",
                moonsetTime = "07:18 م",
                stayDurationMinutes = 37,
                altitudeDegrees = 7.1,
                illuminationPercent = 1.02,
                elongationAngle = 11.6,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_IF_CLEAR
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "06:42 م", "07:19 م", 37, 7.1, 1.02, 11.6, HilalVisibilityStatus.VISIBLE_IF_CLEAR),
                HilalCityData("مكة المكرمة", "06:51 م", "07:31 م", 40, 8.3, 1.15, 12.2, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "الرؤية ممكنة بالعين المجردة مع نقاء الجو، فإن لم تثبت الرؤية لغبار أو نحوه يتم الشهر ثلاثين يوماً.",
            events = listOf(
                IslamicEvent(1, "ولادة السيدة فاطمة المعصومة (ع)", "بداية أيام عشرة الكرامة.", isMajor = true),
                IslamicEvent(11, "ولادة الإمام علي بن موسى الرضا (ع)", "أنيس النفوس وشمس الشموس عليه السلام.", isMajor = true),
                IslamicEvent(25, "يوم دحو الأرض", "يوم بسط الأرض من تحت الكعبة، يُستحب صيامه عبادةً.", isMajor = true),
                IslamicEvent(29, "شهادة الإمام محمد الجواد (ع)", "ذكرى استشهاد الإمام التاسع في بغداد الكاظمية.", isMajor = true)
            )
        ),

        // 12. ذو الحجة الحرام 1446
        HilalMonthRecord(
            id = "hilal_1446_12",
            monthIndex = 12,
            monthNameArabic = "ذو الحجة الحرام",
            monthTitleFull = "هلال شهر ذو الحجة الحرام 1446هـ (الحج وعيد الأضحى والغدير)",
            hijriYear = 1446,
            conjunctionDateTime = "الثلاثاء 27 أيار/مايو 2025 الساعة 06:02 صباحاً",
            observationEvening = "الأربعاء 28 أيار/مايو 2025 (29 ذو القعدة)",
            expectedFirstDay = "الخميس 29 أيار/مايو 2025",
            moonAgeHours = 38.0,
            primaryCity = HilalCityData(
                cityName = "النجف الأشرف",
                sunsetTime = "07:03 م",
                moonsetTime = "08:08 م",
                stayDurationMinutes = 65,
                altitudeDegrees = 12.4,
                illuminationPercent = 2.65,
                elongationAngle = 18.9,
                visibilityStatus = HilalVisibilityStatus.VISIBLE_NAKED_EYE
            ),
            otherCities = listOf(
                HilalCityData("كربلاء المقدسة", "07:04 م", "08:09 م", 65, 12.4, 2.65, 18.9, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بغداد", "07:05 م", "08:09 م", 64, 12.1, 2.60, 18.7, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("مكة المكرمة", "07:00 م", "08:02 م", 62, 13.0, 2.75, 19.3, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("قم المقدسة", "07:11 م", "08:14 م", 63, 11.8, 2.55, 18.5, HilalVisibilityStatus.VISIBLE_NAKED_EYE),
                HilalCityData("بيروت", "07:44 م", "08:51 م", 67, 12.5, 2.68, 19.0, HilalVisibilityStatus.VISIBLE_NAKED_EYE)
            ),
            sistaniFiqhStatement = "الهلال مرتفع جداً ومكثه يزيد عن ساعة، والرؤية بالعين المجردة واضحة ومؤكدة في عموم الآفاق الإسلامية.",
            events = listOf(
                IslamicEvent(1, "زواج الإمام علي والسيدة فاطمة الزهراء (ع)", "اقتران النورين في الأول من ذي الحجة.", isMajor = true),
                IslamicEvent(7, "شهادة الإمام محمد الباقر (ع)", "ذكرى استشهاد الإمام الخامس المدفون بالبقيع.", isMajor = true),
                IslamicEvent(8, "يوم التروية - خروج الإمام الحسين من مكة", "توجه سيد الشهداء نحو العراق وكربلاء.", isMajor = true),
                IslamicEvent(9, "يوم عرفة وشهادة مسلم بن عقيل (ع)", "أعظم أيام الدعاء وسفير الإمام الحسين بالكوفة.", isMajor = true),
                IslamicEvent(10, "عيد الأضحى المبارك", "يوم النحر وأداء مناسك منى الكبرى.", isMajor = true),
                IslamicEvent(18, "عيد الغدير الأغر", "عيد الله الأكبر، إعلان بيعة وإمامة أمير المؤمنين علي بن أبي طالب (ع).", isMajor = true),
                IslamicEvent(24, "يوم المباهلة والتصدق بالخاتم", "مباهلة النبي لنصارى نجران بنزول آية المباهلة.", isMajor = true)
            )
        )
    )

    val fiqhHilalRules: List<HilalFiqhRule> = listOf(
        HilalFiqhRule(
            id = "rule_1",
            title = "معيار الرؤية بالعين المجردة",
            questionOrTopic = "هل تثبت بداية الشهر الشرعي برؤية الهلال بالتلسكوب أو المراصد المقربة؟",
            ruling = "لا يثبت الهلال بالرؤية بالعين المسلحة (كالمراصد والتلسكوبات والكاميرات المقربة)، بل يشترط في ثبوته شرعاً الرؤية بالعين المجردة العادية الاعتيادية. فإذا لم تكن الرؤية ممكنة إلا بالآلات المقربة فلا يحكم بدخول الشهر.",
            source = "منهاج الصالحين - ج1، كتاب الصوم، طرق ثبوت الهلال، م 1044"
        ),
        HilalFiqhRule(
            id = "rule_2",
            title = "مبنى اتحاد الأفق (الاشتراك في الليل)",
            questionOrTopic = "إذا رُئي الهلال في بلد فهل يثبت في سائر البلدان؟ وما هو ضابط وحدة الأفق؟",
            ruling = "يكفي في ثبوت الهلال في بلد أن يُرى في بلد آخر يشترك معه في ليل واحد ولو لجزء يسير منه، بشرط أن يكون البلد الذي رُئي فيه واقعاً في شرق البلد الآخر أو كانا متساويين في خط الطول مع عدم بعد شاسع. أما إذا رُئي في الغرب فلا يثبت في الشرق إلا إذا علم بإمكانية رؤيته هناك.",
            source = "منهاج الصالحين - كتاب الصوم، م 1045 والمسائل المنتخبة م 492"
        ),
        HilalFiqhRule(
            id = "rule_3",
            title = "الحسابات الفلكية ورأي الفلكيين",
            questionOrTopic = "ما هي القيمة الشرعية لتقارير المراصد والحسابات الفلكية في تحديد أوائل الشهور؟",
            ruling = "الحسابات الفلكية لا تجعل حجة شرعية بذاتها ما لم تورث اليقين والاطمئنان الحسي. نعم، إذا أجمع الفلكيون الموثوقون على عدم إمكانية رؤية الهلال إطلاقاً في أفق معين فإن ذلك يمنع عادة من حصول الاطمئنان بدعوى الرؤية الفردية المشكوك فيها.",
            source = "الاستفتاءات الفقهية لسماحة السيد السيستاني - باب الهلال"
        ),
        HilalFiqhRule(
            id = "rule_4",
            title = "طرق ثبوت الهلال الشرعية",
            questionOrTopic = "بماذا يثبت هلال شهر رمضان وهلال شوال؟",
            ruling = "يثبت الهلال بأحد أمور أربعة:\n١- أن يراه المكلف بنفسه بالعين المجردة.\n٢- أن يتواتر أو يشيع الخبر برؤيته بين الناس بما يوجب العلم أو الاطمئنان.\n٣- مضي ثلاثين يوماً من أول الشهر السابق (إتمام العدة).\n٤- شهادة رجلين عادلين برؤيته بالعين المجردة مع عدم وجود معارض أو ريبة تكذب شهادتهما كأن يستهل جمع غفير فلا يراه أحد غيرهما مع صفاء الجو.",
            source = "منهاج الصالحين - ج1، أحكام ثبوت الهلال، م 1044"
        ),
        HilalFiqhRule(
            id = "rule_5",
            title = "حكم تطويق الهلال وبقاؤه بعد الشفق",
            questionOrTopic = "هل يدل تطويق الهلال بالنور أو كبر حجمه أو تأخره في الغروب على أنه لليلتين؟",
            ruling = "لا يثبت كون الهلال لليلتين بتطويقه (إحاطة النور بكامل قرصه بخيط دقيق)، ولا بكونه مرتفعاً أو غيابه بعد الشفق بمدة طويلة. فلا يترتب على ذلك أثر شرعي ولا يُقضى يوم بناءً على ذلك.",
            source = "منهاج الصالحين - ج1، م 1047"
        )
    )
}
