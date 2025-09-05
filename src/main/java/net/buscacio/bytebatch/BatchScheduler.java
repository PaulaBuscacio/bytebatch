package net.buscacio.bytebatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {
  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job job;

  @Scheduled(cron = "0 0 17 17 * ?") // todos os dias 17h18m-segundo minuto hora dia-do-mês mês dia-da-semana
  public void runBatchJob() throws Exception {
    jobLauncher.run(job, new JobParameters());
  }
}
