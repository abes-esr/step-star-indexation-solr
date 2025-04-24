package fr.abes.indexationsolr;


import fr.abes.indexationsolr.chunk.DocumentIndexationSolrProcessor;
import fr.abes.indexationsolr.chunk.DocumentIndexationSolrReader;
import fr.abes.indexationsolr.chunk.DocumentIndexationSolrTasklet;
import fr.abes.indexationsolr.chunk.DocumentIndexationSolrWriter;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
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
    public Job jobTraitement() {
        log.info("debut du job : jobTraitement...");

        return jobs
                .get("chunksJob")
                .start(executerTasklet())
                .next(stepProcessLines(itemReader(), itemProcessor(), itemWriter()))
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

    @Bean
    protected Step stepProcessLines(ItemReader<DocumentIndexationSolr> reader, ItemProcessor<DocumentIndexationSolr, DocumentIndexationSolr> processor, ItemWriter<DocumentIndexationSolr> writer) {
        return stepBuilderFactory
                .get("stepProcessLines").<DocumentIndexationSolr, DocumentIndexationSolr> chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                //.taskExecutor(taskExecutor())
                //.throttleLimit(10)
                .build();
    }

    // ------------- TASKLETS -----------------------
    @Bean
    public DocumentIndexationSolrTasklet documentIndexationSolrTasklet()
    {
        return new DocumentIndexationSolrTasklet();
    }


    // ----------------- CHUNKS ------------------------------

    @Bean
    public ItemReader<DocumentIndexationSolr> itemReader() {
        return new DocumentIndexationSolrReader();
    }

    @Bean
    public ItemProcessor<DocumentIndexationSolr, DocumentIndexationSolr> itemProcessor() {
        return new DocumentIndexationSolrProcessor();
    }

    @Bean
    public ItemWriter<DocumentIndexationSolr> itemWriter() {
        return new DocumentIndexationSolrWriter();
    }
}