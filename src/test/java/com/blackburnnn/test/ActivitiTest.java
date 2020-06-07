package com.blackburnnn.test;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 测试类
 *     作用：测试activiti所需要的25张表的生成
 */
public class ActivitiTest {

    @Test
    public void testGenTable1(){
        //条件：1.activiti配置文件名称：activiti.cfg.xml
        // 2.bean的id="processEngineConfiguration"
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);

       // HistoryService historyService = processEngine.getHistoryService();

    }

    //创建ProcessEngineConfiguration,通过ProcessEngineConfiguration创建ProcessEngine，在创建ProcessEngine时创建数据库
    @Test
    public void testGenTable(){
        //1.创建ProcessEngineConfiguration对象  第一个参数resource:配置文件名称  第二个参数beanName:processEngineConfiguration的bean的id
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml","processEngineConfiguration");
        //2.创建ProcesEngine对象
        ProcessEngine processEngine = configuration.buildProcessEngine();

        //3.输出processEngine对象
        System.out.println("====================================================");
        System.out.println(processEngine);
        System.out.println("====================================================");
    }

    //查询部署的流程定义
    @Test
    public void queryProceccDefinition() {
// 流程定义key
        String processDefinitionKey = "holiday";
// 获取repositoryService
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
// 查询流程定义
        ProcessDefinitionQuery processDefinitionQuery = repositoryService
                .createProcessDefinitionQuery();
//遍历查询结果
        List<ProcessDefinition> list = processDefinitionQuery
                .processDefinitionKey(processDefinitionKey).orderByProcessDefinitionVersion().desc().list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println("------------------------");
            System.out.println(" 流 程 部 署 id ： " +
                    processDefinition.getDeploymentId());
            System.out.println("流程定义id：" + processDefinition.getId());
            System.out.println("流程定义名称：" + processDefinition.getName());
            System.out.println("流程定义key：" + processDefinition.getKey());
            System.out.println("流程定义版本：" + processDefinition.getVersion());
        }
    }
    //删除已部署成功的流程定义
    public void deleteDeployment() {
        // 流程部署id
        String deploymentId = "8801";
        // 通过流程引擎获取repositoryService
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        //repositoryService.deleteDeployment(deploymentId, true);
    }

    //通过流程定义对象获取流程资源
    @Test
    public void getProcessResources() throws IOException {
// 流程定义id
        String processDefinitionId = "holiday:1:4";
// 获取repositoryService
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
// 流程定义对象
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
//获取bpmn
        String resource_bpmn = processDefinition.getResourceName();
//获取png
        String resource_png =
                processDefinition.getDiagramResourceName();
// 资源信息
        System.out.println("bpmn：" + resource_bpmn);
        System.out.println("png：" + resource_png);
        File file_png = new File("d:/purchasingflow01.png");
        File file_bpmn = new File("d:/purchasingflow01.bpmn");
// 输出bpmn
        InputStream resourceAsStream = null;
        resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resource_bpmn);

        FileOutputStream fileOutputStream = new
                FileOutputStream(file_bpmn);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            fileOutputStream.write(b, 0, len);
        }
// 输出图片
        resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resource_png);
        fileOutputStream = new FileOutputStream(file_png);
// byte[] b = new byte[1024];
// int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            fileOutputStream.write(b, 0, len);
        }
    }

    //通过流程部署信息获取流程定义资源
    // 获取流程定义图片资源
    @Test
    public void getProcessResources1() throws IOException {

        //流程部署id act_re_deployment的自增ID_
        String deploymentId = "1";
        // 通过流程引擎获取repositoryService
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        //读取资源名称
        List<String> resources =
                repositoryService.getDeploymentResourceNames(deploymentId);
        String resource_image = null;
        //获取图片
        for(String resource_name :resources){
            if(resource_name.indexOf(".png")>=0){
                resource_image = resource_name;
            } }
        //图片输入流
        InputStream inputStream =
                repositoryService.getResourceAsStream(deploymentId, resource_image);
        File exportFile = new File("d:/holiday.png");
        FileOutputStream fileOutputStream = new
                FileOutputStream(exportFile);
        byte[] buffer = new byte[1024];
        int len = -1;
//输出图片
        while((len = inputStream.read(buffer))!=-1){
            fileOutputStream.write(buffer, 0, len);
        }
        inputStream.close();
        fileOutputStream.close();
    }

    //流程历史信息查询
    public void testHistoric01(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery query =
                historyService.createHistoricActivityInstanceQuery();
        query.processInstanceId("2501");

        List<HistoricActivityInstance> list = query.list();
        for(HistoricActivityInstance ai :list){
            System.out.println(ai.getActivityId());
            System.out.println(ai.getActivityName());
            System.out.println(ai.getProcessDefinitionId());
            System.out.println(ai.getProcessInstanceId());
            System.out.println("==============================");
        }
    }
}
