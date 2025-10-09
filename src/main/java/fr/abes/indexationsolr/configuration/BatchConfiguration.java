package fr.abes.indexationsolr.configuration;

import fr.abes.indexationsolr.chunk.DocumentIndexationSolrTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    protected final JobBuilderFactory jobs;

    protected final StepBuilderFactory stepBuilderFactory;

    protected final DataSource dataSource;

    // ---------- JOB ---------------------------------------------

    public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory, @Qualifier("dataSource") DataSource dataSource) {
        this.jobs = jobs;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public Job documentIndexationSolr() {
        log.info("début du job documentIndexationSolr");
        return jobs
                .get("documentIndexationSolr")
                .start(executerTasklet())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    // ***************** TASK EXECUTOR **************************
    @Bean
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    // ---------- STEP --------------------------------------------

    @Bean
    public Step executerTasklet() {
        return stepBuilderFactory
                .get("executerTasklet").allowStartIfComplete(true)
                .tasklet(documentIndexationSolrTasklet())
                .build();
    }

    // ------------- TASKLETS -----------------------
    @Bean
    public DocumentIndexationSolrTasklet documentIndexationSolrTasklet()
    {
        return new DocumentIndexationSolrTasklet();
    }

}