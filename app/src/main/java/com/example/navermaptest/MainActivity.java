package com.example.navermaptest;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private boolean isMapInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button showMapButton = findViewById(R.id.button_show_map);
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.map).setVisibility(View.VISIBLE);
                if (!isMapInitialized) {
                    initializeMap();
                    isMapInitialized = true;
                }
            }
        });

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void initializeMap() {
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);

        LatLng coord = new LatLng(36.6521, 127.4949);
        LatLng coord2 = new LatLng(36.6520, 127.49701);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        MarkerInfo markerInfo1 = new MarkerInfo("1빵", "도서관", DetailActivity1.class);
        Marker marker1 = new Marker();
        marker1.setPosition(coord);
        marker1.setMap(naverMap);
        marker1.setTag(markerInfo1);

        MarkerInfo markerInfo2 = new MarkerInfo("2빵", "공대건물", DetailActivity2.class);
        Marker marker2 = new Marker();
        marker2.setPosition(coord2);
        marker2.setMap(naverMap);
        marker2.setTag(markerInfo2);

        Overlay.OnClickListener listener = overlay -> {
            Marker marker = (Marker) overlay;
            MarkerInfo info = (MarkerInfo) marker.getTag();

            Intent intent = new Intent(MainActivity.this, info.getDetailActivityClass());
            startActivity(intent);

            return true; // 마커 클릭 이벤트 소비
        };

        marker1.setOnClickListener(listener);
        marker2.setOnClickListener(listener);
    }

    static class MarkerInfo {
        private final String title;
        private final String description;
        private final Class<?> detailActivityClass;

        public MarkerInfo(String title, String description, Class<?> detailActivityClass) {
            this.title = title;
            this.description = description;
            this.detailActivityClass = detailActivityClass;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Class<?> getDetailActivityClass() {
            return detailActivityClass;
        }
    }
}