2020.03 ~ 2020.11
 
  
```
산학협력 과제 스마트 온도계와 안드로이드 
사이의 BLE 통신 주제를 다룬 내용 입니다.

주로 BLE통신과 안드로이드 기능에 초점을 
맞추어 진행하였습니다.
```

# 1. 첫 화면

<img width="271" alt="image" src="https://user-images.githubusercontent.com/62547169/122842972-59e97200-d339-11eb-9b75-843b2f42c55c.png">

```
사용자가 사용하는 IP 주소를 입력합니다. 
PHP 웹 서버와 연동하기 위함 입니다.
```
　          
　          
　          
# 2. 두 번째 화면

<img width="269" alt="image" src="https://user-images.githubusercontent.com/62547169/122843002-68378e00-d339-11eb-857c-a1c5c816a647.png">    <img width="268" alt="image" src="https://user-images.githubusercontent.com/62547169/122843025-738ab980-d339-11eb-9478-4267981a014d.png">
```
스캔 버튼을 눌러 스마트 온도계의 Bluetooth 값을 찾습니다.

스마트 온도계 측에서 전송 버튼을 누르면 값을 받아올 수 
있으며 새로 고침시 화면안에 스와이프를 하면 됩니다.
```
　          
　          
　        
# 3. 세 번째 화면

<img width="269" alt="image" src="https://user-images.githubusercontent.com/62547169/122843050-84d3c600-d339-11eb-8954-ed9cd2e50314.png">

```
데이터베이스에 보낼 값들을 보낼 화면입니다. 
오른쪽 상단에 장소를 추가할 수 있는 항목이 있으며 
온도 측정시 자동으로 온도값과 시간값들이 갱신 됩니다.
```
　          
　          
　      
# 3-1. 세 번째 화면 + drawer

<img width="269" alt="image" src="https://user-images.githubusercontent.com/62547169/122843071-91f0b500-d339-11eb-9e78-530017fb388e.png">


```
왼쪽에서 오른쪽으로 스와이프를 하면 위와같은 장소 입력하는 리스트가 
출력됩니다.

리스트를 추가 하며 사용자가 직접 항목의 이름을 변경가능합니다.

SharedPreferences ArrayList를 이용하여 리스트를 저장하여 앱 
종료시에도 리스트가 유지 됩니다.
```

　          
　 
　          
　 
## ● 개발환경   

 <img width="445" alt="image" src="https://user-images.githubusercontent.com/62547169/122843102-aaf96600-d339-11eb-9ee6-0e21d9817793.png">

```
안드로이드 스튜디오를 사용하였습니다.
```
　 
 <img width="904" alt="image" src="https://user-images.githubusercontent.com/62547169/122843141-b64c9180-d339-11eb-9a3f-ab31b59f30ce.png">


```
PHP 웹 서버를 이용 하였습니다.
```
　          
　 
　          
　 
## ● DB 구조   

 <img width="800" alt="image" src="https://user-images.githubusercontent.com/62547169/122843153-bea4cc80-d339-11eb-86db-5cb9b1f29ded.png">
　          
　 
　          
　 
## ● 시스템 구조   

 <img width="897" alt="image" src="https://user-images.githubusercontent.com/62547169/122843167-c5cbda80-d339-11eb-8b94-d4b07ade0737.png">
  　          
　          
  　          
　          
 
  　          
　          
 
 
 　          
　          
```
안드로이드 소스 코드는 아래의 링크에서 직접 확인할 수 있습니다.
https://github.com/ansanghyun20/android_temp.git
```
