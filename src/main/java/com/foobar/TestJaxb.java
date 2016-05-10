package com.foobar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class TestJaxb {
    public static void main(String[] args) {
        try {
            int maxThreads = 100;
            int maxRequests = 1000;

            File inputFile = new File("test.xml");
            System.out.println("running " + maxRequests + " requests with " + maxThreads + " threads to test JAXBContext overhead");

            // non singleton
            long nonSingletonElapsedTime = runNonSingleton(maxThreads, maxRequests, inputFile);
            System.out.println("total time: " + String.format("%05d", nonSingletonElapsedTime) + " ms (using local instance, non singleton)");

            // singleton
            long singletonElapsedTime = runSingleton(maxThreads, maxRequests, inputFile);
            System.out.println("total time: " + String.format("%05d", singletonElapsedTime) + " ms (using global instance, singleton)");

            if (singletonElapsedTime < nonSingletonElapsedTime) {
                System.out.println("++ summary: singleton is " + String.format("%03d", nonSingletonElapsedTime / singletonElapsedTime) + " times faster than non singleton");
            }
            else {
                System.out.println("++ summary: singleton is " + String.format("%03d", nonSingletonElapsedTime / singletonElapsedTime) + " times slower than non singleton");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long runSingleton(int totalThreads, int totalRequests, File inputFile) {
        long startTime = System.currentTimeMillis();
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticateUserReqMessage.class);
            ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

            for (int i = 0; i < totalRequests; i++) {
                executor.submit(new SingletonRunner(i, inputFile, jaxbContext));
            }

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - startTime;
    }

    private static long runNonSingleton(int totalThreads, int totalRequests, File inputFile) {

        long startTime = System.currentTimeMillis();
        try {

            ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

            for (int i = 0; i < totalRequests; i++) {
                executor.submit(new RunnerNonSingleton(i, inputFile));
            }

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - startTime;
    }

    public static class SingletonRunner implements Runnable {

        int threadId = 0;
        File inputFile = null;
        JAXBContext jaxbContext = null;

        public SingletonRunner(int id, File inputFile, JAXBContext jaxbContext) {
            this.threadId = id;
            this.inputFile = inputFile;
            this.jaxbContext = jaxbContext;
        }

        public void run() {
            try {
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                AuthenticateUserReqMessage authResponse = (AuthenticateUserReqMessage) jaxbUnmarshaller.unmarshal(inputFile);
                if (!authResponse.getChannelPartnerID().equals("testChannel")) {
                    System.out.println("[" + threadId + "] failed in SingletonRunner, response : " + authResponse.getChannelPartnerID());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static class RunnerNonSingleton implements Runnable {

        int threadId = 0;
        File inputFile = null;

        public RunnerNonSingleton(int id, File inputFile) {
            this.threadId = id;
            this.inputFile = inputFile;
        }

        public void run() {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticateUserReqMessage.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                AuthenticateUserReqMessage authResponse = (AuthenticateUserReqMessage) jaxbUnmarshaller.unmarshal(inputFile);
                if (!authResponse.getChannelPartnerID().equals("testChannel")) {
                    System.out.println("[" + threadId + "] failed in RunnerNonSingleton, response : " + authResponse.getChannelPartnerID());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
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

