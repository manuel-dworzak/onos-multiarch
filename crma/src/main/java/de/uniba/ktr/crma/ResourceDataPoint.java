package de.uniba.ktr.crma;

import java.util.List;

public class ResourceDataPoint {
    private String type;
    private String measurement;
    private List<String> tag_set;
    private List<String> field_set;
    private Double timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public List<String> getTag_set() {
        return tag_set;
    }

    public void setTag_set(List<String> tag_set) {
        this.tag_set = tag_set;
    }

    public List<String> getField_set() {
        return field_set;
    }

    public void setField_set(List<String> field_set) {
        this.field_set = field_set;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ResourceDataPoint{" +
                "type='" + type + '\'' +
                ", measurement='" + measurement + '\'' +
                ", tag_set=" + tag_set +
                ", field_set=" + field_set +
                ", timestamp=" + timestamp +
                '}';
    }

    public String buildLineProtocol() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.measurement).append(",").append(String.join(",", this.tag_set))
                .append(" ").append(String.join(",", this.field_set))
                .append(" ").append(String.format("%.0f",this.timestamp * Math.pow(10, 9)));

        return builder.toString();
    }
}
