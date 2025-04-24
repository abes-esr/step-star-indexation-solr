package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DocumentIndexationSolrReader implements ItemReader<DocumentIndexationSolr>, StepExecutionListener {

    List<DocumentIndexationSolr> documentIndexationSolrs;
    final AtomicInteger i = new AtomicInteger();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("DocumentIndexationSolrReader beforeStep");
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        this.documentIndexationSolrs = (List<DocumentIndexationSolr>) executionContext.get("documentIndexationSolrs");
    }

    @Override
    public DocumentIndexationSolr read() {
        Integer index = i.getAndIncrement();
        if (index >= this.documentIndexationSolrs.size()) {
            return null;
        }
        return this.documentIndexationSolrs.get(index);
    }
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("DocumentIndexationSolrReader afterStep");
        return ExitStatus.COMPLETED;
    }
}