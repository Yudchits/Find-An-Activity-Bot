package by.yudchits.when_you_are_bored.client;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class Activity {

    @Autowired
    private OkHttpClient client;

    public String getJSONActivity(String url) {
        var request = new Request.Builder()
                .url(url)
                .build();

        try (var respond = client.newCall(request).execute()) {
            var body = respond.body();

            return body == null ? null : body.string();
        } catch (IOException e) {
            log.error("Occurred error: " + e.getMessage());
        }

        return null;
    }
}
