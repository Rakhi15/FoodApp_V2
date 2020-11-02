package com.healthy.basket.Getset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadMedicalResult {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_detail")
    @Expose
    private UserDetail userDetail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }
    public class UserDetail {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("create_date")
        @Expose
        private String createDate;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

    }
}

