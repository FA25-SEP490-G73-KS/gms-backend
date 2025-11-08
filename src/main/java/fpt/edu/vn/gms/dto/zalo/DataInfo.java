package fpt.edu.vn.gms.dto.zalo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataInfo {
    @SerializedName("sent_time")
    private String sentTime;

    @SerializedName("sending_mode")
    private String sendingMode;

    private Quota quota;

    @SerializedName("msg_id")
    private String msgId;

    @Data
    public static class Quota {
        @SerializedName("remainingQuota")
        private String remainingQuota;

        @SerializedName("dailyQuota")
        private String dailyQuota;
    }
}
