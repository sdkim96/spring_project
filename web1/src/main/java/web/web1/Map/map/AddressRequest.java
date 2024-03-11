package web.web1.Map.map;

public class AddressRequest {
    private int currentPage;
    private int countPerPage;
    private String resultType;
    private String confmKey;
    private String keyword;

    // Getter and Setter methods

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getConfmKey() {
        return confmKey;
    }

    public void setConfmKey(String confmKey) {
        this.confmKey = confmKey;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
