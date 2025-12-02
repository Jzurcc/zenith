package com.cc17.zenith;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import static android.app.Activity.RESULT_OK;

// TODO: Redesign the "Add Patient" button; looks so out of place ngl fr fr
// TODO: Add edit document title functionality
// TODO: Add QR Sync functionality -- will do later (jeni)
public class Documents extends Fragment implements DocumentAdapter.OnDocumentClickListener {
    private Uri imageUri;
    private DocumentAdapter documentAdapter;
    private List<Document> documents;
    private Patient currentPatient;

    private View layoutNoDocuments;
    private RecyclerView recyclerView;
    private SharedViewModel sharedViewModel;

    // Launcher for Gallery
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        addNewDocument(selectedImage);
                    }
                }
            }
    );


    // Launcher for Camera
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // imageUri is already set in the openCamera() method
                    if (imageUri != null) {
                        addNewDocument(imageUri);
                    }
                }
            }
    );

    // Launcher for requesting Camera Permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    showSourceSelectionDialog();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("ZENITH Documents", "Digitize Healthcare");

        layoutNoDocuments = view.findViewById(R.id.layout_no_documents);
        recyclerView = view.findViewById(R.id.document_recycler_view);

        if (getArguments() != null) {
            currentPatient = getArguments().getParcelable("selected_patient");
        }

        setupRecyclerView(view);

        if (currentPatient != null) {
            populatePatientHeader(view, getArguments());
        }

        sharedViewModel.getPatientList().observe(getViewLifecycleOwner(), patientList -> {
            if (currentPatient != null && patientList != null) {
                for (Patient p : patientList) {
                    // Match by MRN to find the "Live" version of this patient
                    if (p.getMrn().equals(currentPatient.getMrn())) {
                        currentPatient = p; // Update our local reference
                        refreshDocumentList(); // Refresh the list
                        break;
                    }
                }
            }
        });



        setupSearchAndFilter(view);
        setupClickListeners(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_documents, container, false);
    }

    private void setupRecyclerView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        documents = new ArrayList<>();

        // load documents FROM from patient object and not a new list
        if (currentPatient != null && currentPatient.getDocuments() != null) {
            documents.addAll(currentPatient.getDocuments()); // use addALL instead of getDocuments()
        }

        // dummy data onleh
        /*documents.add(new Document(R.drawable.mri_scan_thumbnail, "MRI Scan - Brain", "10/12/2025"));
        documents.add(new Document(R.drawable.lab_results_thumbnail, "Blood Work Lab Results", "10/10/2025"));
        documents.add(new Document(R.drawable.visit_note_thumbnail, "Visit Note", "10/09/2025"));
        documents.add(new Document(R.drawable.discharge_summary_thumbnail, "Discharge Summary", "08/26/2025"));
        documents.add(new Document(R.drawable.prescription_summary_thumbnail, "Prescription Summary", "08/24/2025"));*/

        documentAdapter = new DocumentAdapter(documents, this);
        recyclerView.setAdapter(documentAdapter);

        checkEmptyState();
    }

    private void refreshDocumentList() {
        if (currentPatient != null && currentPatient.getDocuments() != null) {
            documents = new ArrayList<>(currentPatient.getDocuments());

            documentAdapter.updateData(documents);
        }
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (documents.isEmpty()) {
            if (layoutNoDocuments != null) layoutNoDocuments.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            if (layoutNoDocuments != null) layoutNoDocuments.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void addNewDocument(Uri uri) {
        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        Document newDocument = new Document(uri, "New Document", currentDate);

        // add to local list
        documents.add(0, newDocument);
        documentAdapter.updateData(documents);
        checkEmptyState();
        recyclerView.scrollToPosition(0);

        if (currentPatient != null) {
            currentPatient.setDocuments(documents);
            if (sharedViewModel != null) {
                sharedViewModel.savePatient(currentPatient);
            }
            Toast.makeText(getContext(), "Document Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void populatePatientHeader(View view, Bundle bundle) {
        currentPatient = bundle.getParcelable("selected_patient");

        if (currentPatient != null) {
            TextView patientNameText = view.findViewById(R.id.patient_name_text);
            TextView patientAgeMrnText = view.findViewById(R.id.patient_age_mrn_text);
            TextView patientMrnFinText = view.findViewById(R.id.patient_mrn_fin_text);
            ImageView patientProfileImage = view.findViewById(R.id.patient_profile_image);

            // Set Name
            patientNameText.setText(String.format("%s, %s %s.",
                    currentPatient.getLastName(),
                    currentPatient.getFirstName(),
                    currentPatient.getMiddleInitial()));

            // Set Age
            patientAgeMrnText.setText(String.format("Age: %s years", currentPatient.getAge()));

            // Set MRN
            patientMrnFinText.setText(String.format("MRN: %s   FIN: %s",
                    currentPatient.getMrn(),
                    currentPatient.getFin()));

            // Set Profile Image
            if (currentPatient.getProfileImage() != 0) {
                patientProfileImage.setImageResource(currentPatient.getProfileImage());
            } else {
                patientProfileImage.setImageResource(R.drawable.default_profile_pic);
            }
        }
    }

    private void setupClickListeners(View view) {
        ImageButton editPatientInfoButton = view.findViewById(R.id.imageButton4);
        editPatientInfoButton.setOnClickListener(v -> {
            if (currentPatient != null) {
                PatientInfo patientInfoFragment = new PatientInfo();
                Bundle args = new Bundle();

                // Pass the existing patient object forward to the edit screen
                args.putParcelable("selected_patient", currentPatient);
                patientInfoFragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, patientInfoFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Error loading patient data", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton newDocumentButton = view.findViewById(R.id.imageButton6);
        newDocumentButton.setOnClickListener(v -> showSourceSelectionDialog());


        // TODO
        view.findViewById(R.id.imageButton8).setOnClickListener(v -> {
            // Logic for QR Sync
            Toast.makeText(getContext(), "Syncing QR...", Toast.LENGTH_SHORT).show();
        });
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
            dialog.dismiss();
            checkCameraPermissionAndOpen();
        });

        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // open camera if already allowed
            openCamera();
        } else {
            // else ask system for permission
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        filePickerLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        filePickerLauncher.launch(intent);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Document");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Captured via Zenith App");
        // insert empty image into MediaStore to get a valid URI
        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        cameraLauncher.launch(intent);
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
