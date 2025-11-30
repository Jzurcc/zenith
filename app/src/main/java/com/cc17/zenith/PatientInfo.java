package com.cc17.zenith;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

// TODO: Make patient info layout responsive
// TODO: Add "Edit Patient Info" button
public class PatientInfo extends Fragment {
    private EditText et_first_name, et_middle_name, et_last_name, et_preferred_name, et_dob,
            et_country_birth, et_city_birth, et_province_birth, et_fin,
            et_mrn, et_marital_status, et_race_ethnicity, et_occupation,
            et_employer, et_education, et_religion, et_preferences, et_lang_record, et_lang_record_no,
            et_email, et_address_line1, et_city_address, et_province_address,
            et_address_line2, et_zipcode, et_region, et_country, mobile1, et_primary_phone,
            mobile2, et_secondary_phone, et_remarks;

    private Button sex_M, sex_F;
    private Button btn_donor_yes, btn_donor_no;
    private Button living_will_yes, living_will_no;
    private Button personal_email_yes, personal_email_no;
    private Button same_mail_yes, same_mail_no;

    private List<View> allInputViews = new ArrayList<>();
    private Button btnAction;
    private boolean isEditing = false;

    private void setEditText(View view, int id, String text) {
        EditText et = view.findViewById(id);
        if (et != null && text != null) {
            et.setText(text);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("Patient Information", "Centralize Patients");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_info, container, false);

        initViews(view);
        setupListeners(view);

        setEditingEnabled(false);

        if (getArguments() != null) {
            // check full JSON from QR Code
            if (getArguments().containsKey("qr_json_data")) {
                populateFromQRJson(getArguments().getString("qr_json_data"));
            }
            else {
                populateDataFromBundle(getArguments());
            }
        }

        return view;
    }

    private void initViews(View view) {
        // toggle buttons
        sex_M = view.findViewById(R.id.sex_M);
        sex_F = view.findViewById(R.id.sex_F);

        btn_donor_yes = view.findViewById(R.id.btn_donor_yes);
        btn_donor_no = view.findViewById(R.id.btn_donor_no);

        living_will_yes = view.findViewById(R.id.living_will_yes);
        living_will_no = view.findViewById(R.id.living_will_no);

        personal_email_yes = view.findViewById(R.id.personal_email_yes);
        personal_email_no = view.findViewById(R.id.personal_email_no);

        same_mail_yes = view.findViewById(R.id.same_mail_yes);
        same_mail_no = view.findViewById(R.id.same_mail_no);


        // Personal Info
        et_first_name = view.findViewById(R.id.et_first_name);
        et_middle_name = view.findViewById(R.id.et_middle_name);
        et_last_name = view.findViewById(R.id.et_last_name);
        et_preferred_name = view.findViewById(R.id.et_preferred_name);
        et_dob = view.findViewById(R.id.et_dob);

        // Birth Info
        et_country_birth = view.findViewById(R.id.et_country_birth);
        et_city_birth = view.findViewById(R.id.et_city_birth);
        et_province_birth = view.findViewById(R.id.et_province_birth);

        // Demographics
        et_fin = view.findViewById(R.id.et_fin);
        et_mrn = view.findViewById(R.id.et_mrn);
        et_marital_status = view.findViewById(R.id.et_marital_status);
        et_race_ethnicity = view.findViewById(R.id.et_race_ethnicity);
        et_occupation = view.findViewById(R.id.et_occupation);
        et_employer = view.findViewById(R.id.et_employer);
        et_education = view.findViewById(R.id.et_education);
        et_religion = view.findViewById(R.id.et_religion);
        et_preferences = view.findViewById(R.id.et_preferences);
        et_lang_record = view.findViewById(R.id.et_lang_record);
        et_lang_record_no = view.findViewById(R.id.et_lang_record_no);

        // Contact & Address
        et_email = view.findViewById(R.id.et_email);
        et_address_line1 = view.findViewById(R.id.et_address_line1);
        et_city_address = view.findViewById(R.id.et_city_address);
        et_province_address = view.findViewById(R.id.et_province_address);
        et_address_line2 = view.findViewById(R.id.et_address_line2);
        et_zipcode = view.findViewById(R.id.et_zipcode);
        et_region = view.findViewById(R.id.et_region);
        et_country = view.findViewById(R.id.et_country);
        mobile1 = view.findViewById(R.id.mobile1);
        et_primary_phone = view.findViewById(R.id.et_primary_phone);
        mobile2 = view.findViewById(R.id.mobile2);
        et_secondary_phone = view.findViewById(R.id.et_secondary_phone);
        et_remarks = view.findViewById(R.id.et_remarks);

        btnAction = view.findViewById(R.id.btn_action);

        allInputViews.clear();

        allInputViews.add(et_first_name);
        allInputViews.add(et_middle_name);
        allInputViews.add(et_last_name);
        allInputViews.add(et_preferred_name);
        allInputViews.add(et_dob);
        allInputViews.add(et_country_birth);
        allInputViews.add(et_city_birth);
        allInputViews.add(et_province_birth);
        allInputViews.add(et_fin);
        allInputViews.add(et_mrn);
        allInputViews.add(et_marital_status);
        allInputViews.add(et_race_ethnicity);
        allInputViews.add(et_occupation);
        allInputViews.add(et_employer);
        allInputViews.add(et_education);
        allInputViews.add(et_religion);
        allInputViews.add(et_preferences);
        allInputViews.add(et_lang_record);
        allInputViews.add(et_lang_record_no);
        allInputViews.add(et_email);
        allInputViews.add(et_address_line1);
        allInputViews.add(et_city_address);
        allInputViews.add(et_province_address);
        allInputViews.add(et_address_line2);
        allInputViews.add(et_zipcode);
        allInputViews.add(et_region);
        allInputViews.add(et_country);
        allInputViews.add(mobile1);
        allInputViews.add(et_primary_phone);
        allInputViews.add(mobile2);
        allInputViews.add(et_secondary_phone);
        allInputViews.add(et_remarks);

        allInputViews.add(sex_M);
        allInputViews.add(sex_F);
        allInputViews.add(btn_donor_yes);
        allInputViews.add(btn_donor_no);
        allInputViews.add(living_will_yes);
        allInputViews.add(living_will_no);
        allInputViews.add(personal_email_yes);
        allInputViews.add(personal_email_no);
        allInputViews.add(same_mail_yes);
        allInputViews.add(same_mail_no);

    }

    private void setupListeners(View view) {
        setupTogglePair(sex_M, sex_F);
        setupTogglePair(btn_donor_yes, btn_donor_no);
        setupTogglePair(living_will_yes, living_will_no);
        setupTogglePair(personal_email_yes, personal_email_no);
        setupTogglePair(same_mail_yes, same_mail_no);

        view.findViewById(R.id.btn_qr).setOnClickListener(v -> generateQRCode());

        btnAction.setOnClickListener(v -> {
            if (isEditing) {
                saveChanges();
            } else {
                enableEditMode();
            }
        });
    }

    private void enableEditMode() {
        setEditingEnabled(true);

        btnAction.setText("Save Changes");
        btnAction.setBackgroundColor(getResources().getColor(R.color.coral));

        isEditing = true;
        Toast.makeText(getContext(), "You can now edit details", Toast.LENGTH_SHORT).show();
    }

    private void saveChanges() {
        savePatientData();
        setEditingEnabled(false);

        btnAction.setText("Edit Info");
        btnAction.setBackgroundColor(getResources().getColor(R.color.moonstone)); // Change back to 'Edit' color
        isEditing = false;

        // go back
        getParentFragmentManager().popBackStack();
        Toast.makeText(getContext(), "Changes Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    private void setEditingEnabled(boolean isEnabled) {
        for (View v : allInputViews) {
            if (v != null) {
                v.setEnabled(isEnabled);

                if (v instanceof EditText) {
                    v.setAlpha(isEnabled ? 1.0f : 0.7f);
                    v.setFocusable(isEnabled);
                    v.setFocusableInTouchMode(isEnabled);
                    v.setClickable(isEnabled);
                }

                if (v instanceof Button && v != btnAction) {
                    v.setAlpha(isEnabled ? 1.0f : 0.6f);
                }
            }
        }
    }

    private void setupTogglePair(Button btn1, Button btn2) {
        btn1.setOnClickListener(v -> {
            btn1.setSelected(true);
            btn2.setSelected(false);
        });

        btn2.setOnClickListener(v -> {
            btn2.setSelected(true);
            btn1.setSelected(false);
        });
    }

    private String getToggleValue(Button btn1, Button btn2) {
        if (btn1.isSelected()) return btn1.getText().toString(); // Returns "M" or "Yes"
        if (btn2.isSelected()) return btn2.getText().toString(); // Returns "F" or "No"
        return "";
    }

    private void populateDataFromBundle(Bundle args) {
        safeSetText(et_first_name, args.getString("firstName"));
        safeSetText(et_middle_name, args.getString("middleInitial"));
        safeSetText(et_last_name, args.getString("lastName"));

        safeSetText(et_dob, args.getString("dob"));
        safeSetText(et_country_birth, args.getString("countryOfBirth"));
        safeSetText(et_city_birth, args.getString("cityOfBirth"));
        safeSetText(et_province_birth, args.getString("provinceOfBirth"));

        safeSetText(et_marital_status, args.getString("maritalStatus"));
        safeSetText(et_race_ethnicity, args.getString("raceEthnicity"));
        safeSetText(et_mrn, args.getString("mrn"));
        safeSetText(et_fin, args.getString("fin"));

        safeSetText(et_occupation, args.getString("occupation"));
        safeSetText(et_employer, args.getString("employer"));
        safeSetText(et_education, args.getString("education"));
        safeSetText(et_religion, args.getString("religion"));
        safeSetText(et_preferences, args.getString("preferences"));
        safeSetText(et_lang_record, args.getString("langRecord"));
        safeSetText(et_lang_record_no, args.getString("langRecordNo"));

        safeSetText(et_email, args.getString("email"));
        safeSetText(et_address_line1, args.getString("address1"));
        safeSetText(et_city_address, args.getString("city"));
        safeSetText(et_province_address, args.getString("province"));
        safeSetText(et_zipcode, args.getString("zip"));
        safeSetText(et_region, args.getString("region"));
        safeSetText(et_country, args.getString("country"));

        safeSetText(et_primary_phone, args.getString("primaryPhone"));
        safeSetText(et_secondary_phone, args.getString("secondaryPhone"));
    }

    // returns an empty string instead of crashing if the EditText is null
    private String safeGetText(EditText editText) {
        if (editText == null) return "";
        return editText.getText().toString().trim();
    }

    // does nothing if the EditText is null
    private void safeSetText(EditText editText, String text) {
        if (editText != null && text != null) {
            editText.setText(text);
        }
    }

    private void savePatientData() {
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        String fName = safeGetText(et_first_name);
        String mName = safeGetText(et_middle_name);
        String lName = safeGetText(et_last_name);
        String dob = safeGetText(et_dob);

        String age = "30";

        String sex = getToggleValue(sex_M, sex_F);

        boolean isDonor = getToggleValue(btn_donor_yes, btn_donor_no).equalsIgnoreCase("Yes");
        boolean isWill = getToggleValue(living_will_yes, living_will_no).equalsIgnoreCase("Yes");


        Patient newPatient = new Patient(
                fName,
                mName,
                lName,
                age,
                sex,
                safeGetText(et_fin), // Using FIN as ID No
                safeGetText(mobile1), // Mobile No
                dob,
                safeGetText(et_country_birth),
                safeGetText(et_city_birth),
                safeGetText(et_province_birth),
                safeGetText(et_marital_status),
                safeGetText(et_race_ethnicity),
                safeGetText(et_mrn),
                safeGetText(et_occupation),
                safeGetText(et_employer),
                safeGetText(et_education),
                safeGetText(et_religion),
                safeGetText(et_preferences),
                safeGetText(et_lang_record),
                safeGetText(et_lang_record_no),
                isDonor,
                isWill,
                safeGetText(et_email),
                safeGetText(et_address_line1),
                safeGetText(et_city_address),
                safeGetText(et_province_address),
                safeGetText(et_zipcode),
                safeGetText(et_region),
                safeGetText(et_country),
                safeGetText(et_primary_phone),
                safeGetText(et_secondary_phone),
                R.drawable.patient_picture // TODO: Replace with actual default image resource
        );

        sharedViewModel.savePatient(newPatient);
    }

    private void generateQRCode(){
        JSONObject data = new JSONObject();

        putIfNotEmpty(data, "fname", et_first_name);
        putIfNotEmpty(data, "mname", et_middle_name);
        putIfNotEmpty(data, "lname", et_last_name);
        putIfNotEmpty(data, "pref", et_preferred_name);
        putIfNotEmpty(data, "dob", et_dob);

        putIfNotEmpty(data, "b_cntry", et_country_birth);
        putIfNotEmpty(data, "b_city", et_city_birth);
        putIfNotEmpty(data, "b_prov", et_province_birth);

        putIfNotEmpty(data, "fin", et_fin);
        putIfNotEmpty(data, "mrn", et_mrn);

        putIfNotEmpty(data, "stat", et_marital_status);
        putIfNotEmpty(data, "race", et_race_ethnicity);
        putIfNotEmpty(data, "job", et_occupation);
        putIfNotEmpty(data, "emp", et_employer);
        putIfNotEmpty(data, "edu", et_education);
        putIfNotEmpty(data, "rel", et_religion);

        putIfNotEmpty(data, "prefs", et_preferences);
        putIfNotEmpty(data, "lang", et_lang_record);
        putIfNotEmpty(data, "lang_n", et_lang_record_no);

        putIfNotEmpty(data, "email", et_email);
        putIfNotEmpty(data, "mob1", mobile1);
        putIfNotEmpty(data, "mob2", mobile2);
        putIfNotEmpty(data, "ph1", et_primary_phone);
        putIfNotEmpty(data, "ph2", et_secondary_phone);

        putIfNotEmpty(data, "addr1", et_address_line1);
        putIfNotEmpty(data, "addr2", et_address_line2);
        putIfNotEmpty(data, "city", et_city_address);
        putIfNotEmpty(data, "prov", et_province_address);
        putIfNotEmpty(data, "zip", et_zipcode);
        putIfNotEmpty(data, "reg", et_region);
        putIfNotEmpty(data, "cntry", et_country);

        putIfNotEmpty(data, "rem", et_remarks);

        try {
            // Sex (M/F)
            String valSex = getToggleValue(sex_M, sex_F);
            if (!valSex.isEmpty()) data.put("sex", valSex);

            // Organ Donor (Yes/No)
            String valDonor = getToggleValue(btn_donor_yes, btn_donor_no);
            if (!valDonor.isEmpty()) data.put("donor", valDonor);

            // Living Will (Yes/No)
            String valWill = getToggleValue(living_will_yes, living_will_no);
            if (!valWill.isEmpty()) data.put("will", valWill);

            // Personal Email (Yes/No)
            String valPEmail = getToggleValue(personal_email_yes, personal_email_no);
            if (!valPEmail.isEmpty()) data.put("p_mail", valPEmail);

            // Same Mail Address (Yes/No)
            String valSameMail = getToggleValue(same_mail_yes, same_mail_no);
            if (!valSameMail.isEmpty()) data.put("sm_addr", valSameMail);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        if (data.length() == 0) {
            Toast.makeText(getActivity(), "No data to generate QR Code", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // convert to bitmap and show dialog
            Bitmap qrBitmap = generateQrBitmap(data.toString(), 800, 800);
            showCustomQrDialog(qrBitmap);
        } catch (WriterException e) {
            Toast.makeText(getActivity(), "Error generating QR code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // only puts the key-value pair into the JSON object if the text field is not null
    private void putIfNotEmpty(JSONObject json, String key, EditText editText) {
        try {
            String value = safeGetText(editText);
            if (!value.isEmpty()) {
                json.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap generateQrBitmap(String content, int width, int height) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);

        int w = bitMatrix.getWidth();
        int h = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    // converting the text string into qr using Zxing
    private void showCustomQrDialog(Bitmap qrBitmap) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.qr_code_dialog, null);

        ImageView generatedQRCode = dialogView.findViewById(R.id.generatedQRCode);
        ImageButton downloadQRBtn = dialogView.findViewById(R.id.downloadQRBtn);
        ImageButton btnClose = dialogView.findViewById(R.id.close_qr_dialog);

        generatedQRCode.setImageBitmap(qrBitmap);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        downloadQRBtn.setOnClickListener(v -> {
            saveImageToGallery(qrBitmap, "PatientQR_" + System.currentTimeMillis());
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Android MediaStore doesn't need explicit WRITE_EXTERNAL_STORAGE permissions
    private void saveImageToGallery(Bitmap bitmap, String fileName) {
        if (getContext() == null) return;

        ContentResolver resolver = getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        // determine path based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PatientQR");
        }

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (imageUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    Toast.makeText(getActivity(), "QR Code saved to Gallery!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateFromQRJson(String jsonString) {
        try {
            JSONObject data = new JSONObject(jsonString);

            safeSetText(et_first_name, data.optString("fname"));
            safeSetText(et_middle_name, data.optString("mname"));
            safeSetText(et_last_name, data.optString("lname"));
            safeSetText(et_preferred_name, data.optString("pref"));
            safeSetText(et_dob, data.optString("dob"));

            safeSetText(et_country_birth, data.optString("b_cntry"));
            safeSetText(et_city_birth, data.optString("b_city"));
            safeSetText(et_province_birth, data.optString("b_prov"));

            safeSetText(et_fin, data.optString("fin"));
            safeSetText(et_mrn, data.optString("mrn"));

            safeSetText(et_marital_status, data.optString("stat"));
            safeSetText(et_race_ethnicity, data.optString("race"));
            safeSetText(et_occupation, data.optString("job"));
            safeSetText(et_employer, data.optString("emp"));
            safeSetText(et_education, data.optString("edu"));
            safeSetText(et_religion, data.optString("rel"));

            safeSetText(et_preferences, data.optString("prefs"));
            safeSetText(et_lang_record, data.optString("lang"));
            safeSetText(et_lang_record_no, data.optString("lang_n"));

            safeSetText(et_email, data.optString("email"));
            safeSetText(mobile1, data.optString("mob1"));
            safeSetText(mobile2, data.optString("mob2"));
            safeSetText(et_primary_phone, data.optString("ph1"));
            safeSetText(et_secondary_phone, data.optString("ph2"));

            safeSetText(et_address_line1, data.optString("addr1"));
            safeSetText(et_address_line2, data.optString("addr2"));
            safeSetText(et_city_address, data.optString("city"));
            safeSetText(et_province_address, data.optString("prov"));
            safeSetText(et_zipcode, data.optString("zip"));
            safeSetText(et_region, data.optString("reg"));
            safeSetText(et_country, data.optString("cntry"));

            safeSetText(et_remarks, data.optString("rem"));

            restoreToggleState(sex_M, sex_F, data.optString("sex"));
            restoreToggleState(btn_donor_yes, btn_donor_no, data.optString("donor"));
            restoreToggleState(living_will_yes, living_will_no, data.optString("will"));
            restoreToggleState(personal_email_yes, personal_email_no, data.optString("p_mail"));
            restoreToggleState(same_mail_yes, same_mail_no, data.optString("sm_addr"));

            Toast.makeText(getContext(), "Patient data loaded from QR", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing QR Data", Toast.LENGTH_SHORT).show();
        }
    }

    // sets the correct button as selected based on the string value
    private void restoreToggleState(Button btn1, Button btn2, String value) {
        if (value == null || value.isEmpty()) return;

        btn1.setSelected(false);
        btn2.setSelected(false);

        // Check if value matches button text ("M" == "M" or "Yes" == "Yes")
        if (btn1.getText().toString().equalsIgnoreCase(value)) {
            btn1.setSelected(true);
        } else if (btn2.getText().toString().equalsIgnoreCase(value)) {
            btn2.setSelected(true);
        }
    }
}