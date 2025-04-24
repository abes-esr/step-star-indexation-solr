package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.dao.IDocumentDao;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import fr.abes.indexationsolr.entities.DocumentIndexationSolrAction;
import fr.abes.indexationsolr.entities.DocumentIndexationSolrOrigin;
import fr.abes.indexationsolr.entities.IDocument;
import fr.abes.indexationsolr.services.DocumentIndexationSolrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DocumentIndexationSolrProcessor implements ItemProcessor<DocumentIndexationSolr, DocumentIndexationSolr>, StepExecutionListener {

    @Autowired
    DocumentIndexationSolrService service;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("DocumentIndexationSolrProcessor beforeStep");
    }

    @Override
    public DocumentIndexationSolr process(DocumentIndexationSolr documentIndexationSolr) {
        log.info("Indexation n° " + documentIndexationSolr.getId());
        IDocument document = service.getDocument(documentIndexationSolr);
        DocumentIndexationSolrAction action = documentIndexationSolr.getAction();
        if (action == DocumentIndexationSolrAction.add) {
            service.indexOnSolr(document);
        }
        else if (action == DocumentIndexationSolrAction.remove){
            service.removeOnSolr(document);
        }
        else {
            throw new IllegalArgumentException("Action " + action + " not supported");
        }


        return documentIndexationSolr;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("DocumentIndexationSolrProcessor afterStep");
        return ExitStatus.COMPLETED;
    }
}