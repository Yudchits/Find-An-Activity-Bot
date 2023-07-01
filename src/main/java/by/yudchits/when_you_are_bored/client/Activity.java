package by.yudchits.when_you_are_bored.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Activity {

    @Autowired
    private OkHttpClient client;

    @Value("${activity.url}")
    private String URL;

    public String getJSONActivity(){
        var request = new Request.Builder()
                .url(URL)
                .build();

        try(var respond = client.newCall(request).execute()){
            var body = respond.body();

            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
