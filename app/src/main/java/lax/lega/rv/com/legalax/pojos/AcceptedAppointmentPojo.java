package lax.lega.rv.com.legalax.pojos;

import android.support.design.widget.TextInputLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcceptedAppointmentPojo {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("list")
    @Expose
    private List list;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public class List {

        @SerializedName("current_page")
        @Expose
        private Integer currentPage;
        @SerializedName("data")
        @Expose
        private java.util.List<Datum> data = null;
        @SerializedName("first_page_url")
        @Expose
        private String firstPageUrl;
        @SerializedName("from")
        @Expose
        private Integer from;
        @SerializedName("last_page")
        @Expose
        private Integer lastPage;
        @SerializedName("last_page_url")
        @Expose
        private String lastPageUrl;
        @SerializedName("next_page_url")
        @Expose
        private Object nextPageUrl;
        @SerializedName("path")
        @Expose
        private String path;
        @SerializedName("per_page")
        @Expose
        private Integer perPage;
        @SerializedName("prev_page_url")
        @Expose
        private Object prevPageUrl;
        @SerializedName("to")
        @Expose
        private Integer to;
        @SerializedName("total")
        @Expose
        private Integer total;

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public java.util.List<Datum> getData() {
            return data;
        }

        public void setData(java.util.List<Datum> data) {
            this.data = data;
        }

        public String getFirstPageUrl() {
            return firstPageUrl;
        }

        public void setFirstPageUrl(String firstPageUrl) {
            this.firstPageUrl = firstPageUrl;
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }

        public Integer getLastPage() {
            return lastPage;
        }

        public void setLastPage(Integer lastPage) {
            this.lastPage = lastPage;
        }

        public String getLastPageUrl() {
            return lastPageUrl;
        }

        public void setLastPageUrl(String lastPageUrl) {
            this.lastPageUrl = lastPageUrl;
        }

        public Object getNextPageUrl() {
            return nextPageUrl;
        }

        public void setNextPageUrl(Object nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Integer getPerPage() {
            return perPage;
        }

        public void setPerPage(Integer perPage) {
            this.perPage = perPage;
        }

        public Object getPrevPageUrl() {
            return prevPageUrl;
        }

        public void setPrevPageUrl(Object prevPageUrl) {
            this.prevPageUrl = prevPageUrl;
        }

        public Integer getTo() {
            return to;
        }

        public void setTo(Integer to) {
            this.to = to;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }


        public class Datum {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("lawyer_id")
            @Expose
            private Integer lawyerId;
            @SerializedName("delete_by_user")
            @Expose
            private Integer deleteByUser;
            @SerializedName("delete_by_lawyer")
            @Expose
            private Integer deleteByLawyer;

            public Integer getDeleteByUser() {
                return deleteByUser;
            }

            public void setDeleteByUser(Integer deleteByUser) {
                this.deleteByUser = deleteByUser;
            }

            public Integer getDeleteByLawyer() {
                return deleteByLawyer;
            }

            public void setDeleteByLawyer(Integer deleteByLawyer) {
                this.deleteByLawyer = deleteByLawyer;
            }

            public String getLawyerFullname() {

                return lawyerFullname;
            }

            public void setLawyerFullname(String lawyerFullname) {
                this.lawyerFullname = lawyerFullname;
            }

            public String getReguserFullname() {
                return reguserFullname;
            }

            public void setReguserFullname(String reguserFullname) {
                this.reguserFullname = reguserFullname;
            }

            @SerializedName("user_id")

            @Expose
            private Integer userId;
            @SerializedName("booking_date")
            @Expose
            private String bookingDate;
            @SerializedName("time")
            @Expose
            private String time;
            @SerializedName("concern")
            @Expose
            private String concern;
            @SerializedName("status")
            @Expose
            private Integer status;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("lawyer_fullname")
            @Expose
            private String lawyerFullname;
            @SerializedName("reguser_fullname")
            @Expose
            private String reguserFullname;

            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("user")
            @Expose
            private User user;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getLawyerId() {
                return lawyerId;
            }

            public void setLawyerId(Integer lawyerId) {
                this.lawyerId = lawyerId;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public String getBookingDate() {
                return bookingDate;
            }

            public void setBookingDate(String bookingDate) {
                this.bookingDate = bookingDate;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getConcern() {
                return concern;
            }

            public void setConcern(String concern) {
                this.concern = concern;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
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

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
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
                @SerializedName("created_at")
                @Expose
                private String createdAt;
                @SerializedName("updated_at")
                @Expose
                private String updatedAt;

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
    }
}

