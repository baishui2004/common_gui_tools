package bs.tool.commongui.utils.interest;

/**
 * 还款计息方式.
 */
public enum LendType {
    /**
     * 等额本息.
     */
    Interest("等额本息"),
    /**
     * 等额本金.
     */
    Principal("等额本金");

    LendType(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }
}
