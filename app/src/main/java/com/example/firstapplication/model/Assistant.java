package com.example.firstapplication.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.example.firstapplication.shared.SharedPreferencesManager;
import com.example.firstapplication.shared.Utilities;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;
import static com.example.firstapplication.shared.Constant.SP_AUTH_TOKEN;
import static com.example.firstapplication.shared.Constant.SP_AUTH_TOKEN_LIFETIME;

@IgnoreExtraProperties

public class Assistant implements Parcelable {

    /**
     * Singleton use for authentication
     */

    private String initial;
    private String uId;
    private String email;

    private String password;

    private String token;
    @Exclude
    private boolean isShared;

    @SerializedName("UserId")
    @Expose
    private String userId;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Roles")
    @Expose
    private List<String> roles = null;
    @SerializedName("BinusianNumber")
    @Expose
    private String binusianNumber;

    @Exclude
    private static Assistant instance;

    private Assistant() {

    }

    private Assistant(String initial, String uId, String email) {
        this.initial = initial;
        this.uId = uId;
        this.email = email;
    }

    public static Assistant getInstance() {
        if (instance == null)
            instance = new Assistant();
        return instance;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public static Creator<Assistant> getCREATOR() {
        return CREATOR;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getBinusianNumber() {
        return binusianNumber;
    }

    public void setBinusianNumber(String binusianNumber) {
        this.binusianNumber = binusianNumber;
    }

    public static void setInstance(Assistant instance) {
        Assistant.instance = instance;
    }

    public static Assistant newInstance() {
        return new Assistant();
    }

    public static Assistant newInstance(String initial, String uId, String email) {
        return new Assistant(initial, uId, email);
    }

    @Exclude
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInitial() {
        return initial.toLowerCase();
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getPassword() {
        return password;
    }

    @Exclude
    public String getPlainPassword() {
        String data = Utilities.removeAllNewLineCharacters(password);
        final byte[] decode = Base64.decode(data, Base64.DEFAULT);
        String x = null;
        try {
            x = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return x;
    }

    public void setEncryptPassword(String password) {
        this.password = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Assistant(Parcel in) {
        this.initial = in.readString();
        this.uId = in.readString();
        this.email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.initial);
        parcel.writeString(this.uId);
        parcel.writeString(this.email);
    }

    public static final Creator<Assistant> CREATOR = new Creator<Assistant>() {
        @Override
        public Assistant createFromParcel(Parcel in) {
            return new Assistant(in);
        }

        @Override
        public Assistant[] newArray(int size) {
            return new Assistant[size];
        }
    };

    public static void saveIdentityToLocal(String token, String initial, Context context) {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);

        Assistant assistant = getInstance();
        assistant.setToken(token);
        assistant.setInitial(initial);

        sharedPreferencesManager.saveString(SP_AUTH_TOKEN, token);
        sharedPreferencesManager.saveString(SP_AUTH_INITIAL, assistant.getInitial());
        sharedPreferencesManager.saveLong(SP_AUTH_TOKEN_LIFETIME, new Date().getTime());
    }
}
