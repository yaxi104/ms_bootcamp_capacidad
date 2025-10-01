package co.com.reactive.model.capacity;

public class PageInfo {
    private Integer page;
    private Integer size;

    public PageInfo(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public PageInfo() {
    }

    public Integer getSize() {
        return size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}