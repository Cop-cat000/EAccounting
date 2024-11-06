package project.EAccounting.config;

import java.util.Map;

import project.EAccounting.persistence.CustomPersistenceUnitInfo;

public final class ProjectConfigData {

    static CustomPersistenceUnitInfo customPersistenceUnitInfo;

    static Map<String,String> hibernateProperties;


    public static void setCustomPersistenceUnitInfo(CustomPersistenceUnitInfo cpui) {
        customPersistenceUnitInfo = cpui;
    }

    public static void setHibernateProperties(Map<String,String> props) {
        hibernateProperties = props;
    }

    private ProjectConfigData() {}
}
