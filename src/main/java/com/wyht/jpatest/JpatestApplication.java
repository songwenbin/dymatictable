package com.wyht.jpatest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class JpatestApplication {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    EntityManager entityManager;

    public static final String XML_MAPPING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE hibernate-mapping PUBLIC\n" +
            "        \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
            "        \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
            "<hibernate-mapping>\n" +
            "    <class entity-name=\"Employes\" table=\"employes\">\n" +
            "    <id name=\"id\" type=\"java.lang.Integer\" length=\"11\" />\n" +
            "        <property name=\"first_name\" type=\"java.lang.String\" column=\"first_name\"/>\n" +
            "        <property name=\"last_name\" type=\"java.lang.String\" column=\"last_name\"/>\n" +
            "    </class>" +
            "</hibernate-mapping>";

    public static void main(String[] args) {
        new JpatestApplication().run();
        //SpringApplication.run(JpatestApplication.class, args);
    }

    public void run() {
        entityManagerFactory = Persistence.createEntityManagerFactory("JPA-UNIT");
        SessionFactory sessionFactory =  entityManagerFactory.unwrap(SessionFactory.class);
        StandardServiceRegistry serviceRegistry = sessionFactory.getSessionFactoryOptions().getServiceRegistry();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        sessionFactory.getSessionFactoryOptions();

        metadataSources.addInputStream(new ByteArrayInputStream(XML_MAPPING.getBytes()));
        Metadata metadata = metadataSources.buildMetadata();
        //更新数据库Schema,如果不存在就创建表,存在就更新字段,不会影响已有数据
        SchemaUpdate schemaUpdate = new SchemaUpdate();
        schemaUpdate.execute(EnumSet.of(TargetType.DATABASE), metadata, serviceRegistry);

        SessionFactory newSessionFactory = metadata.buildSessionFactory();

        Session newSession = newSessionFactory.openSession();
        Transaction tran = newSession.beginTransaction();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> employes = new HashMap<>();
            employes.put("id", i);
            employes.put("first_name", "张三" + i);
            employes.put("last_name", "adsfwr" + i);
            newSession.save("Employes", employes);
        }
        tran.commit();

        System.out.println("11111");
        Query query = newSession.createQuery("from Employes");
        List list = query.getResultList();
        System.out.println("resultList: " + list);
        newSession.close();

    }

}
