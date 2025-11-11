package fpt.edu.vn.gms.dto.zalo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendZnsPayload {

    private String phone;

    @JsonProperty("template_id")
    @SerializedName("template_id")
    private String templateId;

    @JsonProperty("template_data")
    @SerializedName("template_data")
    private Map<String, Object> templateData;

    @JsonProperty("tracking_id")
    @SerializedName("tracking_id")
    private String trackingId;
}