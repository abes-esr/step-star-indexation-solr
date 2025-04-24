
package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

@Slf4j
public class DocumentIndexationSolrTasklet implements Tasklet, StepExecutionListener {

    List<DocumentIndexationSolr> documentIndexationSolrs;

    @Autowired
    DaoProvider dao;

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        log.info("DANS LA TASKLET");

        try {
            documentIndexationSolrs = dao.getDocumentIndexationSolr().findAll(Sort.by(Sort.Order.asc("id")));
        }
        catch (Exception e) {
            log.error("erreur dans la tasklet :" + e);
        }

        return RepeatStatus.FINISHED;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        executionContext.put("documentIndexationSolrs", this.documentIndexationSolrs);
        return stepExecution.getExitStatus();
    }
}
