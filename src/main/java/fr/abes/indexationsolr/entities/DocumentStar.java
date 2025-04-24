package fr.abes.indexationsolr.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "DOCUMENT", schema = "STAR")
@NoArgsConstructor
@Getter
@Setter
public class DocumentStar implements IDocument {

    @Id
    @Column(name = "IDDOC")
    private Integer idDoc;

    @ColumnTransformer(read = "NVL2(DOC, (DOC).getClobVal(), NULL)", write = "NULLSAFE_XMLTYPE(?)")
    @Lob
    @Column(name = "DOC", columnDefinition = "XMLType")
    private String doc;

    @Column(name = "CODEETAB")
    private String codeEtab;

    @Column(name = "TEXTE")
    private String texte;


    @Column(name = "ENVOISOLR")
    private Integer envoiSolr;

    public DocumentStar(Integer idDoc, String doc, String codeEtab, String texte, Integer envoiSolr) {
        this.idDoc = idDoc;
        this.doc = doc;
        this.codeEtab = codeEtab;
        this.texte = texte;
        this.envoiSolr = envoiSolr;
    }


    @Override
    public Integer getId() {
        return idDoc;
    }


}
