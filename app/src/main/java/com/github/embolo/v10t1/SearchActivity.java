package com.github.embolo.v10t1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {
    private CarDataStorage carDataStorage;
    private EditText CityNameEdit;
    private EditText YearEdit;
    private TextView StatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CityNameEdit = findViewById(R.id.CityNameEdit);
        YearEdit = findViewById(R.id.YearEdit);
        StatusText = findViewById(R.id.StatusText);
    }
    public void switchToListInfoActivity(View view) {
        Intent intent = new Intent(this, ListInfoActivity.class);
        startActivity(intent);
    }
    public void getDataButton(View view) {
        String city;
        int year;
        StatusText.setText("Haetaan");

        try {
            city = String.valueOf(CityNameEdit.getText());
            year = Integer.parseInt(String.valueOf(YearEdit.getText()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                getData(SearchActivity.this, city, year);
            }
        });

    }
    private void getData(Context context, String city, int year) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode areas = null;

        try {
            areas = objectMapper.readTree(new URL("https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        for (JsonNode node : areas.get("variables").get(0).get("values")) {
            values.add(node.asText());
        }
        for (JsonNode node : areas.get("variables").get(0).get("valueTexts")) {
            keys.add(node.asText());
        }

        HashMap<String, String> municipalityCodes = new HashMap<>();

        String cityCode;
        for (int i = 0; i < keys.size(); i++) {
            municipalityCodes.put(keys.get(i), values.get(i));
        }
        try {
            cityCode = municipalityCodes.get(city);
        } catch (Exception e) {
            runOnUiThread(() -> StatusText.setText("Haku epäonnistui, kaupunkia ei löytynyt"));
            return;
        }

        JsonNode jsonResponse;
        try {
            URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            JsonNode jsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query));

            ((ObjectNode) jsonInputString.get("query").get(0).get("selection")).putArray("values").add(cityCode);
            ((ObjectNode) jsonInputString.get("query").get(3).get("selection")).putArray("values").add(String.valueOf(year));
            ((ObjectNode) jsonInputString.get("query").get(2).get("selection")).putArray("values").add("0");

            byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
            OutputStream os = connection.getOutputStream();
            os.write(input, 0, input.length);


            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            jsonResponse = objectMapper.readTree(response.toString());
            os.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            runOnUiThread(() -> StatusText.setText("Haku epäonnistui, kaupunkia ei löytynyt"));
            return;
        } catch (ProtocolException e) {
            e.printStackTrace();
            runOnUiThread(() -> StatusText.setText("Haku epäonnistui, kaupunkia ei löytynyt"));
            return;
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> StatusText.setText("Haku epäonnistui, kaupunkia ei löytynyt"));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> StatusText.setText("Haku epäonnistui, kaupunkia ei löytynyt"));
            return;
        }
        runOnUiThread(() -> StatusText.setText("Haku onnistui"));

        carDataStorage = CarDataStorage.getInstance();
        carDataStorage.clearData();

        jsonResponse.get("query").get(1).get("values").get("01");
        /*for (int i = 0; i < 5; i++) {
            jsonResponse.get("query").get(1).get("values").get(01);
        }*/
        //CarData carData = new CarData()


    }
}