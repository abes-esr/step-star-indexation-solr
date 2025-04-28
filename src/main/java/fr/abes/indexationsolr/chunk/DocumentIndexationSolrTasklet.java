
package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import fr.abes.indexationsolr.services.DocumentIndexationSolrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

@Slf4j
public class DocumentIndexationSolrTasklet implements Tasklet, StepExecutionListener {

    @Autowired
    DocumentIndexationSolrService service;

    @Autowired
    DaoProvider dao;

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        log.info("Dans la Tasklet DocumentIndexationSolrTasklet");
        try {
            List<DocumentIndexationSolr> documentIndexationSolrs = dao.getDocumentIndexationSolr().findAll(Sort.by(Sort.Order.asc("id")));
            documentIndexationSolrs.forEach(documentIndexationSolr -> {
                try {
                    log.info("Indexation n°" + documentIndexationSolr.getId() + " en cours.");
                    log.info("IdDoc: " + documentIndexationSolr.getIdDoc());
                    log.info("Action: " + documentIndexationSolr.getAction());
                    log.info("Origin: " + documentIndexationSolr.getOrigin());
                    Boolean isIndexed = service.handle(documentIndexationSolr);
                    if (isIndexed) {
                        log.info("Indexation n°" + documentIndexationSolr.getId() + " a aboutie.");
                        dao.getDocumentIndexationSolr().delete(documentIndexationSolr);
                        log.info("Indexation n°" + documentIndexationSolr.getId() + " supprimée de la table DOCUMENT_INDEXATION_SOLR.");
                    }
                    else {
                        log.error("Indexation n°" + documentIndexationSolr.getId() + " n'a pas aboutie.");
                    }
                } catch (Exception e) {
                    log.error("Indexation n°" + documentIndexationSolr.getId() + " a subit une erreur: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            log.error("Tasklet DocumentIndexationSolrTasklet a subit une erreur: " + e.getMessage());
        }
        return RepeatStatus.FINISHED;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}
