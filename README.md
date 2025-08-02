# Ứng Dụng Quản Lý Điểm Học Sinh

## Mô tả
Ứng dụng quản lý điểm học sinh là một hệ thống desktop được phát triển bằng Java Swing, cho phép giáo viên quản lý điểm số của học sinh một cách hiệu quả. Hệ thống hỗ trợ đăng nhập/đăng ký, quản lý lớp học, và tính toán điểm cuối kỳ theo trọng số.

## Tính năng chính

### 🔐 Hệ thống xác thực
- **Đăng ký tài khoản mới**: Tạo tài khoản với username và password
- **Đăng nhập**: Xác thực người dùng để truy cập hệ thống
- **Lưu trữ dữ liệu**: Dữ liệu được đồng bộ với server online

### 📚 Quản lý lớp học
- **Thêm lớp học mới**: Tạo lớp học với tên và cấu hình trọng số
- **Xóa lớp học**: Loại bỏ lớp học không còn sử dụng
- **Chọn lớp học**: Chuyển đổi giữa các lớp học khác nhau

### 👥 Quản lý học sinh
- **Thêm học sinh**: Nhập thông tin học sinh mới
- **Cập nhật thông tin**: Chỉnh sửa thông tin học sinh
- **Xóa học sinh**: Loại bỏ học sinh khỏi lớp
- **Tìm kiếm**: Tìm kiếm học sinh theo ID hoặc tên

### 📊 Quản lý điểm số
- **Điểm chuyên cần**: Nhập điểm chuyên cần từng buổi học
- **Điểm bài tập**: Nhập điểm bài tập cá nhân
- **Điểm cuối kỳ**: Nhập điểm thi cuối kỳ
- **Tính điểm tự động**: Hệ thống tự động tính điểm cuối kỳ theo trọng số

### ⚖️ Cấu hình trọng số
- **Trọng số điểm cá nhân**: Cấu hình % cho điểm chuyên cần và bài tập
- **Trọng số điểm cuối kỳ**: Cấu hình % cho điểm thi cuối kỳ
- **Áp dụng trọng số**: Cập nhật cách tính điểm cho lớp học

## Yêu cầu hệ thống

### Phần mềm cần thiết
- **Java 8** trở lên (JDK 1.8+)
- **Maven** (để build project)
- **MySQL** (nếu sử dụng database local)

### Cấu hình tối thiểu
- **RAM**: 512MB
- **Ổ cứng**: 100MB trống
- **Hệ điều hành**: Windows, macOS, Linux

## Hướng dẫn cài đặt

### Bước 1: Clone repository
```bash
git clone [URL_REPOSITORY]
cd quanlidiem
```

### Bước 2: Build project
```bash
mvn clean compile
```

### Bước 3: Chạy ứng dụng
```bash
mvn exec:java -Dexec.mainClass="gui.LoginRegisterFrame"
```

Hoặc chạy file JAR đã build:
```bash
java -jar target/quanlidiem-1.0-SNAPSHOT.jar
```

## Hướng dẫn sử dụng

### 1. Đăng nhập/Đăng ký

#### Đăng ký tài khoản mới
1. Mở ứng dụng
2. Nhập username và password mong muốn
3. Click nút **"Register"**
4. Nếu thành công, thông báo sẽ hiển thị

#### Đăng nhập
1. Nhập username và password đã đăng ký
2. Click nút **"Login"**
3. Nếu đúng thông tin, sẽ chuyển sang màn hình quản lý

### 2. Quản lý lớp học

#### Thêm lớp học mới
1. Click nút **"Add Class"**
2. Nhập tên lớp học
3. Click **"OK"** để tạo lớp

#### Xóa lớp học
1. Chọn lớp cần xóa từ dropdown
2. Click nút **"Remove Class"**
3. Xác nhận xóa lớp

#### Cấu hình trọng số
1. Chọn lớp học từ dropdown
2. Nhập % cho **Personal Score** (điểm cá nhân)
3. Nhập % cho **Final** (điểm cuối kỳ)
4. Click nút **"Apply"** để áp dụng

### 3. Quản lý học sinh

#### Thêm học sinh mới
1. Điền thông tin học sinh:
   - **ID**: Mã số học sinh
   - **Name**: Tên học sinh
   - **Attendance**: Điểm chuyên cần (cách nhau bằng dấu phẩy)
   - **Lab Assignment**: Điểm bài tập (cách nhau bằng dấu phẩy)
   - **Final**: Điểm thi cuối kỳ
2. Click nút **"Add Student"**

#### Cập nhật thông tin học sinh
1. Click vào dòng học sinh trong bảng
2. Thông tin sẽ hiển thị trong các ô input
3. Chỉnh sửa thông tin cần thiết
4. Click nút **"Update Student"**

#### Xóa học sinh
1. Chọn học sinh cần xóa trong bảng
2. Click nút **"Delete Student"**
3. Xác nhận xóa

#### Tìm kiếm học sinh
1. Nhập từ khóa vào ô tìm kiếm
2. Kết quả sẽ hiển thị ngay lập tức

### 4. Xem báo cáo điểm

Bảng hiển thị thông tin:
- **ID**: Mã số học sinh
- **Name**: Tên học sinh
- **Personal Score**: Điểm cá nhân (trung bình chuyên cần + bài tập)
- **Final Score**: Điểm thi cuối kỳ
- **Final Grade**: Điểm cuối kỳ (theo trọng số)
- **Letter Grade**: Điểm chữ (A+, A, B+, B, C+, C, F)

## Cấu trúc dữ liệu

### Bảng điểm
- **Điểm chuyên cần**: Danh sách điểm từng buổi học
- **Điểm bài tập**: Danh sách điểm bài tập cá nhân
- **Điểm cuối kỳ**: Điểm thi cuối kỳ

### Cách tính điểm
```
Điểm cá nhân = (Trung bình điểm chuyên cần + Trung bình điểm bài tập) / 2
Điểm cuối kỳ = (Điểm cá nhân × Trọng số cá nhân) + (Điểm thi cuối kỳ × Trọng số cuối kỳ)
```

### Thang điểm chữ
- **A+**: 9.0 - 10.0
- **A**: 8.0 - 8.9
- **B+**: 7.0 - 7.9
- **B**: 6.5 - 6.9
- **C+**: 5.5 - 6.4
- **C**: 5.0 - 5.4
- **F**: Dưới 5.0

## Lưu ý quan trọng

### Bảo mật
- Mật khẩu được mã hóa khi lưu trữ
- Dữ liệu được đồng bộ với server an toàn
- Mỗi người dùng có dữ liệu riêng biệt

### Dữ liệu
- Dữ liệu được tự động lưu khi có thay đổi
- Backup dữ liệu định kỳ được khuyến nghị

### Hiệu suất
- Tìm kiếm real-time không làm chậm hệ thống
- Giao diện responsive và thân thiện

## Xử lý lỗi thường gặp

### Lỗi kết nối mạng
- Kiểm tra kết nối internet
- Thử lại sau vài phút
- Liên hệ admin nếu lỗi kéo dài

### Lỗi đăng nhập
- Kiểm tra username và password
- Đảm bảo đã đăng ký tài khoản
- Thử đăng ký tài khoản mới

### Lỗi nhập điểm
- Điểm phải từ 0.0 đến 10.0
- Sử dụng dấu phẩy để phân cách nhiều điểm
- Không để trống các trường bắt buộc

## Hỗ trợ kỹ thuật

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra log lỗi trong console
2. Thử khởi động lại ứng dụng
3. Liên hệ đội zalo 0326791337

## Phiên bản
- **Version**: 1.0-SNAPSHOT
- **Ngày phát hành**: 2024
- **Tác giả**: Development Team 
