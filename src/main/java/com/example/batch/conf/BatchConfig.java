package com.example.batch.conf;

import com.example.batch.listen.JobCompletionNotificationListener;
import com.example.batch.model.Echo;
import com.example.batch.task.EchoItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Echo> reader() {
        return new FlatFileItemReaderBuilder<Echo>()
                .name("echoItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("call", "response")
                .targetType(Echo.class)
                .build();
    }

    @Bean
    public EchoItemProcessor processor() {
        return new EchoItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Echo> writer(DataSource datasource) {
        return new JdbcBatchItemWriterBuilder<Echo>()
                .sql("INSERT INTO Echo (Call, Response) VALUES (:call, :response)")
                .dataSource(datasource)
                .beanMapped()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager
            , FlatFileItemReader<Echo> reader, EchoItemProcessor processor, JdbcBatchItemWriter<Echo> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Echo, Echo> chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job callAndResponseJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("Call&ResponseJob", jobRepository)
                .incrementer(new RunIdIncrementer())  // NOTE: 同じ Job を Param 気にせず、何度でも叩けるよう設定
                .listener(listener)
                .start(step1)
                .build();
    }
}
