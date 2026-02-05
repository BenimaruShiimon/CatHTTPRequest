package com.github.BenimaruShiimon.CatPost;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class CatHttp {
    public static final String CAT_API_URL = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final Logger log = Logger.getLogger(CatHttp.class.getName());
    static void main() {
        try {
            CloseableHttpClient httpClient = createConfiguredHttpClient();
            HttpGet request = new HttpGet(CAT_API_URL);
            request.setHeader("Accept", "application/json");
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                log.info("_______Анализируем HTTP ответ________");
                log.info("Стартовая строка " + response.getStatusLine());
                log.info("Заголовки: ");
                for (var header : response.getAllHeaders()) {
                    IO.println("HTTP HEADER: " + header.getName() + ": " + header.getValue());
                }
                String jsonResponse = EntityUtils.toString(response.getEntity());
                IO.println("Message body: " + jsonResponse);
                IO.println();
                List<Cat> cats = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                });
                List<Cat> filteredCats = cats.stream()
                        .filter(cat -> cat.getUpvotes() != null && cat.getUpvotes() > 0)
                        .toList();

                for (Cat cat : filteredCats) {
                    IO.println("id: " + cat.getId());
                    IO.println("text: " + cat.getText());
                    IO.println("type: " + cat.getType());
                    IO.println("user: " + cat.getUser());
                    IO.println("upvotes: " + cat.getUpvotes() + "\n");

                }
            } finally {
                response.close();
                httpClient.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }


    public static CloseableHttpClient createConfiguredHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }
}
