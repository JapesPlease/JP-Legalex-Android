package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetChatHistoryPojo {

    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("users")
        @Expose
        private Users users;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public Users getUsers() {
            return users;
        }

        public void setUsers(Users users) {
            this.users = users;
        }


        public class Users {

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

            public class Datum
            {

                @SerializedName("id")
                @Expose
                private Integer id;

                @SerializedName(("group_chat"))
                @Expose
                private String groupchat;

                @SerializedName(("lawyer_no"))
                @Expose
                private Integer lawyer_no;



                @SerializedName("name")
                @Expose
                private String name;
                @SerializedName("last_name")
                @Expose
                private String lastName;
                @SerializedName("last_message")
                @Expose
                private String lastMessage;
                @SerializedName("is_online")
                @Expose
                private Integer isOnline;

                @SerializedName("profile_image")
                @Expose
                private String profile_image;


                @SerializedName(("status"))
                int status;


                @SerializedName(("msg_count"))
                int msg_count;


                @SerializedName(("userdetails"))
                UserDetail userDetail;

                public UserDetail getUserDetail() {
                    return userDetail;
                }

                public void setUserDetail(UserDetail userDetail) {
                    this.userDetail = userDetail;
                }

                public String getGroupchat() {
                    return groupchat;
                }

                public void setGroupchat(String groupchat) {
                    this.groupchat = groupchat;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public String getProfile_image()
                {
                    return profile_image;
                }

                public void setProfile_image(String profile_image) {
                    this.profile_image = profile_image;
                }
                public Integer getIsOnline()
                {
                    return isOnline;
                }

                public void setIsOnline(Integer isOnline)
                {
                    this.isOnline = isOnline;
                }

                public Integer getId() {
                    return id;
                }

                public void setId(Integer id) {
                    this.id = id;
                }


                public Integer getLawyer_no() {
                    return lawyer_no;
                }

                public void setLawyer_no(Integer lawyer_no) {
                    this.lawyer_no = lawyer_no;
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

                public String getLastMessage() {
                    return lastMessage;
                }

                public void setLastMessage(String lastMessage) {
                    this.lastMessage = lastMessage;
                }


                public int getMsg_count() {
                    return msg_count;
                }

                public void setMsg_count(int msg_count) {
                    this.msg_count = msg_count;
                }



                public class UserDetail {

                    @SerializedName("id")
                    @Expose
                    private Integer id;
                    @SerializedName("name")
                    @Expose
                    private String name;

                    @SerializedName("last_name")
                    @Expose
                    private String last_name;

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

                    public String getLast_name() {
                        return last_name;
                    }

                    public void setLast_name(String last_name) {
                        this.last_name = last_name;
                    }
                }



            }
        }
    }
}