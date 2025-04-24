package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.entities.DocumentStar;
import fr.abes.indexationsolr.entities.IDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DocumentIndexationSolrReader implements ItemReader<IDocument>, StepExecutionListener {

    Page<DocumentStar> users;
    final AtomicInteger i = new AtomicInteger();
    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        // this.users = (List<Document>) executionContext.get("userList");
        this.users = (Page<DocumentStar>) executionContext.get("docList");
    }

    @Override
    public IDocument read() {
        DocumentStar user = null;

        if (i.intValue() < this.users.getSize()) {
            user = (DocumentStar) users.get();
        }
        return user;
    }
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Line Reader ended.");
        return ExitStatus.COMPLETED;
    }
}