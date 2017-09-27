package condi.kr.ac.swu.condidemo.data;

import java.util.Properties;

/**
 * Created by 8304 on 2015-10-23.
 */
public class Course {
    public String id;
    public String name;
    public String info;
    public String km;
    public String selector;

    public Course(Properties p) {
        this.id = p.getProperty("id");
        this.name = p.getProperty("name");
        this.info = p.getProperty("info");
        this.km = p.getProperty("km");
        this.selector = null;

        System.out.println("-------------�ڽ�����-----------------");
        System.out.println("id : "+p.getProperty("id"));
        System.out.println("name : "+p.getProperty("name"));
        System.out.println("info : "+p.getProperty("info"));
        System.out.println("km : "+p.getProperty("km"));
    }

    public Course(Properties p, String selector) {
        this.id = p.getProperty("id");
        this.name = p.getProperty("name");
        this.info = p.getProperty("info");
        this.km = p.getProperty("km");
        this.selector = selector;
    }

    public Properties getCourse() {
        Properties p = new Properties();
        p.setProperty("id", this.id);
        p.setProperty("name", this.name);
        p.setProperty("info", this.info);
        p.setProperty("km", this.km);
        p.setProperty("selector", this.selector);

        return p;
    }
}
