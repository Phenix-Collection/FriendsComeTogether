package com.yiwo.friendscometogether.model;

/**
 * Created by Administrator on 2018/9/13.
 */

public class JoinGetCommentInfoModel {

    /**
     * code : 200
     * message : 获取成功
     * obj : {"noname":"0","pfID":"131","title":"少女时代演唱会","content":"","picture":"http://47.92.136.19/uploads/xingcheng/20190228/b5985305073fb0d42a32d2e02fbb289a.png","time":"1970.01.01-1970.01.01","begin_time":"1970.01.01","end_time":"1970.01.01","go_num":"1","price":"0.01","price_type":"自费"}
     */

    private int code;
    private String message;
    private ObjBean obj;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        /**
         * noname : 0   0不是匿名 1是匿名
         * pfID : 131
         * title : 少女时代演唱会
         * content :
         * picture : http://47.92.136.19/uploads/xingcheng/20190228/b5985305073fb0d42a32d2e02fbb289a.png
         * time : 1970.01.01-1970.01.01
         * begin_time : 1970.01.01
         * end_time : 1970.01.01
         * go_num : 1
         * price : 0.01
         * price_type : 自费
         */

        private String noname;
        private String pfID;
        private String title;
        private String content;
        private String picture;
        private String time;
        private String begin_time;
        private String end_time;
        private String go_num;
        private String price;
        private String price_type;

        public String getNoname() {
            return noname;
        }

        public void setNoname(String noname) {
            this.noname = noname;
        }

        public String getPfID() {
            return pfID;
        }

        public void setPfID(String pfID) {
            this.pfID = pfID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getBegin_time() {
            return begin_time;
        }

        public void setBegin_time(String begin_time) {
            this.begin_time = begin_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getGo_num() {
            return go_num;
        }

        public void setGo_num(String go_num) {
            this.go_num = go_num;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPrice_type() {
            return price_type;
        }

        public void setPrice_type(String price_type) {
            this.price_type = price_type;
        }
    }
}
