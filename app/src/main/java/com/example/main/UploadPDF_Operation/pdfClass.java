package com.example.main.UploadPDF_Operation;

public class pdfClass {

    public String name,url;

    public pdfClass(){

    }

    public pdfClass(String name, String url){
        this.name = name;
        this.url = url;
    }

    public String getName(){

        return name;
    }

    public void setName(String name){

        this.name = name;
    }

    public String getUrl(){

        return url;
    }

    public void setUrl(){
        this.url=url;
    }

}
