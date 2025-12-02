package be.teletask.onvif;

import be.teletask.onvif.listeners.OnvifDeviceInformationListener;
import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.listeners.OnvifServicesListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifDeviceInformation;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifServices;
import be.teletask.onvif.responses.OnvifResponse;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DiagnosticTest {

    private final static String USER_NAME = "user";
    private final static String PASSWORD = "qwe12345";
    private final static String IP = "192.168.1.201";

    @Test
    public void testConnection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        OnvifDevice device = new OnvifDevice(IP, USER_NAME, PASSWORD);
        OnvifManager manager = new OnvifManager();

        manager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
                System.out.println("=== Response Success ===");
                System.out.println("Type: " + onvifResponse.getRequestType());
                System.out.println("XML: " + onvifResponse.getXml());
                latch.countDown();
            }

            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
                System.out.println("=== Response Error ===");
                System.out.println("Error Code: " + errorCode);
                System.out.println("Error Message: " + errorMessage);
                latch.countDown();
            }
        });

        System.out.println("Testing connection to: " + IP);
        System.out.println("Getting services...");

        manager.getServices(device, new OnvifServicesListener() {
            @Override
            public void onServicesReceived(OnvifDevice onvifDevice, OnvifServices services) {
                System.out.println("=== Services Received ===");
                System.out.println("Services Path: " + services.getServicesPath());
                System.out.println("Profiles Path: " + services.getProfilesPath());
                System.out.println("Stream URI Path: " + services.getStreamURIPath());
                latch.countDown();
            }
        });

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        if (!completed) {
            System.out.println("Test timed out after 30 seconds");
        }
    }

    @Test
    public void testDeviceInfo() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        OnvifDevice device = new OnvifDevice(IP, USER_NAME, PASSWORD);
        OnvifManager manager = new OnvifManager();

        manager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
                System.out.println("=== Raw Response ===");
                System.out.println("Type: " + onvifResponse.getRequestType());
                System.out.println("Success: " + onvifResponse.isSuccess());
                System.out.println("Error Code: " + onvifResponse.getErrorCode());
                System.out.println("Error Message: " + onvifResponse.getErrorMessage());
                String xml = onvifResponse.getXml();
                if (xml != null && xml.length() > 500) {
                    System.out.println("XML (truncated): " + xml.substring(0, 500) + "...");
                } else {
                    System.out.println("XML: " + xml);
                }
            }

            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
                System.out.println("=== Error ===");
                System.out.println("Error Code: " + errorCode);
                System.out.println("Error Message: " + errorMessage);
                latch.countDown();
            }
        });

        System.out.println("Testing device info (requires auth)...");
        manager.getDeviceInformation(device, new OnvifDeviceInformationListener() {
            @Override
            public void onDeviceInformationReceived(OnvifDevice onvifDevice, OnvifDeviceInformation info) {
                System.out.println("=== Device Info Received ===");
                System.out.println("Manufacturer: " + info.getManufacturer());
                System.out.println("Model: " + info.getModel());
                System.out.println("Firmware: " + info.getFirmwareVersion());
                latch.countDown();
            }
        });

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        if (!completed) {
            System.out.println("Test timed out after 30 seconds");
        }
    }

    @Test
    public void testMediaProfiles() throws Exception {
        OnvifDevice device = new OnvifDevice(IP, USER_NAME, PASSWORD);
        OnvifManager manager = new OnvifManager();

        manager.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
                System.out.println("=== Raw Response ===");
                System.out.println("Type: " + onvifResponse.getRequestType());
                System.out.println("Success: " + onvifResponse.isSuccess());
            }

            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
                System.out.println("=== Error ===");
                System.out.println("Error Code: " + errorCode);
                System.out.println("Error Message: " + errorMessage);
            }
        });

        System.out.println("Testing media profiles (requires auth)...");
        try {
            List<OnvifMediaProfile> profiles = manager.getMediaProfiles(device);
            if (profiles != null && !profiles.isEmpty()) {
                System.out.println("=== Media Profiles ===");
                for (OnvifMediaProfile profile : profiles) {
                    System.out.println("Profile: " + profile.getName() + " - Token: " + profile.getToken());
                }
            } else {
                System.out.println("No profiles returned (null or empty)");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
