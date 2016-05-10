package com.foobar;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class TestJaxb {
    public static void main(String[] args) {
     try {
        int maxThreads = 100;
        int maxRequests = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        File inputFile = new File("test.xml");
        System.out.println("running " + maxRequests + " requests with " + maxThreads + " threads to test JAXBContext overhead");

        long startTime = System.currentTimeMillis();

        for (int i=0; i< maxRequests; i++) {
            executor.submit(new RunClass(i, inputFile));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println("total time for first test (using class instance in each call): " + estimatedTime + " ms");

        JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticateUserReqMessage.class);
        executor = Executors.newFixedThreadPool(maxThreads);

        startTime = System.currentTimeMillis();

        for (int i=0; i< maxRequests; i++) {
            executor.submit(new RunClass2(i, inputFile, jaxbContext));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println("total time for second test(using global class instance): " + estimatedTime + " ms");


    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static class RunClass implements Runnable {

    int threadId = 0;
    File inputFile = null;

    public RunClass(int id, File inputFile) {
        this.threadId = id;
        this.inputFile = inputFile;
    }
    public void run() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticateUserReqMessage.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            AuthenticateUserReqMessage authResponse = (AuthenticateUserReqMessage) jaxbUnmarshaller.unmarshal(inputFile); 
            if (!authResponse.getChannelPartnerID().equals("testChannel")) {
                System.out.println("[" + threadId + "] failed in RunClass, response : " + authResponse.getChannelPartnerID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public static class RunClass2 implements Runnable {

    int threadId = 0;
    File inputFile = null;
    JAXBContext jaxbContext = null;
    Unmarshaller jaxbUnmarshaller;

    public RunClass2(int id, File inputFile, JAXBContext jaxbContext) {
        this.threadId = id;
        this.inputFile = inputFile;
        this.jaxbContext = jaxbContext;
        try {
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (Exception e){e.printStackTrace();}
    }
    public void run() {
        try {
            AuthenticateUserReqMessage authResponse = (AuthenticateUserReqMessage) jaxbUnmarshaller.unmarshal(inputFile); 
            if (!authResponse.getChannelPartnerID().equals("testChannel")) {
                System.out.println("[" + threadId + "] failed in RunClass2, response : " + authResponse.getChannelPartnerID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@XmlRootElement(name = "AuthenticateUserReqMessage")
public static class AuthenticateUserReqMessage {

    private String apiUser;
    private String apiPassword;
    private String contactID;
    private String message;
    private String customerUsername;
    private String customerPassword;    
    private Boolean generateToken;
    private String socialLoginType;
    private String socialLoginID;
    private Boolean rememberMe;
    private Boolean isGlobalSearch;
    private String channelPartnerID;

    @XmlElement(name = "apiUser")
    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    @XmlElement(name = "apiPassword")
    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    @XmlElement(name = "customerPassword")
    public String getCustomerPassword() {
        return customerPassword;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    @XmlElement(name = "customerUsername")
    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    @XmlElement(name = "generateToken")
    public Boolean getGenerateToken() {
        return generateToken;
    }

    public void setGenerateToken(Boolean generateToken) {
        this.generateToken = generateToken;
    }

    @XmlElement(name = "socialLoginType")
    public String getSocialLoginType() {
        return socialLoginType;
    }

    public void setSocialLoginType(String socialLoginType) {
        this.socialLoginType = socialLoginType;
    }

    @XmlElement(name = "socialLoginID")
    public String getSocialLoginID() {
        return socialLoginID;
    }

    public void setSocialLoginID(String socialLoginID) {
        this.socialLoginID = socialLoginID;
    }

    @XmlElement(name = "rememberMe")
    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @XmlElement(name = "isGlobalSearch")
    public Boolean getIsGlobalSearch() {
        return isGlobalSearch;
    }

    public void setIsGlobalSearch(Boolean isGlobalSearch) {
        this.isGlobalSearch = isGlobalSearch;
    }

    @XmlElement(name = "channelPartnerID")
    public String getChannelPartnerID() {
        return channelPartnerID;
    }

    public void setChannelPartnerID(String channelPartnerID) {
        this.channelPartnerID = channelPartnerID;
    }

    @XmlElement(name = "contactID")
    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
}

