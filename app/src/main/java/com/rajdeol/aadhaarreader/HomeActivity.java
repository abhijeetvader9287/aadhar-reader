package com.rajdeol.aadhaarreader;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rajdeol.aadhaarreader.utils.DataAttributes;
import com.rajdeol.aadhaarreader.utils.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Home activity.
 */
@SuppressWarnings("ConstantConditions")
public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    // variables to store extracted xml data
    private String uid;
    private String name;
    private String gender;
    private String yearOfBirth;
    private String careOf;
    private String villageTehsil;
    private String postOffice;
    private String district;
    private String state;
    private String postCode;
    private String dateOfBirth;
    // UI Elements
    private TextView tv_sd_uid;
    private TextView tv_sd_name;
    private TextView tv_sd_gender;
    private TextView tv_sd_yob;
    private TextView tv_sd_co;
    private TextView tv_sd_vtc;
    private TextView tv_sd_po;
    private TextView tv_sd_dist;
    private TextView tv_sd_dob;
    private TextView tv_sd_state;
    private TextView tv_sd_pc;
    private TextView tv_cancel_action;
    private LinearLayout ll_scanned_data_wrapper;
    private LinearLayout ll_data_wrapper;
    private LinearLayout ll_action_button_wrapper;
    // Storage
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide the default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_home);
        // init the UI Elements
        tv_sd_uid = findViewById(R.id.tv_sd_uid);
        tv_sd_name = findViewById(R.id.tv_sd_name);
        tv_sd_gender = findViewById(R.id.tv_sd_gender);
        tv_sd_yob = findViewById(R.id.tv_sd_yob);
        tv_sd_co = findViewById(R.id.tv_sd_co);
        tv_sd_vtc = findViewById(R.id.tv_sd_vtc);
        tv_sd_po = findViewById(R.id.tv_sd_po);
        tv_sd_dist = findViewById(R.id.tv_sd_dist);
        tv_sd_state = findViewById(R.id.tv_sd_state);
        tv_sd_pc = findViewById(R.id.tv_sd_pc);
        tv_cancel_action = findViewById(R.id.tv_cancel_action);
        tv_sd_dob = findViewById(R.id.tv_sd_dob);
        ll_scanned_data_wrapper = findViewById(R.id.ll_scanned_data_wrapper);
        ll_data_wrapper = findViewById(R.id.ll_data_wrapper);
        ll_action_button_wrapper = findViewById(R.id.ll_action_button_wrapper);
        //init storage
        storage = new Storage(this);
    }

    /**
     * Scan now.
     */
    public void scanNow(View view) {
        if (checkAndRequestPermissions()) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan a Aadharcard QR Code");
            integrator.setResultDisplayDuration(500);
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.initiateScan();
        }
    }
    private boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionwifi = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE);
        int phonestateCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int readStoragePermission = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (phonestateCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionwifi != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            // process received data
            processScannedData(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void processScannedData(String scanData) {
        Log.d("Rajdeol", scanData);
        XmlPullParserFactory pullParserFactory;
        try {
            // init the parserfactory
            pullParserFactory = XmlPullParserFactory.newInstance();
            // get the parser
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData));
            // parse the XML
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("Rajdeol", "Start document");
                } else if (eventType == XmlPullParser.START_TAG && DataAttributes.AADHAAR_DATA_TAG.equals(parser.getName())) {
                    // extract data from tag
                    //uid
                    uid = parser.getAttributeValue(null, DataAttributes.AADHAR_UID_ATTR);
                    //name
                    name = parser.getAttributeValue(null, DataAttributes.AADHAR_NAME_ATTR);
                    //gender
                    gender = parser.getAttributeValue(null, DataAttributes.AADHAR_GENDER_ATTR);
                    // year of birth
                    yearOfBirth = parser.getAttributeValue(null, DataAttributes.AADHAR_YOB_ATTR);
                    // care of
                    careOf = parser.getAttributeValue(null, DataAttributes.AADHAR_CO_ATTR);
                    // village Tehsil
                    villageTehsil = parser.getAttributeValue(null, DataAttributes.AADHAR_VTC_ATTR);
                    // Post Office
                    postOffice = parser.getAttributeValue(null, DataAttributes.AADHAR_PO_ATTR);
                    // district
                    district = parser.getAttributeValue(null, DataAttributes.AADHAR_DIST_ATTR);
                    // state
                    state = parser.getAttributeValue(null, DataAttributes.AADHAR_STATE_ATTR);
                    // Post Code
                    postCode = parser.getAttributeValue(null, DataAttributes.AADHAR_PC_ATTR);
                    //dob
                    dateOfBirth = parser.getAttributeValue(null, DataAttributes.AADHAR_DOB_ATTR);
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("Rajdeol", "End tag " + parser.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("Rajdeol", "Text " + parser.getText());
                }
                // update eventType
                eventType = parser.next();
            }
            // display the data on screen
            displayScannedData();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// EO function
    private void displayScannedData() {
        ll_data_wrapper.setVisibility(View.GONE);
        ll_scanned_data_wrapper.setVisibility(View.VISIBLE);
        ll_action_button_wrapper.setVisibility(View.VISIBLE);
        // clear old values if any
        tv_sd_uid.setText("");
        tv_sd_name.setText("");
        tv_sd_gender.setText("");
        tv_sd_yob.setText("");
        tv_sd_co.setText("");
        tv_sd_vtc.setText("");
        tv_sd_po.setText("");
        tv_sd_dist.setText("");
        tv_sd_state.setText("");
        tv_sd_pc.setText("");
        tv_sd_dob.setText("");
        // update UI Elements
        tv_sd_uid.setText(uid);
        tv_sd_name.setText(name);
        tv_sd_gender.setText(gender);
        tv_sd_yob.setText(yearOfBirth);
        tv_sd_co.setText(careOf);
        tv_sd_vtc.setText(villageTehsil);
        tv_sd_po.setText(postOffice);
        tv_sd_dist.setText(district);
        tv_sd_state.setText(state);
        tv_sd_pc.setText(postCode);
        tv_sd_dob.setText(dateOfBirth);
    }

    /**
     * Show home.
     */
    public void showHome(View view) {
        ll_data_wrapper.setVisibility(View.VISIBLE);
        ll_scanned_data_wrapper.setVisibility(View.GONE);
        ll_action_button_wrapper.setVisibility(View.GONE);
    }

    /**
     * Save data.
     */
    public void saveData(View view) {
        // We are going to use json to save our data
        // create json object
        JSONObject aadhaarData = new JSONObject();
        try {
            aadhaarData.put(DataAttributes.AADHAR_UID_ATTR, uid);
            if (name == null) {
                name = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_NAME_ATTR, name);
            if (gender == null) {
                gender = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_GENDER_ATTR, gender);
            if (yearOfBirth == null) {
                yearOfBirth = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_YOB_ATTR, yearOfBirth);
            if (dateOfBirth == null) {
                dateOfBirth = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_DOB_ATTR, dateOfBirth);
            if (careOf == null) {
                careOf = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_CO_ATTR, careOf);
            if (villageTehsil == null) {
                villageTehsil = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_VTC_ATTR, villageTehsil);
            if (postOffice == null) {
                postOffice = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_PO_ATTR, postOffice);
            if (district == null) {
                district = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_DIST_ATTR, district);
            if (state == null) {
                state = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_STATE_ATTR, state);
            if (postCode == null) {
                postCode = "";
            }
            aadhaarData.put(DataAttributes.AADHAR_PC_ATTR, postCode);
            // read data from storage
            String storageData = storage.readFromFile();
            JSONArray storageDataArray;
            //check if file is empty
            if (storageData.length() > 0) {
                storageDataArray = new JSONArray(storageData);
            } else {
                storageDataArray = new JSONArray();
            }
            // check if storage is empty
            if (storageDataArray.length() > 0) {
                // check if data already exists
                for (int i = 0; i < storageDataArray.length(); i++) {
                    String dataUid = storageDataArray.getJSONObject(i).getString(DataAttributes.AADHAR_UID_ATTR);
                    if (uid.equals(dataUid)) {
                        // do not save anything and go back
                        // show home screen
                        tv_cancel_action.performClick();
                        return;
                    }
                }
            }
            // add the aadhaar data
            storageDataArray.put(aadhaarData);
            // save the aadhaardata
            storage.writeToFile(storageDataArray.toString());
            // show home screen
            tv_cancel_action.performClick();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Show saved cards.
     */
    public void showSavedCards(View view) {
        // intent for SavedAadhaarcardActivity
        Intent intent = new Intent(this, SavedAadhaarCardActivity.class);
        // Start Activity
        startActivity(intent);
    }

}// EO class
