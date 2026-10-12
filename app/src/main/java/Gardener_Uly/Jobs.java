package Gardener_Uly;

public class Jobs {
    public String before_pic;
    public String documentId;
    public String customer;
    public String date_time;
    public Double fee;
    public String gardener;
    public String job_description;
    public String job_status;
    public String job_title;
    public String job_type;
    public String location;
    public String repeating;
    public String size;

    public Jobs( String before_pic, String customer, String date_time, Double fee, String gardener, String job_description, String job_status, String job_title, String job_type, String location, String repeating, String size) {
        this.before_pic=before_pic;
        this.customer=customer;
        this.date_time = date_time;
        this.fee = fee;
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

    public String getBefore_pic() {
        return before_pic;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCustomerID() {
        return customer;
    }

    public String getDate_time() {return date_time;}

    public Double getFee() {return fee;}

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
