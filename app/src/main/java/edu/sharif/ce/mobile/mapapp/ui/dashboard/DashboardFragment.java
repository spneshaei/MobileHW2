package edu.sharif.ce.mobile.mapapp.ui.dashboard;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;

import java.util.List;

import edu.sharif.ce.mobile.mapapp.MainActivity;
import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;

public class DashboardFragment extends Fragment implements OnMapReadyCallback, PermissionsListener {
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MarkerViewManager markerViewManager;
    private MapView mapView;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));


        this.root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        root.findViewById(R.id.locNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();

                if (lastKnownLocation != null) {

                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())) // Sets the new camera position
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 1000);
                }

            }
        });

        return root;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        DashboardFragment.this.mapboxMap = mapboxMap;
        DashboardFragment.this.markerViewManager = new MarkerViewManager(mapView, mapboxMap);


        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
            @Override
            public boolean onMapLongClick(@NonNull LatLng point) {
                mapboxMap.clear();
                IconFactory iconFactory = IconFactory.getInstance(getContext());
                Icon icon = iconFactory.fromResource(R.drawable.marker_red3);
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).icon(icon));
//                View customView = LayoutInflater.from(getActivity()).inflate(
//                        R.layout.fragment_dashboard, null);
//                MarkerView markerView = new MarkerView(point, customView);
//                markerViewManager.addMarker(markerView);
//                markerViewManager.removeMarker(markerView);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getContext());
                alert.setMessage("Location Name");
                alert.setTitle("Save Location (" + point.getLatitude() + ", " + point.getAltitude() + ")");
                alert.setView(edittext);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = edittext.getText().toString();
                        Bookmarker.insertBookmark(getContext(), editTextValue, point.getLatitude(), point.getAltitude());
                    }
                });

                alert.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mapboxMap.clear();
                    }
                });

                alert.show();

                return true;
            }
        });

        mapboxMap.setStyle(Style.OUTDOORS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
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
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            getActivity().finish();
        }
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
        mapView.onDestroy();
    }


}