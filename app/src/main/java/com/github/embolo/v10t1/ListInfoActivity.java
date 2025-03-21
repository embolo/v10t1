package com.github.embolo.v10t1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListInfoActivity extends AppCompatActivity {
    private TextView CityText;
    private TextView YearText;
    private TextView CarInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        CityText = findViewById(R.id.CityText);
        YearText = findViewById(R.id.YearText);
        CarInfoText = findViewById(R.id.CarInfoText);
        CarDataStorage carDataStorage = CarDataStorage.getInstance();
        ArrayList<CarData> carData = carDataStorage.getCarData();

        String text = "";
        int sum = 0;
        for (CarData car : carData) {
            text += car.getType() + ": " + car.getAmount()+"\n";
            sum += car.getAmount();
        }
        text += "\nYhteensä: " + sum;

        CityText.setText(carDataStorage.getCity());
        YearText.setText(String.valueOf(carDataStorage.getYear()));
        CarInfoText.setText(text);

    }

}