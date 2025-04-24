package fr.abes.indexationsolr.services;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.dao.IDocumentDao;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import fr.abes.indexationsolr.entities.DocumentIndexationSolrAction;
import fr.abes.indexationsolr.entities.DocumentIndexationSolrOrigin;
import fr.abes.indexationsolr.entities.IDocument;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentIndexationSolrService {

    @Autowired
    IndexationSolr service;

    @Autowired
    DaoProvider dao;

    public IDocument getDocument(DocumentIndexationSolr documentIndexationSolr) {
        DocumentIndexationSolrOrigin origin = documentIndexationSolr.getOrigin();
        IDocumentDao documentDao = null;
        if (origin == DocumentIndexationSolrOrigin.star) {
            documentDao = dao.getDocumentStar();
        }
        else if (origin == DocumentIndexationSolrOrigin.sujets) {
            documentDao = dao.getDocumentSujets();
        } else {
            throw new IllegalArgumentException("Unsupported origin: " + origin);
        }

        if (documentDao == null) {
            throw new IllegalArgumentException("Unsupported indexation: " + documentIndexationSolr.getId());
        }

        return (IDocument) documentDao.getOne(documentIndexationSolr.getIdDoc());
    }

    public Boolean handle (DocumentIndexationSolr documentIndexationSolr) {
        IDocument document = this.getDocument(documentIndexationSolr);
        DocumentIndexationSolrAction action = documentIndexationSolr.getAction();
        if (action == DocumentIndexationSolrAction.add) {
            service.indexerDansSolr(document);
        }
        else if (action == DocumentIndexationSolrAction.remove){
            service.supprimerDeSolr(document);
        }
        else {
            throw new IllegalArgumentException("Action " + action + " not supported");
        }
    }
}
