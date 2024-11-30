package com.example.splashscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UnitConverter extends AppCompatActivity {

    String[] lengthUnits = {"Inches", "Millimeters", "Feet", "Yards", "Meters"};
    String[] areaUnits = {"Laas", "Busal", "Rood", "Acres", "Square Feet", "Square Inches", "Square Centimeters", "Square Meters", "Hectares", "Perches"};

    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView2;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterItems2;

    EditText editTextNumber, editTextNumber2;
    Button button;
    RadioGroup radioGroup;
    RadioButton radioButtonLength, radioButtonArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.unitconverter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        autoCompleteTextView2 = findViewById(R.id.auto_complete_txt2);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        button = findViewById(R.id.button);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonLength = findViewById(R.id.radioButton);
        radioButtonArea = findViewById(R.id.radioButton2);

        // Set default adapters for length units
        setDropdownAdapters(lengthUnits);

        // Reset input and output fields when the radio button selection changes
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    // Length selected
                    setDropdownAdapters(lengthUnits);
                } else if (checkedId == R.id.radioButton2) {
                    // Area selected
                    setDropdownAdapters(areaUnits);
                }

                // Clear the input and output fields
                autoCompleteTextView.setText("");
                autoCompleteTextView2.setText("");
                editTextNumber.setText("");
                editTextNumber2.setText("");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void setDropdownAdapters(String[] units) {
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, units);
        adapterItems2 = new ArrayAdapter<>(this, R.layout.list_item, units);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView2.setAdapter(adapterItems2);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UnitConverter.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UnitConverter.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performConversion() {
        String unitFrom = autoCompleteTextView.getText().toString();
        String unitTo = autoCompleteTextView2.getText().toString();
        String input = editTextNumber.getText().toString();

        if (input.isEmpty() || unitFrom.isEmpty() || unitTo.isEmpty()) {
            Toast.makeText(UnitConverter.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double value = Double.parseDouble(input);
        double result = 0.0;

        if (radioButtonLength.isChecked()) {
            // Length conversion logic
            if (unitFrom.equals("Feet") && unitTo.equals("Meters")) {
                result = value * 0.3048;
            } else if (unitFrom.equals("Meters") && unitTo.equals("Feet")) {
                result = value / 0.3048;
            } else if (unitFrom.equals("Inches") && unitTo.equals("Millimeters")) {
                result = value * 25.4;
            } else if (unitFrom.equals("Millimeters") && unitTo.equals("Inches")) {
                result = value / 25.4;
            } else if (unitFrom.equals("Yards") && unitTo.equals("Meters")) {
                result = value * 0.9144;
            } else if (unitFrom.equals("Meters") && unitTo.equals("Yards")) {
                result = value / 0.9144;
            } else if (unitFrom.equals("Inches") && unitTo.equals("Meters")) {
                result = value * 0.0254;
            } else if (unitFrom.equals("Meters") && unitTo.equals("Inches")) {
                result = value / 0.0254;
            } else if (unitFrom.equals("Feet") && unitTo.equals("Inches")) {
                result = value * 12;
            } else if (unitFrom.equals("Inches") && unitTo.equals("Feet")) {
                result = value / 12;
            } else if (unitFrom.equals("Yards") && unitTo.equals("Inches")) {
                result = value * 36;
            } else if (unitFrom.equals("Inches") && unitTo.equals("Yards")) {
                result = value / 36;
            } else if (unitFrom.equals("Feet") && unitTo.equals("Millimeters")) {
                result = value * 304.8;
            } else if (unitFrom.equals("Millimeters") && unitTo.equals("Feet")) {
                result = value / 304.8;
            } else if (unitFrom.equals("Yards") && unitTo.equals("Millimeters")) {
                result = value * 914.4;
            } else if (unitFrom.equals("Millimeters") && unitTo.equals("Yards")) {
                result = value / 914.4;
            } else if (unitFrom.equals("Meters") && unitTo.equals("Millimeters")) {
                result = value * 1000;
            } else if (unitFrom.equals("Millimeters") && unitTo.equals("Meters")) {
                result = value / 1000;
            } else if (unitFrom.equals("Yards") && unitTo.equals("Feet")) {
                result = value * 3;
            } else if (unitFrom.equals("Feet") && unitTo.equals("Yards")) {
                result = value / 3;
            } else {
                result = value;
            }
        } else if (radioButtonArea.isChecked()) {
            // Area conversion logic
            if (unitFrom.equals("Acres") && unitTo.equals("Square Meters")) {
                result = value * 4046.86;
            } else if (unitFrom.equals("Square Meters") && unitTo.equals("Acres")) {
                result = value / 4046.86;
            } else if (unitFrom.equals("Square Feet") && unitTo.equals("Square Centimeters")) {
                result = value * 929.03;
            } else if (unitFrom.equals("Square Centimeters") && unitTo.equals("Square Feet")) {
                result = value / 929.03;
            } else if (unitFrom.equals("Square Meters") && unitTo.equals("Square Feet")) {
                result = value * 10.76;
            } else if (unitFrom.equals("Square Feet") && unitTo.equals("Square Meters")) {
                result = value / 10.76;
            } else if (unitFrom.equals("Square Centimeters") && unitTo.equals("Square Inches")) {
                result = value * 0.16;
            } else if (unitFrom.equals("Square Inches") && unitTo.equals("Square Centimeters")) {
                result = value / 0.16;
            } else if (unitFrom.equals("Hectares") && unitTo.equals("Acres")) {
                result = value * 2.47;
            } else if (unitFrom.equals("Acres") && unitTo.equals("Hectares")) {
                result = value / 2.47;
            } else if (unitFrom.equals("Hectares") && unitTo.equals("Square Meters")) {
                result = value * 10000;
            } else if (unitFrom.equals("Square Meters") && unitTo.equals("Hectares")) {
                result = value / 10000;
            } else if (unitFrom.equals("Perches") && unitTo.equals("Square Feet")) {
                result = value * 272.25;
            } else if (unitFrom.equals("Square Feet") && unitTo.equals("Perches")) {
                result = value / 272.25;
            } else if (unitFrom.equals("Rood") && unitTo.equals("Square Meters")) {
                result = value * 1011.71;
            } else if (unitFrom.equals("Square Meters") && unitTo.equals("Rood")) {
                result = value / 1011.71;
            } else if (unitFrom.equals("Busal") && unitTo.equals("Square Feet")) {
                result = value * 2112;
            } else if (unitFrom.equals("Square Feet") && unitTo.equals("Busal")) {
                result = value / 2112;
            } else if (unitFrom.equals("Laas") && unitTo.equals("Acres")) {
                result = value * 8;
            } else if (unitFrom.equals("Acres") && unitTo.equals("Laas")) {
                result = value / 8;
            } else if (unitFrom.equals("Laas") && unitTo.equals("Square Feet")) {
                result = value * 348480;
            } else if (unitFrom.equals("Square Feet") && unitTo.equals("Laas")) {
                result = value / 348480;
            } else {
                result = value;
            }
        }

        editTextNumber2.setText(String.valueOf(result));
    }
}
