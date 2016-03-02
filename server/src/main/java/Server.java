import java.io.File;

/**
 * Main server class
 */
public class Server {
    public static void main(String[] args) {
        // проверяем, существует ли файл с настройками. если нет - запускаем процесс подготовки
        String settingsPath = "./settings";
        File settings = new File(settingsPath);
        if (!settings.exists() && settings.isFile()) {
            ServerPreparer.main(new String[] {settingsPath});
        }
    }
}
