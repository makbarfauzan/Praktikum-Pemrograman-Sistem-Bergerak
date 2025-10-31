package id.ac.unp.ft.informatika.ppsb.constrainlayoutbaseadapter;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Item item1 = new Item("Laptop", "Laptop LENOVO LOQ");
        Item item2 = new Item("Keyboard", "Keyboard Razer");
        Item item3 = new Item("Mouse", "Mouse ROG");
        Item item4 = new Item("Laptop", "Laptop LENOVO LOQ");
        Item item5 = new Item("Keyboard", "Keyboard Razer");
        Item item6 = new Item("Mouse", "Mouse ROG");
        Item item7 = new Item("Laptop", "Laptop LENOVO LOQ");
        Item item8 = new Item("Keyboard", "Keyboard Razer");
        Item item9 = new Item("Mouse", "Mouse ROG");
        Item item10 = new Item("Laptop", "Laptop LENOVO LOQ");
        Item item11 = new Item("Keyboard", "Keyboard Razer");
        Item item12 = new Item("Mouse", "Mouse ROG");
        Item item13 = new Item("Laptop", "Laptop LENOVO LOQ");
        Item item14 = new Item("Keyboard", "Keyboard Razer");
        Item item15 = new Item("Mouse", "Mouse ROG");

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);
        items.add(item9);
        items.add(item10);
        items.add(item11);
        items.add(item12);
        items.add(item13);
        items.add(item14);
        items.add(item15);

        ItemAdapter itemAdapter = new ItemAdapter(this, items);

        ListView listview = findViewById(R.id.listview);
        listview.setAdapter(itemAdapter);
    }
}
