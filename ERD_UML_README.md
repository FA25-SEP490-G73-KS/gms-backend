# ERD UML Diagrams - Hướng Dẫn Sử Dụng

## Tổng Quan

File này chứa các diagram UML cho ERD (Entity Relationship Diagram) của hệ thống GMS Backend.

## Các File UML

### 1. `ERD_UML_Standard.puml`
File PlantUML chuẩn ERD với:
- **Entities**: Các thực thể được đánh dấu `<<Entity>>`
- **Relationships**: Các bảng trung gian được đánh dấu `<<Relationship>>`
- **Cardinality**: Sử dụng ký hiệu chuẩn:
  - `||--o{` : 1-N (một-nhiều, tùy chọn)
  - `||--|{` : 1-N (một-nhiều, bắt buộc)
  - `||--o|` : 1-0..1 (một-không hoặc một)
  - `}o--o{` : N-N (nhiều-nhiều)
  - `}o--o|` : 0..1-1 (không hoặc một - một)

### 2. `ERD_UML.puml`
File PlantUML Class Diagram với package organization.

## Cách Sử Dụng

### 1. Xem Diagram Online

1. Truy cập: http://www.plantuml.com/plantuml/
2. Copy nội dung file `.puml`
3. Paste vào editor
4. Diagram sẽ được render tự động

### 2. Sử Dụng Plugin trong IDE

#### IntelliJ IDEA / Android Studio
1. Cài đặt plugin "PlantUML integration"
2. Mở file `.puml`
3. Diagram sẽ hiển thị tự động

#### VS Code
1. Cài đặt extension "PlantUML"
2. Mở file `.puml`
3. Nhấn `Alt+D` để preview

#### Eclipse
1. Cài đặt plugin "PlantUML Eclipse Plugin"
2. Mở file `.puml`
3. Right-click → "Open Diagram"

### 3. Export sang PNG/SVG

#### Sử dụng PlantUML CLI

```bash
# Cài đặt PlantUML
# Windows (với Chocolatey)
choco install plantuml

# macOS (với Homebrew)
brew install plantuml

# Linux
sudo apt-get install plantuml

# Export sang PNG
plantuml ERD_UML_Standard.puml

# Export sang SVG
plantuml -tsvg ERD_UML_Standard.puml

# Export với độ phân giải cao
plantuml -SDPI=300 ERD_UML_Standard.puml
```

#### Sử dụng Docker

```bash
docker run --rm -v "$(pwd):/work" plantuml/plantuml ERD_UML_Standard.puml
```

#### Sử dụng Online Service

1. Truy cập: https://www.plantuml.com/plantuml/uml/
2. Upload file `.puml`
3. Download diagram dưới dạng PNG/SVG

### 4. Tích Hợp vào Tài Liệu

#### Markdown
```markdown
![ERD Diagram](path/to/ERD_UML_Standard.png)
```

#### HTML
```html
<img src="path/to/ERD_UML_Standard.png" alt="ERD Diagram" />
```

#### Confluence
- Upload file PNG/SVG
- Hoặc sử dụng PlantUML macro

## Ký Hiệu Cardinality

| Ký Hiệu | Ý Nghĩa | Ví Dụ |
|---------|---------|-------|
| `||--o{` | 1-N (tùy chọn) | Một khách hàng có thể có nhiều xe |
| `||--|{` | 1-N (bắt buộc) | Một báo giá phải có nhiều hạng mục |
| `||--o|` | 1-0..1 (tùy chọn) | Một lịch hẹn có thể có một phiếu dịch vụ |
| `}o--o{` | N-N | Nhiều nhân viên nhận nhiều hạng mục xuất kho |
| `}o--o|` | 0..1-1 | Có những employee không có account |

## Cấu Trúc Entities

### Primary Key (PK)
- Được đánh dấu `*` và `<<PK>>`
- Ví dụ: `* customerId : BIGINT <<PK>>`

### Foreign Key (FK)
- Được đánh dấu `<<FK>>`
- Ví dụ: `customerId : BIGINT <<FK>>`

### Unique Key (UK)
- Được đánh dấu `<<UK>>`
- Ví dụ: `phone : VARCHAR <<UK>>`

## Lưu Ý

1. **File Size**: Diagram lớn có thể mất thời gian render
2. **Layout**: PlantUML tự động sắp xếp layout, có thể cần điều chỉnh thủ công
3. **Colors**: Có thể tùy chỉnh màu sắc trong file `.puml`
4. **Grouping**: Các entities được nhóm theo package để dễ quản lý

## Troubleshooting

### Diagram không hiển thị
- Kiểm tra syntax PlantUML
- Đảm bảo file có extension `.puml`
- Kiểm tra các ký tự đặc biệt trong tên entity

### Layout không đẹp
- Sử dụng `skinparam linetype ortho` cho đường thẳng
- Điều chỉnh `skinparam` để tùy chỉnh giao diện

### Export bị lỗi
- Kiểm tra Java đã được cài đặt (PlantUML cần Java)
- Kiểm tra đường dẫn file
- Thử export với format khác (PNG, SVG, PDF)

## Tài Liệu Tham Khảo

- [PlantUML Documentation](https://plantuml.com/)
- [PlantUML Class Diagram](https://plantuml.com/class-diagram)
- [PlantUML Entity Relationship](https://plantuml.com/iea-diagram)

