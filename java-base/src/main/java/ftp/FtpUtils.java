package ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;


/**
 * @author:youzhiming
 * @date: 2022/9/20
 * @description:
 */
public class FtpUtils {
    String ftpIp;
    int ftpPort;
    String ftpUser;
    String ftpPsw;


    /**
     * @param path     上传ftp的路径
     * @param fileName 上传ftp文件名称
     * @param content  上传内容
     */
    public boolean uploadFile(String path, String fileName, String content) {

        FTPClient ftpClient = new FTPClient();

        try {
            //将内容写入输入流
            InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
            if (in == null) {
                return false;
            }
            //建立连接
            ftpClient.connect(ftpIp, ftpPort);
            //登录
            ftpClient.login(ftpUser, ftpPsw);
            //判断是否连接成功
            if (FTPReply.isPositiveCompletion(ftpClient.getReply())) {
                //进入到指定的工作目录
                ftpClient.changeWorkingDirectory(path);
                //设置文件为二进制格式
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                //保存输入流到指定文件
                if (!ftpClient.storeFile(fileName, in)) {
                    return false;
                }
                //关闭输入流
                in.close();
                //登出
                ftpClient.logout();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


    /**
     * @param path  下载ftp文件目录地址
     * @param fileName 下载ftp文件名称
     * @return 文件内容
     */
    public String downFile(String path,String fileName){

        String result=null;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpIp,ftpPort);
            ftpClient.login(ftpUser,ftpPsw);
            if (FTPReply.isPositiveCompletion(ftpClient.getReply())) {
                ftpClient.changeWorkingDirectory(path);
                //读取ftp文件到输入流
                InputStream in = ftpClient.retrieveFileStream(fileName);
                //完成命令
                ftpClient.completePendingCommand();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                String line =null;
                StringBuilder sb=new StringBuilder();
                while ((line=reader.readLine())!=null){
                    sb.append(line);
                    sb.append("\n");
                }

                result=sb.toString();


                ftpClient.logout();
                in.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

}
