package out;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Supplier;

public interface ResponseFormer {
    ResponseFormer DEFAULT_INSTANCE = new ResponseFormerImpl();
    ResponseForm getResponse(Runnable runnable);
    <T> ResponseForm getResponse(Supplier<T> supplier);

    static void writeResponse(ResponseForm responseForm, HttpServletResponse response) throws IOException {
        response.setStatus(responseForm.getResponseCode());
        if (responseForm.getResponseBody() == null) {
            return;
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();

        writer.print(responseForm.getResponseBody());
        writer.flush();
        writer.close();
    }
}
