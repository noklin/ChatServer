
package com.noklin.chatserver.database;
 
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY;
    
    static {
        Configuration cfg = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties());
        SESSION_FACTORY = cfg.buildSessionFactory(builder.build()); 
    }
    
    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
