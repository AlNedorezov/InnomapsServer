package rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by alnedorezov on 3/21/16.
 */
@Component
public class MyBean {

    private String prop;

    @Autowired
    public MyBean(@Value("${server.port}") String prop) {
        this.prop = prop;
        System.out.println("Server is working on the following port: " + prop);
    }
}