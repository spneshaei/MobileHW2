package edu.sharif.ce.mobile.mapapp.ui.notifications;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.UUID;

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;

public class NotificationsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
     }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        Preference preference = getPreferenceManager().findPreference("clearData");
        preference.setOnPreferenceClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void showEraseDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setTitle("Clear All Data")
                .setMessage("All your data will be erased. Proceed?")
                .setPositiveButton("Yes", (dialogInterface, i) -> eraseAllData());
        dialogBuilder.show();
    }

    private void eraseAllData () {
        Bookmarker.deleteAllBookmarks(getContext());
        getPreferenceScreen().getSharedPreferences()
                .edit().putBoolean("darkMode", false).apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!key.equals("darkMode")) return;
        if (sharedPreferences.getBoolean(key, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("clearData")) {
            showEraseDialog();
            return true;
        }
        return false;
    }
}