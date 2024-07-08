package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.view.utils.GeminiApi
import com.example.madcamp2_frontend.R
import kotlinx.coroutines.*
import java.io.InputStream

class LoadingActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var bitmapFileUriString: String
    private lateinit var drawingBitmap: Bitmap
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val geminiApi = GeminiApi()
    private var remainingMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        progressBar = findViewById(R.id.loadingProgressBar)

        // Get data from intent
        bitmapFileUriString = intent.getStringExtra("bitmapFileUri") ?: ""
        remainingMilliSeconds = intent.getLongExtra("remainingMilliSeconds", 0)
        val bitmapFileUri = Uri.parse(bitmapFileUriString)
        val inputStream: InputStream? = contentResolver.openInputStream(bitmapFileUri)
        drawingBitmap = BitmapFactory.decodeStream(inputStream)

        // Start processing the image
        processImage()
    }

    private fun processImage() {
        coroutineScope.launch {
            val predictions = mutableListOf<Pair<String, Float>>()
            geminiApi.generateContent("Which of the 345 objects of 'quick, draw' dataset does this image look like? Give exactly four responses in the format of \"사과(apple)\uD83C\uDF4E:0.9(matching percentage from 0 to 1)\", one response for each line. Even if you cannot recognize, give any predictions from the dataset. Word list: [\n" +
                    "    \"항공모함(aircraft carrier)\",\n" +
                    "    \"비행기(airplane)\",\n" +
                    "    \"알람 시계(alarm clock)\",\n" +
                    "    \"구급차(ambulance)\",\n" +
                    "    \"천사(angel)\",\n" +
                    "    \"동물 이주(animal migration)\",\n" +
                    "    \"개미(ant)\",\n" +
                    "    \"모루(anvil)\",\n" +
                    "    \"사과(apple)\",\n" +
                    "    \"팔(arm)\",\n" +
                    "    \"아스파라거스(asparagus)\",\n" +
                    "    \"도끼(axe)\",\n" +
                    "    \"배낭(backpack)\",\n" +
                    "    \"바나나(banana)\",\n" +
                    "    \"붕대(bandage)\",\n" +
                    "    \"헛간(barn)\",\n" +
                    "    \"야구(야구)\",\n" +
                    "    \"야구 방망이(baseball bat)\",\n" +
                    "    \"바구니(basket)\",\n" +
                    "    \"농구(농구)\",\n" +
                    "    \"박쥐(bat)\",\n" +
                    "    \"욕조(bathtub)\",\n" +
                    "    \"해변(beach)\",\n" +
                    "    \"곰(bear)\",\n" +
                    "    \"수염(beard)\",\n" +
                    "    \"침대(bed)\",\n" +
                    "    \"벌(bee)\",\n" +
                    "    \"벨트(belt)\",\n" +
                    "    \"벤치(bench)\",\n" +
                    "    \"자전거(bicycle)\",\n" +
                    "    \"쌍안경(binoculars)\",\n" +
                    "    \"새(bird)\",\n" +
                    "    \"생일 케이크(birthday cake)\",\n" +
                    "    \"블랙베리(blackberry)\",\n" +
                    "    \"블루베리(blueberry)\",\n" +
                    "    \"책(book)\",\n" +
                    "    \"부메랑(boomerang)\",\n" +
                    "    \"병뚜껑(bottlecap)\",\n" +
                    "    \"나비넥타이(bowtie)\",\n" +
                    "    \"팔찌(bracelet)\",\n" +
                    "    \"뇌(brain)\",\n" +
                    "    \"빵(bread)\",\n" +
                    "    \"다리(bridge)\",\n" +
                    "    \"브로콜리(broccoli)\",\n" +
                    "    \"빗자루(broom)\",\n" +
                    "    \"양동이(bucket)\",\n" +
                    "    \"불도저(bulldozer)\",\n" +
                    "    \"버스(bus)\",\n" +
                    "    \"관목(bush)\",\n" +
                    "    \"나비(butterfly)\",\n" +
                    "    \"선인장(cactus)\",\n" +
                    "    \"케이크(케이크)\",\n" +
                    "    \"계산기(calculator)\",\n" +
                    "    \"달력(calendar)\",\n" +
                    "    \"낙타(camel)\",\n" +
                    "    \"카메라(camera)\",\n" +
                    "    \"위장(camouflage)\",\n" +
                    "    \"캠프파이어(campfire)\",\n" +
                    "    \"양초(촛불)\",\n" +
                    "    \"대포(cannon)\",\n" +
                    "    \"카누(canoe)\",\n" +
                    "    \"자동차(car)\",\n" +
                    "    \"당근(carrot)\",\n" +
                    "    \"성(castle)\",\n" +
                    "    \"고양이(cat)\",\n" +
                    "    \"천장 선풍기(ceiling fan)\",\n" +
                    "    \"첼로(cello)\",\n" +
                    "    \"휴대전화(cell phone)\",\n" +
                    "    \"의자(chair)\",\n" +
                    "    \"샹들리에(chandelier)\",\n" +
                    "    \"교회(church)\",\n" +
                    "    \"원(circle)\",\n" +
                    "    \"클라리넷(clarinet)\",\n" +
                    "    \"시계(clock)\",\n" +
                    "    \"구름(구름)\",\n" +
                    "    \"커피 컵(coffee cup)\",\n" +
                    "    \"나침반(compass)\",\n" +
                    "    \"컴퓨터(computer)\",\n" +
                    "    \"쿠키(cookie)\",\n" +
                    "    \"쿨러(cooler)\",\n" +
                    "    \"소파(couch)\",\n" +
                    "    \"소(cow)\",\n" +
                    "    \"게(crab)\",\n" +
                    "    \"크레용(crayon)\",\n" +
                    "    \"악어(crocodile)\",\n" +
                    "    \"왕관(crown)\",\n" +
                    "    \"유람선(cruise ship)\",\n" +
                    "    \"컵(cup)\",\n" +
                    "    \"다이아몬드(diamond)\",\n" +
                    "    \"식기세척기(dishwasher)\",\n" +
                    "    \"다이빙 보드(diving board)\",\n" +
                    "    \"개(dog)\",\n" +
                    "    \"돌고래(dolphin)\",\n" +
                    "    \"도넛(donut)\",\n" +
                    "    \"문(door)\",\n" +
                    "    \"용(dragon)\",\n" +
                    "    \"옷장(dresser)\",\n" +
                    "    \"드릴(drill)\",\n" +
                    "    \"드럼(drums)\",\n" +
                    "    \"오리(duck)\",\n" +
                    "    \"덤벨(dumbbell)\",\n" +
                    "    \"귀(ear)\",\n" +
                    "    \"팔꿈치(elbow)\",\n" +
                    "    \"코끼리(elephant)\",\n" +
                    "    \"봉투(envelope)\",\n" +
                    "    \"지우개(eraser)\",\n" +
                    "    \"눈(eye)\",\n" +
                    "    \"안경(eyeglasses)\",\n" +
                    "    \"얼굴(face)\",\n" +
                    "    \"선풍기(fan)\",\n" +
                    "    \"깃털(feather)\",\n" +
                    "    \"울타리(fence)\",\n" +
                    "    \"손가락(finger)\",\n" +
                    "    \"소화전(fire hydrant)\",\n" +
                    "    \"벽난로(fireplace)\",\n" +
                    "    \"소방차(firetruck)\",\n" +
                    "    \"물고기(fish)\",\n" +
                    "    \"플라밍고(flamingo)\",\n" +
                    "    \"손전등(flashlight)\",\n" +
                    "    \"슬리퍼(flip flops)\",\n" +
                    "    \"스탠드(floor lamp)\",\n" +
                    "    \"꽃(꽃)\",\n" +
                    "    \"비행접시(flying saucer)\",\n" +
                    "    \"발(foot)\",\n" +
                    "    \"포크(fork)\",\n" +
                    "    \"개구리(frog)\",\n" +
                    "    \"프라이팬(frying pan)\",\n" +
                    "    \"정원(garden)\",\n" +
                    "    \"정원 호스(garden hose)\",\n" +
                    "    \"기린(giraffe)\",\n" +
                    "    \"염소수염(goatee)\",\n" +
                    "    \"골프 클럽(golf club)\",\n" +
                    "    \"포도(grapes)\",\n" +
                    "    \"잔디(grass)\",\n" +
                    "    \"기타(guitar)\",\n" +
                    "    \"햄버거(hamburger)\",\n" +
                    "    \"망치(hammer)\",\n" +
                    "    \"손(hand)\",\n" +
                    "    \"하프(harp)\",\n" +
                    "    \"모자(hat)\",\n" +
                    "    \"헤드폰(headphones)\",\n" +
                    "    \"고슴도치(hedgehog)\",\n" +
                    "    \"헬리콥터(helicopter)\",\n" +
                    "    \"헬멧(helmet)\",\n" +
                    "    \"육각형(hexagon)\",\n" +
                    "    \"하키 퍽(hockey puck)\",\n" +
                    "    \"하키 스틱(hockey stick)\",\n" +
                    "    \"말(horse)\",\n" +
                    "    \"병원(hospital)\",\n" +
                    "    \"열기구(hot air balloon)\",\n" +
                    "    \"핫도그(hot dog)\",\n" +
                    "    \"온수 욕조(hot tub)\",\n" +
                    "    \"모래시계(hourglass)\",\n" +
                    "    \"집(house)\",\n" +
                    "    \"실내 식물(house plant)\",\n" +
                    "    \"허리케인(hurricane)\",\n" +
                    "    \"아이스크림(ice cream)\",\n" +
                    "    \"재킷(jacket)\",\n" +
                    "    \"감옥(jail)\",\n" +
                    "    \"캥거루(kangaroo)\",\n" +
                    "    \"열쇠(key)\",\n" +
                    "    \"키보드(keyboard)\",\n" +
                    "    \"무릎(knee)\",\n" +
                    "    \"칼(knife)\",\n" +
                    "    \"사다리(ladder)\",\n" +
                    "    \"랜턴(lantern)\",\n" +
                    "    \"노트북(laptop)\",\n" +
                    "    \"잎(leaf)\",\n" +
                    "    \"다리(leg)\",\n" +
                    "    \"전구(light bulb)\",\n" +
                    "    \"라이터(lighter)\",\n" +
                    "    \"등대(lighthouse)\",\n" +
                    "    \"번개(lightning)\",\n" +
                    "    \"선(line)\",\n" +
                    "    \"사자(lion)\",\n" +
                    "    \"립스틱(lipstick)\",\n" +
                    "    \"바닷가재(lobster)\",\n" +
                    "    \"사탕(lollipop)\",\n" +
                    "    \"우체통(mailbox)\",\n" +
                    "    \"지도(map)\",\n" +
                    "    \"마커(marker)\",\n" +
                    "    \"성냥(matches)\",\n" +
                    "    \"확성기(megaphone)\",\n" +
                    "    \"인어(mermaid)\",\n" +
                    "    \"마이크(microphone)\",\n" +
                    "    \"전자레인지(microwave)\",\n" +
                    "    \"원숭이(monkey)\",\n" +
                    "    \"달(moon)\",\n" +
                    "    \"모기(mosquito)\",\n" +
                    "    \"오토바이(motorbike)\",\n" +
                    "    \"산(mountain)\",\n" +
                    "    \"쥐(mouse)\",\n" +
                    "    \"콧수염(moustache)\",\n" +
                    "    \"입(mouth)\",\n" +
                    "    \"머그잔(mug)\",\n" +
                    "    \"버섯(mushroom)\",\n" +
                    "    \"손톱(nail)\",\n" +
                    "    \"목걸이(necklace)\",\n" +
                    "    \"코(nose)\",\n" +
                    "    \"바다(ocean)\",\n" +
                    "    \"팔각형(octagon)\",\n" +
                    "    \"문어(octopus)\",\n" +
                    "    \"양파(onion)\",\n" +
                    "    \"오븐(oven)\",\n" +
                    "    \"올빼미(owl)\",\n" +
                    "    \"붓(paintbrush)\",\n" +
                    "    \"페인트 통(paint can)\",\n" +
                    "    \"야자수(palm tree)\",\n" +
                    "    \"판다(panda)\",\n" +
                    "    \"바지(pants)\",\n" +
                    "    \"종이 클립(paper clip)\",\n" +
                    "    \"낙하산(parachute)\",\n" +
                    "    \"앵무새(parrot)\",\n" +
                    "    \"여권(passport)\",\n" +
                    "    \"땅콩(peanut)\",\n" +
                    "    \"배(pear)\",\n" +
                    "    \"완두콩(peas)\",\n" +
                    "    \"연필(pencil)\",\n" +
                    "    \"펭귄(penguin)\",\n" +
                    "    \"피아노(piano)\",\n" +
                    "    \"픽업 트럭(pickup truck)\",\n" +
                    "    \"액자(picture frame)\",\n" +
                    "    \"돼지(pig)\",\n" +
                    "    \"베개(pillow)\",\n" +
                    "    \"파인애플(pineapple)\",\n" +
                    "    \"피자(pizza)\",\n" +
                    "    \"플라이어(pliers)\",\n" +
                    "    \"경찰차(police car)\",\n" +
                    "    \"연못(pond)\",\n" +
                    "    \"수영장(당구)\",\n" +
                    "    \"아이스바(popsicle)\",\n" +
                    "    \"엽서(postcard)\",\n" +
                    "    \"감자(potato)\",\n" +
                    "    \"콘센트(power outlet)\",\n" +
                    "    \"지갑(purse)\",\n" +
                    "    \"토끼(rabbit)\",\n" +
                    "    \"너구리(raccoon)\",\n" +
                    "    \"라디오(radio)\",\n" +
                    "    \"비(비)\",\n" +
                    "    \"무지개(rainbow)\",\n" +
                    "    \"갈퀴(rake)\",\n" +
                    "    \"리모컨(remote control)\",\n" +
                    "    \"코뿔소(rhinoceros)\",\n" +
                    "    \"소총(rifle)\",\n" +
                    "    \"강(river)\",\n" +
                    "    \"롤러코스터(roller coaster)\",\n" +
                    "    \"롤러스케이트(rollerskates)\",\n" +
                    "    \"범선(sailboat)\",\n" +
                    "    \"샌드위치(sandwich)\",\n" +
                    "    \"톱(saw)\",\n" +
                    "    \"색소폰(saxophone)\",\n" +
                    "    \"스쿨버스(school bus)\",\n" +
                    "    \"가위(scissors)\",\n" +
                    "    \"전갈(scorpion)\",\n" +
                    "    \"드라이버(screwdriver)\",\n" +
                    "    \"바다거북(sea turtle)\",\n" +
                    "    \"시소(see saw)\",\n" +
                    "    \"상어(shark)\",\n" +
                    "    \"양(sheep)\",\n" +
                    "    \"신발(shoe)\",\n" +
                    "    \"반바지(shorts)\",\n" +
                    "    \"삽(shovel)\",\n" +
                    "    \"싱크대(sink)\",\n" +
                    "    \"스케이트보드(skateboard)\",\n" +
                    "    \"해골(skull)\",\n" +
                    "    \"고층 건물(skyscraper)\",\n" +
                    "    \"침낭(sleeping bag)\",\n" +
                    "    \"웃는 얼굴(smiley face)\",\n" +
                    "    \"달팽이(snail)\",\n" +
                    "    \"뱀(snake)\",\n" +
                    "    \"스노클(snorkel)\",\n" +
                    "    \"눈송이(snowflake)\",\n" +
                    "    \"눈사람(snowman)\",\n" +
                    "    \"축구공(soccer ball)\",\n" +
                    "    \"양말(sock)\",\n" +
                    "    \"스피드보트(speedboat)\",\n" +
                    "    \"거미(spider)\",\n" +
                    "    \"숟가락(spoon)\",\n" +
                    "    \"스프레드시트(spreadsheet)\",\n" +
                    "    \"정사각형(square)\",\n" +
                    "    \"곡선(squiggle)\",\n" +
                    "    \"다람쥐(squirrel)\",\n" +
                    "    \"계단(stairs)\",\n" +
                    "    \"별(별)\",\n" +
                    "    \"스테이크(steak)\",\n" +
                    "    \"스테레오(stereo)\",\n" +
                    "    \"청진기(stethoscope)\",\n" +
                    "    \"바늘땀(stitches)\",\n" +
                    "    \"정지 신호(stop sign)\",\n" +
                    "    \"스토브(stove)\",\n" +
                    "    \"딸기(strawberry)\",\n" +
                    "    \"가로등(streetlight)\",\n" +
                    "    \"깍지콩(string bean)\",\n" +
                    "    \"잠수함(submarine)\",\n" +
                    "    \"여행 가방(suitcase)\",\n" +
                    "    \"태양(해)\",\n" +
                    "    \"백조(swan)\",\n" +
                    "    \"스웨터(sweater)\",\n" +
                    "    \"그네(swing set)\",\n" +
                    "    \"검(sword)\",\n" +
                    "    \"주사기(syringe)\",\n" +
                    "    \"테이블(table)\",\n" +
                    "    \"주전자(teapot)\",\n" +
                    "    \"테디 베어(teddy bear)\",\n" +
                    "    \"전화(telephone)\",\n" +
                    "    \"텔레비전(television)\",\n" +
                    "    \"테니스 라켓(tennis racquet)\",\n" +
                    "    \"텐트(tent)\",\n" +
                    "    \"에펠탑(The Eiffel Tower)\",\n" +
                    "    \"만리장성(The Great Wall of China)\",\n" +
                    "    \"모나리자(The Mona Lisa)\",\n" +
                    "    \"호랑이(tiger)\",\n" +
                    "    \"토스터(toaster)\",\n" +
                    "    \"발가락(toe)\",\n" +
                    "    \"화장실(toilet)\",\n" +
                    "    \"이(tooth)\",\n" +
                    "    \"칫솔(toothbrush)\",\n" +
                    "    \"치약(toothpaste)\",\n" +
                    "    \"토네이도(tornado)\",\n" +
                    "    \"트랙터(tractor)\",\n" +
                    "    \"신호등(traffic light)\",\n" +
                    "    \"기차(train)\",\n" +
                    "    \"나무(tree)\",\n" +
                    "    \"삼각형(triangle)\",\n" +
                    "    \"트롬본(trombone)\",\n" +
                    "    \"트럭(truck)\",\n" +
                    "    \"트럼펫(trumpet)\",\n" +
                    "    \"티셔츠(t-shirt)\",\n" +
                    "    \"우산(umbrella)\",\n" +
                    "    \"속옷(underwear)\",\n" +
                    "    \"밴(van)\",\n" +
                    "    \"꽃병(vase)\",\n" +
                    "    \"바이올린(violin)\",\n" +
                    "    \"세탁기(washing machine)\",\n" +
                    "    \"수박(watermelon)\",\n" +
                    "    \"워터슬라이드(waterslide)\",\n" +
                    "    \"고래(whale)\",\n" +
                    "    \"바퀴(wheel)\",\n" +
                    "    \"풍차(windmill)\",\n" +
                    "    \"와인 병(wine bottle)\",\n" +
                    "    \"와인 잔(wine glass)\",\n" +
                    "    \"손목시계(wristwatch)\",\n" +
                    "    \"요가(yoga)\",\n" +
                    "    \"얼룩말(zebra)\",\n" +
                    "    \"지그재그(zigzag)\"\n" +
                    "]\n", drawingBitmap)
                .collect { response ->
                    response.text?.let { text ->
                        Log.d("LoadingActivity", text)
                        predictions.addAll(parsePredictions(text))
                    }
                }
            val sortedPredictions = predictions.sortedByDescending { it.second }
            navigateToResultActivity(sortedPredictions)
        }
    }

    private fun parsePredictions(resultText: String): List<Pair<String, Float>> {
        val predictions = mutableListOf<Pair<String, Float>>()
        val lines = resultText.split("\n")
        for (line in lines) {
            val parts = line.split(":")
            if (parts.size == 2) {
                val label = parts[0].trim()
                val confidence = parts[1].trim().toFloatOrNull() ?: 0f
                predictions.add(Pair(label, confidence))
            }
        }
        return predictions
    }

    private fun navigateToResultActivity(predictions: List<Pair<String, Float>>) {
        if (predictions.size < 4) {
            runOnUiThread {
                Toast.makeText(this, "Insufficient predictions received.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val bestPrediction = predictions[0]
        val secondPrediction = predictions[1]
        val thirdPrediction = predictions[2]
        val fourthPrediction = predictions[3]

        val score = calculateScore(bestPrediction.second, remainingMilliSeconds)

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("bitmapFileUri", bitmapFileUriString)
            putExtra("bestPrediction", bestPrediction.first)
            putExtra("bestPredictionPercentage", bestPrediction.second)
            putExtra("secondPrediction", secondPrediction.first)
            putExtra("secondPredictionPercentage", secondPrediction.second)
            putExtra("thirdPrediction", thirdPrediction.first)
            putExtra("thirdPredictionPercentage", thirdPrediction.second)
            putExtra("fourthPrediction", fourthPrediction.first)
            putExtra("fourthPredictionPercentage", fourthPrediction.second)
            putExtra("score", score)
        }
        startActivity(intent)
        finish()
    }

    private fun calculateScore(bestPredictionConfidence: Float, remainingMilliSeconds: Long): Float {
        return 100 * bestPredictionConfidence * (1 - 0.1f * (5000 - remainingMilliSeconds) / 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
