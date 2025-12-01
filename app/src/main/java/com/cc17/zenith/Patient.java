package com.cc17.zenith;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Patient implements Parcelable {
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String preferredName;
    private String dob;
    private String age;
    private String countryOfBirth;
    private String sex;
    private String cityOfBirth;
    private String provinceOfBirth;
    private String fin;
    private String maritalStatus;
    private String raceEthnicity;
    private String mrn;
    private String occupation;
    private String employer;
    private String education;
    private String religion;
    private String preferences;
    private boolean isOrganDonor;
    private String langRecord;
    private String langRecordNo;
    private boolean hasLivingWill;
    private String email;
    private boolean isPersonalEmail;
    private String address1;
    private String city;
    private String province;
    private String address2;
    private String zipcode;
    private String region;
    private String country;
    private boolean isSameMail;
    private String primaryPhoneLabel;
    private String primaryPhoneNumber;
    private String secondaryPhoneLabel;
    private String secondaryPhoneNumber;
    private String remarks;
    private int profileImage;
    private List<Document> documents;

    public Patient(String firstName, String middleInitial, String lastName, String preferredName,
                   String dob, String age, String countryOfBirth, String sex, String cityOfBirth,
                   String provinceOfBirth, String fin, String maritalStatus, String raceEthnicity,
                   String mrn, String occupation, String employer, String education, String religion,
                   String preferences, boolean isOrganDonor, String langRecord, String langRecordNo,
                   boolean hasLivingWill, String email, boolean isPersonalEmail,
                   String address1, String city, String province, String address2, String zipcode,
                   String region, String country, boolean isSameMail, String primaryPhoneLabel,
                   String primaryPhoneNumber, String secondaryPhoneLabel, String secondaryPhoneNumber,
                   String remarks, int profileImage) {

        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.dob = dob;
        this.age = age;
        this.countryOfBirth = countryOfBirth;
        this.sex = sex;
        this.cityOfBirth = cityOfBirth;
        this.provinceOfBirth = provinceOfBirth;
        this.fin = fin;
        this.maritalStatus = maritalStatus;
        this.raceEthnicity = raceEthnicity;
        this.mrn = mrn;
        this.occupation = occupation;
        this.employer = employer;
        this.education = education;
        this.religion = religion;
        this.preferences = preferences;
        this.isOrganDonor = isOrganDonor;
        this.langRecord = langRecord;
        this.langRecordNo = langRecordNo;
        this.hasLivingWill = hasLivingWill;
        this.email = email;
        this.isPersonalEmail = isPersonalEmail;
        this.address1 = address1;
        this.city = city;
        this.province = province;
        this.address2 = address2;
        this.zipcode = zipcode;
        this.region = region;
        this.country = country;
        this.isSameMail = isSameMail;
        this.primaryPhoneLabel = primaryPhoneLabel;
        this.primaryPhoneNumber = primaryPhoneNumber;
        this.secondaryPhoneLabel = secondaryPhoneLabel;
        this.secondaryPhoneNumber = secondaryPhoneNumber;
        this.remarks = remarks;
        this.profileImage = profileImage;
        this.documents = new ArrayList<>();
    }

    protected Patient(Parcel in) {
        firstName = in.readString();
        middleInitial = in.readString();
        lastName = in.readString();
        preferredName = in.readString();
        dob = in.readString();
        age = in.readString();
        countryOfBirth = in.readString();
        sex = in.readString();
        cityOfBirth = in.readString();
        provinceOfBirth = in.readString();
        fin = in.readString();
        maritalStatus = in.readString();
        raceEthnicity = in.readString();
        mrn = in.readString();
        occupation = in.readString();
        employer = in.readString();
        education = in.readString();
        religion = in.readString();
        preferences = in.readString();
        isOrganDonor = in.readByte() != 0;
        langRecord = in.readString();
        langRecordNo = in.readString();
        hasLivingWill = in.readByte() != 0;
        email = in.readString();
        isPersonalEmail = in.readByte() != 0;
        address1 = in.readString();
        city = in.readString();
        province = in.readString();
        address2 = in.readString();
        zipcode = in.readString();
        region = in.readString();
        country = in.readString();
        isSameMail = in.readByte() != 0;
        primaryPhoneLabel = in.readString();
        primaryPhoneNumber = in.readString();
        secondaryPhoneLabel = in.readString();
        secondaryPhoneNumber = in.readString();
        remarks = in.readString();
        profileImage = in.readInt();

        documents = in.createTypedArrayList(Document.CREATOR);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public String getDob() {
        return dob;
    }

    public String getAge() {
        return age;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public String getCityOfBirth() {
        return cityOfBirth;
    }

    public String getProvinceOfBirth() {
        return provinceOfBirth;
    }

    public String getFin() {
        return fin;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getRaceEthnicity() {
        return raceEthnicity;
    }

    public String getMrn() {
        return mrn;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getEmployer() {
        return employer;
    }

    public String getEducation() {
        return education;
    }

    public String getReligion() {
        return religion;
    }

    public String getPreferences() {
        return preferences;
    }

    public boolean isOrganDonor() {
        return isOrganDonor;
    }

    public String getLangRecord() {
        return langRecord;
    }

    public String getLangRecordNo() {
        return langRecordNo;
    }

    public boolean hasLivingWill() {
        return hasLivingWill;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPersonalEmail() {
        return isPersonalEmail;
    }

    public String getAddress1() {
        return address1;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getAddress2() {
        return address2;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public boolean isSameMail() {
        return isSameMail;
    }

    public String getPrimaryPhoneLabel() {
        return primaryPhoneLabel;
    }

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public String getSecondaryPhoneLabel() {
        return secondaryPhoneLabel;
    }

    public String getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public List<Document> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<>();
        }
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(middleInitial);
        dest.writeString(lastName);
        dest.writeString(preferredName);
        dest.writeString(dob);
        dest.writeString(age);
        dest.writeString(countryOfBirth);
        dest.writeString(sex);
        dest.writeString(cityOfBirth);
        dest.writeString(provinceOfBirth);
        dest.writeString(fin);
        dest.writeString(maritalStatus);
        dest.writeString(raceEthnicity);
        dest.writeString(mrn);
        dest.writeString(occupation);
        dest.writeString(employer);
        dest.writeString(education);
        dest.writeString(religion);
        dest.writeString(preferences);
        dest.writeByte((byte) (isOrganDonor ? 1 : 0));
        dest.writeString(langRecord);
        dest.writeString(langRecordNo);
        dest.writeByte((byte) (hasLivingWill ? 1 : 0));
        dest.writeString(email);
        dest.writeByte((byte) (isPersonalEmail ? 1 : 0));
        dest.writeString(address1);
        dest.writeString(city);
        dest.writeString(province);
        dest.writeString(address2);
        dest.writeString(zipcode);
        dest.writeString(region);
        dest.writeString(country);
        dest.writeByte((byte) (isSameMail ? 1 : 0));
        dest.writeString(primaryPhoneLabel);
        dest.writeString(primaryPhoneNumber);
        dest.writeString(secondaryPhoneLabel);
        dest.writeString(secondaryPhoneNumber);
        dest.writeString(remarks);
        dest.writeInt(profileImage);
        dest.writeTypedList(documents);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };
}
