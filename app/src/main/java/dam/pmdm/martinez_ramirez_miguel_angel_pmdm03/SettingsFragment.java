package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat eliminateModePref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Cargar las preferencias desde el archivo XML
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Configurar el Listener para el modo oscuro
        SwitchPreferenceCompat darkModePref = findPreference("dark_mode");
        if (darkModePref != null) {
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

            darkModePref.setChecked(isDarkMode);

            darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dark_mode", isEnabled);
                editor.apply();

                applyTheme(isEnabled);

                String toastMessage = isEnabled ? getString(R.string.dark_mode_activated) : getString(R.string.dark_mode_desactivated);
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();

                return true;
            });
        }

        // Configurar el Listener para la acción de "About"
        Preference aboutPref = findPreference("about");
        if (aboutPref != null) {
            aboutPref.setOnPreferenceClickListener(preference -> {
                showAboutDialog();
                return true;
            });
        }

        // Configurar el Listener para la acción de "Log Out"
        Preference logOutPref = findPreference("log_out");
        if (logOutPref != null) {
            logOutPref.setOnPreferenceClickListener(preference -> {
                logOut();
                return true;
            });
        }

        // Configurar el Switch para permitir eliminar Pokémon
        eliminateModePref = findPreference("eliminate_mode");
        if (eliminateModePref != null) {
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            boolean isEliminateModeEnabled = sharedPreferences.getBoolean("eliminate_mode", false);
            eliminateModePref.setChecked(isEliminateModeEnabled);

            eliminateModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("eliminate_mode", isEnabled);
                editor.apply();

                String toastMessage = getString(isEnabled ? R.string.eliminate_mode_on : R.string.eliminate_mode_off);
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();

                return true;
            });
        }

        // Configurar el Listener para el cambio de idioma
        ListPreference languagePref = findPreference("language");
        if (languagePref != null) {
            languagePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedLanguage = (String) newValue;

                // Cambiar el idioma
                setLocale(selectedLanguage);

                String toastMessage = getString(R.string.language_selected) + " " + getLanguageName(selectedLanguage);
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();

                return true;
            });
        }
    }

    // Método para aplicar el tema dinámicamente
    private void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (getActivity() != null) {
            getActivity().getWindow().setBackgroundDrawableResource(
                    isDarkMode ? R.color.backgroundColor : R.color.backgroundColorDarkTheme
            );
        }
    }

    // Método para mostrar información sobre la app
    private void showAboutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about)
                .setMessage(R.string.about_text)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), R.string.session_closed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    // Método para cambiar el idioma
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Recargar la actividad actual para aplicar el idioma
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
        }
    }

    // Método para obtener el nombre del idioma seleccionado
    private String getLanguageName(String languageCode) {
        switch (languageCode) {
            case "es":
                return getString(R.string.spanish);
            case "en":
                return getString(R.string.english);
            default:
                return getString(R.string.unknown_language);
        }
    }
}