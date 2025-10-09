package fr.abes.indexationsolr.dao;

import fr.abes.indexationsolr.entities.IDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IDocumentDao<K extends IDocument> extends JpaRepository<K, Integer> {
        Page<K> findAll(Pageable pageable);
}
