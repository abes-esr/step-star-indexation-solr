package fr.abes.indexationsolr.entities;

import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

public interface IDocument extends Serializable, GenericEntity<Integer> {

    Integer getIdDoc();
    String getDoc();
    String getCodeEtab();
}
