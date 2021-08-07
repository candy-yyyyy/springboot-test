package cn.c7n6y.springboot.personal_test.pojo;

public class Content {
    private String price;
    private String title;
    private String pic;

    public Content(String price, String title, String pic) {
        this.price = price;
        this.title = title;
        this.pic = pic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "Content{" +
                "price='" + price + '\'' +
                ", title='" + title + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}
