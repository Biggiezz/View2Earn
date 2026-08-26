# AGENTS.md --- Android Project Coding & UI Rules

## 0. MỤC ĐÍCH

Đây là file quy ước bắt buộc của dự án.

**Mọi AI Agent phải đọc file này trước khi bắt đầu phân tích, tạo hoặc
chỉnh sửa code.**

Thứ tự ưu tiên khi bắt đầu một task:

1.  Đọc `AGENTS.md`.
2.  **Ưu tiên đọc và tuân thủ Ponytail trước tiên** nếu
    Ponytail/plugin/skill đang được cài đặt hoặc khả dụng trong môi
    trường.
3.  Kiểm tra cấu trúc project hiện tại và các component/base class đã
    có.
4.  Kiểm tra các màn hình tương tự đã tồn tại để tái sử dụng convention.
5.  Chỉ sau khi hoàn thành các bước trên mới bắt đầu code.

Nếu yêu cầu của task mâu thuẫn với quy ước trong file này, **không tự ý
bỏ qua quy ước**. Phải ưu tiên kiến trúc và convention của project; chỉ
thay đổi khi task yêu cầu rõ ràng.

---

# 1. PONYTAIL --- ƯU TIÊN ĐỌC TRƯỚC KHI CODE

Ponytail là nguồn tham khảo ưu tiên đầu tiên khi Agent bắt đầu task.

Agent phải:

- Kiểm tra Ponytail/plugin/skill có khả dụng hay không.
- Đọc hướng dẫn và convention liên quan từ Ponytail trước khi triển
  khai.
- Ưu tiên các pattern, component, workflow và best practice mà
  Ponytail cung cấp.
- Không tự tạo một pattern mới nếu Ponytail hoặc project đã có pattern
  tương đương.
- Sau khi đọc Ponytail mới tiến hành kiểm tra code hiện tại.

**Không được bắt đầu code ngay chỉ dựa trên yêu cầu của user.**

---

# 2. UI DESIGN SYSTEM

Thiết kế UI của project sử dụng phong cách Material hiện đại, tối giản,
với bảng màu lấy theo design system hiện tại.

## 2.1. Color Palette

### Primary

```text
#F59E0B
```

Dùng cho:

- Primary button
- CTA
- Active state
- Highlight
- Điểm nhấn quan trọng
- Progress/indicator chính

### Secondary

```text
#1E293B
```

Dùng cho:

- Text chính
- Heading
- Nội dung quan trọng
- Icon chính
- Navigation text

### Tertiary

```text
#64748B
```

Dùng cho:

- Text phụ
- Hint
- Description
- Metadata
- Icon phụ
- Disabled/secondary information

### Neutral / Background

```text
#F8FAFC
```

Dùng cho:

- Background màn hình
- Surface sáng
- Container nền sáng

### On Primary

```text
#FFFFFF
```

Dùng cho:

- Text trên background/button Primary
- Icon trên Primary

---

# 3. QUY ƯỚC MÀU CHỮ

Không tự ý sử dụng màu chữ ngẫu nhiên trong XML.

Ưu tiên sử dụng color resource trong:

```text
res/values/colors.xml
```

Quy ước:

Mục đích Màu

---

Primary text `#1E293B`
Secondary text `#64748B`
Hint / Placeholder `#64748B`
Text trên Primary `#FFFFFF`
Background `#F8FAFC`
Accent / CTA `#F59E0B`

**Không hard-code màu trực tiếp trong layout XML nếu màu đó đã có trong
`colors.xml`.**

Ví dụ KHÔNG nên:

```xml
android:textColor="#1E293B"
```

Nếu color resource đã tồn tại, phải dùng:

```xml
android:textColor="@color/secondary"
```

---

# 4. QUY ƯỚC TYPOGRAPHY

Ưu tiên sử dụng font **Plus Jakarta Sans** nếu project đã có hoặc có thể
tích hợp theo convention hiện tại.

Không tự ý đặt `textSize` tùy tiện cho từng màn hình.

## Type scale

Thành phần Size

---

Screen Header **20sp**
Section Title 18sp
Body 16sp
Input text 16sp
Button text 14sp
Label 14sp
Secondary / Description 14sp
Caption 12sp

### Screen Header bắt buộc

Header của màn hình phải:

- `20sp`
- Title nằm **chính giữa theo chiều ngang**
- Không sử dụng `Toolbar`
- Có icon Back khi màn hình cần quay lại
- Icon Back có:

```xml
android:layout_width="wrap_content"
android:layout_height="wrap_content"
```

Không dùng kích thước cố định cho width/height của vùng Back icon nếu
không có yêu cầu đặc biệt.

---

# 5. HEADER --- KHÔNG DÙNG TOOLBAR

**TUYỆT ĐỐI KHÔNG sử dụng `Toolbar` cho header màn hình.**

Không tạo:

```xml
<Toolbar />
```

hoặc:

```xml
<androidx.appcompat.widget.Toolbar />
```

Header phải được xây dựng bằng layout thông thường, ưu tiên:

```xml
<LinearLayout>
```

hoặc layout tương thích với cấu trúc màn hình hiện tại.

Header tiêu chuẩn:

```text
┌─────────────────────────────────────┐
│  ←                 Title             │
└─────────────────────────────────────┘
```

Title phải nằm chính giữa màn hình.

Back icon:

```xml
android:layout_width="wrap_content"
android:layout_height="wrap_content"
```

Title:

```xml
android:textSize="20sp"
```

Không đặt title lệch theo vị trí của Back icon.

Nếu cần title luôn nằm chính xác giữa màn hình, phải thiết kế layout
header để vị trí title độc lập với độ rộng của icon bên trái.

---

# 6. INPUT FIELD --- BẮT BUỘC MATERIAL

Tất cả ô nhập liệu của project phải sử dụng Material component.

## Bắt buộc

Outer container:

```xml
<com.google.android.material.textfield.TextInputLayout>
```

Input:

```xml
<com.google.android.material.textfield.TextInputEditText>
```

Ví dụ:

```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</com.google.android.material.textfield.TextInputLayout>
```

## Không được sử dụng trực tiếp

```xml
<EditText />
```

hoặc:

```xml
<android.widget.EditText />
```

cho input thông thường của project.

Mọi input mới phải kiểm tra xem có thể dùng
`TextInputLayout + TextInputEditText` hay không trước khi tạo component
khác.

---

# 7. LAYOUT XML --- CHỈ DÙNG LINEARLAYOUT

Các file layout XML phải ưu tiên và mặc định sử dụng:

```xml
<LinearLayout>
```

**KHÔNG ĐƯỢC sử dụng `ConstraintLayout`.**

Không tạo:

```xml
<androidx.constraintlayout.widget.ConstraintLayout>
```

trong layout mới.

Nếu cần bố trí phức tạp, phải giải quyết bằng cách:

- `LinearLayout` ngang
- `LinearLayout` dọc
- `FrameLayout` khi thực sự cần overlay
- Các layout/component phù hợp khác khi có lý do kỹ thuật rõ ràng

Nhưng `ConstraintLayout` bị cấm trong layout XML của project.

---

# 8. CẤU TRÚC LAYOUT

Ưu tiên:

```xml
<LinearLayout
    android:orientation="vertical">

    <!-- Header -->

    <!-- Content -->

</LinearLayout>
```

Đối với nhóm component theo hàng:

```xml
<LinearLayout
    android:orientation="horizontal">
```

Không tạo hierarchy phức tạp nếu có thể giải quyết bằng cấu trúc
LinearLayout rõ ràng.

---

# 9. KHÔNG HARD-CODE DESIGN TOKEN

Không hard-code các design token trực tiếp trong Fragment/Activity nếu
project đã có resource tương ứng.

Không nên:

```java
textView.setTextSize(20);
```

Không nên:

```java
view.setBackgroundColor(Color.parseColor("#F59E0B"));
```

Không nên:

```xml
android:textSize="20sp"
```

nếu project đã định nghĩa style/dimension tương ứng để tái sử dụng.

Ưu tiên:

```text
colors.xml
dimens.xml
styles.xml
themes.xml
```

và các resource/style/component dùng chung của project.

---

# 10. ACTIVITY / FRAGMENT

Không nhồi toàn bộ UI và business logic vào một Activity.

Ưu tiên:

- Activity: điều phối màn hình/navigation.
- Fragment: quản lý UI của từng màn hình.
- ViewModel: state và UI logic nếu project sử dụng MVVM.
- Repository: data/business access.
- Adapter: riêng cho RecyclerView/List.
- Component/View riêng: cho UI component có khả năng tái sử dụng.

Không tạo một Activity khổng lồ chứa toàn bộ Fragment và logic của tất
cả màn hình.

---

# 11. REUSE COMPONENT

Trước khi tạo UI component mới, Agent phải tìm kiếm:

1.  Component tương tự trong project.
2.  Layout tương tự.
3.  Style hiện có.
4.  Color/dimension resource hiện có.
5.  Component được Ponytail khuyến nghị.

Nếu đã có component tương đương thì **tái sử dụng**, không duplicate
code.

---

# 12. QUY TRÌNH BẮT BUỘC TRƯỚC KHI CODE

Mỗi task phải thực hiện theo workflow:

### Step 1 --- Read Agent Rules

Đọc `AGENTS.md`.

### Step 2 --- Read Ponytail

Ưu tiên đọc Ponytail/plugin/skill liên quan.

### Step 3 --- Inspect Project

Kiểm tra:

- Project structure
- Existing Activity/Fragment
- Existing layout
- Existing components
- colors.xml
- dimens.xml
- styles.xml
- themes.xml
- Navigation
- Architecture hiện tại

### Step 4 --- Find Similar Implementation

Tìm màn hình/component tương tự đã tồn tại.

### Step 5 --- Plan

Xác định:

- File nào cần tạo
- File nào cần sửa
- Component nào có thể reuse
- UI structure
- Data flow
- Navigation

### Step 6 --- Implement

Chỉ bắt đầu code sau khi hoàn thành các bước trên.

### Step 7 --- Verify

Sau khi code phải kiểm tra:

- Có dùng Toolbar không?
- Header title có 20sp không?
- Header title có chính giữa không?
- Back icon có `wrap_content` không?
- Input có dùng `TextInputLayout + TextInputEditText` không?
- Có `ConstraintLayout` không?
- Có hard-code màu không?
- Có duplicate component không?
- Có vi phạm Ponytail/project convention không?

---

# 13. CHECKLIST TRƯỚC KHI HOÀN THÀNH TASK

- [ ] Đã đọc `AGENTS.md`.
- [ ] Đã ưu tiên đọc Ponytail.
- [ ] Đã kiểm tra code/component hiện có.
- [ ] Đã tái sử dụng component nếu có.
- [ ] Header không sử dụng Toolbar.
- [ ] Header title = `20sp`.
- [ ] Header title nằm chính giữa.
- [ ] Back icon sử dụng `wrap_content` cho width/height.
- [ ] Input sử dụng `TextInputLayout`.
- [ ] Input sử dụng `TextInputEditText`.
- [ ] Không dùng `EditText` trực tiếp.
- [ ] Layout XML không sử dụng `ConstraintLayout`.
- [ ] Layout XML ưu tiên `LinearLayout`.
- [ ] Màu sắc sử dụng design token/resource.
- [ ] Không hard-code màu khi đã có resource.
- [ ] Không tạo duplicate component.
- [ ] Code tuân thủ architecture hiện tại.
- [ ] Đã kiểm tra build/compile sau khi thay đổi.

---

# 14. QUY TẮC QUAN TRỌNG NHẤT

Nếu phải nhớ ngắn gọn, Agent phải tuân thủ 10 nguyên tắc sau:

1.  **Read `AGENTS.md` first.**
2.  **Read Ponytail first.**
3.  **Inspect existing project before coding.**
4.  **Reuse existing components.**
5.  **No Toolbar for screen headers.**
6.  **Header title = 20sp and centered.**
7.  **Back icon width/height = wrap_content.**
8.  **Every input = TextInputLayout + TextInputEditText.**
9.  **No ConstraintLayout. Use LinearLayout for XML layouts.**
10. **Follow the project's existing architecture and design system.**

# 15. DRAWABLE BACKGROUND — KHÔNG TẠO `bg_` LINH TINH

Không được tự ý tạo các file drawable có prefix:

```text
bg_
```

Ví dụ không được tự tiện tạo:

```text
bg_button.xml
bg_card.xml
bg_input.xml
bg_header.xml
bg_item.xml
bg_custom.xml
```

chỉ để phục vụ một UI component đơn lẻ nếu chưa có lý do rõ ràng.

Trước khi tạo một file `bg_`, Agent **bắt buộc phải kiểm tra**:

1. Drawable tương tự đã tồn tại trong project hay chưa.
2. Có thể sử dụng `shape`, `style`, `theme`, Material component hoặc resource hiện có hay không.
3. Có thể tái sử dụng drawable hiện tại hay không.
4. File drawable mới có thực sự cần thiết và được sử dụng ở nhiều nơi hay không.

### Quy tắc đặt tên

Chỉ tạo `bg_*.xml` khi:

- Có nhu cầu UI thực sự rõ ràng.
- Không thể tái sử dụng drawable/style hiện có.
- Tên file mô tả đúng mục đích.
- Drawable có khả năng được tái sử dụng hoặc có vai trò rõ ràng trong design system.

Không tạo drawable chỉ vì "tiện tay" hoặc để giải quyết nhanh một vấn đề layout.

### Ưu tiên

Ưu tiên theo thứ tự:

```text
Existing drawable
        ↓
Existing style/theme
        ↓
Material component
        ↓
Existing shape/style resource
        ↓
Tạo drawable mới nếu thực sự cần
```

**Không được tạo hàng loạt file `bg_*.xml` cho từng màn hình hoặc từng View một cách tùy tiện.**

Sau khi tạo drawable mới, Agent phải kiểm tra xem nó có thể tái sử dụng cho component khác hay không và tránh tạo các drawable trùng chức năng.
