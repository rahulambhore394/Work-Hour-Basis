package com.example.main.VisitorOperation;

public class VisitorDataRetrieve {

    public String vis_name;
    public String vis_email;
    public String vis_pnumber;
    public String vis_qualification;
    private String vis_image;
    public String vis_subject;
    public String key;
    public String vis_year,vis_Subject_branch;

    public String vis_acc_no;
    public String vis_bank_name;
    public String vis_branch;
    public String vis_ifsc;

    public static String final_vis_name="ZH";

    public String getVis_year() {
        return vis_year;
    }

    public void setVis_year(String vis_year) {
        this.vis_year = vis_year;
    }

    public String getVis_Subject_branch() {
        return vis_Subject_branch;
    }

    public void setVis_Subject_branch(String vis_Subject_branch) {
        this.vis_Subject_branch = vis_Subject_branch;
    }

    public String getVis_acc_no() {
        return vis_acc_no;
    }

    public void setVis_acc_no(String vis_acc_no) {
        this.vis_acc_no = vis_acc_no;
    }

    public String getVis_bank_name() {
        return vis_bank_name;
    }

    public void setVis_bank_name(String vis_bank_name) {
        this.vis_bank_name = vis_bank_name;
    }

    public String getVis_branch() {
        return vis_branch;
    }

    public void setVis_branch(String vis_branch) {
        this.vis_branch = vis_branch;
    }

    public String getVis_ifsc() {
        return vis_ifsc;
    }

    public void setVis_ifsc(String vis_ifsc) {
        this.vis_ifsc = vis_ifsc;
    }

    public void setVis_name(String vis_name) {
        this.vis_name = vis_name;
    }

    public void setVis_email(String vis_email) {
        this.vis_email = vis_email;
    }

    public void setVis_pnumber(String vis_pnumber) {
        this.vis_pnumber = vis_pnumber;
    }

    public void setVis_qualification(String vis_qualification) {
        this.vis_qualification = vis_qualification;
    }

    public void setVis_image(String vis_image) {
        this.vis_image = vis_image;
    }

    public void setVis_subject(String vis_subject) {
        this.vis_subject = vis_subject;
    }


    public VisitorDataRetrieve(String vis_name, String vis_email, String vis_pnumber, String vis_qualification, String vis_subject,String vis_year,String vis_Subject_branch ,String vis_acc_no, String vis_bank_name, String vis_branch, String vis_ifsc) {
        this.vis_name = vis_name;
        this.vis_email = vis_email;
        this.vis_pnumber = vis_pnumber;
        this.vis_qualification = vis_qualification;
        this.vis_subject = vis_subject;
        this.vis_year = vis_year;
        this.vis_Subject_branch = vis_Subject_branch;
        this.vis_acc_no = vis_acc_no;
        this.vis_bank_name = vis_bank_name;
        this.vis_branch = vis_branch;
        this.vis_ifsc = vis_ifsc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public VisitorDataRetrieve() {

    }

    public String getVis_name() {
        return vis_name;
    }

    public String getVis_email() {
        return vis_email;
    }

    public String getVis_pnumber() {
        return vis_pnumber;
    }

    public String getVis_qualification() {
        return vis_qualification;
    }

//    public String getVis_image() {
//        return vis_image;
//    }

    public String getVis_subject() {
        return vis_subject;
    }


}
