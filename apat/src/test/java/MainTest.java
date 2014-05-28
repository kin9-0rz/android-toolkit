import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 6/3/13
 * Time: 11:09 AM
 */
public class MainTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMain() throws Exception {
        Pattern p = Pattern.compile("[A-Za-z]+://[A-Za-z0-9./?=:&-_%]+",Pattern.CASE_INSENSITIVE );

        Matcher m = p.matcher("hTTp://www.qqgb.com/Program/Java/JavaFAQ/JavaJ2SE/Program_146959.html  ftp://xxxx http://ss");

        while (m.find())          {
            System.out.println(m.group());
        }

        m = p.matcher("http://baike.baidu.com/view/230199.htm?fr=ala0_1");

        if (m.find()) {
            System.out.println(m.group());
        }

        m = p.matcher("http://www.google.cn/gwt/x?u=http%3A%2F%2Fanotherbug.blog.chinajavaworld.com%2Fentry%2F4550%2F0%2F&btnGo=Go&source=wax&ie=UTF-8&oe=UTF-8");

        if (m.find()) {
            System.out.println(m.group());
        }

        m = p.matcher("http://zh.wikipedia.org:80/wiki/Special:Search?search=tielu&go=Go");

        if (m.find()) {
            System.out.println(m.group());
        }

    }



}
