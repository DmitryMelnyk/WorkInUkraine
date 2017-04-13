package com.example;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHTTPSamples {

    OkHttpClient client = new OkHttpClient();
    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) // response OK
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    final static String URL = "https://hh.ua/search/vacancy?text=android&area=115";
    public static void main(String[] args) throws IOException, JSONException {
        OkHTTPSamples sample = new OkHTTPSamples();
        String response = sample.run(URL);
//        System.out.println(response);

        String text = "1 - Киев\n" +
                "2 - Львов\n" +
                "3 - Одесса\n" +
                "4 - Днепр (Днепропетровск)\n" +
                "5 - Винница\n" +
                "6 - Донецк\n" +
                "7 - Житомир\n" +
                "9 - Запорожье\n" +
                "10 - Ивано-Франковск\n" +
                "11 - Кропивницкий (Кировоград)\n" +
                "13 - Луганск\n" +
                "14 - Луцк\n" +
                "15 - Николаев\n" +
                "16 - Никополь\n" +
                "17 - Полтава\n" +
                "18 - Ровно\n" +
                "19 - Сумы\n" +
                "20 - Тернополь\n" +
                "21 - Харьков\n" +
                "22 - Херсон\n" +
                "23 - Хмельницкий\n" +
                "24 - Черкассы\n" +
                "25 - Чернигов\n" +
                "26 - Черновцы\n" +
                "27 - Мариуполь\n" +
                "28 - Ужгород\n" +
                "29 - Симферополь\n" +
                "31 - Кривой Рог\n" +
                "32 - Севастополь";

        String[] parsedString = text.split("\n");
        TreeMap<String, String> data = new TreeMap<String, String>();

        for (String couple : parsedString) {
            String[] pair = couple.split(" - ");
            String number = pair[0];
            String city = pair[1];

            data.put(city, number);
        }

//        for (String key : data.keySet()) {
//            System.out.println(key + " - ");
//        }

        JSONObject objectRabota = new JSONObject();
        for(String key : data.keySet()) {
            objectRabota.put(key, data.get(key));
        }

        String jobUa = "Винница - vinnica\n" +
                "Днепр (Днепропетровск) - dnepr\n" +
                "Донецк - donetsk\n" +
                "Житомир - zhitomir\n" +
                "Запорожье - zaporozhie\n" +
                "Ивано-Франковск - ivano-frankovsk\n" +
                "Киев - kiev\n" +
                "Кривой Рог - krivoy_rog\n" +
                "Кропивницкий (Кировоград) - krivoy_rog\n" +
                "Луганск - lugansk\n" +
                "Луцк - lutsk\n" +
                "Львов - lvov\n" +
                "Мариуполь - mariupol\n" +
                "Николаев - nikolaev\n" +
                "Никополь - nikopol\n" +
                "Одесса - odessa\n" +
                "Полтава - poltava\n" +
                "Ровно - rovno\n" +
                "Севастополь - sevastopol\n" +
                "Симферополь - simferopol\n" +
                "Сумы - sumi\n" +
                "Тернополь - ternopol\n" +
                "Ужгород - uzhgorod\n" +
                "Харьков - kharkov\n" +
                "Херсон - kherson\n" +
                "Хмельницкий - hmelnitsky\n" +
                "Черкассы - cherkassy\n" +
                "Чернигов - chernigov\n" +
                "Черновцы - chernovci";
        data.clear();
        JSONObject objectJobsUa = new JSONObject();

        String[] parsedString2 = jobUa.split("\n");
        for (String couple : parsedString2) {
            String[] pair = couple.split(" - ");
            objectJobsUa.put(pair[0], pair[1]);
//            data.put(pair[0], pair[1]);
        }

        String jobHH = "Винница - 116 \n" +
                "Днепр (Днепропетровск) - 117\n" +
                "Донецк - 118\n" +
                "Житомир - 119\n" +
                "Запорожье - 120\n" +
                "Ивано-Франковск - 121\n" +
                "Киев - 115\n" +
                "Кривой Рог - 2101\n" +
                "Кропивницкий (Кировоград) - 122\n" +
                "Луганск - 123\n" +
                "Луцк - 124\n" +
                "Львов - 125\n" +
                "Мариуполь - 2104\n" +
                "Николаев - 126\n" +
                "Никополь - 2131\n" +
                "Одесса - 127\n" +
                "Полтава - 128\n" +
                "Ровно - 129\n" +
                "Севастополь - 130\n" +
                "Симферополь - 131\n" +
                "Сумы - 132\n" +
                "Тернополь - 133\n" +
                "Ужгород - 134\n" +
                "Харьков - 135\n" +
                "Херсон - 136\n" +
                "Хмельницкий - 137\n" +
                "Черкассы - 138\n" +
                "Чернигов - 140\n" +
                "Черновцы - 139";
        String[] parsedStringHH = jobHH.split("\n");
        JSONObject objectHH = new JSONObject();
        for (String couple : parsedStringHH) {
            String[] pair = couple.split(" - ");
            objectHH.put(pair[0], pair[1]);
        }

        String jobWorkUa = "Винница - vinnytsya\n" +
                "Днепр (Днепропетровск) - dnipro \n" +
                "Донецк - donetsk\n" +
                "Житомир - zhytomyr\n" +
                "Запорожье - zaporizhzhya\n" +
                "Ивано-Франковск - ivano-frankivsk\n" +
                "Киев - kyiv\n" +
                "Кривой Рог - kryvyi_rih\n" +
                "Кропивницкий (Кировоград) - kropyvnytskyi\n" +
                "Луганск - luhansk\n" +
                "Луцк - lutsk\n" +
                "Львов - lviv\n" +
                "Мариуполь - mariupol\n" +
                "Николаев - Николаев\n" +
                "Никополь - nikopol\n" +
                "Одесса - odesa\n" +
                "Полтава - poltava\n" +
                "Ровно - rivne\n" +
                "Севастополь - sevastopol\n" +
                "Симферополь - simferopol\n" +
                "Сумы - sumy\n" +
                "Тернополь - ternopil\n" +
                "Ужгород - uzhhorod\n" +
                "Харьков - kharkiv\n" +
                "Херсон - kherson\n" +
                "Хмельницкий - khmelnytskyi\n" +
                "Черкассы - cherkasy\n" +
                "Чернигов - chernihiv\n" +
                "Черновцы - chernivtsi_cv";
        String[] parsedStringWorkUa = jobWorkUa.split("\n");
        JSONObject objectWorkUa = new JSONObject();
        for (String couple : parsedStringWorkUa) {
            String[] pair = couple.split(" - ");
            objectWorkUa.put(pair[0], pair[1]);
        }

        String jobWorkNewInfo = "Винница - 428\n" +
                "Днепр (Днепропетровск) - 417\n" +
                "Донецк - 418\n" +
                "Житомир - 429\n" +
                "Запорожье - 433\n" +
                "Ивано-Франковск - 421\n" +
                "Киев - 415\n" +
                "Кривой Рог - 508\n" +
                "Кропивницкий (Кировоград) - 437 \n" +
                "Луганск - 432\n" +
                "Луцк - 430\n" +
                "Львов - 414\n" +
                "Мариуполь - 495\n" +
                "Николаев - 422\n" +
                "Никополь - 510\n" +
                "Одесса - 419\n" +
                "Полтава - 427\n" +
                "Ровно - 431\n" +
                "Севастополь - 439\n" +
                "Симферополь - 438\n" +
                "Сумы - 423\n" +
                "Тернополь - 426\n" +
                "Ужгород - 436\n" +
                "Харьков - 416\n" +
                "Херсон - 435\n" +
                "Хмельницкий - 434\n" +
                "Черкассы - 425\n" +
                "Чернигов - 424\n" +
                "Черновцы - 420"; // TODO: add hear cities list
        String[] parsedStringWorkNewInfo = jobWorkNewInfo.split("\n");
        JSONObject objectWorkNewInfo = new JSONObject();
        for (String couple : parsedStringWorkNewInfo) {
            String[] pair = couple.split(" - ");
            objectWorkNewInfo.put(pair[0], pair[1]);
        }

        JSONObject all = new JSONObject();
        all.put("work.ua", objectWorkUa);
        all.put("rabota.ua", objectRabota);
        all.put("jobs.ua", objectJobsUa);
        all.put("hh.ua", objectHH);
        all.put("worknew.info", objectWorkNewInfo);
        // TODO: add cities from other sites

         //writing json to file
        try {
            FileWriter writer = new FileWriter("/home/dmitry/sites_key.json");
            writer.write(all.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
