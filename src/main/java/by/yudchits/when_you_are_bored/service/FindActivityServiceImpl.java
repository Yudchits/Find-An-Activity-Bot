package by.yudchits.when_you_are_bored.service;

import by.yudchits.when_you_are_bored.client.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindActivityServiceImpl implements FindActivityService{

    @Autowired
    private Activity activity;

    @Override
    public String getActivity() {
        return activity.getActivity();
    }
}
