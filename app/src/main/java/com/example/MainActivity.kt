package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset

fun Modifier.geometricGridBackground(color: Color = Color(0xFF6750A4)): Modifier = this.drawBehind {
    val dotRadius = 1.2f.dp.toPx()
    val gap = 24.dp.toPx()
    val cols = (size.width / gap).toInt() + 1
    val rows = (size.height / gap).toInt() + 1
    for (c in 0..cols) {
        for (r in 0..rows) {
            drawCircle(
                color = color.copy(alpha = 0.05f),
                radius = dotRadius,
                center = Offset(c * gap, r * gap)
            )
        }
    }
}

// --- Localization ---
enum class AppLanguage { AR, EN }

fun getTranslated(key: String, language: AppLanguage): String {
    return when (language) {
        AppLanguage.AR -> when (key) {
            "app_title" -> "حبر سري"
            "app_subtitle" -> "لعبة الغموض والتجسس الجماعية"
            "btn_start" -> "ابدأ اللعبة"
            "btn_how_to" -> "كيف تلعب؟"
            "btn_ai_rounds" -> "جولات الذكاء الاصطناعي ✦"
            "setup_title" -> "إعداد اللعبة"
            "lbl_lang" -> "اللغة / Language"
            "lbl_players" -> "عدد اللاعبين"
            "lbl_hackers" -> "عدد الهاكرز (العملاء)"
            "lbl_timer" -> "مؤقت الجولة"
            "lbl_timer_minutes" -> "دقائق"
            "lbl_no_timer" -> "بدون مؤقت"
            "lbl_mode" -> "مصدر الكلمات"
            "mode_random" -> "عشوائي من الـ 15 جولة"
            "mode_select" -> "اختر فئة محددة"
            "mode_ai" -> "توليد فئة بالذكاء الاصطناعي ✦"
            "ai_prompt_hint" -> "مثال: مسلسلات رمضان، حضارات قديمة، أفلام عربية..."
            "ai_prompt_lbl" -> "موضوع الجولة المخصص (اختياري)"
            "btn_generate_ai" -> "توليد جولة ذكية"
            "ai_loading" -> "جاري صبّ الحبر السري بالذكاء الاصطناعي..."
            "ai_success" -> "تم توليد الجولة بنجاح!"
            "ai_key_warning" -> "تنبيه: مفتاح الذكاء الاصطناعي غير مفعل. سنستخدم فئة جاهزة ممتازة."
            "lbl_player_names" -> "أسماء اللاعبين"
            "btn_launch" -> "دخول الغرفة وبدء اللعبة"
            "pass_title" -> "توزيع الأدوار السري"
            "pass_desc" -> "مرر الهاتف إلى اللاعب المطلوب واضغط للرؤية"
            "pass_instructions" -> "تأكد من عدم وجود أحد بجانبك يرى الشاشة!"
            "btn_reveal" -> "اضغط للكشف عن السر 🔓"
            "role_citizen" -> "أنت مواطن صالح! 👥"
            "role_hacker" -> "أنت الهاكر المندّس! 🕵️‍♂️"
            "desc_citizen" -> "أنت تعرف الكلمة السرية. قم بوصفها للاعبين بذكاء دون أن يفهمها الهاكر."
            "desc_hacker" -> "أنت لا تعرف الكلمة السرية! تظاهر بالمعرفة وحاول استنتاجها من تلميحات الآخرين لتفوز."
            "lbl_general_category" -> "الفئة العامة:"
            "lbl_secret_word" -> "الكلمة السرية:"
            "lbl_hidden_word" -> "⚠️ مخفية عنك!"
            "btn_done" -> "فهمت، أغلِق الشاشة ومرر الهاتف 👥"
            "disc_title" -> "النقاش والتحقيق 🔎"
            "disc_desc" -> "ابدؤوا بالنقاش والأسئلة! على كل لاعب إعطاء تلميح واحد للكلمة السرية."
            "btn_pause" -> "إيقاف مؤقت"
            "btn_resume" -> "تشغيل"
            "btn_vote_now" -> "انتقال للتصويت النهائي 🗳️"
            "vote_title" -> "من هو الهاكر المشبوه؟"
            "vote_desc" -> "اتفقوا معاً على لاعب واحد لطرده واضغطوا على اسمه لمعرفة هويته."
            "result_citizen_voted" -> "يا للأسف! لقد طردتم مواطناً صالحاً!"
            "result_hacker_voted" -> "عمل رائع! لقد كشفتم الهاكر بنجاح!"
            "result_guess_desc" -> "لكن لم ينتهِ الأمر! لدى الهاكر فرصة أخيرة لتخمين الكلمة السرية وسرقة الفوز!"
            "btn_guess" -> "تخمين الكلمة السرية"
            "guess_title" -> "تخمين الهاكر الأخير"
            "guess_desc" -> "أيها الهاكر، اختر الكلمة التي تعتقد أنها الكلمة السرية الصحيحة:"
            "game_over_title" -> "نهاية اللعبة"
            "winner_citizens" -> "انتصار ساحق للمواطنين! 🎉"
            "winner_hacker" -> "انتصار ساحق للهاكر السري! 🏆"
            "winner_citizens_desc" -> "لقد كشفتم الهاكر وفشل في تخمين الكلمة السرية."
            "winner_hacker_desc" -> "لقد انتصر الهاكر إما بطرد مواطن صالح أو بتخمين الكلمة السرية بشكل صحيح!"
            "btn_play_again" -> "العب مجدداً 🔄"
            "rule_1" -> "1. اللعبة تتطلب من 3 إلى 10 لاعبين يلعبون بهاتف واحد."
            "rule_2" -> "2. يحصل كل مواطن على الكلمة السرية والفئة العامة، بينما يحصل الهاكر على الفئة العامة فقط."
            "rule_3" -> "3. يتحدث اللاعبون بالتناوب عن الكلمة بعبارات غامضة (مثل: شيء نستخدمه يومياً)."
            "rule_4" -> "4. بعد انتهاء الوقت، يصوت الجميع لطرد المشتبه به. إذا طُرد مواطن، يفوز الهاكر فوراً."
            "rule_5" -> "5. إذا طُرد الهاكر، يحصل على فرصة لتخمين الكلمة السرية من بين 4 خيارات. إذا أصابها، يفوز الهاكر!"
            "rules_close" -> "إغلاق وقبول التحدي"
            "rules_header" -> "قوانين لعبة حبر سري"
            "p_default_name" -> "لاعب"
            "err_players_count" -> "يجب أن يكون عدد اللاعبين أكبر من الهاكرز!"
            "status_turn" -> "اللاعب الحالي"
            "btn_back" -> "رجوع"
            else -> key
        }
        AppLanguage.EN -> when (key) {
            "app_title" -> "Secret Ink"
            "app_subtitle" -> "The Ultimate Local Party Mystery Game"
            "btn_start" -> "Start Game"
            "btn_how_to" -> "How to Play?"
            "btn_ai_rounds" -> "AI Custom Rounds ✦"
            "setup_title" -> "Game Setup"
            "lbl_lang" -> "Language / اللغة"
            "lbl_players" -> "Number of Players"
            "lbl_hackers" -> "Number of Hackers"
            "lbl_timer" -> "Round Timer"
            "lbl_timer_minutes" -> "min"
            "lbl_no_timer" -> "No Timer"
            "lbl_mode" -> "Word Source"
            "mode_random" -> "Random from 15 preloaded"
            "mode_select" -> "Select Specific Category"
            "mode_ai" -> "Generate with Gemini AI ✦"
            "ai_prompt_hint" -> "e.g., Arabic Movies, Space Travels, Fast Food..."
            "ai_prompt_lbl" -> "Custom Round Theme (Optional)"
            "btn_generate_ai" -> "Generate Smart Round"
            "ai_loading" -> "Pouring Secret Ink with AI..."
            "ai_success" -> "Round generated successfully!"
            "ai_key_warning" -> "Warning: AI Key is not active. Using a highly fun preloaded category."
            "lbl_player_names" -> "Player Names"
            "btn_launch" -> "Start Room & Roll Roles"
            "pass_title" -> "Secret Role Reveal"
            "pass_desc" -> "Pass phone to the player, then tap to reveal"
            "pass_instructions" -> "Make sure nobody is peeking over your shoulder!"
            "btn_reveal" -> "Tap to Reveal Secret 🔓"
            "role_citizen" -> "You are a Citizen! 👥"
            "role_hacker" -> "You are the Hacker! 🕵️‍♂️"
            "desc_citizen" -> "You know the secret word. Hint at it smartly without giving it away to the Hacker."
            "desc_hacker" -> "You do not know the secret word! Pretend you do, and deduce it from others' hints to win."
            "lbl_general_category" -> "General Category:"
            "lbl_secret_word" -> "Secret Word:"
            "lbl_hidden_word" -> "⚠️ Hidden from you!"
            "btn_done" -> "Got it, Close & Pass Phone 👥"
            "disc_title" -> "Investigation & Talk 🔎"
            "disc_desc" -> "Start debating! Every player must say exactly one hint about their card."
            "btn_pause" -> "Pause"
            "btn_resume" -> "Resume"
            "btn_vote_now" -> "Go to Voting 🗳️"
            "vote_title" -> "Who is the Imposter Hacker?"
            "vote_desc" -> "Deconstruct clues and choose one player to vote out."
            "result_citizen_voted" -> "Oh no! You voted out an innocent Citizen!"
            "result_hacker_voted" -> "Great job! You exposed the Hacker!"
            "result_guess_desc" -> "But wait! The Hacker has one last chance to steal victory!"
            "btn_guess" -> "Guess the Secret Word"
            "guess_title" -> "Hacker's Final Guess"
            "guess_desc" -> "Hacker, choose the correct secret word from the list below:"
            "game_over_title" -> "Game Over"
            "winner_citizens" -> "Citizens Won! 🎉"
            "winner_hacker" -> "Hacker Won! 🏆"
            "winner_citizens_desc" -> "You successfully kicked the Hacker, and they failed their final guess."
            "winner_hacker_desc" -> "The Hacker won either by fooling you to vote out a Citizen or guessing the word!"
            "btn_play_again" -> "Play Again 🔄"
            "rule_1" -> "1. The game requires 3 to 10 players on a single pass-and-play phone."
            "rule_2" -> "2. Citizens get the secret word and category, while Hackers only get the category."
            "rule_3" -> "3. Players describe the word sequentially with ambiguous terms."
            "rule_4" -> "4. After the timer, vote to expel the suspect. Kicking an innocent Citizen grants an immediate win to the Hacker."
            "rule_5" -> "5. Kicking the Hacker triggers their final guess. A correct guess steals the win!"
            "rules_close" -> "Close & Accept Challenge"
            "rules_header" -> "Rules of Secret Ink"
            "p_default_name" -> "Player"
            "err_players_count" -> "Players count must be greater than Hackers!"
            "status_turn" -> "Current Player"
            "btn_back" -> "Back"
            else -> key
        }
    }
}

// --- Game State Models ---
enum class GameStage {
    Title,
    HowToPlay,
    Setup,
    PassAndPlay,
    Discuss,
    Voting,
    HackerGuessing,
    GameOver
}

data class PlayerState(
    val id: Int,
    var name: String,
    var isHacker: Boolean = false,
    var isVotedOut: Boolean = false
)

// --- ViewModel ---
class SecretInkViewModel : ViewModel() {
    // Basic settings
    var appLanguage by mutableStateOf(AppLanguage.AR)
    var playersCount by mutableIntStateOf(4)
    var hackersCount by mutableIntStateOf(1)
    var timerMinutes by mutableIntStateOf(3) // 0 means no timer
    var selectedCategoryIndex by mutableIntStateOf(0) // Index in preloaded, or -1 for random, -2 for AI
    var aiCustomPrompt by mutableStateOf("")

    // Game states
    var currentStage by mutableStateOf(GameStage.Title)
    var players = mutableStateListOf<PlayerState>()
    var currentRevealingPlayerIndex by mutableIntStateOf(0)
    var isRevealed by mutableStateOf(false)

    // Current Round Info
    var activeRound by mutableStateOf<GameRound?>(null)

    // Timer state
    var timerSecondsLeft by mutableIntStateOf(180)
    var isTimerRunning by mutableStateOf(false)

    // Voting and Reveal info
    var votedPlayer: PlayerState? by mutableStateOf(null)
    var hackerGuessedCorrectly: Boolean? by mutableStateOf(null)
    var finalWinningGroup: String by mutableStateOf("") // "citizens" or "hacker"

    // AI Generation Status
    var aiLoading by mutableStateOf(false)
    var aiErrorMessage by mutableStateOf("")

    init {
        resetPlayersList()
    }

    fun resetPlayersList() {
        players.clear()
        for (i in 1..playersCount) {
            val name = "${getTranslated("p_default_name", appLanguage)} $i"
            players.add(PlayerState(id = i, name = name))
        }
    }

    fun updatePlayersCount(newCount: Int) {
        if (newCount in 3..10) {
            playersCount = newCount
            resetPlayersList()
        }
    }

    fun selectRandomRound() {
        val randomIndex = Random.nextInt(GameData.PreloadedRounds.size)
        activeRound = GameData.PreloadedRounds[randomIndex]
    }

    fun startNewGame(customRound: GameRound? = null) {
        // Validation
        if (playersCount <= hackersCount) {
            return
        }

        // Setup the round
        if (customRound != null) {
            activeRound = customRound
        } else {
            if (selectedCategoryIndex == -1) {
                selectRandomRound()
            } else {
                activeRound = GameData.PreloadedRounds.getOrNull(selectedCategoryIndex) ?: GameData.PreloadedRounds[0]
            }
        }

        // Re-initialize players and assign Hackers
        resetPlayersList()
        players.forEach {
            it.isHacker = false
            it.isVotedOut = false
        }

        // Randomly choose hackers
        val indices = (0 until playersCount).shuffled().take(hackersCount)
        indices.forEach { index ->
            players[index].isHacker = true
        }

        currentRevealingPlayerIndex = 0
        isRevealed = false
        timerSecondsLeft = timerMinutes * 60
        isTimerRunning = false
        votedPlayer = null
        hackerGuessedCorrectly = null
        finalWinningGroup = ""

        currentStage = GameStage.PassAndPlay
    }

    fun triggerTimer(coroutineScope: kotlinx.coroutines.CoroutineScope) {
        if (isTimerRunning) return
        isTimerRunning = true
        coroutineScope.launch {
            while (isTimerRunning && timerSecondsLeft > 0) {
                delay(1000)
                if (isTimerRunning) {
                    timerSecondsLeft--
                }
            }
            if (timerSecondsLeft == 0) {
                isTimerRunning = false
            }
        }
    }

    fun handleVotePlayer(player: PlayerState) {
        votedPlayer = player
        player.isVotedOut = true
        isTimerRunning = false

        if (player.isHacker) {
            // Hacker is caught! Now Hacker gets a guessing screen
            currentStage = GameStage.HackerGuessing
        } else {
            // Citizens kicked an innocent citizen! Hacker wins immediately!
            finalWinningGroup = "hacker"
            currentStage = GameStage.GameOver
        }
    }

    fun submitHackerGuess(guessedWord: String) {
        val correctWord = if (appLanguage == AppLanguage.AR) activeRound?.secretWordAr else activeRound?.secretWordEn
        val correct = guessedWord.trim().equals(correctWord?.trim(), ignoreCase = true)
        hackerGuessedCorrectly = correct
        finalWinningGroup = if (correct) "hacker" else "citizens"
        currentStage = GameStage.GameOver
    }

    fun generateWithAI(coroutineScope: kotlinx.coroutines.CoroutineScope, apiKey: String, onComplete: (Boolean) -> Unit) {
        aiLoading = true
        aiErrorMessage = ""
        coroutineScope.launch {
            try {
                val round = GameData.generateWithGemini(aiCustomPrompt, apiKey)
                aiLoading = false
                startNewGame(round)
                onComplete(true)
            } catch (e: Exception) {
                aiLoading = false
                aiErrorMessage = e.message ?: "Failed to generate AI round"
                // Fallback to random preloaded round immediately to avoid blocking the user
                selectRandomRound()
                startNewGame(activeRound)
                onComplete(false)
            }
        }
    }
}

// --- Main UI Activity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GeoBg)
                ) { innerPadding ->
                    val viewModel: SecretInkViewModel = viewModel()
                    val layoutDirection = if (viewModel.appLanguage == AppLanguage.AR) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    }

                    // Direct system-level RTL override based on game state
                    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(GeoBg)
                                .geometricGridBackground()
                        ) {
                            when (viewModel.currentStage) {
                                GameStage.Title -> TitleScreen(viewModel)
                                GameStage.HowToPlay -> HowToPlayScreen(viewModel)
                                GameStage.Setup -> SetupScreen(viewModel)
                                GameStage.PassAndPlay -> PassAndPlayScreen(viewModel)
                                GameStage.Discuss -> DiscussScreen(viewModel)
                                GameStage.Voting -> VotingScreen(viewModel)
                                GameStage.HackerGuessing -> HackerGuessingScreen(viewModel)
                                GameStage.GameOver -> GameOverScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 1. Title Screen ---
@Composable
fun TitleScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Language Toggle Badge in corner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoPillBg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier
                    .testTag("lang_toggle")
                    .clickable {
                        viewModel.appLanguage = if (lang == AppLanguage.AR) AppLanguage.EN else AppLanguage.AR
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Language",
                        tint = GeoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "English" else "العربية",
                        color = GeoTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // High-fidelity custom game logo icon
        Card(
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .size(150.dp)
                .background(Color.Transparent),
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.img_app_icon_1782487548110),
                contentDescription = "Secret Ink Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = getTranslated("app_title", lang),
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = GeoPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = getTranslated("app_subtitle", lang),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = GeoTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1.2f))

        Button(
            onClick = {
                viewModel.resetPlayersList()
                viewModel.currentStage = GameStage.Setup
            },
            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_game_button"),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = getTranslated("btn_start", lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GeoWhite
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { viewModel.currentStage = GameStage.HowToPlay },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPrimary),
            border = BorderStroke(1.5.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("how_to_play_button"),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = getTranslated("btn_how_to", lang),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- 2. How To Play Screen ---
@Composable
fun HowToPlayScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.currentStage = GameStage.Title },
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GeoPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = getTranslated("btn_how_to", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GeoTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = getTranslated("rules_header", lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                val rules = listOf("rule_1", "rule_2", "rule_3", "rule_4", "rule_5")
                items(rules) { ruleKey ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GeoPillBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = getTranslated(ruleKey, lang),
                            fontSize = 15.sp,
                            color = GeoTextPrimary,
                            modifier = Modifier.padding(14.dp),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.currentStage = GameStage.Title },
            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = getTranslated("rules_close", lang),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GeoWhite
            )
        }
    }
}

// --- 3. Setup Screen ---
@Composable
fun SetupScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.currentStage = GameStage.Title },
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GeoPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = getTranslated("setup_title", lang),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoTextPrimary
                )
            }
        }

        // Language settings inside Setup too for quick changes
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTranslated("lbl_lang", lang),
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isAr = viewModel.appLanguage == AppLanguage.AR
                        Button(
                            onClick = { viewModel.appLanguage = AppLanguage.AR },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAr) GeoPrimary else GeoPillBg,
                                contentColor = if (isAr) GeoWhite else GeoTextSecondary
                            ),
                            border = if (!isAr) BorderStroke(1.dp, GeoBorder) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("العربية")
                        }
                        val isEn = viewModel.appLanguage == AppLanguage.EN
                        Button(
                            onClick = { viewModel.appLanguage = AppLanguage.EN },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEn) GeoPrimary else GeoPillBg,
                                contentColor = if (isEn) GeoWhite else GeoTextSecondary
                            ),
                            border = if (!isEn) BorderStroke(1.dp, GeoBorder) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("English")
                        }
                    }
                }
            }
        }

        // Players Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTranslated("lbl_players", lang),
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val canDec = viewModel.playersCount > 3
                        IconButton(
                            onClick = { viewModel.updatePlayersCount(viewModel.playersCount - 1) },
                            enabled = canDec
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Decrease",
                                tint = if (canDec) GeoPrimary else GeoBorder
                            )
                        }
                        Text(
                            text = "${viewModel.playersCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = GeoPrimary
                        )
                        val canInc = viewModel.playersCount < 10
                        IconButton(
                            onClick = { viewModel.updatePlayersCount(viewModel.playersCount + 1) },
                            enabled = canInc
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Increase",
                                tint = if (canInc) GeoPrimary else GeoBorder
                            )
                        }
                    }
                }
            }
        }

        // Hackers Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTranslated("lbl_hackers", lang),
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val canDec = viewModel.hackersCount > 1
                        IconButton(
                            onClick = {
                                if (canDec) {
                                    viewModel.hackersCount--
                                }
                            },
                            enabled = canDec
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Decrease",
                                tint = if (canDec) GeoPrimary else GeoBorder
                            )
                        }
                        Text(
                            text = "${viewModel.hackersCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = GeoRed
                        )
                        val canInc = viewModel.hackersCount < 2 && viewModel.hackersCount < viewModel.playersCount - 2
                        IconButton(
                            onClick = {
                                if (canInc) {
                                    viewModel.hackersCount++
                                }
                            },
                            enabled = canInc
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Increase",
                                tint = if (canInc) GeoPrimary else GeoBorder
                            )
                        }
                    }
                }
            }
        }

        // Round timer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTranslated("lbl_timer", lang),
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val times = listOf(0, 1, 2, 3, 5)
                        times.forEach { t ->
                            val isSel = viewModel.timerMinutes == t
                            val txt = if (t == 0) getTranslated("lbl_no_timer", lang) else "$t ${getTranslated("lbl_timer_minutes", lang)}"
                            Button(
                                onClick = { viewModel.timerMinutes = t },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSel) GeoPrimary else GeoPillBg,
                                    contentColor = if (isSel) GeoWhite else GeoTextSecondary
                                ),
                                border = if (!isSel) BorderStroke(1.dp, GeoBorder) else null,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = txt,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) DarkInkBg else PureWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Source Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTranslated("lbl_mode", lang),
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mode tabs
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val isRand = viewModel.selectedCategoryIndex == -1
                            Button(
                                onClick = { viewModel.selectedCategoryIndex = -1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRand) GeoPrimary else GeoPillBg,
                                    contentColor = if (isRand) GeoWhite else GeoTextSecondary
                                ),
                                border = if (!isRand) BorderStroke(1.dp, GeoBorder) else null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = getTranslated("mode_random", lang),
                                    fontSize = 12.sp
                                )
                            }

                            val isSelTab = viewModel.selectedCategoryIndex >= 0
                            Button(
                                onClick = { viewModel.selectedCategoryIndex = 0 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelTab) GeoPrimary else GeoPillBg,
                                    contentColor = if (isSelTab) GeoWhite else GeoTextSecondary
                                ),
                                border = if (!isSelTab) BorderStroke(1.dp, GeoBorder) else null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = getTranslated("mode_select", lang),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        val isAiMode = viewModel.selectedCategoryIndex == -2
                        Button(
                            onClick = { viewModel.selectedCategoryIndex = -2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAiMode) GeoPrimary else GeoPillBg,
                                contentColor = if (isAiMode) GeoWhite else GeoTextSecondary
                            ),
                            border = if (!isAiMode) BorderStroke(1.dp, GeoBorder) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = getTranslated("mode_ai", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Dynamic UI depending on source
                    if (viewModel.selectedCategoryIndex >= 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Category Selector Dropdown list
                        var expanded by remember { mutableStateOf(false) }
                        val currentCategory = GameData.PreloadedRounds.getOrNull(viewModel.selectedCategoryIndex)
                        val currentCategoryName = if (lang == AppLanguage.AR) currentCategory?.categoryAr else currentCategory?.categoryEn

                        Box {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.5.dp, GeoBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPrimary)
                            ) {
                                Text(currentCategoryName ?: "", color = GeoTextPrimary)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(GeoWhite)
                            ) {
                                GameData.PreloadedRounds.forEachIndexed { idx, round ->
                                    val name = if (lang == AppLanguage.AR) round.categoryAr else round.categoryEn
                                    DropdownMenuItem(
                                        text = { Text(name, color = GeoTextPrimary) },
                                        onClick = {
                                            viewModel.selectedCategoryIndex = idx
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else if (viewModel.selectedCategoryIndex == -2) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // AI input details
                        Text(
                            text = getTranslated("ai_prompt_lbl", lang),
                            fontSize = 12.sp,
                            color = GeoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewModel.aiCustomPrompt,
                            onValueChange = { viewModel.aiCustomPrompt = it },
                            placeholder = { Text(getTranslated("ai_prompt_hint", lang), color = Color.Gray, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorder,
                                focusedTextColor = GeoTextPrimary,
                                unfocusedTextColor = GeoTextPrimary
                             )
                        )
                    }
                }
            }
        }

        // Player Names Editor
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTranslated("lbl_player_names", lang),
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    viewModel.players.forEachIndexed { index, pState ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Badge(
                                containerColor = if (index < viewModel.hackersCount) GeoRed else GeoPrimary,
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = pState.name,
                                onValueChange = { newValue ->
                                    pState.name = newValue
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = GeoTextPrimary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GeoPrimary,
                                    unfocusedBorderColor = GeoBorder,
                                    focusedTextColor = GeoTextPrimary,
                                    unfocusedTextColor = GeoTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // LAUNCH GAME ACTION
        item {
            if (viewModel.aiLoading) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GeoWhite),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, GeoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = GeoPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getTranslated("ai_loading", lang),
                            color = GeoTextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        if (viewModel.playersCount <= viewModel.hackersCount) {
                            Toast.makeText(context, getTranslated("err_players_count", lang), Toast.LENGTH_LONG).show()
                        } else {
                            if (viewModel.selectedCategoryIndex == -2) {
                                val key = BuildConfig.GEMINI_API_KEY
                                if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
                                    Toast.makeText(context, getTranslated("ai_key_warning", lang), Toast.LENGTH_LONG).show()
                                    viewModel.selectedCategoryIndex = -1 // fallback to preloaded
                                    viewModel.startNewGame()
                                } else {
                                    viewModel.generateWithAI(coroutineScope, key) { success ->
                                        if (success) {
                                            Toast.makeText(context, getTranslated("ai_success", lang), Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "AI Error: ${viewModel.aiErrorMessage}. Loaded preloaded words.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                viewModel.startNewGame()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("launch_game_button"),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = getTranslated("btn_launch", lang),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoWhite
                    )
                }
            }
        }
    }
}

// --- 4. Pass & Play Role Reveal Screen ---
@Composable
fun PassAndPlayScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage
    val currentPlayer = viewModel.players.getOrNull(viewModel.currentRevealingPlayerIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(GeoPrimaryContainer, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${viewModel.currentRevealingPlayerIndex + 1} / ${viewModel.playersCount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoOnPrimaryContainer
                )
            }
            Text(
                text = getTranslated("pass_title", lang),
                fontSize = 16.sp,
                color = GeoTextPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        // Middle Reveal Box
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            if (!viewModel.isRevealed) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Lock visual
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = GeoPrimary,
                        modifier = Modifier.size(90.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${getTranslated("status_turn", lang)}:",
                        fontSize = 14.sp,
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = currentPlayer.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = GeoTextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = getTranslated("pass_desc", lang),
                        fontSize = 14.sp,
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = getTranslated("pass_instructions", lang),
                        fontSize = 12.sp,
                        color = GeoRed,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.isRevealed = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("reveal_secret_button"),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text(
                            text = getTranslated("btn_reveal", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoWhite
                        )
                    }
                }
            } else {
                // Revealed State
                val isHacker = currentPlayer.isHacker
                val roleTitle = getTranslated(if (isHacker) "role_hacker" else "role_citizen", lang)
                val roleDesc = getTranslated(if (isHacker) "desc_hacker" else "desc_citizen", lang)

                val categoryText = if (lang == AppLanguage.AR) viewModel.activeRound?.categoryAr else viewModel.activeRound?.categoryEn
                val secretWordText = if (lang == AppLanguage.AR) viewModel.activeRound?.secretWordAr else viewModel.activeRound?.secretWordEn

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top half: Category section
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(GeoPrimaryContainer)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (lang == AppLanguage.AR) "الفئة / Category" else "Category / الفئة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextSecondary,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = categoryText ?: "",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoOnPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Dashed line divider
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                        val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                        drawLine(
                            color = GeoBorder,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = pathEffect,
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Bottom half: Secret Word section
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(GeoWhite)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(4.dp)
                                .background(GeoPrimary.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (lang == AppLanguage.AR) "الكلمة السرية / Secret Word" else "Secret Word / الكلمة السرية",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextSecondary,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHacker) getTranslated("lbl_hidden_word", lang) else (secretWordText ?: ""),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isHacker) GeoRed else GeoPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Role Identity Pill in the box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(GeoPillBg, RoundedCornerShape(20.dp))
                                .border(1.dp, GeoBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Badge(
                                containerColor = if (isHacker) GeoRed else GeoGreen,
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = roleTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Bottom pass actions
        if (viewModel.isRevealed) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.isRevealed = false
                        if (viewModel.currentRevealingPlayerIndex < viewModel.playersCount - 1) {
                            viewModel.currentRevealingPlayerIndex++
                        } else {
                            // Transition to discussion
                            viewModel.currentStage = GameStage.Discuss
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("done_pass_button"),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = getTranslated("btn_done", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoWhite
                    )
                }
                Text(
                    text = if (lang == AppLanguage.AR)
                        "حافظ على سرية الشاشة! اضغط على الزر ومرر الهاتف للاعب التالي."
                    else
                        "Keep the screen hidden! Click the button and pass the phone to the next player.",
                    fontSize = 11.sp,
                    color = GeoTextSecondary,
                    textAlign = TextAlign.Center,
                    style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

// --- 5. Discussion Screen (With live timer) ---
@Composable
fun DiscussScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage
    val coroutineScope = rememberCoroutineScope()

    // Start timer automatically on entry
    LaunchedEffect(Unit) {
        if (viewModel.timerMinutes > 0) {
            viewModel.triggerTimer(coroutineScope)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = getTranslated("disc_title", lang),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GeoTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Timer visual
        if (viewModel.timerMinutes > 0) {
            val minutes = viewModel.timerSecondsLeft / 60
            val seconds = viewModel.timerSecondsLeft % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .border(4.dp, if (viewModel.timerSecondsLeft < 30) GeoRed else GeoPrimary, CircleShape)
                    .background(GeoWhite, CircleShape)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (viewModel.timerSecondsLeft < 30) GeoRed else GeoPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (viewModel.isTimerRunning) "LIVE" else "PAUSED",
                        fontSize = 12.sp,
                        color = if (viewModel.isTimerRunning) GeoGreen else GeoTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer controls
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        viewModel.isTimerRunning = !viewModel.isTimerRunning
                        if (viewModel.isTimerRunning) {
                            viewModel.triggerTimer(coroutineScope)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (viewModel.isTimerRunning) getTranslated("btn_pause", lang) else getTranslated("btn_resume", lang),
                        color = GeoWhite
                    )
                }
            }
        } else {
            // Infinity no timer visual
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Infinity",
                tint = GeoPrimary,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = getTranslated("disc_desc", lang),
                    fontSize = 14.sp,
                    color = GeoTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active category reminder to help people talk
                Card(
                    colors = CardDefaults.cardColors(containerColor = GeoPrimaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = getTranslated("lbl_general_category", lang), color = GeoPrimary, fontWeight = FontWeight.Bold)
                        val cat = if (lang == AppLanguage.AR) viewModel.activeRound?.categoryAr else viewModel.activeRound?.categoryEn
                        Text(text = cat ?: "", color = GeoOnPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Players List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(viewModel.players) { p ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GeoPillBg),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = p.name, color = GeoTextPrimary, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Badge(containerColor = GeoGreen, modifier = Modifier.size(8.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "ACTIVE", color = GeoTextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.currentStage = GameStage.Voting
            },
            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("go_to_voting_button"),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = getTranslated("btn_vote_now", lang),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GeoWhite
            )
        }
    }
}

// --- 6. Voting Screen ---
@Composable
fun VotingScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = getTranslated("vote_title", lang),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GeoTextPrimary
        )

        Text(
            text = getTranslated("vote_desc", lang),
            fontSize = 14.sp,
            color = GeoTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid/List of players to expel
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(viewModel.players) { p ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GeoPillBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.handleVotePlayer(p)
                            }
                            .testTag("vote_player_${p.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Suspect",
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = p.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoTextPrimary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Vote out",
                                tint = GeoPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 7. Hacker Guessing Screen (Decoy selection) ---
@Composable
fun HackerGuessingScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage
    val activeR = viewModel.activeRound ?: return

    val correctWord = if (lang == AppLanguage.AR) activeR.secretWordAr else activeR.secretWordEn
    val decoys = if (lang == AppLanguage.AR) activeR.decoysAr else activeR.decoysEn

    // Mix correct word with decoys for MCQ options
    val mcqOptions = remember {
        (decoys + correctWord).shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = getTranslated("result_hacker_voted", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GeoGreen,
                textAlign = TextAlign.Center
            )

            Text(
                text = getTranslated("result_guess_desc", lang),
                fontSize = 14.sp,
                color = GeoTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getTranslated("guess_desc", lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Render MCQ items
                mcqOptions.forEach { opt ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GeoPillBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                viewModel.submitHackerGuess(opt)
                            }
                            .testTag("guess_option_$opt")
                    ) {
                        Text(
                            text = opt,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- 8. Game Over Screen ---
@Composable
fun GameOverScreen(viewModel: SecretInkViewModel) {
    val lang = viewModel.appLanguage
    val activeR = viewModel.activeRound
    val winGroup = viewModel.finalWinningGroup

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = getTranslated("game_over_title", lang),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GeoTextPrimary
        )

        // Winner card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (winGroup == "citizens") GeoGreen.copy(alpha = 0.1f) else GeoRed.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            border = BorderStroke(1.5.dp, if (winGroup == "citizens") GeoGreen else GeoRed)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (winGroup == "citizens") Icons.Default.Star else Icons.Default.Warning,
                    contentDescription = "Win Icon",
                    tint = if (winGroup == "citizens") GeoGreen else GeoRed,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (winGroup == "citizens") getTranslated("winner_citizens", lang) else getTranslated("winner_hacker", lang),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = if (winGroup == "citizens") GeoGreen else GeoRed,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (winGroup == "citizens") getTranslated("winner_citizens_desc", lang) else getTranslated("winner_hacker_desc", lang),
                    fontSize = 14.sp,
                    color = GeoTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Stats card details
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoWhite),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secret Word info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = getTranslated("lbl_secret_word", lang), color = GeoPrimary, fontWeight = FontWeight.Bold)
                    val sw = if (lang == AppLanguage.AR) activeR?.secretWordAr else activeR?.secretWordEn
                    Text(text = sw ?: "", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = getTranslated("lbl_general_category", lang), color = GeoPrimary, fontWeight = FontWeight.Bold)
                    val cat = if (lang == AppLanguage.AR) activeR?.categoryAr else activeR?.categoryEn
                    Text(text = cat ?: "", color = GeoTextPrimary, fontWeight = FontWeight.SemiBold)
                }

                HorizontalDivider(color = GeoBorder)

                // List of Hackers
                Text(text = getTranslated("lbl_hackers", lang) + ":", color = GeoPrimary, fontWeight = FontWeight.Bold)
                viewModel.players.forEach { p ->
                    if (p.isHacker) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GeoPillBg),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = p.name, color = GeoTextPrimary, fontWeight = FontWeight.Bold)
                                Text(text = "HACKER", color = GeoRed, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.currentStage = GameStage.Setup
            },
            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = GeoWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("play_again_button"),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = getTranslated("btn_play_again", lang),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GeoWhite
            )
        }
    }
}
