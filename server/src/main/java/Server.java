import java.io.File;

/**
 * Main server class
 */
public class Server {
    /**
     * usage:
     * no args => will run server with basic config
     * full_path_to_settings_file => will run server using particular settings from file
     * -c => reconfigure server settings and database
     * -cg => same as -c but running GUI
     * */
    public static void main(String[] args) {
        String settingsPath = "./settings";
        if (args.length > 0 && args[0].charAt(0) != '-') settingsPath = args[0];

        File settings = new File(settingsPath);
        // проверяем, существует ли файл с настройками. если нет - запускаем процесс подготовки
        // если в аргументах первый символ '-' - передаем в процесс подготовки эти аргументы
        if (!settings.exists() || !settings.isFile()
                || (args.length > 0 && args[0].charAt(0) == '-')) {
            ServerPreparer.main(args);
        }
    }
}
