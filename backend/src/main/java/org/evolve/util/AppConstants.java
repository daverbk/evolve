package org.evolve.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConstants {
    public static String APP_URL;
    public static String EVOLVE_MAILBOX;

    @Value("${app.url}")
    public void setAppUrl(String appUrl) {
        APP_URL = appUrl;
    }

    @Value("${spring.mail.username}")
    public void setEvolveMailbox(String evolveMailbox) {
        EVOLVE_MAILBOX = evolveMailbox;
    }
}
