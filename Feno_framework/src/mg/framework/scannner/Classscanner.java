package mg.framework.scanner;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static List<Class<?>> scanPackage(String packageName) {

        List<Class<?>> classes = new ArrayList<>();

        try {

            String path = packageName.replace('.', '/');

            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            URL resource = loader.getResource(path);

            if (resource == null)
                return classes;

            File directory = new File(resource.toURI());

            File[] files = directory.listFiles();

            if (files == null)
                return classes;

            for (File file : files) {

                if (file.getName().endsWith(".class")) {

                    String className = packageName + "."
                            + file.getName().replace(".class", "");

                    classes.add(Class.forName(className));

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }
    
}