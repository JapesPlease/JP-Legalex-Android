package lax.lega.rv.com.legalax.fragment;

import java.util.List;

public class MyLogic {
    private Boolean success;
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private Integer currentPage;
        private List<Datum> data = null;
        private String firstPageUrl;
        private Integer from;
        private Integer lastPage;
        private String lastPageUrl;
        private Object nextPageUrl;
        private String path;
        private Integer perPage;
        private Object prevPageUrl;
        private Integer to;
        private Integer total;

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
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
    }

    public class Datum {

        private Integer id;
        private Integer userId;
        private Integer categoryId;
        private String employerName;
        private String communityName;
        private String managemnetName;
        private String postingName;
        private String employerEmail;
        private String employerPhone;
        private String communityAddress;
        private String latitude;
        private String longitude;
        private String postalCode;
        private Object city;
        private Object state;
        private String paymentMethod;
        private String jobDuration;
        private String startDate;
        private String endDate;
        private String skill;
        private String experience;
        private String multipalHeroes;
        private String unitNumber;
        private String bedrooms;
        private String colorChange;
        private String partialOrFull;
        private String additionalRepairs;
        private String standardClean;
        private String counterTops;
        private String bathtubs;
        private String tubSurrounds;
        private String necessaryParts;
        private String punch;
        private String sinks;
        private String emergencyTime;
        private String attachment;
        private String jobDescription;
        private String status;
        private String createdAt;
        private String updatedAt;
        private Userinfo userinfo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public String getEmployerName() {
            return employerName;
        }

        public void setEmployerName(String employerName) {
            this.employerName = employerName;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public String getManagemnetName() {
            return managemnetName;
        }

        public void setManagemnetName(String managemnetName) {
            this.managemnetName = managemnetName;
        }

        public String getPostingName() {
            return postingName;
        }

        public void setPostingName(String postingName) {
            this.postingName = postingName;
        }

        public String getEmployerEmail() {
            return employerEmail;
        }

        public void setEmployerEmail(String employerEmail) {
            this.employerEmail = employerEmail;
        }

        public String getEmployerPhone() {
            return employerPhone;
        }

        public void setEmployerPhone(String employerPhone) {
            this.employerPhone = employerPhone;
        }

        public String getCommunityAddress() {
            return communityAddress;
        }

        public void setCommunityAddress(String communityAddress) {
            this.communityAddress = communityAddress;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
            this.city = city;
        }

        public Object getState() {
            return state;
        }

        public void setState(Object state) {
            this.state = state;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getJobDuration() {
            return jobDuration;
        }

        public void setJobDuration(String jobDuration) {
            this.jobDuration = jobDuration;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getMultipalHeroes() {
            return multipalHeroes;
        }

        public void setMultipalHeroes(String multipalHeroes) {
            this.multipalHeroes = multipalHeroes;
        }

        public String getUnitNumber() {
            return unitNumber;
        }

        public void setUnitNumber(String unitNumber) {
            this.unitNumber = unitNumber;
        }

        public String getBedrooms() {
            return bedrooms;
        }

        public void setBedrooms(String bedrooms) {
            this.bedrooms = bedrooms;
        }

        public String getColorChange() {
            return colorChange;
        }

        public void setColorChange(String colorChange) {
            this.colorChange = colorChange;
        }

        public String getPartialOrFull() {
            return partialOrFull;
        }

        public void setPartialOrFull(String partialOrFull) {
            this.partialOrFull = partialOrFull;
        }

        public String getAdditionalRepairs() {
            return additionalRepairs;
        }

        public void setAdditionalRepairs(String additionalRepairs) {
            this.additionalRepairs = additionalRepairs;
        }

        public String getStandardClean() {
            return standardClean;
        }

        public void setStandardClean(String standardClean) {
            this.standardClean = standardClean;
        }

        public String getCounterTops() {
            return counterTops;
        }

        public void setCounterTops(String counterTops) {
            this.counterTops = counterTops;
        }

        public String getBathtubs() {
            return bathtubs;
        }

        public void setBathtubs(String bathtubs) {
            this.bathtubs = bathtubs;
        }

        public String getTubSurrounds() {
            return tubSurrounds;
        }

        public void setTubSurrounds(String tubSurrounds) {
            this.tubSurrounds = tubSurrounds;
        }

        public String getNecessaryParts() {
            return necessaryParts;
        }

        public void setNecessaryParts(String necessaryParts) {
            this.necessaryParts = necessaryParts;
        }

        public String getPunch() {
            return punch;
        }

        public void setPunch(String punch) {
            this.punch = punch;
        }

        public String getSinks() {
            return sinks;
        }

        public void setSinks(String sinks) {
            this.sinks = sinks;
        }

        public String getEmergencyTime() {
            return emergencyTime;
        }

        public void setEmergencyTime(String emergencyTime) {
            this.emergencyTime = emergencyTime;
        }

        public String getAttachment() {
            return attachment;
        }

        public void setAttachment(String attachment) {
            this.attachment = attachment;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
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

        public Userinfo getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(Userinfo userinfo) {
            this.userinfo = userinfo;
        }
    }

    public class Userinfo {

        private Integer id;
        private Integer role;
        private String name;
        private String email;
        private String phoneNumber;
        private Object facebookUrl;
        private Object twitterUrl;
        private Object image;
        private Integer status;
        private String createdAt;
        private String updatedAt;
        private Object city;
        private Object country;
        private Object latitude;
        private Object longitude;
        private String fcmToken;
        private String deviceType;
        private Object occupation;
        private Integer isOnline;
        private Object catIds;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getRole() {
            return role;
        }

        public void setRole(Integer role) {
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Object getFacebookUrl() {
            return facebookUrl;
        }

        public void setFacebookUrl(Object facebookUrl) {
            this.facebookUrl = facebookUrl;
        }

        public Object getTwitterUrl() {
            return twitterUrl;
        }

        public void setTwitterUrl(Object twitterUrl) {
            this.twitterUrl = twitterUrl;
        }

        public Object getImage() {
            return image;
        }

        public void setImage(Object image) {
            this.image = image;
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

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
            this.city = city;
        }

        public Object getCountry() {
            return country;
        }

        public void setCountry(Object country) {
            this.country = country;
        }

        public Object getLatitude() {
            return latitude;
        }

        public void setLatitude(Object latitude) {
            this.latitude = latitude;
        }

        public Object getLongitude() {
            return longitude;
        }

        public void setLongitude(Object longitude) {
            this.longitude = longitude;
        }

        public String getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(String fcmToken) {
            this.fcmToken = fcmToken;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public Object getOccupation() {
            return occupation;
        }

        public void setOccupation(Object occupation) {
            this.occupation = occupation;
        }

        public Integer getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Integer isOnline) {
            this.isOnline = isOnline;
        }

        public Object getCatIds() {
            return catIds;
        }

        public void setCatIds(Object catIds) {
            this.catIds = catIds;
        }

    }
}
