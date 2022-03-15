package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMessagesPojo {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("last_message")
    @Expose
    private LastMessage lastMessage;

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

    public LastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }


    public static class LastMessage {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("sender_id")
        @Expose
        private Integer senderId;
        @SerializedName("receiver_id")
        @Expose
        private Integer receiverId;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("file")
        @Expose
        private String file;
        @SerializedName("seen_status")
        @Expose
        private Integer seenStatus;
        @SerializedName("firmid")
        @Expose
        private Object firmid;
        @SerializedName("group_chat")
        @Expose
        private Integer groupChat;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("lawyer_ids")
        @Expose
        private Object lawyerIds;
        @SerializedName("same_firm")
        @Expose
        private Integer sameFirm;
        @SerializedName("sender_name")
        @Expose
        private String senderName;
        @SerializedName("receiver_name")
        @Expose
        private String receiverName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSenderId() {
            return senderId;
        }

        public void setSenderId(Integer senderId) {
            this.senderId = senderId;
        }

        public Integer getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Integer receiverId) {
            this.receiverId = receiverId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public Integer getSeenStatus() {
            return seenStatus;
        }

        public void setSeenStatus(Integer seenStatus) {
            this.seenStatus = seenStatus;
        }

        public Object getFirmid() {
            return firmid;
        }

        public void setFirmid(Object firmid) {
            this.firmid = firmid;
        }

        public Integer getGroupChat() {
            return groupChat;
        }

        public void setGroupChat(Integer groupChat) {
            this.groupChat = groupChat;
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

        public Object getLawyerIds() {
            return lawyerIds;
        }

        public void setLawyerIds(Object lawyerIds) {
            this.lawyerIds = lawyerIds;
        }

        public Integer getSameFirm() {
            return sameFirm;
        }

        public void setSameFirm(Integer sameFirm) {
            this.sameFirm = sameFirm;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

    }
}
