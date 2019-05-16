package com.example.atividade_maps;

import android.database.Cursor;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    JSONArray locations;
    JSONLoader loader = new JSONLoader();
    ArrayList<Contato> contatos = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader.downloadJSON();
        locations = loader.loadJSONobject();
        loadContacts();
        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng catolica = new LatLng(-26.466535, -49.114006);
        mMap.addMarker(new MarkerOptions().position(catolica).title("Local onde foi desenvolvido o app")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(catolica, 9));

        for (Contato c : contatos) {
            LatLng coords = new LatLng(c.getLatitude(), c.getLongitude());
            mMap.addMarker(new MarkerOptions().position(coords).title(c.getNome() + " - " + c.getEmail()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

    }

    public void loadJsonToDB() {
        try {
            DAL dal = new DAL(MapsActivity.this);
            for (int i = 0; i < locations.length(); i++) {
                Contato c = new Contato();
                c.nome = locations.getJSONObject(i).getString("nome");
                c.email = locations.getJSONObject(i).getString("email");
                c.latitude = locations.getJSONObject(i).getDouble("latitude");
                c.longitude = locations.getJSONObject(i).getDouble("longitude");
                dal.insert(c.getNome(), c.getEmail(), c.getLatitude(), c.getLatitude());
                contatos.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadContacts() {
        DAL dal = new DAL(MapsActivity.this);
        Cursor cursor = dal.loadAll();
        if (cursor.getCount() > 0) {
            do{
                Contato c = new Contato();
                c.setNome(cursor.getString(1));
                c.setEmail(cursor.getString(2));
                c.setLatitude(cursor.getDouble(3));
                c.setLongitude(cursor.getDouble(4));
                contatos.add(c);
                cursor.moveToNext();
            }while(cursor.moveToNext());
        }else{
            cursor.close();
            loadJsonToDB();
        }

    }
}
