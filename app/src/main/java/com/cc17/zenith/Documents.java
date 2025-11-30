package com.cc17.zenith;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class Documents extends Fragment implements DocumentAdapter.OnDocumentClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri imageUri;
    private DocumentAdapter documentAdapter;
    private List<Document> documents;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("ZENITH Documents", "Digitize Healthcare");

        setupRecyclerView(view);
        setupSearchAndFilter(view);
        setupClickListeners(view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            populatePatientHeader(view, arguments);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_documents, container, false);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.document_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        documents = new ArrayList<>();
        documents.add(new Document(R.drawable.mri_scan_thumbnail, "MRI Scan - Brain", "10/12/2025"));
        documents.add(new Document(R.drawable.lab_results_thumbnail, "Blood Work Lab Results", "10/10/2025"));
        documents.add(new Document(R.drawable.visit_note_thumbnail, "Visit Note", "10/09/2025"));
        documents.add(new Document(R.drawable.discharge_summary_thumbnail, "Discharge Summary", "08/26/2025"));
        documents.add(new Document(R.drawable.prescription_summary_thumbnail, "Prescription Summary", "08/24/2025"));

        documentAdapter = new DocumentAdapter(documents, this);
        recyclerView.setAdapter(documentAdapter);
    }

    private void populatePatientHeader(View view, Bundle bundle) {
        TextView patientNameText = view.findViewById(R.id.patient_name_text);
        TextView patientAgeMrnText = view.findViewById(R.id.patient_age_mrn_text);
        TextView patientMrnFinText = view.findViewById(R.id.patient_mrn_fin_text);
        ImageView patientProfileImage = view.findViewById(R.id.patient_profile_image);

        String firstName = bundle.getString("firstName", "");
        String lastName = bundle.getString("lastName", "");
        String middleInitial = bundle.getString("middleInitial", "");
        String age = bundle.getString("age", "");
        String mrn = bundle.getString("mrn", "");

        patientNameText.setText(String.format("%s, %s %s.", lastName, firstName, middleInitial));
        patientAgeMrnText.setText(String.format("Age: %s years", age));
        patientMrnFinText.setText(String.format("MRN: %s   FIN: 1005-63251", mrn));

        int profileImageResId = getProfileImageResource(lastName);
        patientProfileImage.setImageResource(profileImageResId);
    }

    private int getProfileImageResource(String lastName) {
        switch (lastName.toLowerCase()) {
            case "alvarez":
                return R.drawable.alvarez_profile;
            case "bautista":
                return R.drawable.bautista_profile;
            case "cruz":
                return R.drawable.cruz_profile;
            case "dela rosa":
                return R.drawable.dela_rosa_profile;
            default:
                return R.drawable.alvarez_profile;
        }
    }

    private void setupSearchAndFilter(View view) {
        SearchView searchView = view.findViewById(R.id.document_search_view);
        searchView.setOnClickListener(v -> searchView.onActionViewExpanded());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                documentAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                documentAdapter.filter(newText);
                return false;
            }
        });

        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this::showFilterMenu);
    }

    private void setupClickListeners(View view) {
        ImageButton editPatientInfoButton = view.findViewById(R.id.imageButton4);
        editPatientInfoButton.setOnClickListener(v -> {
            PatientInfo patientInfoFragment = new PatientInfo();
            patientInfoFragment.setArguments(getArguments());

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, patientInfoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        ImageButton newDocumentButton = view.findViewById(R.id.imageButton6);
        newDocumentButton.setOnClickListener(v -> showPermissionDialog());
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission Request")
                .setMessage("To add a new document, please grant access to your device\'s storage and camera.")
                .setPositiveButton("Accept", (dialog, which) -> showSourceSelectionDialog())
                .setNegativeButton("Deny Access", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showSourceSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_source_selection, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.option_files).setOnClickListener(v -> {
            openFilePicker();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.option_gallery).setOnClickListener(v -> {
            openGallery();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.option_camera).setOnClickListener(v -> {
            openCamera();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = null;
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImage = data.getData();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                selectedImage = imageUri;
            }

            if (selectedImage != null) {
                String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                Document newDocument = new Document(selectedImage, "New Document", currentDate);
                documentAdapter.addDocument(newDocument);
            }
        }
    }

    private void showFilterMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_date_asc) {
                documentAdapter.sort(Comparator.comparing(Document::getDate));
                return true;
            } else if (itemId == R.id.filter_date_desc) {
                documentAdapter.sort(Comparator.comparing(Document::getDate).reversed());
                return true;
            } else if (itemId == R.id.filter_name_asc) {
                documentAdapter.sort(Comparator.comparing(Document::getTitle));
                return true;
            } else if (itemId == R.id.filter_name_desc) {
                documentAdapter.sort(Comparator.comparing(Document::getTitle).reversed());
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onDocumentClick(Document document) {
        showImageDialog(document);
    }

    private void showImageDialog(Document document) {
        if (document == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_image_viewer);

        ImageView imageView = dialog.findViewById(R.id.dialog_image_view);
        Button closeButton = dialog.findViewById(R.id.dialog_close_button);

        if (document.getImageUri() != null) {
            imageView.setImageURI(document.getImageUri());
        } else {
            imageView.setImageResource(document.getThumbnail());
        }

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
