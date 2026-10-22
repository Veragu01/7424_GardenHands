package Customer_Aidan;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

public class Quote implements Serializable {
    private String quoteId;
    private String customerId;
    private String gardenerId;
    private String gardenerName;
    private String fee;
    private String timeslotId;
    private String date;
    private String session;
    private String timeslot;
    private String notes;
    private String jobId;
    private String status;

    public Quote() {
    }

    public Quote(String quoteId, String customerId, String gardenerId, String gardenerName,
                 String fee, String timeslotId, String date, String session, String timeslot,
                 String notes, String jobId, String status) {
        this.quoteId = quoteId;
        this.customerId = customerId;
        this.gardenerId = gardenerId;
        this.gardenerName = gardenerName;
        this.fee = fee;
        this.timeslotId = timeslotId;
        this.date = date;
        this.session = session;
        this.timeslot = timeslot;
        this.notes = notes;
        this.jobId = jobId;
        this.status = status;
    }

    private static Object getValueCaseInsensitive(Map<String, Object> data, String... keys) {
        if (data == null) return null;
        for (String key : keys) {
            if (data.containsKey(key) && data.get(key) != null) {
                return data.get(key);
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public static Quote fromDocument(DocumentSnapshot doc) {
        Quote q = new Quote();
        q.setQuoteId(doc.getId());

        Map<String, Object> data = doc.getData();

        // Customer ID
        Object customerObj = getValueCaseInsensitive(data, "customer", "customer_id", "customerId", "customerID");
        if (customerObj instanceof DocumentReference) {
            q.setCustomerId(((DocumentReference) customerObj).getId());
        } else if (customerObj != null) {
            q.setCustomerId(String.valueOf(customerObj));
        }

        // Gardener ID
        Object gardenerObj = getValueCaseInsensitive(data, "gardener", "gardener_id", "gardenerId", "gardenerID");
        if (gardenerObj instanceof DocumentReference) {
            q.setGardenerId(((DocumentReference) gardenerObj).getId());
        } else if (gardenerObj != null) {
            q.setGardenerId(String.valueOf(gardenerObj));
        }

        // Gardener Name
        Object gardenerNameObj = getValueCaseInsensitive(data, "gardener_name", "gardenerName", "gardenerNAME", "name");
        if (gardenerNameObj != null) {
            q.setGardenerName(String.valueOf(gardenerNameObj));
        }

        // Fee / Price
        Object feeObj = getValueCaseInsensitive(data, "fee", "price", "amount", "cost", "quote_fee");
        if (feeObj != null) {
            q.setFee(String.valueOf(feeObj));
        } else {
            q.setFee("0.00");
        }

        // Timeslot ID
        Object tsIdObj = getValueCaseInsensitive(data, "timeslotID", "timeslot_id", "timeslotId", "time_slot_id", "timeslot_ref", "timeslotRef");
        if (tsIdObj instanceof DocumentReference) {
            q.setTimeslotId(((DocumentReference) tsIdObj).getId());
        } else if (tsIdObj != null) {
            q.setTimeslotId(String.valueOf(tsIdObj));
        }

        // Date
        Object dateObj = getValueCaseInsensitive(data, "date", "quote_date", "date_time", "dateTime", "day");
        if (dateObj != null) {
            q.setDate(String.valueOf(dateObj));
        }

        // Session
        Object sessionObj = getValueCaseInsensitive(data, "session", "quote_session", "session_time", "time_session", "slot");
        if (sessionObj != null) {
            q.setSession(String.valueOf(sessionObj));
        }

        // Timeslot / Raw time
        Object timeObj = getValueCaseInsensitive(data, "timeslot", "time_slot", "timeSlot", "date_time", "dateTime", "time");
        if (timeObj instanceof DocumentReference) {
            if (q.getTimeslotId() == null || q.getTimeslotId().isEmpty()) {
                q.setTimeslotId(((DocumentReference) timeObj).getId());
            }
            q.setTimeslot(((DocumentReference) timeObj).getId());
        } else if (timeObj != null) {
            q.setTimeslot(String.valueOf(timeObj));
            if (q.getTimeslotId() == null || q.getTimeslotId().isEmpty()) {
                q.setTimeslotId(String.valueOf(timeObj));
            }
        } else {
            q.setTimeslot("N/A");
        }

        // Notes / Quote Notes
        Object notesObj = getValueCaseInsensitive(data, "notes", "quote_notes", "quoteNotes", "description", "note");
        if (notesObj != null) {
            q.setNotes(String.valueOf(notesObj));
        } else {
            q.setNotes("");
        }

        // Job ID
        Object jobObj = getValueCaseInsensitive(data, "job_id", "jobID", "jobId", "job", "job_doc", "jobRef");
        if (jobObj instanceof DocumentReference) {
            q.setJobId(((DocumentReference) jobObj).getId());
        } else if (jobObj != null) {
            q.setJobId(String.valueOf(jobObj));
        }

        // Status
        Object statusObj = getValueCaseInsensitive(data, "status", "quote_status", "quoteStatus", "state");
        if (statusObj != null) {
            q.setStatus(String.valueOf(statusObj));
        } else {
            q.setStatus("Pending");
        }

        return q;
    }

    public String getFormattedTimeslot() {
        StringBuilder sb = new StringBuilder();
        if (date != null && !date.trim().isEmpty() && !"N/A".equalsIgnoreCase(date)) {
            sb.append("Date: ").append(date);
        }
        if (session != null && !session.trim().isEmpty() && !"N/A".equalsIgnoreCase(session)) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append("Session: ").append(session);
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        if (timeslot != null && !timeslot.trim().isEmpty()) {
            return timeslot;
        }
        return "N/A";
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

    public String getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(String timeslotId) {
        this.timeslotId = timeslotId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
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
