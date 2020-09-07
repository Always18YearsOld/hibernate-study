package com.hibernate.test;

import com.hibernate.pojo.Category;
import com.hibernate.pojo.Product;
import com.hibernate.pojo.User;
import com.hibernate.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestHibernate {

    static Session s5;
    static Session s6;
    public static void main(String[] args) throws InterruptedException {

        /*添加一条记录*/
//		add();

        /*添加多条记录*/
//		adds();

        /*查询一个产品*/
//		selectOne();

        /* 删除元素
         * get和load的区别，
         * 1.get是立刻执行，load是延迟加载
         * 2.get查不到数据会返回null，load查不到数据会抛异常
         * 这里由于load抛出异常，所以没有提交成功，rollback了
         */
//		deleteOne();


        /*更新元素，先查到元素，修改后直接update更新*/
//		updateOne();


        /*HQL查询*/
//		hqlSelect();

        /*
         * Criteria查询
         * Criteria与HQL和SQL的区别是
         * Criteria完全是 面向对象 的方式在进行数据查询，将不再看到有sql语句的痕迹
         */
//		criteriaSelect();

        /*
         * 使用Session的createSQLQuery方法执行标准SQL语句
         * 因为标准SQL语句有可能返回各种各样的结果，
         * 比如多表查询，分组统计结果等等。
         * 不能保证其查询结果能够装进一个Product对象中，
         * 所以返回的集合里的每一个元素是一个对象数组。
         * 然后再通过下标把这个对象数组中的数据取出来。
         */
//		SQLSelect();

        /*使用Hibernate实现多对一关系*/
//		manyToOne();

        /*实现一对多
        * 由于一对多和多对一同时实现，
        * 导致在查询的时候会出现嵌套死循环，
        * 因此在使用时候不要一起用
        */
//        oneToMany();


        /*实现多对多
        * 不知道为什么，最后没法插入数据库
        */
        manyToMany();

        /*一级缓存*/
//        firstLevelCache();

        /*二级缓存
        * 二级缓存也没有实现，是不是哪里配置错了？
        * //todo
        */
//        secondLevelCache();

        /*分页功能*/
//        paging();

        /*openSession和getCurrentSession
            他们的区别在于
            1. 获取的是否是同一个session对象
            openSession每次都会得到一个新的Session对象
            getCurrentSession在同一个线程中，每次都是获取相同的Session对象，
            但是在不同的线程中获取的是不同的Session对象
            2. 事务提交的必要性
            openSession只有在增加，删除，修改的时候需要事务，查询时不需要的
            getCurrentSession是所有操作都必须放在事务中进行，
            并且提交事务后，session就自动关闭，不能够再进行关闭
        */
//        session();
    }

    private static void session() throws InterruptedException {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s1 = sf.openSession();
        Session s2 = sf.openSession();

        System.out.println("s1 == s2:" + (s1 == s2));

        Session s3 = sf.getCurrentSession();
        Session s4 = sf.getCurrentSession();

        System.out.println("s3 == s4:" + (s3 == s4));

        Thread t1 = new Thread(() -> s5 = sf.getCurrentSession());
        t1.start();

        Thread t2 = new Thread(() -> s6 = sf.getCurrentSession());
        t2.start();

        t1.join();
        t2.join();

        System.out.println("s5 == s6:"+(s5 == s6));

        s1.close();
        s2.close();
        s3.close();
        s4.close();
        s5.close();
        s6.close();
        sf.close();
    }

    private static void paging() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        String name = "iphone";
        Criteria c = s.createCriteria(Product.class);
        c.add(Restrictions.like("name", "%" + name + "%"));
        c.setFirstResult(2);
        c.setMaxResults(5);
        List<Product> ps = c.list();
        System.out.println(ps.size());

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void secondLevelCache() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        Category c1 = (Category)s.get(Category.class, 1);
        System.out.println("log1");
        Category c2= (Category)s.get(Category.class, 1);
        System.out.println("log2");
        s.getTransaction().commit();
        s.close();
        Session s2 = sf.openSession();
        s2.beginTransaction();
        Category c3 = (Category)s2.get(Category.class, 1);
        System.out.println("log3");

        s2.getTransaction().commit();
        s2.close();
        sf.close();
    }

    private static void firstLevelCache() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        System.out.println("log1");
        Category c1 = (Category)s.get(Category.class, 1);
        System.out.println("log2");
        Category c2= (Category)s.get(Category.class, 1);
        System.out.println("log3");

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void manyToMany() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        String name = "0";
        Query q = s.createQuery("from User u where u.name like ?");
        q.setString(0, "%" + name + "%");
        List<User> uList = q.list();
        Set<User> uSet = new HashSet<>(uList);

        //产品1被用户user0购买
        Product p1 = (Product) s.get(Product.class, 1);

        p1.setUsers(uSet);
        s.save(p1);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void oneToMany() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        Category c = (Category) s.get(Category.class, 1);
        List<Product> ps = c.getProducts();
        System.out.println(ps.size());
        for (Product p : ps) {
            System.out.println(p);
        }

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void manyToOne() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        Category c = new Category();
        c.setName("c1");
        s.save(c);

        Product p = (Product) s.get(Product.class, 8);
        p.setCategory(c);
        s.update(p);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void SQLSelect() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        String name = "iphone";
        String sql = "select * from product_ p where p.name like '%" + name + "%'";

        Query q = s.createSQLQuery(sql);
        List<Object[]> list = q.list();
        List<Product> pList = new ArrayList<>();
        for (Object[] os : list) {
            Product product = new Product();
            for (int i = 0; i < os.length; i++) {
                product.setId(Integer.parseInt(os[0].toString()));
                product.setName(os[1].toString());
                product.setPrice(Float.parseFloat(os[2].toString()));
            }
            pList.add(product);
        }
        pList.forEach(System.out::println);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void criteriaSelect() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        String name = "iphone";
        Criteria c = s.createCriteria(Product.class);
        c.add(Restrictions.like("name", "%" + name + "%"));
        List<Product> ps = c.list();
        for (Product p : ps) {
            System.out.println(p);
        }

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void hqlSelect() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        String name = "iphone";
        Query q = s.createQuery("from Product p where p.name like ?");
        q.setString(0, "%" + name + "%");
        List<Product> ps = q.list();
        for (Product p : ps) {
            System.out.println(p);
        }

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void updateOne() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        Product product = (Product) s.get(Product.class, 1);
        System.out.println(product);
        product.setName("newName");
        s.update(product);
        Product product1 = (Product) s.get(Product.class, 1);
        System.out.println(product1);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void deleteOne() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        Product product = (Product) s.get(Product.class, 7);
        System.out.println("before==========" + product);
        s.delete(product);
        Product product1 = (Product) s.get(Product.class, 7);
        System.out.println("get==========" + product1);
        Product product2 = (Product) s.load(Product.class, 7);
        System.out.println("load==========" + product2);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void selectOne() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        Product product = (Product) s.get(Product.class, 6);
        System.out.println(product);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void adds() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();

        for (int i = 0; i < 100; i++) {
            Product p = new Product();
            p.setName("iphone" + i);
            p.setPrice(i);
            s.save(p);
        }

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

    private static void add() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        s.beginTransaction();
        Product p = new Product();
        p.setName("iphone7");
        p.setPrice(7000);
        s.save(p);

        s.getTransaction().commit();
        s.close();
        sf.close();
    }

}