package org.evolve.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConstants {
    public static String APP_URL;

    @Value("${app.url}")
    public void setAppUrl(String appUrl) {
        APP_URL = appUrl;
    }
}
