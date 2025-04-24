package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.entities.IDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class DocumentIndexationSolrProcessor implements ItemProcessor<IDocument, IDocument>, StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Line Processor initialized.");
    }

    @Override
    public IDocument process(IDocument user) {
        log.info(user.toString());
        return user;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Line Processor ended.");
        return ExitStatus.COMPLETED;
    }
}