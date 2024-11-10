package project.EAccounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import project.EAccounting.config.ProjectConfig;
import project.EAccounting.config.ProjectConfigData;
import project.EAccounting.persistence.CustomPersistenceUnitInfo;

import java.util.HashMap;

@SpringBootApplication
public class EAccountingApplication {

	public static void main(String[] args) {
		try {
			String puName = "EAccounting_persistence_unit";
			String url = args[0];
			String username = args[1];
			String passwd = args[2];
			CustomPersistenceUnitInfo customPersistenceUnitInfo = new CustomPersistenceUnitInfo(puName, url, username, passwd);

			ProjectConfigData.setCustomPersistenceUnitInfo(customPersistenceUnitInfo);
			ProjectConfigData.setHibernateProperties(new HashMap<String, String>());

			//var context = new AnnotationConfigApplicationContext
					//(ProjectConfig.class);

			SpringApplication.run(EAccountingApplication.class, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
