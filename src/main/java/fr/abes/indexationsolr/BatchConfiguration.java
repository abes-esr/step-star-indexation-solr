package fr.abes.indexationsolr;


import fr.abes.indexationsolr.chunk.DocumentIndexationSolrProcessor;
import fr.abes.indexationsolr.chunk.DocumentIndexationSolrReader;
import fr.abes.indexationsolr.chunk.DocumentIndexationSolrWriter;
import fr.abes.indexationsolr.entities.IDocument;
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

    public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory, @Qualifier("dataSourceOracle") DataSource dataSourceOracle) {
        this.jobs = jobs;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSourceOracle;
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
                .tasklet(uneTasklet())
                .build();
    }

    @Bean
    protected Step stepProcessLines(ItemReader<IDocument> reader, ItemProcessor<IDocument, IDocument> processor, ItemWriter<IDocument> writer) {
        return stepBuilderFactory
                .get("stepProcessLines").<IDocument, IDocument> chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                //.taskExecutor(taskExecutor())
                //.throttleLimit(10)
                .build();
    }

    // ------------- TASKLETS -----------------------
    @Bean
    public Tasklet uneTasklet()
    {
        return new UneTasklet();
    }


    // ----------------- CHUNKS ------------------------------

    @Bean
    public ItemReader<IDocument> itemReader() {
        return new DocumentIndexationSolrReader();
    }

    @Bean
    public ItemProcessor<IDocument, IDocument> itemProcessor() {
        return new DocumentIndexationSolrProcessor();
    }

    @Bean
    public ItemWriter<IDocument> itemWriter() {
        return new DocumentIndexationSolrWriter();
    }
}