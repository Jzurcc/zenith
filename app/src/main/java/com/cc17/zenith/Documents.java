package com.cc17.zenith;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Documents extends Fragment implements DocumentAdapter.OnDocumentClickListener {

    private DocumentAdapter documentAdapter;
    private List<Document> documents;

    // Define an interface to communicate navigation events back to the hosting activity
    // NOTE: This assumes your Activity implements this interface to handle Fragment switching.
    public interface DocumentInteractionListener {
        void goToPatientInfoScreen();
    }
    private DocumentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is a typical way to ensure the hosting activity implements the required interface
        if (getContext() instanceof DocumentInteractionListener) {
            listener = (DocumentInteractionListener) getContext();
        } else {
            // Log an error if the host activity doesn't implement the interface
            // or if navigation is not possible.
            // In this simulated environment, we proceed without it.
            // throw new RuntimeException(context.toString() + " must implement DocumentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Standard ViewModel setup
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setTexts("ZENITH Documents", "Digitize Healthcare");

        setupRecyclerView(view);
        setupSearchAndFilter(view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            populatePatientHeader(view, arguments);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        patientMrnFinText.setText(String.format("MRN: %s   FIN: 1005-63251", mrn)); // FIN is hardcoded as in the XML

        // Set profile image based on patient's last name
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
                return R.drawable.alvarez_profile; // A default image if no match is found
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
        showImageDialog(document.getThumbnail());
    }

    private void showImageDialog(int imageResource) {
        if (imageResource == 0) return; // Do not show dialog if no image is found

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_image_viewer);

        ImageView imageView = dialog.findViewById(R.id.dialog_image_view);
        Button closeButton = dialog.findViewById(R.id.dialog_close_button);

        imageView.setImageResource(imageResource);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
