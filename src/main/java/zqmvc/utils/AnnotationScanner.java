package zqmvc.utils;

import zqmvc.annotation.Router;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouqing
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @// TODO: 2020/8/12 该类用于扫描各类注解
 */

public class AnnotationScanner {
    private final List<String> classList = new ArrayList();
    private String packageName = null;
    private static AnnotationScanner scanner;

    private AnnotationScanner(){};

    public synchronized static AnnotationScanner getScanner(){
        if (scanner==null)
            scanner = new AnnotationScanner();
        return scanner;
    }

    public List<String> scanner(String packageName, Class<? extends Annotation> needScannerAnnotation) {
        this.packageName = packageName;
        packageName = packageName.replace(".", "/");
        return this.getClassList(packageName, needScannerAnnotation);
    }

    private List<String> getClassList(String packageName, Class<? extends Annotation> needScannerAnnotation){
        String path = (AnnotationScanner.class.getClassLoader().getResource("")
                .getPath()+packageName)
                //.replace("/",File.separator)
                //.substring(1)
                ;
        //System.out.println(path);
        scannerPath(new File(path));
        System.out.println(classList);
        return classList;
    }

    private void scannerPath(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f:
                 files) {
                scannerPath(f);
            }
        }else if (file.getName().endsWith(".class")){
            String className = packageName+"."+file.getName().replace(".class","");
            try {
                if (Class.forName(className).getDeclaredAnnotation(Router.class)!=null)
                    classList.add(packageName+"."+file.getName().replace(".class",""));
            } catch (ClassNotFoundException e) {
                System.out.println("load the Router failed!");
                e.printStackTrace();
            }
        }
    }

}
