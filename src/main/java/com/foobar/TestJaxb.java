package com.foobar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.*;


public class TestJaxb {

    public static void main(String[] args) {

        try {

            OptionsParser optionsParser = new OptionsParser(TestJaxb.class.getName());
            optionsParser.parse(args);

            int maxRequests = optionsParser.getRequestsCount();
            int maxThreads = optionsParser.getThreadsCount();

            File inputFile = new File("test.xml");
            System.out.println("running " + maxRequests + " requests with " + maxThreads + " threads to test JAXBContext overhead");

            // non singleton
            long nonSingletonElapsedTime = runNonSingleton(maxThreads, maxRequests, inputFile);
            System.out.println("total time: " + String.format("%05d", nonSingletonElapsedTime) + " ms (using local instance, non singleton)");

            // singleton
            long singletonElapsedTime = runSingleton(maxThreads, maxRequests, inputFile);
            System.out.println("total time: " + String.format("%05d", singletonElapsedTime) + " ms (using global instance, singleton)");

            if (singletonElapsedTime < nonSingletonElapsedTime) {
                System.out.println("\n++ SUMMARY: singleton is " + nonSingletonElapsedTime / singletonElapsedTime + " TIMES *FASTER* than non singleton\n");
            }
            else {
                System.out.println("\n++ SUMMARY: singleton is " + nonSingletonElapsedTime / singletonElapsedTime + " TIMES *SLOWER* than non singleton\n");
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
            executor.awaitTermination(60, TimeUnit.SECONDS);

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
            executor.awaitTermination(60, TimeUnit.SECONDS);
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
                AuthenticateUserReqMessage authResponse = (AuthenticateUserReqMessage) (jaxbContext.createUnmarshaller()).unmarshal(inputFile);
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
                AuthenticateUserReqMessage authResponse = (AuthenticateUserReqMessage) (jaxbContext.createUnmarshaller()).unmarshal(inputFile);
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

    public static class OptionsParser {

        private String className = this.getClass().getName();
        private int threadsCount = 100;
        private int requestsCount = 2000;

        private Options options = new Options();

        public OptionsParser(String className) {

            options.addOption("h", "help", false, "show this help message.");
            options.addOption("t", "threads", true, "total threads (default 100).");
            options.addOption("r", "requests", true, "total requests (default 2000).");

            this.className = className;
        }

        public int getThreadsCount() { return threadsCount; }
        public int getRequestsCount() { return requestsCount; }

        public void parse(String[] args) {
            try {
                CommandLine cmd = new DefaultParser().parse(options, args);

                if (cmd.hasOption('h'))
                    help();

                if (cmd.hasOption('r')) {
                    this.requestsCount =  Integer.parseInt(cmd.getOptionValue('r'));
                }

                if (cmd.hasOption('t')) {
                    this.threadsCount =  Integer.parseInt(cmd.getOptionValue("threads"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                help();
            }
        }

        private void help() {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(className, options);
            System.exit(0);
        }
    }
}

