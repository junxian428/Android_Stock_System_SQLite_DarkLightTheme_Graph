package com.example.mprogramq2;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.mprogramq2.*;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private DBHelper db;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private Button btnAddSample, btnAdd;
    public Switch switchTheme;

    private BarChart barChart;
    private TextView tvCount;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);
        // ✅ Initialize DB first
        dbHelper = new DBHelper(this);
        barChart = findViewById(R.id.barChart);  // ✅ initialize
        tvCount = findViewById(R.id.tvCount);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddSample = findViewById(R.id.btnAddSample);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(db.getAllProducts(), this);
        recyclerView.setAdapter(adapter);
        switchTheme = findViewById(R.id.switchTheme);

        btnAdd.setOnClickListener(v -> showAddDialog());
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            Toast.makeText(this, "Theme will apply after restart", Toast.LENGTH_SHORT).show();
        });


        btnAddSample.setOnClickListener(v -> {
            // Add a few sample products (for demo/testing)
            addSampleProducts();
            refreshList();
        });
        loadChartData();

        refreshList();
    }

    private boolean isDarkTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }


    private void loadChartData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, price FROM products", null);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            float price = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));

            entries.add(new BarEntry(index, price));
            labels.add(name);
            index++;
        }

        cursor.close();
        db.close();

        // If no data → placeholder entry
        if (entries.isEmpty()) {
            entries.add(new BarEntry(0, 0));
            labels.add("No Data");
        }

        BarDataSet dataSet = new BarDataSet(entries, "Product Prices");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        // 🎨 Detect current theme (dark / light)
        int textColor = isDarkTheme() ? Color.WHITE : Color.BLACK;
        dataSet.setValueTextColor(textColor);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // 🧠 Chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setTextColor(textColor);

        // 🧭 X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(textColor);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 🧮 Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setGridColor(isDarkTheme() ? Color.GRAY : Color.LTGRAY);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setTextColor(textColor);
        rightAxis.setGridColor(isDarkTheme() ? Color.GRAY : Color.LTGRAY);

        barChart.animateY(800);
        barChart.invalidate();

        tvCount.setText("Products: " + entries.size());
    }


    private void updateBarChart() {
        // 1️⃣ Create a list of BarEntries
        List<BarEntry> entries = new ArrayList<>();

        // 2️⃣ Query prices from database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, price FROM products", null);

        int index = 0;
        while (cursor.moveToNext()) {
            float price = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));
            entries.add(new BarEntry(index++, price));
        }
        cursor.close();
        db.close();

        // 3️⃣ Check if empty
        if (entries.isEmpty()) {
            // Add a zero placeholder bar if no data
            entries.add(new BarEntry(0, 0));
        }

        // 4️⃣ Create dataset for chart
        BarDataSet dataSet = new BarDataSet(entries, "Product Prices");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        // 5️⃣ Bind to chart
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(800);
        barChart.invalidate();
        // 🎨 Detect current theme mode
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

// 🎨 Adjust chart colors based on theme
        int labelColor = isDarkTheme ? Color.WHITE : Color.BLACK;

// Set text and axis colors
        dataSet.setValueTextColor(labelColor);
        barChart.getXAxis().setTextColor(labelColor);
        barChart.getAxisLeft().setTextColor(labelColor);
        barChart.getAxisRight().setTextColor(labelColor);
        barChart.getLegend().setTextColor(labelColor);
        barChart.getDescription().setTextColor(labelColor);


        // 6️⃣ Update product count text
        tvCount.setText("Products: " + entries.size());
    }


    private void addSampleProducts() {
        // add few sample if table empty
        if (db.getAllProducts().size() == 0) {
            db.addProduct(new Product("Blue Widget", "SN-1001", "A blue widget", 9.99));
            db.addProduct(new Product("Red Widget", "SN-1002", "A red widget", 11.49));
            db.addProduct(new Product("Green Widget", "SN-1003", "A green widget", 7.25));
            Toast.makeText(this, "Sample products added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Products already exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshList() {
        List<Product> list = db.getAllProducts();
        adapter.setProducts(list);
        TextView tvCount = findViewById(R.id.tvCount);
        tvCount.setText("Products: " + list.size());
    }

    // Add new product dialog
    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        EditText etName = view.findViewById(R.id.etName);
        EditText etSerial = view.findViewById(R.id.etSerial);
        EditText etDesc = view.findViewById(R.id.etDescription);
        EditText etPrice = view.findViewById(R.id.etPrice);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Register New Product")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String serial = etSerial.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Name and price required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Product p = new Product(name, serial, desc, price);
                    long id = db.addProduct(p);
                    if (id == -1) {
                        Toast.makeText(this, "Could not add product (name may already exist)", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                    updateBarChart();

                })
                .setNegativeButton("Cancel", null)
                .create();
        dlg.show();
    }

    // Show details when clicked
    @Override
    public void onItemClick(Product product) {
        Product p = db.getProduct(product.getId());
        if (p == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            refreshList();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(p.getName()).append("\n\n");
        sb.append("Serial: ").append(p.getSerial()).append("\n\n");
        sb.append("Description: ").append(p.getDescription()).append("\n\n");
        sb.append("Price: ").append(String.format("RM %.2f", p.getPrice())).append("\n");

        new AlertDialog.Builder(this)
                .setTitle("Product Details")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .setNeutralButton("Update", (d, w) -> showUpdateDialog(p))
                .setNegativeButton("Remove (Sold out)", (d, w) -> {
                    db.deleteProduct(p.getId());
                    Toast.makeText(this, "Product removed", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .show();
        updateBarChart();

    }

    // long click to show options anchored menu
    @Override
    public void onItemLongClick(Product product, View anchor) {
        // show simple options: view, update, delete
        CharSequence[] opts = {"View details", "Update description/price", "Remove (sold out)"};
        new AlertDialog.Builder(this)
                .setTitle(product.getName())
                .setItems(opts, (dialog, which) -> {
                    if (which == 0) onItemClick(product);
                    else if (which == 1) showUpdateDialog(product);
                    else if (which == 2) {
                        db.deleteProduct(product.getId());
                        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                }).show();
        updateBarChart();

    }

    // Update dialog
    private void showUpdateDialog(Product product) {
        Product p = db.getProduct(product.getId());
        if (p == null) {
            Toast.makeText(this, "Product missing", Toast.LENGTH_SHORT).show();
            refreshList();
            return;
        }

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_update_product, null);
        EditText etDesc = v.findViewById(R.id.etDescriptionUpdate);
        EditText etPrice = v.findViewById(R.id.etPriceUpdate);

        etDesc.setText(p.getDescription());
        etPrice.setText(String.valueOf(p.getPrice()));
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Update " + p.getName())
                .setView(v)
                .setPositiveButton("Save", (d, w) -> {
                    String desc = etDesc.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    if (priceStr.isEmpty()) {
                        Toast.makeText(this, "Price required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    p.setDescription(desc);
                    p.setPrice(price);
                    db.updateProduct(p);
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
        updateBarChart();
    }
}