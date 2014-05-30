package detect;

/**
 * Created by lai on 3/13/14.
 */
public class Main {
    public static void main(String[] args) {

        /**
         * 根据已知样本做唯一匹配。
         */
        // main.detect md5
        String malName = MD5Detector.detect(args[0]);

        if (malName != null) {
            System.out.println("It's the sample." + malName);
            return;
        }

        //
        /**
         * 根据已知样本做精准匹配
         */
        // 检测证书（黑名单）+class.dex 大小精准
        boolean isMatchCert = CertDetector.detect(args[0]);

        // 检测证书（黑名单）+ 清单MD5 MD5 大小精准

        // // 检测证书（黑名单） + 包名 可疑精准
        // // 检测证书（黑名单） + Receivers + Intent 可疑精准

        // classes.dex 特定位置字符串？——包名——特定字符串

        /**
         * 根据已知样本模式，做近似分析
         */


        /**
         * 对应用做权限，接收器，API、文件分析，做可疑行为分析-安全评估。
         */

    }
}
