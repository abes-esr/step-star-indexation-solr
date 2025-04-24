package fr.abes.indexationsolr;

import fr.abes.indexationsolr.dao.DaoProvider;
import fr.abes.indexationsolr.entities.DocumentStar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;


@Slf4j
public class UneTasklet implements Tasklet, StepExecutionListener  {

	long star = 0;
	long sujets = 0;
	Page<DocumentStar> documents;

	@Autowired
	DaoProvider dao;

	@Override
	public void beforeStep(StepExecution stepExecution) {

	}

	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

		log.info("DANS LA TASKLET");

		try
		{
			star = dao.getDocumentStar().count();
			sujets = dao.getDocumentSujets().count();
			log.debug("Star list size ="+ star);
			log.debug("Sujets list size ="+ sujets);
//			log.debug("Star2 list size ="+ dao.getDocument().count());
			Pageable pageable = PageRequest.of(0, 10, Sort.by("idDoc").ascending());

			documents = dao.getDocumentStar().findAll(pageable);
		}
		catch (Exception e)
		{
			log.error("erreur dans la tasklet :" + e);
		}

		return RepeatStatus.FINISHED;
	}


	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution
				.getJobExecution()
				.getExecutionContext();
		executionContext.put("docList", this.documents);
		return stepExecution.getExitStatus();
	}
}
