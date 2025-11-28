package com.cc17.zenith;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String age;
    private String sex;
    private String idNo;
    private String mobileNo;
    private String dob;
    private String countryOfBirth;
    private String cityOfBirth;
    private String provinceOfBirth;
    private String maritalStatus;
    private String raceEthnicity;
    private String mrn;
    private String occupation;
    private String employer;
    private String education;
    private String religion;
    private String preferences;
    private String langRecord;
    private String langRecordNo;
    private boolean isOrganDonor;
    private boolean isLivingWill;
    private String email;
    private String address1;
    private String city;
    private String province;
    private String zip;
    private String region;
    private String country;
    private String primaryPhone;
    private String secondaryPhone;
    private int profileImage;

    public Patient(String firstName, String middleInitial, String lastName, String age, String sex, String idNo, String mobileNo, String dob, String countryOfBirth, String cityOfBirth, String provinceOfBirth, String maritalStatus, String raceEthnicity, String mrn, String occupation, String employer, String education, String religion, String preferences, String langRecord, String langRecordNo, boolean isOrganDonor, boolean isLivingWill, String email, String address1, String city, String province, String zip, String region, String country, String primaryPhone, String secondaryPhone, int profileImage) {
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.age = age;
        this.sex = sex;
        this.idNo = idNo;
        this.mobileNo = mobileNo;
        this.dob = dob;
        this.countryOfBirth = countryOfBirth;
        this.cityOfBirth = cityOfBirth;
        this.provinceOfBirth = provinceOfBirth;
        this.maritalStatus = maritalStatus;
        this.raceEthnicity = raceEthnicity;
        this.mrn = mrn;
        this.occupation = occupation;
        this.employer = employer;
        this.education = education;
        this.religion = religion;
        this.preferences = preferences;
        this.langRecord = langRecord;
        this.langRecordNo = langRecordNo;
        this.isOrganDonor = isOrganDonor;
        this.isLivingWill = isLivingWill;
        this.email = email;
        this.address1 = address1;
        this.city = city;
        this.province = province;
        this.zip = zip;
        this.region = region;
        this.country = country;
        this.primaryPhone = primaryPhone;
        this.secondaryPhone = secondaryPhone;
        this.profileImage = profileImage;
    }

    protected Patient(Parcel in) {
        firstName = in.readString();
        middleInitial = in.readString();
        lastName = in.readString();
        age = in.readString();
        sex = in.readString();
        idNo = in.readString();
        mobileNo = in.readString();
        dob = in.readString();
        countryOfBirth = in.readString();
        cityOfBirth = in.readString();
        provinceOfBirth = in.readString();
        maritalStatus = in.readString();
        raceEthnicity = in.readString();
        mrn = in.readString();
        occupation = in.readString();
        employer = in.readString();
        education = in.readString();
        religion = in.readString();
        preferences = in.readString();
        langRecord = in.readString();
        langRecordNo = in.readString();
        isOrganDonor = in.readByte() != 0;
        isLivingWill = in.readByte() != 0;
        email = in.readString();
        address1 = in.readString();
        city = in.readString();
        province = in.readString();
        zip = in.readString();
        region = in.readString();
        country = in.readString();
        primaryPhone = in.readString();
        secondaryPhone = in.readString();
        profileImage = in.readInt();
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

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getIdNo() {
        return idNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getDob() {
        return dob;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public String getCityOfBirth() {
        return cityOfBirth;
    }

    public String getProvinceOfBirth() {
        return provinceOfBirth;
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

    public String getLangRecord() {
        return langRecord;
    }

    public String getLangRecordNo() {
        return langRecordNo;
    }

    public boolean isOrganDonor() {
        return isOrganDonor;
    }

    public boolean isLivingWill() {
        return isLivingWill;
    }

    public String getEmail() {
        return email;
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

    public String getZip() {
        return zip;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public int getProfileImage() {
        return profileImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(middleInitial);
        dest.writeString(lastName);
        dest.writeString(age);
        dest.writeString(sex);
        dest.writeString(idNo);
        dest.writeString(mobileNo);
        dest.writeString(dob);
        dest.writeString(countryOfBirth);
        dest.writeString(cityOfBirth);
        dest.writeString(provinceOfBirth);
        dest.writeString(maritalStatus);
        dest.writeString(raceEthnicity);
        dest.writeString(mrn);
        dest.writeString(occupation);
        dest.writeString(employer);
        dest.writeString(education);
        dest.writeString(religion);
        dest.writeString(preferences);
        dest.writeString(langRecord);
        dest.writeString(langRecordNo);
        dest.writeByte((byte) (isOrganDonor ? 1 : 0));
        dest.writeByte((byte) (isLivingWill ? 1 : 0));
        dest.writeString(email);
        dest.writeString(address1);
        dest.writeString(city);
        dest.writeString(province);
        dest.writeString(zip);
        dest.writeString(region);
        dest.writeString(country);
        dest.writeString(primaryPhone);
        dest.writeString(secondaryPhone);
        dest.writeInt(profileImage);
    }
}
