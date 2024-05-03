package util.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseForm {
    private String responseBody;
    private int responseCode;
}
