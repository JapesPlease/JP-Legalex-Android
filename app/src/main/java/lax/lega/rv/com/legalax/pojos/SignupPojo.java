package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignupPojo {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class User {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("age")
        @Expose
        private String age;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("phone")
        @Expose
        private String phone;

        public String getFcm_token() {
            return fcm_token;
        }

        public void setFcm_token(String fcm_token) {
            this.fcm_token = fcm_token;
        }

        @SerializedName("fcm_token")
        @Expose

        private String fcm_token;
        @SerializedName("role")
        @Expose
        private Integer role;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("four_digit_code")
        @Expose
        private Object fourDigitCode;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("email_verified_at")
        @Expose
        private Object emailVerifiedAt;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("profile_image")
        @Expose
        private String profile_image;

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAge() {
            return age;
        }

        @Override
        public String toString()
        {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", age=" + age +
                    ", location='" + location + '\'' +
                    ", phone='" + phone + '\'' +
                    ", fcm_token='" + fcm_token + '\'' +
                    ", role=" + role +
                    ", status=" + status +
                    ", fourDigitCode=" + fourDigitCode +
                    ", email='" + email + '\'' +
                    ", emailVerifiedAt=" + emailVerifiedAt +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    ", profile_image='" + profile_image + '\'' +
                    '}';
        }


        public void setAge(String age) {
            this.age = age;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getRole() {
            return role;
        }

        public void setRole(Integer role) {
            this.role = role;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Object getFourDigitCode() {
            return fourDigitCode;
        }

        public void setFourDigitCode(Object fourDigitCode) {
            this.fourDigitCode = fourDigitCode;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Object getEmailVerifiedAt() {
            return emailVerifiedAt;
        }

        public void setEmailVerifiedAt(Object emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}