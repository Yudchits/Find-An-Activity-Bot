package by.yudchits.when_you_are_bored.service;

import by.yudchits.when_you_are_bored.client.Activity;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FindActivityServiceImpl implements FindActivityService{

    @Autowired
    private Activity activity;

    private String url = "http://www.boredapi.com/api/activity";

    @Override
    public String getActivity() {
        String json = activity.getJSONActivity(url);

        return parseJSON(json);
    }

    @Override
    public String getActivityByType(String type) {
        String json = activity.getJSONActivity(url + "?type=" + type);

        return parseJSON(json);
    }

    private String parseJSON(String json){
        JSONParser parser = new JSONParser();

        try {
            JSONObject object = (JSONObject) parser.parse(json);

            String activity =(String) object.get("activity");
            String type =(String) object.get("type");
            String link =(String) object.get("link");

            String text = """
                    activity : %s,
                    type: %s,
                    link: %s
                    """;

            return String.format(text, activity, type, link);
        } catch (ParseException e) {
            log.error("Occurred error: " + e.getMessage());
        }

        return null;
    }
}
