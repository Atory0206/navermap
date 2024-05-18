package com.example.navermaptest;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

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
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private NaverMap naverMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;


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

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
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

        LatLng coord = new LatLng(37.5670135, 126.9783740);
        LatLng coord2 = new LatLng(37.566, 126.9783740);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        MarkerInfo markerInfo1 = new MarkerInfo("1빵", "도서관");
        Marker marker1 = new Marker();
        marker1.setPosition(coord);
        marker1.setMap(naverMap);
        marker1.setTag(markerInfo1);

        MarkerInfo markerInfo2 = new MarkerInfo("2빵", "공대건물");
        Marker marker2 = new Marker();
        marker2.setPosition(coord2);
        marker2.setMap(naverMap);
        marker2.setTag(markerInfo2);

        Button detailButton = new Button(this);
        detailButton.setText("상세페이지로 이동");

        InfoWindow infoWindow = new InfoWindow();



        Overlay.OnClickListener listener = overlay -> {
            Marker marker = (Marker) overlay;
            MarkerInfo info = (MarkerInfo) marker.getTag();

            if (marker.getInfoWindow() == null) {
                infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
                    @NonNull
                    @Override
                    protected View getContentView(@NonNull InfoWindow infoWindow) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_info_window, null);
                        TextView textView = view.findViewById(R.id.text_info);
                        Button detailButton = view.findViewById(R.id.button_detail);


                        textView.setText(info != null ? info.getTitle() + "\n" + info.getDescription() : "No info");

                        // 상세페이지 버튼 클릭 시 동작 설정
                        detailButton.setOnClickListener(v -> {
                            // 버튼 클릭 시 상세페이지로 이동하는 기능
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            startActivity(intent);
                            infoWindow.close(); // 상세페이지로 이동한 후 인포 윈도우를 닫음
                        });


                        return view;
                    }
                });
                infoWindow.open(marker);
            } else {
                infoWindow.close();
            }
            return true;
        };

        marker1.setOnClickListener(listener);
        marker2.setOnClickListener(listener);

    }

    static class MarkerInfo {
        private final String title;
        private final String description;

        public MarkerInfo(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}