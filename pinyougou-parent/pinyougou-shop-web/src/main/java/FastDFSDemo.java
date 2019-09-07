import org.csource.fastdfs.*;
import org.junit.Test;

public class FastDFSDemo {
    public static void main(String[] args) throws Exception {

        // 1、加载配置文件，配置文件中的内容就是 tracker 服务的地址。
        ClientGlobal.init("D:\\project\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fdfs_client.conf");
        // 2、创建一个 TrackerClient 对象
        TrackerClient trackerClient = new TrackerClient();
        // 3、使用 TrackerClient 对象创建连接，获得一个 TrackerServer 对象。
        TrackerServer trackerServer = trackerClient.getConnection();
        // 4、创建一个 StorageServer 的引用，值为 null
        StorageServer storageServer = null;
        // 5、创建一个 StorageClient 对象，需要两个参数 TrackerServer 对象、StorageServer 的引用
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        // 6、使用 StorageClient 对象上传图片，返回一个地址数组，//扩展名不带“.”
        String[] strings = storageClient.upload_file("F:\\毕业照片\\桌面图\\445443.jpg", "jpg", null);
        // 7、返回数组。包含组名和图片的路径。
        for (String string : strings) {
            /**
             * group1
            M00/00/00/wKgZhV1ntkqAfD9hAAFe0SaR4Ek154.jpg
            * http://192.168.25.133/group1/M00/00/00/wKgZhV1ntkqAfD9hAAFe0SaR4Ek154.jpg
            *就可以访问到上传的图片
            */
            System.out.println(string);
        }
    }
    @Test
    public void test(){
        double x=-11.6;
        x=Math.round(x);
        System.out.println(x);
    }


}
