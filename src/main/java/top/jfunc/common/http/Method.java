package top.jfunc.common.http;

/**
 * Http方法枚举
 * @author Looly
 * @author xiongshiyan
 */
public enum Method {
	/**
	 * HTTP相关方法
	 */
	GET {
        @Override
        public boolean hasContent() {
            return false;
        }
    }, POST {
        @Override
        public boolean hasContent() {
            return true;
        }
    }, HEAD {
        @Override
        public boolean hasContent() {
            return false;
        }
    }, OPTIONS {
        @Override
        public boolean hasContent() {
            return false;
        }
    }, PUT {
        @Override
        public boolean hasContent() {
            return true;
        }
    }, DELETE {
        @Override
        public boolean hasContent() {
            return false;
        }
    }, TRACE {
        @Override
        public boolean hasContent() {
            return false;
        }
    }, PATCH {
        @Override
        public boolean hasContent() {
            return true;
        }
    };

    /**
     * 需要写数据吗？
     */
    public abstract boolean hasContent();
}
