package be.teletask.onvif;

import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifType;
import be.teletask.onvif.responses.OnvifResponse;
import be.teletask.onvif.util.OnvifResponsesAnalyzeUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author BOj
 * @desc Onvif云台操作的单元测试类
 * @since 11/12/2024  10:21 AM
 */

public class PtzTest {

    private final static String USER_NAME = "user";
    private final static String PASSWORD = "qwe12345";

    private final static String IP = "192.168.1.201";

    public static OnvifManager ONVIF_MANGER = new OnvifManager();

    public static OnvifDevice ONVIF_DEVICE = null;

    static {
        ONVIF_DEVICE = new be.teletask.onvif.models.OnvifDevice(IP, USER_NAME, PASSWORD);

        ONVIF_MANGER.setOnvifResponseListener(new OnvifResponseListener() {
            // 请求成功处理
            @Override
            public void onResponse(be.teletask.onvif.models.OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
                System.out.println("onvifResponse = " + onvifResponse.getXml());
                OnvifType requestType = onvifResponse.getRequestType();
                switch (requestType) {
                    case GET_PTZ_URI:
                        System.out.println("云台移动成功");
                        break;
                    default:
                        System.out.println("onvifResponse = " + onvifResponse.getXml());
                        break;
                }
            }

            //请求失败处理
            @Override
            public void onError(be.teletask.onvif.models.OnvifDevice onvifDevice, int i, String s) {
                System.out.println("error = " + s);
                System.out.println(("失败"));
            }
        });
    }

    @Test
    public void ptzMove() throws Exception {
        // 首先获取mediaProfiles文件
        List<OnvifMediaProfile> mediaProfiles = ONVIF_MANGER.getMediaProfiles(ONVIF_DEVICE);
        // 具体取第几个配置文件根据实际情况决定
        String token = mediaProfiles.get(0).getToken();
        // 进行云台的伸缩持续运动
        ONVIF_MANGER.sendMoveRequestAndBody(ONVIF_DEVICE, token, 0, 0, 1);
        // 异步消息需要等待,不然直接程序走完销毁消息传达不到设备
        Thread.sleep(10);
        // 停止
        ONVIF_MANGER.sendStopRequest(ONVIF_DEVICE, token, true, true);
    }

    @Test
    public void ptzMoveDirections() throws Exception {
        List<OnvifMediaProfile> mediaProfiles = ONVIF_MANGER.getMediaProfiles(ONVIF_DEVICE);
        String token = mediaProfiles.get(0).getToken();

        // 向上移动
        System.out.println("Moving UP...");
        ONVIF_MANGER.sendMoveRequestAndBody(ONVIF_DEVICE, token, 0, 0.5, 0);
        Thread.sleep(1000);
        ONVIF_MANGER.sendStopRequest(ONVIF_DEVICE, token, true, true);
        Thread.sleep(500);

        // 向下移动
        System.out.println("Moving DOWN...");
        ONVIF_MANGER.sendMoveRequestAndBody(ONVIF_DEVICE, token, 0, -0.5, 0);
        Thread.sleep(1000);
        ONVIF_MANGER.sendStopRequest(ONVIF_DEVICE, token, true, true);
        Thread.sleep(500);

        // 向左移动
        System.out.println("Moving LEFT...");
        ONVIF_MANGER.sendMoveRequestAndBody(ONVIF_DEVICE, token, -0.5, 0, 0);
        Thread.sleep(1000);
        ONVIF_MANGER.sendStopRequest(ONVIF_DEVICE, token, true, true);
        Thread.sleep(500);

        // 向右移动
        System.out.println("Moving RIGHT...");
        ONVIF_MANGER.sendMoveRequestAndBody(ONVIF_DEVICE, token, 0.5, 0, 0);
        Thread.sleep(1000);
        ONVIF_MANGER.sendStopRequest(ONVIF_DEVICE, token, true, true);

        System.out.println("PTZ direction test completed!");
    }
}
