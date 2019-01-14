package com.yyxnb.arch;

import java.io.Serializable;

public class TestData implements Serializable {


    /**
     * code : 200
     * message : 成功!
     * result : [{"title":"李白","content":"风骨神仙籍里人，诗狂酒圣且平生。|开元一遇成何事，留得千秋万古名。","authors":"徐钧"}]
     */



        /**
         * title : 李白
         * content : 风骨神仙籍里人，诗狂酒圣且平生。|开元一遇成何事，留得千秋万古名。
         * authors : 徐钧
         */

        private String title;
        private String content;
        private String authors;

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

        public String getAuthors() {
            return authors;
        }

        public void setAuthors(String authors) {
            this.authors = authors;
        }
}
