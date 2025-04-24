package fr.abes.indexationsolr.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;

@Entity
@Table(name = "DOCUMENT", schema = "SUJETS")
@NoArgsConstructor
@Getter
@Setter
public class DocumentSujets implements IDocument {

    @Id
    @Column(name = "IDDOC")
    private Integer idDoc;

    @ColumnTransformer(read = "NVL2(DOC, (DOC).getClobVal(), NULL)", write = "NULLSAFE_XMLTYPE(?)")
    @Lob
    @Column(name = "DOC", columnDefinition = "XMLType")
    private String doc;

    @Column(name = "CODEETAB")
    private String codeEtab;


    public DocumentSujets(Integer idDoc, String doc, String codeEtab) {
        this.idDoc = idDoc;
        this.doc = doc;
        this.codeEtab = codeEtab;
    }

    @Override
    public Integer getId() {
        return idDoc;
    }

}
