package fr.abes.indexationsolr.chunk;

import fr.abes.indexationsolr.entities.IDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
public class DocumentIndexationSolrWriter implements ItemWriter<IDocument>, StepExecutionListener {

    private String source = "test";
    private PrintWriter out;
    private Long counTer;
    @Override
    public void beforeStep(StepExecution stepExecution) {
        counTer = 0L;
        try {
            out = new PrintWriter(new FileWriter(source));
        } catch (IOException e) {
            log.error(e.toString());
        }
        log.info("Line Writer initialized.");
    }

	@Override
    public void write(List<? extends IDocument>lines) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        for (IDocument line : lines) {
			stringBuilder.append(line + "...");
            out.println(line);
            counTer++;
        }
		log.info("dans le writer : " + stringBuilder.toString());

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        out.println(String.format("===== Il y a %s utilisateurs dans la base =====", counTer));
        out.close();
        return ExitStatus.COMPLETED;
    }
}
