package ctj.celia.calculadorhoras;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class RegistroActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    public TextView datos;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    MyRecyclerViewAdapter adapter;

    String[] registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

  super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registro);
       //recuperamos el csv y lo visualizamos como RecyclerView
        Context contexto = getApplicationContext();

        File path = contexto.getFilesDir();
        String saveDir = path.toString();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String micsv = readFromFile(contexto);
        registros = micsv.split("\r\n");

        RecyclerView recyclerView = findViewById(R.id.rv_listado);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        List<String> arl = Arrays.asList(registros);
        adapter = new MyRecyclerViewAdapter(this, arl);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Click en item " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
    }
    private String readFromFile(Context context) {
        String ret = "";

 //leemos el csv
        try {
            InputStream inputStream = context.openFileInput("registros.csv");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                // StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    ret += receiveString + "\r\n";

                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }



}