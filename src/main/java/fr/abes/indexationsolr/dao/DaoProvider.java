package fr.abes.indexationsolr.dao;

import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Getter
@Service
public class DaoProvider {

    @Resource
    private IDocumentSujetsDao documentSujets;

    @Resource
    private IDocumentStarDao documentStar;

    @Resource
    private  IDocumentIndexationSolrDao documentIndexationSolr;
}
