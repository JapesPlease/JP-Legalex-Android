package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class LoginPojo {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
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
        @SerializedName("about_us")
        @Expose
        private String aboutUs;
        @SerializedName("information")
        @Expose
        private String information;
        @SerializedName("write_something")
        @Expose
        private String writeSomething;
        @SerializedName("parent_id")
        @Expose
        private Object parentId;
        @SerializedName("age")
        @Expose
        private String age;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("role")
        @Expose
        private Integer role;
        @SerializedName("facebook_id")
        @Expose
        private Object facebookId;
        @SerializedName("four_digit_code")
        @Expose
        private Object fourDigitCode;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("email_verified_at")
        @Expose
        private Object emailVerifiedAt;
        @SerializedName("device_type")
        @Expose
        private Integer deviceType;
        @SerializedName("fcm_token")
        @Expose
        private Object fcmToken;
        @SerializedName("badge")
        @Expose
        private Integer badge;

        @SerializedName("points")
        @Expose
        private Integer points;

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        @SerializedName("is_online")
        @Expose
        private Integer isOnline;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;


        @SerializedName("video_call_status")
        @Expose
        Long video_call_status;

        public Long getVideo_call_status() {
            return video_call_status;
        }

        public void setVideo_call_status(Long video_call_status) {
            this.video_call_status = video_call_status;
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

        public String getAboutUs() {
            return aboutUs;
        }

        public void setAboutUs(String aboutUs) {
            this.aboutUs = aboutUs;
        }

        public String getInformation() {
            return information;
        }

        public void setInformation(String information) {
            this.information = information;
        }

        public String getWriteSomething() {
            return writeSomething;
        }

        public void setWriteSomething(String writeSomething) {
            this.writeSomething = writeSomething;
        }

        public Object getParentId() {
            return parentId;
        }

        public void setParentId(Object parentId) {
            this.parentId = parentId;
        }

        public String getAge() {
            return age;
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

        public Object getFacebookId() {
            return facebookId;
        }

        public void setFacebookId(Object facebookId) {
            this.facebookId = facebookId;
        }

        public Object getFourDigitCode() {
            return fourDigitCode;
        }

        public void setFourDigitCode(Object fourDigitCode) {
            this.fourDigitCode = fourDigitCode;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public Object getEmailVerifiedAt() {
            return emailVerifiedAt;
        }

        public void setEmailVerifiedAt(Object emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
        }

        public Integer getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(Integer deviceType) {
            this.deviceType = deviceType;
        }

        public Object getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(Object fcmToken) {
            this.fcmToken = fcmToken;
        }

        public Integer getBadge() {
            return badge;
        }

        public void setBadge(Integer badge) {
            this.badge = badge;
        }

        public Integer getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Integer isOnline) {
            this.isOnline = isOnline;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(id, user.id) &&
                    Objects.equals(name, user.name) &&
                    Objects.equals(lastName, user.lastName) &&
                    Objects.equals(aboutUs, user.aboutUs) &&
                    Objects.equals(information, user.information) &&
                    Objects.equals(writeSomething, user.writeSomething) &&
                    Objects.equals(parentId, user.parentId) &&
                    Objects.equals(age, user.age) &&
                    Objects.equals(location, user.location) &&
                    Objects.equals(phone, user.phone) &&
                    Objects.equals(role, user.role) &&
                    Objects.equals(facebookId, user.facebookId) &&
                    Objects.equals(fourDigitCode, user.fourDigitCode) &&
                    Objects.equals(status, user.status) &&
                    Objects.equals(profileImage, user.profileImage) &&
                    Objects.equals(email, user.email) &&
                    Objects.equals(emailVerifiedAt, user.emailVerifiedAt) &&
                    Objects.equals(deviceType, user.deviceType) &&
                    Objects.equals(fcmToken, user.fcmToken) &&
                    Objects.equals(badge, user.badge) &&
                    Objects.equals(isOnline, user.isOnline) &&
                    Objects.equals(createdAt, user.createdAt) &&
                    Objects.equals(updatedAt, user.updatedAt);
        }

        @Override
        public int hashCode() {

            return Objects.hash(id, name, lastName, aboutUs, information, writeSomething, parentId, age, location, phone, role, facebookId, fourDigitCode, status, profileImage, email, emailVerifiedAt, deviceType, fcmToken, badge, isOnline, createdAt, updatedAt);
        }
    }
}