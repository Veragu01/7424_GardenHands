package Gardener_Uly;

public class Jobs {

    public String customer;
    public String gardener;
    public String job_description;
    public String job_status;
    public String job_title;
    public String job_type;
    public String location;
    public String repeating;
    public String size;

    public Jobs( String customer, String gardener, String job_description, String job_status, String job_title, String job_type, String location, String repeating, String size) {
        this.customer=customer;
        this.gardener=gardener;
        this.job_description = job_description;
        this.job_status = job_status;
        this.job_title = job_title;
        this.job_type = job_type;
        this.location = location;
        this.repeating = repeating;
        this.size = size;
    }

    public Jobs() {

    }

    public String getCustomerID() {
        return customer;
    }

    public String getGardenerID() {
        return gardener;
    }

    public String getJob_description() {
        return job_description;
    }

    public String getJob_status() {
        return job_status;
    }

    public String getJob_title() {
        return job_title;
    }

    public String getJob_type() {
        return job_type;
    }

    public String getLocation() {
        return location;
    }

    public String getRepeating() {
        return repeating;
    }

    public String getSize() {
        return size;
    }
}
