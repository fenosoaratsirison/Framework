package mg.framework.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Scanne un package (et ses sous-packages) pour récupérer la liste des
 * classes qu'il contient. Fonctionne que les classes soient sous forme
 * de fichiers .class sur disque (WEB-INF/classes) ou packagées dans un .jar
 * (WEB-INF/lib), ce qui est important sous Tomcat.
 */
public class ClassScanner {

    public static List<Class<?>> scanPackage(String packageName) {

        List<Class<?>> classes = new ArrayList<>();

        if (packageName == null || packageName.isEmpty()) {
            return classes;
        }

        try {
            String path = packageName.replace('.', '/');
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            Enumeration<URL> resources = loader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                URLConnection connection = resource.openConnection();

                if (connection instanceof JarURLConnection) {
                    JarURLConnection jarConnection = (JarURLConnection) connection;
                    classes.addAll(findClassesInJar(jarConnection.getJarFile(), packageName));
                } else {
                    File directory = new File(resource.toURI());
                    classes.addAll(findClassesInDirectory(directory, packageName));
                }
            }

        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static List<Class<?>> findClassesInDirectory(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // scan récursif des sous-packages
                classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                addClass(classes, className);
            }
        }

        return classes;
    }

    private static List<Class<?>> findClassesInJar(JarFile jarFile, String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                String className = name.replace('/', '.').replace(".class", "");
                addClass(classes, className);
            }
        }

        return classes;
    }

    private static void addClass(List<Class<?>> classes, String className) {
        try {
            classes.add(Class.forName(className, false, Thread.currentThread().getContextClassLoader()));
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // classe non chargeable (ex: fichier .class orphelin), on l'ignore simplement
            System.err.println("[ClassScanner] Impossible de charger la classe : " + className);
        }
    }
}
