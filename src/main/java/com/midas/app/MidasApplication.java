package com.midas.app;

import com.midas.app.workflows.CreateAccountActivityImpl;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.CreateAccountWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class MidasApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(MidasApplication.class, args);

    // Retrieve the API key from application.properties
    Environment env = context.getBean(Environment.class);
    String apiKey = env.getProperty("stripe.api.key");

    // Configuration for Temporal
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    WorkflowClient client = WorkflowClient.newInstance(service);

    // Worker factory is used to create Workers that poll specific Task Queues.
    WorkerFactory factory = WorkerFactory.newInstance(client);

    // this worker take care of entire workflow
    Worker worker = factory.newWorker(CreateAccountWorkflow.QUEUE_NAME);
    worker.registerWorkflowImplementationTypes(CreateAccountWorkflowImpl.class);
    worker.registerActivitiesImplementations(new CreateAccountActivityImpl(apiKey));
    factory.start();
  }
}
