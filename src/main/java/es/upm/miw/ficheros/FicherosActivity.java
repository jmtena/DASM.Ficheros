package es.upm.miw.ficheros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FicherosActivity extends AppCompatActivity {

    private String NOMBRE_FICHERO = "miFichero.txt";
    private String RUTA_FICHERO;         /** SD card o phone memory**/
    EditText lineaTexto;
    Button botonAniadir;
    TextView contenidoFichero;

    private Menu menu_ficheros;

    private static final int RESULT_SETTINGS = 1;

    @Override
    protected void onStart() {
        super.onStart();

        inicializar_fichero();
        inicializar_icono_destino();
        lineaTexto.setText("");
        mostrarContenido(contenidoFichero);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ficheros);

        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);

        inicializar_fichero();
        inicializar_icono_destino();
    }

    private void inicializar_fichero(){
        NOMBRE_FICHERO = getNombre_de_fichero();
        if (ExternalSdStorageActivated()) {
            /** SD card **/
            RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;
        }else {
            /** phone memory **/
            RUTA_FICHERO = getFilesDir() + "/" + NOMBRE_FICHERO;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        inicializar_icono_destino();
        return super.onPrepareOptionsMenu(menu);
    }

    private void inicializar_icono_destino(){
        if (menu_ficheros==null)
            return;

        MenuItem menuItem = menu_ficheros.findItem(R.id.icono_almacenamiento);
        if (ExternalSdStorageActivated()){
            menuItem.setIcon(R.drawable.sd);
        }
        else {
            menuItem.setIcon(R.drawable.memory);
        }
    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
    public void accionAniadir(View v) {
        try {  // Añadir al fichero
            if (ExternalSdStorageActivated()) {
                /** Comprobar estado SD card **/
                String estadoTarjetaSD = Environment.getExternalStorageState();
                RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {  /** SD card **/
                    escribirEnFichero(RUTA_FICHERO,true);
                }
            }
            else{
                RUTA_FICHERO = getFilesDir() + "/" + NOMBRE_FICHERO;
                escribirEnFichero(RUTA_FICHERO,true);
            }

        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void escribirEnFichero(String ruta, boolean append){
        try {
            FileOutputStream fos = new FileOutputStream(ruta, append);
            fos.write(lineaTexto.getText().toString().getBytes());
            fos.write('\n');
            fos.close();
        }catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        mostrarContenido(contenidoFichero);
        Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
    }

    private boolean ExternalSdStorageActivated(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getBoolean("sd_storage_active", true);
    }

    private String getNombre_de_fichero(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString("filename", null);
    }

    /**
     * Se pulsa sobre el textview -> mostrar contenido del fichero
     * Si está vacío -> mostrar un Toast
     *
     * @param textviewContenidoFichero TextView contenido del fichero
     */
    public void mostrarContenido(View textviewContenidoFichero) {
        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");
        try {
            boolean mostrar_mem = fichero.exists() && !ExternalSdStorageActivated();
            boolean mostrar_sd = fichero.exists() && ExternalSdStorageActivated() && estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED);
            if (mostrar_mem || mostrar_sd) {
                // BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //menu.add(Menu.NONE, 1, Menu.NONE, R.string.opcionVaciar)
        //        .setIcon(android.R.drawable.ic_menu_delete); // sólo visible android < 3.0

        // Inflador del menú: añade elementos a la action bar
        getMenuInflater().inflate(R.menu.menu, menu);

        menu_ficheros = menu;
        inicializar_icono_destino();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case 1:
            case R.id.accionVaciar:
                borrarContenido();
                break;
            //case 2:
            case R.id.action_settings:
                //****//
                Intent i = new Intent(this, UserSettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;
        }

        return true;
    }

    /**
     * Vaciar el contenido del fichero, la línea de edición y actualizar
     *
     */
    public void borrarContenido() {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {  // Vaciar el fichero
            boolean borrar_mem = !ExternalSdStorageActivated();
            boolean borrar_sd = ExternalSdStorageActivated() && estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED);
            if (borrar_mem || borrar_sd) {
                // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
