package com.journaldev.hibernate.main;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

import com.journaldev.hibernate.model.Employee;
import com.journaldev.hibernate.util.HibernateUtil;

/**
 * @author Aleksandr Konstantinovitch
 * @version 1.0
 * @date 17/02/2015
 * {@link http://www.journaldev.com/2980/hibernate-ehcache-second-level-caching-example-tutorial}
 */
public class HibernateEHCacheMain {

	public static void main(String[] args) {
		System.out.println("Temp Dir:"+System.getProperty("java.io.tmpdir"));
		
		// Инициализация сессии
        System.out.println("Инициализация сессии (Temp Dir: " + System.getProperty("java.io.tmpdir") + ")");
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Statistics         statistics = sessionFactory.getStatistics();
		System.out.println("Statistics enabled = '" + statistics.isStatisticsEnabled() + "'");
        statistics.setStatisticsEnabled(true);
        System.out.println("Statistics enabled = '" + statistics.isStatisticsEnabled() + "'\n");
		
		Session              session = sessionFactory.openSession();
		Session         otherSession = sessionFactory.openSession();
		Transaction      transaction = session.beginTransaction();
		Transaction otherTransaction = otherSession.beginTransaction();
        printStatistics(statistics, 0);
		
		Employee emp = (Employee) session.load(Employee.class, 1L);
        printDBData(emp, statistics, 1);
		
		emp = (Employee) session.load(Employee.class, 1L);
        printDBData(emp, statistics, 2);
		
		// Очистить кэш первого уровня, так что используется кэш-память второго уровня
        System.out.println("Очистить кэш первого уровня, так что используется кэш-память второго уровня");
		session.evict(emp);
		emp = (Employee) session.load(Employee.class, 1L);
        printDBData(emp, statistics, 3);
		
		emp = (Employee) session.load(Employee.class, 3L);
        printDBData(emp, statistics, 4);
		
		emp = (Employee) otherSession.load(Employee.class, 1L);
        printDBData(emp, statistics, 5);
		
		// Освобождение ресурсов
        System.out.println("Освобождение ресурсов");
		transaction.commit();
		otherTransaction.commit();
		sessionFactory.close();
	}

	private static void printStatistics(Statistics statistics, int count) {
        System.out.println("+------+---------------+------------------------+-------------------------+------------------------+");
        System.out.println("|  ID  | Получить счет | Second Level Hit Count | Second Level Miss Count | Second Level Put Count |");
        System.out.println("+------+---------------+------------------------+-------------------------+------------------------+");
        String strOut = "|  " + count + "    ";
        strOut = strOut.substring(0,7);
        strOut += strOut = "|       " + statistics.getEntityFetchCount() + "              ";
        strOut = strOut.substring(0,23);
        strOut += strOut = "|            " + statistics.getSecondLevelCacheHitCount() + "              ";
        strOut = strOut.substring(0,48);
        strOut += strOut = "|            " + statistics.getSecondLevelCacheMissCount() + "              ";
        strOut = strOut.substring(0,74);
        strOut += strOut = "|            " + statistics.getSecondLevelCachePutCount() + "              ";
        strOut = strOut.substring(0,95) + "    |";
        System.out.println( strOut );
        System.out.println("+------+---------------+------------------------+-------------------------+------------------------+");
	}

	private static void printDBData(Employee emp, Statistics stats, int count) {
        printStatistics(stats, count);
        System.out.println("+------+--------------+-------------+");
        System.out.println("|  ID  |     Name     |   Zipcode   |");
        System.out.println("+------+--------------+-------------+");
        String strOut = "|  " + count + "           ";
        strOut = strOut.substring(0,7);
        strOut += strOut = "|     " + emp.getName() + "           ";
        strOut = strOut.substring(0,22);
        strOut += strOut = "|   " + emp.getAddress().getZipcode() + "           ";
        strOut = strOut.substring(0,34) + "  |";
        System.out.println( strOut );
        System.out.println("+------+--------------+-------------+\n");
	}

}
