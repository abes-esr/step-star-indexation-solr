package fr.abes.indexationsolr.dao;

import fr.abes.indexationsolr.entities.DocumentIndexationSolr;
import fr.abes.indexationsolr.entities.IDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

public interface IDocumentIndexationSolrDao extends JpaRepository<DocumentIndexationSolr, Integer> {
        Page<DocumentIndexationSolr> findAll(Pageable pageable);
}
