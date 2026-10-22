package Customer_Aidan;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class Job implements Serializable {
    private String jobId;
    private String beforePic;
    private String customer;
    private String dateTime;
    private String fee;
    private String gardener;
    private String jobDescription;
    private String jobStatus;
    private String jobTitle;
    private String jobType;
    private String location;
    private String size;

    public Job() {
    }

    public static Job fromDocument(DocumentSnapshot doc) {
        Job j = new Job();
        j.setJobId(doc.getId());

        if (doc.contains("before_pic")) j.setBeforePic(doc.getString("before_pic"));
        if (doc.contains("customer")) j.setCustomer(doc.getString("customer"));
        if (doc.contains("date_time")) j.setDateTime(doc.getString("date_time"));
        
        if (doc.get("fee") != null) {
            j.setFee(String.valueOf(doc.get("fee")));
        }
        
        if (doc.contains("gardener")) j.setGardener(doc.getString("gardener"));
        if (doc.contains("job_description")) j.setJobDescription(doc.getString("job_description"));
        if (doc.contains("job_status")) j.setJobStatus(doc.getString("job_status"));
        if (doc.contains("job_title")) j.setJobTitle(doc.getString("job_title"));
        if (doc.contains("job_type")) j.setJobType(doc.getString("job_type"));
        if (doc.contains("location")) j.setLocation(doc.getString("location"));
        if (doc.contains("size")) j.setSize(doc.getString("size"));

        return j;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getBeforePic() {
        return beforePic;
    }

    public void setBeforePic(String beforePic) {
        this.beforePic = beforePic;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getGardener() {
        return gardener;
    }

    public void setGardener(String gardener) {
        this.gardener = gardener;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
