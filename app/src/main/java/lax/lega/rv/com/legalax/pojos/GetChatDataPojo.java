package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetChatDataPojo implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("chat")
    @Expose
    private Chat chat;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public class Chat implements Serializable{

        @SerializedName("current_page")
        @Expose
        private Integer currentPage;
        @SerializedName("data")
        @Expose
        private ArrayList<Datum> data = null;
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

        public ArrayList<Datum> getData() {
            return data;
        }

        public void setData(ArrayList<Datum> data) {
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

    public class Datum implements Serializable {


        @SerializedName("sender_name")
        @Expose
        private String sender_name;



        @SerializedName("receiver_name")
        @Expose
        private String receiver_name;

        @SerializedName("firmid")
        @Expose
        private Integer firmId;


        @SerializedName("id")
        @Expose
        private Integer id;


        @SerializedName("sender_id")
        @Expose
        private Integer sender_id;



        @SerializedName("receiver_id")
        @Expose
        private Integer receiver_id;


        @SerializedName("group_chat")
        @Expose
        private Integer group_chat;


        @SerializedName("message")
        @Expose
        private String message;



        @SerializedName("created_at")
        @Expose
        private String created_at;



        @SerializedName("updated_at")
        @Expose
        private String updated_at;


        @SerializedName("file")
        @Expose
        private String file;


        public Integer getFirmId() {
            return firmId;
        }

        public void setFirmId(Integer firmId) {
            this.firmId = firmId;
        }

        public Integer getGroup_chat() {
            return group_chat;
        }

        public void setGroup_chat(Integer group_chat) {
            this.group_chat = group_chat;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getSenderName() {
            return sender_name;
        }

        public void setSenderName(String senderName) {
            this.sender_name = senderName;
        }

        public String getReceiverName() {
            return receiver_name;
        }

        public void setReceiverName(String receiverName) {
            this.receiver_name = receiverName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSenderId() {
            return sender_id;
        }

        public void setSenderId(Integer senderId) {
            this.sender_id = senderId;
        }

        public Integer getReceiverId() {
            return receiver_id;
        }

        public void setReceiverId(Integer receiverId) {
            this.receiver_id = receiverId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCreatedAt() {
            return created_at;
        }

        public void setCreatedAt(String createdAt) {
            this.created_at = createdAt;
        }

        public String getUpdatedAt() {
            return updated_at;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updated_at = updatedAt;
        }

    }
}