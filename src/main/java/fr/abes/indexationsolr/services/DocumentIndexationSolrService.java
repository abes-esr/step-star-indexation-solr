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

/**
 * Service qui traite une indexation de document dans SOLR en fonction de l'action (ADD/REMOVE) et de l'origine (STEP/STAR)
 * ADD: Ajoute ou mets à jour le document dans SOLR
 * REMOVE: Supprime le document de SOLR
 */
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
        IDocumentDao documentDao;
        if (origin == DocumentIndexationSolrOrigin.star) {
            documentDao = dao.getDocumentStarDao();
        }
        else if (origin == DocumentIndexationSolrOrigin.sujets) {
            documentDao = dao.getDocumentSujetsDao();
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
            pathsFromProperties.setPathsParam();
            DocumentIndexationSolrAction action = documentIndexationSolr.getAction();
            Integer idDoc = documentIndexationSolr.getIdDoc();
            String urlSolr = this.getUrlSolr(documentIndexationSolr);

            // Dans le cas où l'on souhaite supprimer une thèse de l'indexation
            // Alors, on la supprime de SOLR
            if (action == DocumentIndexationSolrAction.remove){
                service.supprimerDeSolr(idDoc, urlSolr);
            }
            // Dans le cas où l'on souhaite indexer une thèse
            else if (action == DocumentIndexationSolrAction.add) {
                Optional<IDocument> document = this.getDocument(documentIndexationSolr);
                // Si la thèse n'est pas présente en base de données Oracle
                // Alors, on la supprime de SOLR
                if (!document.isPresent()) {
                    service.supprimerDeSolr(idDoc, urlSolr);
                }
                // Si elle est présente en base de données Oracle
                // Alors, on l'ajoute dans SOLR
                else {
                    String tef = document.get().getDoc();
                    String cheminXsl = this.getCheminXsl(documentIndexationSolr);
                    service.indexerDansSolr(idDoc, tef, cheminXsl, urlSolr);
                }
            }
            else {
                throw new IllegalArgumentException("Unsupproted action: " + action);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
