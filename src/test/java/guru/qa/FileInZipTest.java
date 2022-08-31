package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import guru.qa.domains.SoccerPlayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;


public class FileInZipTest {
    ClassLoader classLoader = FileInZipTest.class.getClassLoader();


    String zipName = "resources.zip";
    String xlsFileName = "exampleXlsx.xlsx";
    String pdfFileName = "examplePdf.pdf";
    String csvFileName = "exampleCsv.csv";

    @DisplayName("Проверка файла exampleCsv.csv в архиве resources.zip")
    @Test
    void zipCsvTest() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zipFile = new ZipFile(new File("src/test/resources/" + zipName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equals(csvFileName)) {
                try (InputStream stream = zipFile.getInputStream(entry)) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
                    List<String[]> csv = reader.readAll();
                    assertThat(csv).contains(
                            new String[]{"Марка", "модель", "производство"},
                            new String[]{"BMW", "x5", "Германия"},
                            new String[]{"Peugeot", "508", "Франция"}
                    );
                }
            }
        }
    }


    @DisplayName("Проверка файла exampleXlsx.xlsx в архиве resources.zip")
    @Test
    void xlsxZipTest() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipName);
        ZipInputStream zip = new ZipInputStream(is);
        ZipEntry entry;
        ZipFile zipFile = new ZipFile(new File("src/test/resources/" + zipName));
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().contains(xlsFileName)) {
                try (InputStream stream = zipFile.getInputStream(entry)) {
                    XLS xls = new XLS(stream);
                    assertThat(xls.excel.getSheetAt(0)
                            .getRow(0)
                            .getCell(3)
                            .getStringCellValue()).contains("ЖИРЫ");
                }
            }
        }
    }

    @DisplayName("Проверка файла examplePdf.pdf в архиве resources.zip")
    @Test
    void pdfZipTest() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipName);
        ZipInputStream zip = new ZipInputStream(is);
        ZipEntry entry;
        ZipFile zipFile = new ZipFile(new File("src/test/resources/" + zipName));
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().contains(pdfFileName)) {
                try (InputStream stream = zipFile.getInputStream(entry)) {
                    PDF pdf = new PDF(stream);
                    assertThat(pdf.text).contains("Пример PDF файла");
                }
            }
        }
    }

    @DisplayName("Проверка файла soccerPlayer.json + jackson")
    @Test
    void jsonJacksonTest() throws Exception {
        InputStream is = classLoader.getResourceAsStream("soccerPlayer.json");
        ObjectMapper objectMapper = new ObjectMapper();
        SoccerPlayer soccerPlayer = objectMapper.readValue(is, SoccerPlayer.class);
        assertThat(soccerPlayer.getFirstName().equals("Lev"));
        assertThat(soccerPlayer.getLastName()).isEqualTo("Yashin");
        assertThat(soccerPlayer.isBestPlayer()).isEqualTo(true);
    }
}
