package com.cc17.zenith;

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

    public void initializeDefaultPatients() {
        List<Patient> current = patientList.getValue();
        if (current == null || current.isEmpty()) {
            List<Patient> dummyData = new ArrayList<>();
            dummyData.add(new Patient("Julian", "R", "Alvarez", "46", "Male", "6202158", "0965 0568 555",
                    "12/21/1979", "Philippines", "Baguio City", "Benguet",
                    "Married", "Ilocano", "200365448", "Architect",
                    "Visionarch", "College Graduate", "Christian", "N/A",
                    "English", "1977232", true, true,
                    "julianalvarez@gmail.com", "142 Holy Ghost Hill Ext. Rd.", "Baguio",
                    "Benguet", "2600", "CAR", "Philippines",
                    "(63+) 927 910 7392", "(214) 723-9001", R.drawable.alvarez_profile));
            // Add other dummy patients...

            patientList.setValue(dummyData);
        }
    }
}