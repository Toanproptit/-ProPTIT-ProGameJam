# Báo cáo kết quả bài tập Code Game
## 1 Thông Tin Nhóm

Tên dự án: Cultivator’s Dread
Link dự án: [Github](https://github.com/Toanproptit/-ProPTIT-ProGameJam.git)

Thành viên nhóm:
* Nguyễn Trọng Toàn

Mô hình làm việc:
* Solo:
  
Khi làm việc solo, tôi chia dự án thành từng giai đoạn nhỏ, mỗi giai đoạn có mục tiêu rõ ràng. Tôi sử dụng Notion để quản lý task, ưu tiên các phần quan trọng trước và dành một buổi mỗi tuần để tự đánh giá tiến độ. Mỗi ngày tôi đều cố gắng hoàn thành ít nhất một việc có giá trị – dù nhỏ – nhưng tạo động lực duy trì lâu dài."
## 2 Giới thiệu dự án
Cultivator’s Dread mặc dù dự án chưa được giống với cái tên ban đầu đề ra nhưng đây là tựa game hành động – phiêu lưu nơi người chơi hóa thân thành một tân binh đầy tiềm năng, đối mặt với quái vật và boss tại nhiều bản đồ độc đáo. Với hệ thống hoạt ảnh mượt mà, nhiệm vụ đa dạng, kỹ năng bay nhảy tự do cùng sự hỗ trợ từ UFO, trò chơi mang đến trải nghiệm nhập vai cá nhân hóa, đầy thử thách nhưng cũng cực kỳ cuốn hút!
## 3 Các chức năng chính
* Khi vào sẽ có nhạc nền cũng như âm thanh khi đánh quái
* hệ thông chiêu mặc dù không đa dạng nhưng đủ để dùng
* Có thể tăng các chỉ số của nhân vật
* hoàn thành nhiệm vụ để đánh quái cũng như có thể vượt map
## 4 Công nghệ 
## 4.1 Công nghệ sử dụng 
* Java
* LibGDX
* Aseprite
* Tiled Map
* Gradle
## Cấu trúc dự án
```php
CultivatorGame/
├── .gradle/
├── .idea/
├── assets/
│   └── assets/
│       ├── Map/
│       ├── Monster/
│       ├── Player/
│       ├── Skills/
│       ├── Sound/
│       ├── UFO/
│       ├── Ui/
│       ├── assets.txt
│       └── default.fnt
├── Map/
├── Monster/
├── Player/
├── Skills/
├── Sound/
├── UFO/
├── Ui/
│   ├── assets.txt
│   └── default.fnt
├── build/
├── core/
│   ├── build/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── Trongtoan/
│   │                   └── ProjGame/
│   │                       ├── animation/
│   │                       ├── entities/
│   │                       ├── iteams/
│   │                       ├── logic/
│   │                       ├── screen/
│   │                       ├── skills/
│   │                       ├── ui/
│   │                       ├── utils/
│   │                       └── Main.java
│   └── build.gradle
├── gradle/
├── lwjgl3/
│   ├── build/
│   ├── icons/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── Trongtoan/
│   │       │           └── ProjGame/
│   │       │               └── lwjgl3/
│   │       │                   ├── Lwjgl3Launcher.java
│   │       │                   └── StartupHelper.java
│   │       └── resources/
│   └── build.gradle
├── build.gradle

```
## Diễn Giải
* assets: Chứa các tài nguyên như hình ảnh, âm thanh
* core: Chứa các class chính của game như model, view, controller
* lw3jgl3 Chứa các class để chạy trên các nền tảng desktop
## 5 Ảnh và video demo
 Ảnh demo:
![alt text](README_Anh/Anh1.png)
![alt text](README_Anh/Anh2.png)
![alt text](README_Anh/Anh3.png)
![alt text](README_Anh/Anh4.png)
![alt text](README_Anh/Anh5.png)
![alt text](README_Anh/Anh6.png)
 Video demo: [Video](https://youtu.be/TCp_8JqZPzA)
## 6 Các vấn đề gặp phải.
## Vấn đề 1:
* Thời gian một tháng không đủ để mở rộng được nhiều cốt truyện cũng như map, kĩ năng.
## Hành động giải quyết
**Giải pháp**: tối ưu game hết mức có thể, chia thời gian code hợp lí.
## Kết quả: Game đủ để chơi, không ngắn quá.
## Vấn đề 22:
* Có nhiều bug kĩ năng , va chạm cũng như vùng kiểm tra do map nào cũng có scale :((
* ## Hành động giải quyết:
**Giải pháp**: chưa thể tìm ra hướng tối ưu nhất
## Kết quả: mặc dù skill dùng vẫn ổn nhưng một số chỗ vẫn không thể fix được :((

## 7 Kết luận
Kết quả đạt được: [Game chạy khá ổn, trơn tru, skill cũng khá đẹp nhưng kiểm va chạm chưa ổn lắm]

Hướng phát triển tiếp theo: [sẽ mở thêm nhiều map, xây dụng cốt truyện ,cùng với hệ thống kĩ năng nhân vật]
