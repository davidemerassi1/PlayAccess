package it.unimi.di.ewlab.iss.common.model.actions;

public class VocalFile {

    public String file_name, data, duration;

    public VocalFile(String file_name, String data, String duration) {

        this.file_name = file_name;
        this.data = data;
        this.duration = duration;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VocalFile {" +
                "file_name='" + file_name + '\'' +
                ", data='" + data + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
