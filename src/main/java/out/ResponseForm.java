package out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseForm {
    public static final ResponseForm URI_IS_NOT_FOUND = new ResponseForm(
            "{\"Requested URI is not found.\"}", 404);
    public static final ResponseForm METHOD_IS_NOT_ALLOWED = new ResponseForm(
            "{\"Method is not allowed.\"}", 405);
    public static final ResponseForm NECESSARY_PARAMETER_MISSING = new ResponseForm(
            "{\"One or several necessary request parameters are missing.\"}", 400);
    private String responseBody;
    private int responseCode;
}
