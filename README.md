# APP_WhenHyuGaYa_Solo
2019 국방부 SW집체교육 APP분야 프로젝트<br>
2019/10/21~2019/10/24 ver 1.0 개발

# 컴퓨터 구성 / 필수 조건 안내 (Prerequisites)
 안드로이드 지원 (iOS 미지원)
 
 
# 설치 안내 (Installation Process)
 APK파일 다운로드 후 설치
 
# 사용법 (Getting Started)
 어플을 설치한 뒤 실행시킵니다.<br>
 왠(When) 휴가야?는 군 장병에 최적화된 휴가플래너로 User,WishList,Planner 3개의 기능으로 나눠집니다.<br>
 초기 화면을 보면 캘린더가 보이는데 이것이 Planner입니다.<br>
 좌측 상단 아이콘은 유저 설정으로, 우측 상단 아이콘은 WishList로 이동합니다.<br>
 <br>
 <h3>아래는 상세 사용법입니다.</h3>
<h4>유저 설정</h4>
 최초 실행시 좌측 상단의 유저 아이콘을 눌러 'User 설정'에서 초기 설정을 실시합니다.<br>
 초기설정은 개인정보(이름, 입대일, 전역일, 남은 연가)를 저장합니다.<br>
 초기 설정을 완료하면 이름, 입대일, 오늘, 전역일과 현재 까지 군 복무 %를 보여줍니다.<br>
 또한 휴가 추가 버튼을 통해 본인이 가지고있는 휴가를 등록하여 관리할수있습니다.<br>
 마지막으로 알림바 스위치가 있는데 이를 On하면 Notification이 실행됩니다.<br>
 여기서 어느 때나 WishList에 Wish를 추가할수있습니다.<br> 
<h4>WishList</h4>
 다음으로 우측 상단 아이콘을 눌러 'WishList'로 이동합니다.<br>
 이곳은 본인이 휴가때 하고싶은 일-Wish를 저장하며 보여줍니다.<br>
 우측 하단 추가 버튼을 누르면 위시 이름과 사진,별점(선호도), 상세내용을 입력할수있습니다.<br>
 사진은 앨범에서 불러오거나 아무때나 이미지 공유를 통해 불러올수 있으며 필요한 부분만 잘라내여 저장 할수있습니다.<br>
 별점은 본인이 이 일을 휴가때 얼마나 하고싶은지 별 갯수를 통해 저장하는 것입니다.<br>
 모두 입력하였으면 하단의 완료 버튼을 눌러 Wish를 저장합니다.<br>
<h4>Planner</h4>
다시 처음화면으로 돌아가 휴가 계획을 시작하겠습니다.<br>
캘린더에서 본인이 나가고싶은 일자를 눌러 휴가 계획하기 버튼을 눌러 계획할수있습니다.<br>
휴가이름, 휴가를 추가하여 복귀일과 몇박 몇일인지를 자동으로 계산해줍니다.<br>
휴가 계획이 끝났으면 완료버튼을 눌러 저장합니다.<br>
저장이 완료되었으면 캘린더에 본인이 계획한 휴가가 보일것입니다.<br>
이를 누르면 휴가 일정을 계획할수있습니다.<br>
일차별로 일정을 계획할 수있는데 휴가가 3일 이상일 경우 좌우로 스와이프할수있습니다.<br>
일차 별로 하고싶은 일을 하단의 WishList아이콘을 눌러 저장할수있습니다.<br>
WishList아이콘을 누르면 위시리스트 목록이 나오고 이중 하고싶은 일(Wish)을 누르면 일차들이 빨갛게 바뀝니다.<br>
여기서 일차를 눌르면 해당 일차에 Wish가 저장됩니다.<br>
이런 식으로 휴가를 계획하고 확인 할수있습니다.
 

# 파일 정보 및 목록 (File Manifest)

-Planner 관련<br>
MainActivity : 휴가계획 목록 및 캘린더 Activity<br>
VacPlanActivity : 휴가 계획 추가 Activity<br>
VacscheduleActivity : 휴가 일정 Activity<br>
GridFragment : 휴가 일정에서 ViewPager 사용 부분(Wish목록들) Activity<br>
<br>
-WishList 관련<br>
WishActivity : WishList 기능 Activity<br>
WishaddActivity : WishList 추가 및 수정 및 상세보기 Activity<br>
WishPopupActivity : VacscheduleActiviy에서 WishList 불러오는 PopUp Activity<br>
<br>
-User 관련<br>
SettingActivity : 유저 정보,휴가 추가 Activity<br>
<br>
-기타<br>
BackPressCloseHandler : 뒤로가기 두번 기능<br>
DBHelper : sqlite 데이터베이스<br>
SplashActivity : Splash화면



# 저작권 및 사용권 정보 (Copyright / End User License)
-

# 배포자 및 개발자의 연락처 정보 (Contact Information)
  배포 및 개발자 : 백진석<br>
  이메일 : js980112@naver.com<br>
  이메일로 연락바랍니다.
 
# 알려진 버그 (Known Issues)
  알림바 broadcast
  
# 문제 발생에 대한 해결책 (Troubleshooting)
  이메일로 문제발생 과정을 문의해주시면 해결하겠습니다.
  
# 크레딧 (Credit)
  백진석(Baek Jin Seok)
  
# 업데이트 정보 (Change Log)
10/24 왠(When) 휴가야? ver 1.0 
