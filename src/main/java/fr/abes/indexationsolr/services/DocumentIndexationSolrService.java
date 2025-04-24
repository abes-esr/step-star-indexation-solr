package fr.abes.indexationsolr.services;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.dao.IDocumentDao;
import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import fr.abes.indexationsolr.entities.DocumentIndexationSolrAction;
import fr.abes.indexationsolr.entities.DocumentIndexationSolrOrigin;
import fr.abes.indexationsolr.entities.IDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class DocumentIndexationSolrService {

    @Autowired
    IndexationSolr service;

    @Autowired
    DaoProvider dao;

    @Autowired
    private PathsFromProperties pathsFromProperties;

    @Transactional
    public Optional<IDocument> getDocument(DocumentIndexationSolr documentIndexationSolr) {
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

        return documentDao.findById(documentIndexationSolr.getIdDoc());
    }

    public String getCheminXsl (DocumentIndexationSolr documentIndexationSolr) {
        DocumentIndexationSolrOrigin origin = documentIndexationSolr.getOrigin();
        if (origin == DocumentIndexationSolrOrigin.star) {
            return pathsFromProperties.getCheminXslStar();
        }
        else if (origin == DocumentIndexationSolrOrigin.sujets) {
            return pathsFromProperties.getCheminXslSujets();
        } else {
            throw new IllegalArgumentException("Unsupported origin: " + origin);
        }
    }

    public String getUrlSolr (DocumentIndexationSolr documentIndexationSolr) {
        DocumentIndexationSolrOrigin origin = documentIndexationSolr.getOrigin();
        if (origin == DocumentIndexationSolrOrigin.star) {
            return pathsFromProperties.getUrlSolrStar();
        }
        else if (origin == DocumentIndexationSolrOrigin.sujets) {
            return pathsFromProperties.getUrlSolrSujets();
        } else {
            throw new IllegalArgumentException("Unsupported origin: " + origin);
        }
    }

    public Boolean handle (DocumentIndexationSolr documentIndexationSolr) {
        try {
            Optional<IDocument> document = this.getDocument(documentIndexationSolr);
            pathsFromProperties.setPathsParam();
            DocumentIndexationSolrAction action = documentIndexationSolr.getAction();
            Integer idDoc = documentIndexationSolr.getIdDoc();
            String urlSolr = this.getUrlSolr(documentIndexationSolr);

            if (!document.isPresent()) {
                service.supprimerDeSolr(idDoc, urlSolr);
            }
            else if (action == DocumentIndexationSolrAction.add) {
                String tef = document.get().getDoc();
                String cheminXsl = this.getCheminXsl(documentIndexationSolr);
                service.indexerDansSolr(idDoc, tef, cheminXsl, urlSolr);
            }
            else if (action == DocumentIndexationSolrAction.remove){
                service.supprimerDeSolr(idDoc, urlSolr);
            }
            else {
                throw new IllegalArgumentException("Action " + action + " not supported");
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
