package util.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

public class ObjectMapperTuner {
    static {
        tuned = new ObjectMapper();
        tune();
    }
    @Getter
    private static final ObjectMapper tuned;

    private static void tune(){
        tuned.registerModule(new JavaTimeModule());
        tuned.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
