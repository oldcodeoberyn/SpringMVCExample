package com.example.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.example.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories( basePackageClasses = Application.class )
class JpaConfig
{

    @Value( "${dataSource.driverClassName}" )
    private String driver;
    @Value( "${dataSource.url}" )
    private String url;
    @Value( "${dataSource.username}" )
    private String username;
    @Value( "${dataSource.password}" )
    private String password;
    @Value( "${hibernate.dialect}" )
    private String dialect;
    @Value( "${hibernate.hbm2ddl.auto}" )
    private String hbm2ddlAuto;

    @Bean
    public DataSource dataSource() throws PropertyVetoException
    {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass( driver );
        dataSource.setJdbcUrl( url );
        dataSource.setUser( username );
        dataSource.setPassword( password );
        dataSource.setMinPoolSize( 3 );
        dataSource.setAcquireIncrement( 5 );
        dataSource.setMaxPoolSize( 20 );
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory( DataSource dataSource )
    {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource( dataSource );

        String entities = ClassUtils.getPackageName( Application.class );
        String converters = ClassUtils.getPackageName( Jsr310JpaConverters.class );
        entityManagerFactoryBean.setPackagesToScan( entities, converters );

        entityManagerFactoryBean.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );

        Properties jpaProperties = new Properties();
        jpaProperties.put( org.hibernate.cfg.Environment.DIALECT, dialect );
        jpaProperties.put( org.hibernate.cfg.Environment.HBM2DDL_AUTO, hbm2ddlAuto );
        entityManagerFactoryBean.setJpaProperties( jpaProperties );

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager( EntityManagerFactory entityManagerFactory )
    {
        return new JpaTransactionManager( entityManagerFactory );
    }
}
