package com.example.atividade_maps;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Contato> contatos = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadContacts();
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

        for (final Contato c : contatos) {
            LatLng coords = new LatLng(c.getLatitude(), c.getLongitude());
            mMap.addMarker(new MarkerOptions().position(coords).title(c.getNome() + " - " + c.getEmail()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }


    public void loadContacts() {
        DAL dal = new DAL(MapsActivity.this);
        Cursor cursor = dal.loadAll();
        if (cursor.getCount() > 0) {
            do {
                Contato c = new Contato();
                c.setNome(cursor.getString(0));
                c.setEmail(cursor.getString(1));
                c.setLatitude(cursor.getDouble(2));
                c.setLongitude(cursor.getDouble(3));
                contatos.add(c);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        } else {
            cursor.close();
            try {
                JSONLoader loader = new JSONLoader();
                loader.downloadJSON();
                JSONArray locations = loader.loadJSONobject();

                for (int i = 0; i < locations.length(); i++) {
                    Contato c = new Contato();
                    c.nome = locations.getJSONObject(i).getString("nome");
                    c.email = locations.getJSONObject(i).getString("email");
                    c.latitude = locations.getJSONObject(i).getDouble("latitude");
                    c.longitude = locations.getJSONObject(i).getDouble("longitude");
                    dal.insert(c.getNome(), c.getEmail(), c.getLatitude(), c.getLongitude());
                    contatos.add(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
