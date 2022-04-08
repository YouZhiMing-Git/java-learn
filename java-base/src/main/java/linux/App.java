package linux;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author:youzhiming
 * @date: 2022/4/1
 * @description:
 */
public class App {

    static String URL = "http://localhost:8077/";

    public static void main(String[] args) {
        App app = new App();

        try {
            app.restart(args);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String restart(String[] args) throws Exception {
        String result = request(URL + "applications");
        String localHost=getLocalIp();
        List<String> appInfoList = JSON.parseArray(result, String.class);
        for (String appInfo : appInfoList) {
            JSONObject appInfoObj = JSON.parseObject(appInfo);
            String serviceName = appInfoObj.getString("name");
            if ("consul".equals(serviceName) || "spring-boot-admin-server".equals(serviceName)) {
                continue;
            }

            if(!isRestart(args,serviceName)){
                continue;
            }

            List<String> instances = JSON.parseArray(appInfoObj.getString("instances"), String.class);
            for (String instance : instances) {
                JSONObject instanceObj = JSON.parseObject(instance);
                String id = instanceObj.getString("id");
                String serviceUrl = JSON.parseObject(instanceObj.getString("registration")).getString("serviceUrl");
                String ip = serviceUrl.replaceAll("http://", "");
                ip = ip.split(":")[0];
                String infoResult = request(URL + "instances/" + id + "/actuator/info");
                if(infoResult==null||"".equals(infoResult))
                    continue;
                JSONObject jsonObject = JSON.parseObject(infoResult);
                String path = jsonObject.getString("path");
                String pwd = jsonObject.getString("pwd");
                if (pwd == null || pwd.equals("")) {
                    continue;
                }
                if (path == null || path.equals("")) {
                    continue;
                }
                System.out.println("重启服务：" + serviceName + " " + ip);

                if(ip.equals(localHost)){
                    restartLocalInstance(path);
                }else {
                    restartInstance(ip, pwd, path);
                }

            }
        }
        return null;
    }


    public boolean isRestart(String[] reStartServices,String service){

        if(reStartServices.length==0||"*".equals(reStartServices[0])){
            return true;
        }
        for (String reStartService : reStartServices) {
            String temp=reStartService.split("\\*")[0];
            if(service.indexOf(temp)!=-1){
                return true;
            }
        }
        return false;
    }


    public String request(String url) throws IOException {


        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);

        String response = "";
        try {
            httpClient.executeMethod(getMethod);
            byte[] responseBody = getMethod.getResponseBody();// 读取为字节数组
            response = new String(responseBody, "utf-8");
        } finally {
            getMethod.releaseConnection();
        }
        return response;
    }


    public void restartLocalInstance(String path){
        String stopCommand = "bash " + path + "/bin/stop.sh\"";
        String startCommand = "bash " + path + "/bin/start.sh\"";
        executeLinuxCommand(stopCommand);
        executeLinuxCommand(startCommand);
    }

    public void restartInstance(String ip, String pwd, String path) {

        String stopCommand = "source /etc/profile;sshpass -p \"" + pwd + "\" ssh root@" + ip + "  \"bash " + path + "/bin/stop.sh\" >/var/log/restart.log";
        String startCommand = "source /etc/profile;sshpass -p \"" + pwd + "\" ssh root@" + ip + "  \"source /etc/profile;bash " + path + "/bin/start.sh\" >/var/log/restart.log";
        executeLinuxCommand(stopCommand);
        executeLinuxCommand(startCommand);
    }

    public String executeLinuxCommand(String command) {
        System.out.println(command);
        String[] cmds={"sh","-c",command};
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(cmds);
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            process.destroy();
            System.out.println(stringBuffer.toString());
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }


    public String getLocalIp() throws UnknownHostException {
        String localIP = "";
        InetAddress addr = (InetAddress) InetAddress.getLocalHost();
        //获取本机IP
        localIP = addr.getHostAddress().toString();
        return localIP;
    }
}
