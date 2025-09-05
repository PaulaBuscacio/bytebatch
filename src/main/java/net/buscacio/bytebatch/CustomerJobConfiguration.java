package net.buscacio.bytebatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;

@Configuration
@Slf4j
public class CustomerJobConfiguration {

  @Autowired
  private PlatformTransactionManager transactionManager;


  @Bean
  public Job job(Step startStep, JobRepository repository) {
    return new JobBuilder("customer-job", repository)
        .start(startStep)
        .next(moveFilesStep(repository))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  public Step startStep(JobRepository repository, ItemReader<Customer> reader, ItemWriter<Customer> writer) {
    return new StepBuilder("start-step", repository)
        .<Customer, Customer>chunk(100, transactionManager)
        .reader(reader)
        .writer(writer)
        .build();
  }

  @Bean
  public ItemReader<Customer> reader() {
    return new FlatFileItemReaderBuilder<Customer>()
        .name("customer-reader")
        .resource(new FileSystemResource("input/data.csv"))
        .delimited().delimiter("|")
        .names("nome", "cpf", "agencia", "conta", "valor", "mesDeReferencia")
        .fieldSetMapper(new CustomerMapper())
        .linesToSkip(1)
        .build();
  }

  @Bean
  public ItemWriter<Customer> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Customer>()
        .dataSource(dataSource)
        .sql("INSERT INTO customer (nome, cpf, agencia, conta, valor, mes_de_referencia, hora_importacao) " +
            "VALUES (:nome, :cpf, :agencia, :conta, :valor, :mesDeReferencia, :horaImportacao)")
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .build();
  }


  @Bean
  public Tasklet moveFilesTasklet() {
    return (contribution, chunkContext) -> {
      File origin = new File("input");
      File destiny = new File("imported-files");

      if (!destiny.exists()) {
        destiny.mkdir();
      }

      File[] files = origin.listFiles((dir, name) -> name.endsWith(".csv"));
      if (files != null) {
        for (File file : files) {
          File fileDest = new File(destiny, file.getName());
          if (file.renameTo(fileDest)) {
            log.info("File moved with success: {}", file.getName());
          } else {
            throw new RuntimeException("File error: " + file.getName());
          }
        }
      }
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  public Step moveFilesStep(JobRepository jobRepository) {
    return new StepBuilder("move-file", jobRepository)
        .tasklet(moveFilesTasklet(), transactionManager)
        .build();
  }
}
