package com.polije.sem3.util;

import android.content.Context;

import com.polije.sem3.datashare.DataShared;
import com.polije.sem3.model.UserModel;

public class UsersUtil {
    private final DataShared dataShared;

    public UsersUtil(Context context, UserModel model) {
        dataShared = new DataShared(context);

        if (model != null) {
            setId(String.valueOf(model.getIdUser()));
            setEmail(model.getEmailUser());
            setUsername(model.getUsername());
            setAlamat(model.getAlamat());
//            setVerified(model.getVerified());
            setUserPhoto(model.getGambar());
            setNoTelp(model.getNoTelepon());
        }
    }

    public UsersUtil(Context context) {
        this(context, null);
    }

    public boolean isSignIn() {
        return dataShared.contains(DataShared.KEY.ACC_USERNAME) &&
                !dataShared.getData(DataShared.KEY.ACC_USERNAME).isEmpty();
    }

    public String getId() {
        return dataShared.getData(DataShared.KEY.ACC_ID_USER);
    }

    public void setId(String id) {
        dataShared.setData(DataShared.KEY.ACC_ID_USER, id);
    }

    public String getNoTelp() {
        return dataShared.getData(DataShared.KEY.ACC_NOTELP);
    }

    public void setNoTelp(String noTelp) {
        dataShared.setData(DataShared.KEY.ACC_NOTELP, noTelp);
    }

    public String getAlamat() {
        return dataShared.getData(DataShared.KEY.ACC_ALAMAT);
    }

    public void setAlamat(String alamat) {
        dataShared.setData(DataShared.KEY.ACC_ALAMAT, alamat);
    }


    public String getUsername() {
        return dataShared.getData(DataShared.KEY.ACC_USERNAME);
    }

    public void setUsername(String username) {
        dataShared.setData(DataShared.KEY.ACC_USERNAME, username);
    }

    public String getEmail() {
        return dataShared.getData(DataShared.KEY.ACC_EMAIL);
    }

    public void setEmail(String email) {
        dataShared.setData(DataShared.KEY.ACC_EMAIL, email);
    }

    public String getFullName() {
        return dataShared.getData(DataShared.KEY.ACC_FULL_NAME);
    }

    public void setFullName(String name) {
        dataShared.setData(DataShared.KEY.ACC_FULL_NAME, name);
    }

    public String getLevel() {
        return dataShared.getData(DataShared.KEY.ACC_LEVEL);
    }

    public void setLevel(String level) {
        dataShared.setData(DataShared.KEY.ACC_LEVEL, level);
    }

    public String getVerified() {
        return dataShared.getData(DataShared.KEY.ACC_EMAIL_VERIFY);
    }

    public void setVerified(String verified) {
        dataShared.setData(DataShared.KEY.ACC_EMAIL_VERIFY, verified);
    }

    public void setUserPhoto(String newUserPhoto) {
        dataShared.setData(DataShared.KEY.ACC_PHOTO, newUserPhoto);
    }

    public String getUserPhoto() {
        return dataShared.getData(DataShared.KEY.ACC_PHOTO);
    }

    public String getCreated() {
        return dataShared.getData(DataShared.KEY.ACC_CREATED);
    }

    public void setCreated(String created) {
        dataShared.setData(DataShared.KEY.ACC_CREATED, created);
    }

    public void signOut() {
        dataShared.setNullData(DataShared.KEY.ACC_ID_USER);
        dataShared.setNullData(DataShared.KEY.ACC_USERNAME);
        dataShared.setNullData(DataShared.KEY.ACC_EMAIL);
        dataShared.setNullData(DataShared.KEY.ACC_FULL_NAME);
        dataShared.setNullData(DataShared.KEY.ACC_LEVEL);
        dataShared.setNullData(DataShared.KEY.ACC_PHOTO);
        dataShared.setNullData(DataShared.KEY.ACC_EMAIL_VERIFY);
        dataShared.setNullData(DataShared.KEY.ACC_CREATED);
    }

}
