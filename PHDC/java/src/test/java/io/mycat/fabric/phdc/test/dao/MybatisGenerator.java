package io.mycat.fabric.phdc.test.dao;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MybatisGenerator {

    public static void main(String args[]) {

        try {
            System.setProperty("file.encoding", "UTF-8");
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(
                    MybatisGenerator.class.getResourceAsStream("/mybatis-generator.xml"));
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(new ProgressCallback() {
                @Override
                public void introspectionStarted(int totalTasks) {

                }

                @Override
                public void generationStarted(int totalTasks) {

                }

                @Override
                public void saveStarted(int totalTasks) {

                }

                @Override
                public void startTask(String taskName) {
                    System.out.println(taskName);
                }

                @Override
                public void done() {

                }

                @Override
                public void checkCancel() throws InterruptedException {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
