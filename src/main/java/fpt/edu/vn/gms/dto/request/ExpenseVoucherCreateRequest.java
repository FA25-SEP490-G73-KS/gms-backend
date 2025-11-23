package fpt.edu.vn.gms.dto.request;


import lombok.Data;

@Data
public class ExpenseVoucherCreateRequest {

    /**
     * Nội dung phiếu chi, ví dụ:
     * "Thanh toán NCC cho vật tư: Dầu máy 5W-30"
     */
    private String description;

    /**
     * Nếu bạn cho kế toán upload file khác lúc này
     * (ví dụ ủy nhiệm chi scan) thì có thể nhận sẵn url ở đây.
     * Nếu không cần thì bỏ field này.
     */
    private String attachmentUrl;
}
