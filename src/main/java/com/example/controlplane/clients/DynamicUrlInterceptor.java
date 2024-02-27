package com.example.controlplane.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 7bin
 * @date 2024/02/26
 */
@Component
public class DynamicUrlInterceptor implements RequestInterceptor {

    private static final ThreadLocal<String> dynamicUrl = new ThreadLocal<>();

    public static void setDynamicUrl(String url) {
        clearDynamicUrl();
        dynamicUrl.set(url);
    }

    public static void clearDynamicUrl() {
        dynamicUrl.remove();
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = dynamicUrl.get();
        if (url != null) {
            requestTemplate.insert(0, url);
        }
    }
}
