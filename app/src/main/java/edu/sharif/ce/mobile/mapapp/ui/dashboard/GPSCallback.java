package edu.sharif.ce.mobile.mapapp.ui.dashboard;

import android.location.Location;

public interface GPSCallback
{
    public abstract void onGPSUpdate(Location location);
}