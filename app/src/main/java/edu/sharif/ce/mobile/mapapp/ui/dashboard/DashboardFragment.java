package edu.sharif.ce.mobile.mapapp.ui.dashboard;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;


import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber;
import edu.sharif.ce.mobile.mapapp.model.utils.NetworkInterface;
import edu.sharif.ce.mobile.mapapp.ui.home.BookmarkRecyclerViewAdapter;
import edu.sharif.ce.mobile.mapapp.ui.home.HomeFragment;

import static android.app.Activity.RESULT_OK;


public class DashboardFragment extends Fragment implements OnMapReadyCallback, PermissionsListener, GPSCallback {
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MarkerViewManager markerViewManager;
    private MapView mapView;
    private GPSManager gpsManager = null;
    private TextView mySpeedText;
    private AutoCompleteTextView autoCompleteTextView;
    private static final ArrayList<String> searchBookmarks = new ArrayList<>();
    private PlaceAdapter searchAdapter;
    public static final Integer RecordAudioRequestCode = 1;
    public static final int REQUEST_CODE_SPEECH_INTENT = 1000;
    private ImageView speechToTextImg;
    private SpeechRecognizer speechRecognizer;


    private final DashboardFragment.WeakHandler handler = new DashboardFragment.WeakHandler(this);

    private static class WeakHandler extends Handler implements Subscriber {
        private final WeakReference<DashboardFragment> fragment;

        public WeakHandler(DashboardFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DashboardFragment fragment = this.fragment.get();
            if (fragment != null && msg.what == NotificationID.TopRelatedSearches.NEW_DATA_LOADED_FOR_UI) {
                fragment.notifyDataSetChanged();
            }
        }
    }

    public void notifyDataSetChanged() {
        searchBookmarks.clear();
        searchBookmarks.addAll(NetworkInterface.searchNames);
        searchAdapter.notifyDataSetChanged();
        autoCompleteTextView.showDropDown();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));


        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        NotificationCenter.registerForNotification(this.handler, NotificationID.TopRelatedSearches.NEW_DATA_LOADED_FOR_UI);

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mySpeedText = root.findViewById(R.id.mySpeed);

        root.findViewById(R.id.locNow).setOnClickListener(view -> {
            Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            if (lastKnownLocation != null) {
                animateCamera(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
        });
        autoCompleteTextView = root.findViewById(R.id.autoCompleteTextView);
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getCurrentSpeed(getView());

        ImageView imageView = root.findViewById(R.id.searchLocation);
        imageView.setOnClickListener(view -> {
            String text = autoCompleteTextView.getText().toString();
            Bookmark bookmark = Bookmark.getBookmarkByName(text, NetworkInterface.searchBookmarks);
            if (bookmark == null) NetworkInterface.getLocData(text);
            else showBookMark(bookmark);
        });

        speechToTextImg = root.findViewById(R.id.voiceImg);

        if (getActivity() != null &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        speechToTextImg.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INTENT);
            } else {
                Toast.makeText(getActivity(), "Your device doesn't support speech input", Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    RecordAudioRequestCode);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchAdapter = new PlaceAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, searchBookmarks);
        autoCompleteTextView.setAdapter(searchAdapter);
        autoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> {
            String selected = (String) adapterView.getItemAtPosition(i);
            Bookmark bookmark = Bookmark.getBookmarkByName(selected, NetworkInterface.searchBookmarks);
            showBookMark(bookmark);
        });
    }

    public void getCurrentSpeed(View view) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        gpsManager = new GPSManager(getContext());
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            gpsManager.startListening(getActivity().getApplicationContext());
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
    }

    public void showBookMark(Bookmark bookmark) {
        mapboxMap.clear();
        IconFactory iconFactory = IconFactory.getInstance(getContext());
        Icon icon = iconFactory.fromResource(R.drawable.marker_red3);
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(bookmark.getLat(), bookmark.getLon())).icon(icon));
        animateCamera(bookmark.getLat(), bookmark.getLon());
    }

    public void animateCamera(double lat, double lon) {
        CameraPosition position = new CameraPosition.Builder().target(new LatLng(lat, lon))
                .zoom(15).bearing(0).tilt(0)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                DashboardFragment.this.getActivity().runOnUiThread(() -> {
                    DashboardFragment.this.mapboxMap = mapboxMap;
                    DashboardFragment.this.markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                    if (getArguments() != null) {
                        showBookMark((Bookmark) getArguments().getSerializable("bookmark"));
                    }

                    mapboxMap.addOnMapLongClickListener(point -> {
                        mapboxMap.clear();
                        IconFactory iconFactory = IconFactory.getInstance(getContext());
                        Icon icon = iconFactory.fromResource(R.drawable.marker_red3);

                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).icon(icon));

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        final EditText edittext = new EditText(getContext());
                        edittext.setPadding(40, 10, 10, 10);
                        edittext.setHint("Type location name here");
                        edittext.setMaxLines(1);
                        alert.setMessage("Location Name");
                        DecimalFormat df = new DecimalFormat("#.##");

                        alert.setTitle("Save Location (" + df.format(point.getLatitude()) + ", " + df.format(point.getLongitude()) + ")");
                        alert.setView(edittext);

                        alert.setPositiveButton("Save", (dialog, whichButton) -> {
                            String editTextValue = edittext.getText().toString();
                            Bookmarker.insertBookmark(getContext(), editTextValue, point.getLatitude(), point.getLongitude());
                        });

                        alert.setNegativeButton("Cancel", (dialog, whichButton) -> mapboxMap.clear());

                        AlertDialog dialog = alert.create();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

                        layoutParams.gravity = Gravity.BOTTOM;
                        layoutParams.verticalMargin = 0.08F;

                        dialog.show();

                        return true;
                    });

                    mapboxMap.setStyle(Style.OUTDOORS, DashboardFragment.this::enableLocationComponent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getActivity(), loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), "on explanation needed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) mapboxMap.getStyle(this::enableLocationComponent);
        else getActivity().finish();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (markerViewManager != null) {
            markerViewManager.onDestroy();
        }
        if (mapView != null) mapView.onDestroy();
        if (gpsManager != null) {
            gpsManager.stopListening();
            gpsManager.setGPSCallback(null);
            gpsManager = null;
        }

        if (speechRecognizer != null) speechRecognizer.destroy();
    }


    @Override
    public void onGPSUpdate(Location location) {
        double speed = location.getSpeed();
        double currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        double kmphSpeed = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
        Log.d("speed", kmphSpeed + "km/h");
        mySpeedText.setText(String.format("%s km/h", kmphSpeed));
    }

    public static double round(double unRounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unRounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INTENT) {
            if (data != null && resultCode == RESULT_OK) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                autoCompleteTextView.setText(result.get(0));
            }
        }
    }


}