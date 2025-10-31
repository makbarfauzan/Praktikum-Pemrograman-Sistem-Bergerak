package id.ac.unp.ft.informatika.ppsb.baseadapter;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listview1 = findViewById(R.id.listview1);

        ItemAdapter adapteritem1 = new ItemAdapter(this, Item.generateDateItem());
        listview1.setAdapter(adapteritem1);
    }
}
