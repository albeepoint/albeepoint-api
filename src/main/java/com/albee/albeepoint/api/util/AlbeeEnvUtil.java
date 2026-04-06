package com.albee.albeepoint.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlbeeEnvUtil {
    public static String timezone;

    @Value("${user.timezone}")
    public void setTimezone(String timezone){
        this.timezone = timezone;
    }

    public String getTimezone(){
        return timezone;
    }
}
