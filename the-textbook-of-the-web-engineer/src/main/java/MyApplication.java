import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author syobochim
 */
@ApplicationPath("/") // このアノテーションのvalueがアプリケーションのURLのベースになる。
public class MyApplication extends Application {
}
