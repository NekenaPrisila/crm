package site.easy.to.build.crm.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CsvGenerator {

    public <T> void generateCsv(final String filePath, final List<T> records, final Class<T> clazz) throws IOException {
        try (Writer outputWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath), StandardOpenOption.CREATE), StandardCharsets.UTF_8)) {
            final CsvWriterSettings csvWriterSettings = new CsvWriterSettings();
            csvWriterSettings.setRowWriterProcessor(new BeanWriterProcessor<>(clazz));

            final CsvWriter writer = new CsvWriter(outputWriter, csvWriterSettings);
            writer.writeHeaders();
            writer.processRecords(records);
            writer.close();
        }
    }
}

