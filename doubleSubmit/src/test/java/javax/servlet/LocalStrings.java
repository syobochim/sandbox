package javax.servlet;

import java.util.ListResourceBundle;

/**
 * HttpServletをインスタンス化しようとするとResourceBundleが
 * "javax.servlet.LocalStrings"を探しに来て死ぬので、
 * 死なないようにするだけの適当な実装です。
 *
 * @author irof
 */
public class LocalStrings extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[0][];
    }
}
