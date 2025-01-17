
✏️ 일상에서 벗어나 캔버스 위에 꿈을 펼치고 싶다면, 망설이지 마세요!
한 시간에 한번씩 바뀌는 단어를 마음껏 그려보세요!

그러니까, 음… **5초 안에**요! 😆

</aside>

## 🎨 Drawdle 소개

혹시 Wordle을 아시나요? 2021년 [Josh Wardle](https://github.com/powerlanguage)에 의해 만들어진 단어 게임으로, 하루에 한 번만 플레이할 수 있는 특징이 있습니다. 너무 적은 게 아닌가 싶을 수도 있지만, 이 독특한 방식 때문에 더욱 하고 싶은 마음이 드는 게임이기도 하죠.


Drawdle은 조금 더 자유롭습니다! 한 시간에 한 번, 여러분이 원할 때에 플레이할 수 있습니다. 규칙은 간단하죠! 주어진 단어를, **5초 안에**, 빠르게 그려내는 겁니다. 그러면 앱에 탑재된 인공지능이 여러분의 그림을 분석하고, 목표 단어와 일치하는지를 알려줄겁니다. 그러면 여러분은 점수를 얻을 수 있죠. 어때요, 심플하죠?

## 🖥️ 개발 환경

[FE] Kotlin, After Effects, Adobe Firefly

[BE] Node.js(Express.js)

[DB] MongoDB

[Cloud] AWS

[IDE] Android Studio, Visual Studio Code

## 👤 팀원

김민경: [kim-minkyoung - Overview](https://github.com/kim-minkyoung/)

안규찬: [gyuch-an02 - Overview](https://github.com/gyuch-an02)


## API-Docs (swagger)

[**바로가기**](https://www.notion.so/madcamp2-api-docs-1b4e35f390db4da4b475ac03b282b84b?pvs=21)

## DB (mongoDB)

```jsx
const userSchema = new mongoose.Schema({
  email: { type: String, unique: true },
  nickname: { type: String },
  profileImage: { type: String },
  compareWord: { type: String },
  playCount: { type: Number },
  score: { type: Number },
  totalScore: { type: Number },
});
```

## 기능별 앱 소개

|로그인 화면|플레이 팁|내 프로필 보기|게임 플레이|랭킹|광고 표시|기회 소진 시|
|------------|----------|---------------|-----------|----|----------|-------------|
| ![KakaoTalk_20240710_183407177](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/88e1d159-38e8-4a4d-82d9-211166d6fade) | ![KakaoTalk_20240710_183407177_05](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/941a4fc6-ca40-4af9-ba78-ae704ab45e6d) | ![KakaoTalk_20240710_183407177_02](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/1b77dce2-5154-4a22-ad99-b34c3b1d8643) | ![KakaoTalk_20240710_183407177_04](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/30ecbfc2-066c-462d-a0c9-a4e50a3b2c2e) | ![KakaoTalk_20240710_183407177_01](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/c7433cd6-e1b9-4e06-bdb6-99e30470ffbf) | ![KakaoTalk_20240710_183407177_03](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/cd5b4e7d-29df-4bec-bb88-90ae0c965cb3) | ![KakaoTalk_20240710_183407177_06](https://github.com/kim-minkyoung/madcamp2_frontend/assets/127263741/d50147ec-162c-449d-b7a4-c0ddac2f4df6) |


### 🎨 로그인

- 구글 로그인 SDK를 이용: 로그인 내역이 없다면 자동으로 회원가입, 있다면 로그인
    - 가입 시 구글 계정 이름과 이메일을 가져와 사용
 
### 🎨 플레이 팁

- Drawdle에 대한 설명과 작동방식에 대한 설명을 Q&A 형식으로 확인 가능
- 내용:
    
    `Q. Drawdle은 무엇인가요?`
    
    `A. Drawdle은 매 시간마다 업데이트되는 단어를 맞히는 게임입니다. 정확한 단어를 추측하여 당신의 그림 실력과 순발력을 테스트해보세요.`
    
    `Q. 한 시간에 두 번보다 많이 플레이할 수는 없나요?`
    
    `A. Drawdle은 매 시간마다 단어가 업데이트되지만, 각 단어 당 두 번의 기회를 줍니다.
    우리는 하루에 24 번, 모든 참여자가 동일한 정답을 맞추는 게임 경험을 제공합니다.
    정답 단어는 매 시각의 정각에 업데이트됩니다.`

### 🎨 내 프로필 보기

- 닉네임 조회 및 수정 가능
- 로그인된 이메일, 누적 점수, 최근 점수를 조회 가능
- 아바타를 클릭할 때마다 랜덤으로 아바타를 변경
    - 변경된 아바타는 자동으로 DB에 연동되어 저장
- 로그 아웃 또는 계정 삭제 가능

### 🎨 게임 플레이

- 기본적으로 사용자는 한 시간에 단 한 판만 플레이할 수 있음.
- 제시된 단어(자연스럽지 못한 번역을 고려하여 “한글(영어)” 형식으로 제시됨)를 확인하고, 5초 내에 캔버스에 그림을 그림.
- Gemini API를 사용하여 그림의 상위 4개의 예측 결과값을 주고, 4개 안에 제시된 단어가 있다면 점수 산출 기준에 따라 점수를 산출
    - 상위 4개 예측값에 제시된 단어가 없다면, 사용자는 0점을 받음.
    - 점수 산출 기준: 100 * 일치율 * (1 - 0.1 * 그리는 데 소요된 시간[ms]) / 5000
- “한 판 더” 버튼: 광고를 보면 같은 단어를 한 번 더 그릴 수 있음.
    - 하지만 기회는 단 한 번만 더 주어지고, 이후 다시 시도할 경우 기회를 모두 사용했다는 메시지가 뜸.

### 🎨 랭킹

- 누적 점수 랭킹과 이번 단어 랭킹 조회 가능
- 나의 랭킹은 하단에 따로 표시되며, 순위는 선택한 탭에 따라 연동되어 달라짐.


## 사용된 외부 API 목록

### Quick, Draw!

애당초 Drawdle을 개발하는 과정에서 우리는 Google Creative Lab의 Quick, Draw! 데이터셋을 사용하기로 계획했습니다. 스케치 인식의 대명사인만큼 참고할 자료나 모델이 많았기 때문입니다. 

그러나, TensorFlow.js로 train된 모델을 사용하는 과정에서 버전 오류를 겪게 되었고, 어찌저찌 모델을 가져와 구현해보아도 인식율이 그리 좋지 못했습니다.

### Gemini

예상보다 저조한 인식율에 낙담하던 저희에게 그 다음으로 떠오른 방법은 Gemini API를 사용하는 것이었습니다. 우리는 알 수 없는 방법으로 인공지능은 이미지를 분석하는 능력이 탁월하였고, 우리로서는 이를 이용하지 않을 이유가 없었죠. Image와 적절한 프롬프팅을 통해 저희는 Gemini로부터 스케치의 상위 4개 예측값을 json  형식으로 받아오는 데 성공하였고, 이를 이용해 앱을 구성하게 되었습니다.

### AdMob

우리는 너그러운 마음씨로 플레이어들에게 한 번의 추가 기회를 제공하기로 했습니다. 한 시간에 한 번뿐인 기회를 5초만에 날려먹으면, 그 상심이 얼마나 크겠어요? 하지만 그냥 한 번 더 플레이하는 것은 시시할 것이라고 생각한 저희는 **보상형 광고를 삽입하기로** 했습니다. Google의 AdMob으로부터 짧은 광고를 받아오고, 이를 적절한 입력에 대해 출력함으로써 ‘한 번 더’의 기회를 얻는 것이 쉽지만은 않도록 하였습니다.

그러나 실제 광고를 불러오기 위해서는 계정 인증과 더불어 복잡한 절차들이 필요했기 때문에, 스토어에 배포하지 않은 우리 앱에 대해서는 일단 테스트용 광고를 내보이기로 했습니다. 상업적으로 이용할 계획은 없었으니, 이 편이 더 좋은 선택이었을지도요!

## 디자인

### 로고

Drawdle의 로고는 Adobe Firefly를 이용해 생성되었습니다. 스케치라는 앱의 컨셉에 맞추어 연필로 스케치한 듯한 알파벳 이미지가 필요했는데, 이를 만들기 위한 적절한 툴이었죠. Firefly를 통해 생성된 알파벳 이미지를 적절히 배치하여 원하는 로고를 만드는 데 성공했습니다. 하지만 이대로 앱에 삽입하기는 심심할 것 같아 After Effects를 이용해 꿈틀거리는 효과를 주게 되었습니다.

![drawdle](https://github.com/kim-minkyoung/madcamp2_frontend/assets/105150339/714b45d8-0bb0-4c5d-ab6e-a64b6e93f2f1)


꿈틀대는 Drawdle의 메인 로고

### 색상

![drawdle-color](https://github.com/kim-minkyoung/madcamp2_frontend/assets/105150339/a23bc7b9-4164-4f5d-b0f8-b39af55fc35f)


두 팀원 모두 몰입캠프의 분홍색 담요를 지급받은 것을 계기로 앱의 테마 색상을 분홍색으로 결정하였습니다. 그러면서도 단색의 심심한 배경으로 남지 않게 그라데이션을 넣어 앱의 색상을 결정하게 되었습니다.

## Technical Issues we’ve met

[FE]

- ViewModel을 activity 간 공유하지 못하는 점 때문에 많은 불편함을 겪으면서 여러 activity를 사용하는 것의 필요성에 대해 고찰하게 되었다.
    - Single Activity에 대한 도움되는 영상이 있어 공유한다. 앞으로 개발에서는 이러한 점을 고려하는 것이 좋겠다. (https://youtu.be/2k8x8V77CrU?si=XEfF5SuoP0uXtECm))
    

[BE]

- MongoDB가 분명 연결되었음을 확인할 수 있는데 timeout error 뜨는 이유
    
    : moongose와 mongodb를 비동기적으로 처리하지 않아 moongose가 mongodb보다 먼저 연결되어 발생하는 문제였다. (https://dev.to/arunkc/solve-mongooseerror-operation-x-find-buffering-timed-out-after-10000ms-3d3j)
    
- AWS permission denied error: ‘서울’ 지역으로 인스턴스를 다시 만들어서 서버를 연결하니 해결됨

## 느낀 점 + 더 공부해보고 싶은 것

[김민경]

- 느낀점
    - 백엔드가 어떻게 구성되는지조차 거의 몰랐었는데, 매일 12시간 이상 공부하고 개발하며 흐름을 알게 되었다.
    - mongoDB가 json 형식이라 데이터를 상대적으로 유연하게 다룰 수 있었는데, 그래서 어디까지를 한 object로 합쳐야할지에 대한 경계를 임의로 정하기가 어려웠다.
- 더 공부해보고 싶은 것
    - **git push → 서버 죽이기 → git pull → 서버 연결**의 과정이 상당히 번거로웠다. 다음 프로젝트 때는 CICD 자동 배포를 시도해 보고 싶다.
    - 관계형DB도 다뤄보고 싶다.

[안규찬]

- 느낀점
    - 아무것도 모르던 1주차에 비하면 원하는 기능을 만들기 위해 적절한 인터페이스를 가져오고 사용하는 데 있어 큰 무리가 없게 되었다. 다룰 수 있는 툴이 늘어날수록 원하는 결과에 가깝게 만들 수 있다는 것에 뿌듯함을 느낀다.
    - 프론트엔드와 백엔드 각자의 역할과 둘의 연동 과정에 대해 보다 잘 이해할 수 있었다. 다만 백엔드보다는 프론트엔드에 집중하여 작업하여 이 점은 아쉬운 부분이다.
- 더 공부해보고 싶은 것
    - 이번 프로젝트에서는 DB를 간단하게 구성하여 넣을 수 있는 기능이 제한되었는데, 다음에는 보다 복잡한 DB 스키마를 효과적으로 구축하고 다루는 방법에 대해 공부하고 싶다.
