package com.cc17.zenith;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> subtitle = new MutableLiveData<>();

    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getSubtitle() { return subtitle; }

    public void setTexts(String newTitle, String newSubtitle) {
        title.setValue(newTitle);
        subtitle.setValue(newSubtitle);
    }

    private final MutableLiveData<List<Patient>> patientList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Patient>> getPatientList() {
        return patientList;
    }

    public void savePatient(Patient newPatient) {
        List<Patient> currentList = patientList.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        boolean isExisting = false;

        // Loop through the list to find a match based on MRN
        for (int i = 0; i < currentList.size(); i++) {
            Patient existing = currentList.get(i);

            // Check if MRN matches (assuming MRN is the unique ID)
            if (existing.getMrn().equals(newPatient.getMrn())) {
                currentList.set(i, newPatient);
                isExisting = true;
                break;
            }
        }

        // INSERT: If no match was found, add it as a new patient
        if (!isExisting) {
            currentList.add(newPatient);
        }

        patientList.setValue(currentList);
    }

    public void initializeDefaultPatients(Context context) {
        List<Patient> current = patientList.getValue();
        if (current == null || current.isEmpty()) {
            List<Patient> dummyData = new ArrayList<>();

            Patient p1 = new Patient(
                    "Julian", "R", "Alvarez", "Julian",
                    "12/21/1979", "46", "Philippines", "Male",
                    "Baguio City", "Benguet", "1005-63251", "Married",
                    "Ilocano", "200365448", "Architect", "Visionarch",
                    "College Graduate", "Christian", "N/A", true,
                    "English", "1977232", true,
                    "julianalvarez@gmail.com", true,
                    "142 Holy Ghost Hill Ext. Rd.", "Baguio", "Benguet", "",
                    "2600", "CAR", "Philippines", true,
                    "Mobile", "(63+) 927 910 7392",
                    "Telephone", "(214) 723-9001",
                    "No known allergies.", resourceToUriString(context, R.drawable.alvarez_profile)
            );

            // Add Documents for Julian
            // Note: Ensure you have these drawables, or use a placeholder like R.drawable.ic_launcher_background
            p1.addDocument(new Document(R.drawable.mri_scan_thumbnail, "MRI Scan - Brain", "10/12/2025"));
            p1.addDocument(new Document(R.drawable.lab_results_thumbnail, "Blood Work Lab Results", "10/10/2025"));
            p1.addDocument(new Document(R.drawable.visit_note_thumbnail, "Visit Note", "10/09/2025"));

            dummyData.add(p1);

            Patient p2 = new Patient(
                    "Angela", "M", "Bautista", "Ange",
                    "10/15/1984", "41", "Philippines", "Female",
                    "Manila", "Metro Manila", "1000001", "Single",
                    "Tagalog", "200365449", "Designer", "Self-Employed",
                    "High School", "Catholic", "None", false,
                    "Tagalog", "1000001", false,
                    "angelabautista@gmail.com", true,
                    "123 Main St.", "Manila", "Metro Manila", "",
                    "1000", "NCR", "Philippines", false,
                    "Mobile", "(63+) 917 2256 432",
                    "Work", "(02) 8123-4567",
                    "", resourceToUriString(context, R.drawable.bautista_profile)
            );
            List<String> bautistaAllergies = new ArrayList<>();
            bautistaAllergies.add("Penicillin");
            p2.setAllergies(bautistaAllergies);

            // Add Documents for Angela
            p2.addDocument(new Document(R.drawable.prescription_summary_thumbnail, "Prescription", "11/01/2025"));
            p2.addDocument(new Document(R.drawable.discharge_summary_thumbnail, "Discharge Summary", "08/26/2025"));

            dummyData.add(p2);

            Patient p3 = new Patient(
                    "Michael", "J", "Cruz", "Mike",
                    "05/01/1983", "42", "Philippines", "Male",
                    "Cebu City", "Cebu", "1005-62041", "Married",
                    "Cebuano", "200365450", "Engineer", "MegaWorld",
                    "Masters Degree", "Catholic", "N/A", true,
                    "Cebuano", "2000002", false,
                    "michaelcruz@gmail.com", true,
                    "456 Ocean View Rd.", "Cebu", "Cebu", "",
                    "6000", "VII", "Philippines", true,
                    "Mobile", "(63+) 995 8457 210",
                    "Telephone", "(032) 567-8901",
                    "Regular checkups requested.", resourceToUriString(context, R.drawable.cruz_profile)
            );
            // No documents for Michael (Empty state test)
            dummyData.add(p3);

            Patient p4 = new Patient(
                    "Camille", "A", "Dela Rosa", "Camille",
                    "11/20/1998", "27", "Philippines", "Female",
                    "Davao City", "Davao Del Sur", "1005-62021", "Single",
                    "Bicolano", "200365451", "Student", "None",
                    "Undergraduate", "Atheist", "Vegan", false,
                    "English", "3000003", false,
                    "camille.dela.rosa@gmail.com", true,
                    "789 Pine Tree Lane", "Davao", "Davao Del Sur", "",
                    "8000", "XI", "Philippines", false,
                    "Mobile", "(63+) 921 7789 654",
                    "Work", "N/A",
                    "Strict Vegan diet.", resourceToUriString(context, R.drawable.dela_rosa_profile)
            );
            dummyData.add(p4);

            patientList.setValue(dummyData);
        }
    }

    private String resourceToUriString(Context context, int resId) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.getResources().getResourcePackageName(resId))
                .appendPath(context.getResources().getResourceTypeName(resId))
                .appendPath(context.getResources().getResourceEntryName(resId))
                .build()
                .toString();
    }
}
