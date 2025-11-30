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
        if (getArguments() != null) {
            populateDataFromBundle(getArguments());
        }

        return view;

/*
        // --- 1. Set Click Listeners for Interactive Elements ---

        // The 'sex' button listener you originally provided
        ImageButton sex = view.findViewById(R.id.sex);
        sex.setOnClickListener(v -> Toast.makeText(getActivity(), "Sex selection toggled", Toast.LENGTH_SHORT).show());

        // Other ImageButtons
        view.findViewById(R.id.button2).setOnClickListener(v -> Toast.makeText(getActivity(), "Organ Donor status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button3).setOnClickListener(v -> Toast.makeText(getActivity(), "Living Will status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button4).setOnClickListener(v -> Toast.makeText(getActivity(), "Personal Email status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button5).setOnClickListener(v -> Toast.makeText(getActivity(), "Same Mail Address status toggled", Toast.LENGTH_SHORT).show());

        // QR Code ImageView is also clickable
        view.findViewById(R.id.imageView32).setOnClickListener(v -> generateQRCode());

        // --- 2. Retrieve and Populate Data ---
        Bundle args = getArguments();
        if (args != null) {

            // Basic Info Fields
            setEditText(view, R.id.et_first_name, args.getString("firstName"));
            setEditText(view, R.id.et_middle_name, args.getString("middleInitial"));
            setEditText(view, R.id.et_last_name, args.getString("lastName"));
            // Preferred name field is typically left empty unless data exists for it

            // Detailed Info Fields (Matching the image snippet and preceding logic)
            setEditText(view, R.id.et_dob, args.getString("dob"));
            setEditText(view, R.id.et_country_birth, args.getString("countryOfBirth"));
            setEditText(view, R.id.et_city_birth, args.getString("cityOfBirth"));
            setEditText(view, R.id.et_province_birth, args.getString("provinceOfBirth"));

            setEditText(view, R.id.et_marital_status, args.getString("maritalStatus"));
            setEditText(view, R.id.et_race_ethnicity, args.getString("raceEthnicity"));
            setEditText(view, R.id.et_mrn, args.getString("mrn"));
            setEditText(view, R.id.et_fin, args.getString("mrn")); // Assuming FIN uses MRN value for sample data

            setEditText(view, R.id.et_occupation, args.getString("occupation"));
            setEditText(view, R.id.et_employer, args.getString("employer"));
            setEditText(view, R.id.et_education, args.getString("education"));

            setEditText(view, R.id.et_religion, args.getString("religion"));
            setEditText(view, R.id.et_preferences, args.getString("preferences"));
            setEditText(view, R.id.et_lang_record, args.getString("langRecord"));
            setEditText(view, R.id.et_lang_record_no, args.getString("langRecordNo"));

            // Contact/Address Fields
            setEditText(view, R.id.et_email, args.getString("email"));

            setEditText(view, R.id.et_address_line1, args.getString("address1"));
            setEditText(view, R.id.et_city_address, args.getString("city"));
            setEditText(view, R.id.et_province_address, args.getString("province"));

            setEditText(view, R.id.et_address_line2, null); // Placeholder for Address Line 2
            setEditText(view, R.id.et_zipcode, args.getString("zip"));
            setEditText(view, R.id.et_region, args.getString("region"));
            setEditText(view, R.id.et_country, args.getString("country"));

            setEditText(view, R.id.et_primary_phone, args.getString("primaryPhone"));
            setEditText(view, R.id.et_secondary_phone, args.getString("secondaryPhone"));

            setEditText(view, R.id.et_remarks, null); // Placeholder for Remarks

            // Note: Boolean fields (isOrganDonor, isLivingWill, etc.) would require specific logic to toggle the ImageButton background drawable.
        }

        return view;*/
    }

    private void initViews(View view) {
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
    }

    private void setupListeners(View view) {
        // original toggle buttons
        // TODO: Change ImageButtons to Toggle Buttons and implement their listeners
        view.findViewById(R.id.sex).setOnClickListener(v -> Toast.makeText(getActivity(), "Sex selection toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button2).setOnClickListener(v -> Toast.makeText(getActivity(), "Organ Donor status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button3).setOnClickListener(v -> Toast.makeText(getActivity(), "Living Will status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button4).setOnClickListener(v -> Toast.makeText(getActivity(), "Personal Email status toggled", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.button5).setOnClickListener(v -> Toast.makeText(getActivity(), "Same Mail Address status toggled", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.imageView32).setOnClickListener(v -> generateQRCode());
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
}