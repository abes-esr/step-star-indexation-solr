package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class DocumentIndexationSolrWriter implements ItemWriter<DocumentIndexationSolr>, StepExecutionListener {

    @Autowired
    DaoProvider daoProvider;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("DocumentIndexationSolrWriter beforeStep");
    }

	@Override
    public void write(List<? extends DocumentIndexationSolr>lines) {
        for (DocumentIndexationSolr documentIndexationSolr : lines) {
            log.info("Removing line " + documentIndexationSolr.getId());
            daoProvider.getDocumentIndexationSolr().delete(documentIndexationSolr);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("DocumentIndexationSolrWriter afterStep");
        return ExitStatus.COMPLETED;
    }
}
