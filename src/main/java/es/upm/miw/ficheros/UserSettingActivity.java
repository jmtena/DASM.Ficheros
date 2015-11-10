package es.upm.miw.ficheros;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by JTENAESCOB001 on 10/11/2015.
 */
public class UserSettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.settings);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
