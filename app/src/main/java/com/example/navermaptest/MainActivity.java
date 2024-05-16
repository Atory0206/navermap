package com.example.navermaptest;



import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private NaverMap naverMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });                         //  화면관리 도와줌 기본적으로 있던것

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();    // 지도객체
        }

        mapFragment.getMapAsync(this); //콜백 메서드 호출

    }
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // 네이버 지도가 준비되었을 때 호출되는 콜백 메서드입니다.
        // 여기에 지도 초기화 및 기타 작업을 수행합니다.

        LatLng coord = new LatLng(37.5670135, 126.9783740);
        LatLng coord2 = new LatLng(37.566, 126.9783740);

        Toast.makeText(getApplicationContext(),
                "위도: " + coord.latitude + ", 경도: " + coord.longitude,
                Toast.LENGTH_SHORT).show();                                   // 위치가 어딘지 메세지 표시

        UiSettings uiSettings = naverMap.getUiSettings(); //UI 활성화

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true); //  자기 위치 활성화

        uiSettings.setLocationButtonEnabled(true); // 자기 위치 버튼 활성화

        MyLocationSource locationSource = new MyLocationSource();
        naverMap.setLocationSource(locationSource);

        MarkerInfo markerInfo1 = new MarkerInfo("1빵", "도서관");
        Marker marker1 = new Marker();
        marker1.setPosition(coord);
        marker1.setMap(naverMap);      //마커 정보
        marker1.setTag(markerInfo1); //마커 객체 정보 연결


        MarkerInfo markerInfo2 = new MarkerInfo("2빵", "공대건물");
        Marker marker2 = new Marker();
        marker2.setPosition(coord2);
        marker2.setMap(naverMap);      //마커 정보
        marker2.setTag(markerInfo2); //마커 객체 정보 연결

        InfoWindow infoWindow = new InfoWindow();//인포윈도우객체생성



        naverMap.setOnMapClickListener((pointF, latLng) -> {
            infoWindow.close();
        });

// 마커를 클릭하면:
        Overlay.OnClickListener listener = overlay -> {
            Marker marker = (Marker) overlay;
            MarkerInfo info = (MarkerInfo) marker.getTag();

            if (marker.getInfoWindow() == null) {
                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                    @NonNull
                    @Override
                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
                        return info != null ? info.getTitle() + "\n" + info.getDescription() : "No info";
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