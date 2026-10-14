
package Admin_Greg.Jobs.JobDetails;

public class Admin_Job {

    String documentId;
    private String before_pic;
    private String customer;
    private String date_time;
    private Double fee;
    private String gardener;
    private String job_description;
    private String job_status;
    private String job_title;
    private String job_type;
    private String location;
    private String repeating;
    private String size;

    public Admin_Job() {
        // Required for Firebase
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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerID() {
        return customer;
    }

    public String getDate_time() {
        return date_time;
    }

    public Double getFee() {
        return fee;
    }

    public String getGardener() {
        return gardener;
    }

    public void setGardener(String gardener) {
        this.gardener = gardener;
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
