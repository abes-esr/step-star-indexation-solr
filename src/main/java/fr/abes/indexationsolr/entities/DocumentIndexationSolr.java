package fr.abes.indexationsolr.entities;

import fr.abes.indexationsolr.dao.DaoProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DOCUMENT_INDEXATION_SOLR", schema = "PORTAIL")
@NoArgsConstructor
@Getter
@Setter
public class DocumentIndexationSolr implements Serializable, GenericEntity<Integer> {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "IDDOC")
    private Integer idDoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION")
    private DocumentIndexationSolrAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGIN")
    private DocumentIndexationSolrOrigin origin;

}
