package Customer_Aidan;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class Quote implements Serializable {
    private String quoteId;
    private String customerId;
    private String gardenerId;
    private String gardenerName;
    private String fee;
    private String timeslot;
    private String notes;
    private String jobId;
    private String status;

    public Quote() {
    }

    public Quote(String quoteId, String customerId, String gardenerId, String gardenerName,
                 String fee, String timeslot, String notes, String jobId, String status) {
        this.quoteId = quoteId;
        this.customerId = customerId;
        this.gardenerId = gardenerId;
        this.gardenerName = gardenerName;
        this.fee = fee;
        this.timeslot = timeslot;
        this.notes = notes;
        this.jobId = jobId;
        this.status = status;
    }

    public static Quote fromDocument(DocumentSnapshot doc) {
        Quote q = new Quote();
        q.setQuoteId(doc.getId());

        // Customer ID
        if (doc.contains("customer")) {
            q.setCustomerId(doc.getString("customer"));
        } else if (doc.contains("customer_id")) {
            q.setCustomerId(doc.getString("customer_id"));
        } else if (doc.contains("customerId")) {
            q.setCustomerId(doc.getString("customerId"));
        }

        // Gardener ID
        if (doc.contains("gardener")) {
            q.setGardenerId(doc.getString("gardener"));
        } else if (doc.contains("gardener_id")) {
            q.setGardenerId(doc.getString("gardener_id"));
        } else if (doc.contains("gardenerId")) {
            q.setGardenerId(doc.getString("gardenerId"));
        }

        // Gardener Name
        if (doc.contains("gardener_name")) {
            q.setGardenerName(doc.getString("gardener_name"));
        } else if (doc.contains("gardenerName")) {
            q.setGardenerName(doc.getString("gardenerName"));
        }

        // Fee / Price
        if (doc.get("fee") != null) {
            q.setFee(String.valueOf(doc.get("fee")));
        } else if (doc.get("price") != null) {
            q.setFee(String.valueOf(doc.get("price")));
        } else {
            q.setFee("0.00");
        }

        // Timeslot / Date/Time
        if (doc.contains("timeslot")) {
            q.setTimeslot(doc.getString("timeslot"));
        } else if (doc.contains("time_slot")) {
            q.setTimeslot(doc.getString("time_slot"));
        } else if (doc.contains("date_time")) {
            q.setTimeslot(doc.getString("date_time"));
        } else if (doc.contains("dateTime")) {
            q.setTimeslot(doc.getString("dateTime"));
        } else {
            q.setTimeslot("N/A");
        }

        // Notes / Quote Notes
        if (doc.contains("notes")) {
            q.setNotes(doc.getString("notes"));
        } else if (doc.contains("quote_notes")) {
            q.setNotes(doc.getString("quote_notes"));
        } else if (doc.contains("description")) {
            q.setNotes(doc.getString("description"));
        } else {
            q.setNotes("");
        }

        // Job ID
        if (doc.contains("job_id")) {
            q.setJobId(doc.getString("job_id"));
        } else if (doc.contains("jobId")) {
            q.setJobId(doc.getString("jobId"));
        } else if (doc.contains("job")) {
            q.setJobId(doc.getString("job"));
        }

        // Status
        if (doc.contains("status")) {
            q.setStatus(doc.getString("status"));
        } else if (doc.contains("quote_status")) {
            q.setStatus(doc.getString("quote_status"));
        } else {
            q.setStatus("Pending");
        }

        return q;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGardenerId() {
        return gardenerId;
    }

    public void setGardenerId(String gardenerId) {
        this.gardenerId = gardenerId;
    }

    public String getGardenerName() {
        return gardenerName;
    }

    public void setGardenerName(String gardenerName) {
        this.gardenerName = gardenerName;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
