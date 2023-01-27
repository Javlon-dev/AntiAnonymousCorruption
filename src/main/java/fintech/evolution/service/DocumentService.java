package fintech.evolution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.entity.user.SecondUserDebate;
import fintech.evolution.variable.entity.user.UserDebate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService extends AbstractService {

    public byte[] getExcelBot() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Очиқ мулоқот");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.BLACK1.getIndex());
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setFont(font);
        style.setAlignment(CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);


        for (int i = 0; i < 3; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 14; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(style);
            }
        }

        var row1 = sheet.getRow(0);
        row1.setHeightInPoints(39.6F);


        var row2 = sheet.getRow(1);
        row2.setHeightInPoints(31.8F);
        var row3 = sheet.getRow(2);
        row3.setHeightInPoints(25.8F);


        Cell cell = row1.getCell(0);
        sheet.setColumnWidth(0, 6 * 256);
        sheet.setColumnWidth(1, 23 * 256);
        sheet.setColumnWidth(2, 23 * 256);
        sheet.setColumnWidth(3, 17 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 12 * 256);
        sheet.setColumnWidth(6, 13 * 256);
        sheet.setColumnWidth(7, 13 * 256);
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 13 * 256);
        sheet.setColumnWidth(10, 18 * 256);
        sheet.setColumnWidth(11, 16 * 256);
        sheet.setColumnWidth(12, 18 * 256);
        sheet.setColumnWidth(13, 23 * 256);


        cell.setCellValue("Т/р");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));

        cell = row1.getCell(1);
        cell.setCellValue("Ф.И.О");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 1));

        cell = row1.getCell(2);
        cell.setCellValue("Тадбиркорлик субъектининг номи");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 2, 2));


        cell = row1.getCell(3);
        cell.setCellValue("Жойлашган ҳудуди");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));

        cell = row2.getCell(3);
        cell.setCellValue("вилоят");
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 3, 3));

        cell = row2.getCell(4);
        cell.setCellValue("шахар/туман");
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 4, 4));


        cell = row1.getCell(5);
        cell.setCellValue("стир");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 5, 5));
        sheet.setColumnWidth(5, 12 * 256);


        cell = row1.getCell(6);
        cell.setCellValue("Фаолият тури ва товар (иш, хизмат) номи");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 6, 7));


        cell = row1.getCell(8);
        cell.setCellValue("Давлат харидлари электрон тизимидан рўйхатдан ўтганлиги");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10));

        cell = row2.getCell(8);
        cell.setCellValue("Оператор номи");
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8));

        cell = row2.getCell(9);
        cell.setCellValue("Рўйхатдан ўтган");
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));

        cell = row3.getCell(9);
        cell.setCellValue("санаси");

        cell = row3.getCell(10);
        cell.setCellValue("ҳудуди");


        cell = row1.getCell(11);
        cell.setCellValue("Давлат харидларида фаолиятини бошлаган санаси");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 11, 11));


        cell = row1.getCell(12);
        cell.setCellValue("Телефон рақами");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 12, 12));


        cell = row1.getCell(13);
        cell.setCellValue("E-mail манзили");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 13, 13));


        List<UserDebate> all = userDebateRepository.findAll();

        XSSFFont userFont = ((XSSFWorkbook) workbook).createFont();
        userFont.setFontName("Arial");
        userFont.setFontHeightInPoints((short) 11);
        userFont.setColor(IndexedColors.BLACK1.getIndex());


        CellStyle userStyle = workbook.createCellStyle();
        userStyle.setWrapText(true);
        userStyle.setFont(userFont);
        userStyle.setAlignment(CENTER);
        userStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 3; i < all.size() + 3; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 14; j++) {
                Cell userCell = row.createCell(j);
                userCell.setCellStyle(userStyle);
            }
        }

        for (int i = 3; i < all.size() + 3; i++) {
            UserDebate debate = all.get(i - 3);
            Row row = sheet.getRow(i);
            row.getCell(0).setCellValue(i - 2);
            String name = debate.getName() == null ? "" : debate.getName();
            String surname = debate.getSurname() == null ? "" : debate.getSurname();
            String fatherName = debate.getFatherName() == null ? "" : debate.getFatherName();
            row.getCell(1).setCellValue(surname + " " + name + " " + fatherName);
            row.getCell(2).setCellValue(debate.getSubjectName());
            row.getCell(3).setCellValue(debate.getRegion());
            row.getCell(4).setCellValue(debate.getDistrict());
            row.getCell(5).setCellValue(debate.getStir().toString());
            row.getCell(6).setCellValue(debate.getServiceType());
            row.getCell(7).setCellValue(debate.getProductName());
            row.getCell(8).setCellValue(debate.getOperatorName());
            row.getCell(9).setCellValue(debate.getRegisteredYear().toString());
            row.getCell(10).setCellValue(debate.getArea());
            row.getCell(11).setCellValue(debate.getActivityYear().toString());
            row.getCell(12).setCellValue(debate.getPhoneNumber());
            row.getCell(13).setCellValue(debate.getEmail());
        }

        try {
            workbook.write(bos);
        } catch (IOException e) {
            log.warn("----> WORK_BOOK ----> " + e.getMessage());
        }
        return bos.toByteArray();
    }

    public byte[] getExcelGoogleForm() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Очиқ мулоқот");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.BLACK1.getIndex());
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setFont(font);
        style.setAlignment(CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);


        for (int i = 0; i < 3; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 14; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(style);
            }
        }

        var row1 = sheet.getRow(0);
        row1.setHeightInPoints(39.6F);


        var row2 = sheet.getRow(1);
        row2.setHeightInPoints(31.8F);
        var row3 = sheet.getRow(2);
        row3.setHeightInPoints(25.8F);


        Cell cell = row1.getCell(0);
        sheet.setColumnWidth(0, 6 * 256);
        sheet.setColumnWidth(1, 23 * 256);
        sheet.setColumnWidth(2, 23 * 256);
        sheet.setColumnWidth(3, 17 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 12 * 256);
        sheet.setColumnWidth(6, 13 * 256);
        sheet.setColumnWidth(7, 13 * 256);
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 13 * 256);
        sheet.setColumnWidth(10, 18 * 256);
        sheet.setColumnWidth(11, 16 * 256);
        sheet.setColumnWidth(12, 18 * 256);
        sheet.setColumnWidth(13, 23 * 256);


        cell.setCellValue("Т/р");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));

        cell = row1.getCell(1);
        cell.setCellValue("Ф.И.О");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 1));

        cell = row1.getCell(2);
        cell.setCellValue("Тадбиркорлик субъектининг номи");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 2, 2));


        cell = row1.getCell(3);
        cell.setCellValue("Жойлашган ҳудуди");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));

        cell = row2.getCell(3);
        cell.setCellValue("вилоят");
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 3, 3));

        cell = row2.getCell(4);
        cell.setCellValue("шахар/туман");
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 4, 4));


        cell = row1.getCell(5);
        cell.setCellValue("стир");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 5, 5));
        sheet.setColumnWidth(5, 12 * 256);


        cell = row1.getCell(6);
        cell.setCellValue("Фаолият тури ва товар (иш, хизмат) номи");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 6, 7));


        cell = row1.getCell(8);
        cell.setCellValue("Давлат харидлари электрон тизимидан рўйхатдан ўтганлиги");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10));

        cell = row2.getCell(8);
        cell.setCellValue("Оператор номи");
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8));

        cell = row2.getCell(9);
        cell.setCellValue("Рўйхатдан ўтган");
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));

        cell = row3.getCell(9);
        cell.setCellValue("санаси");

        cell = row3.getCell(10);
        cell.setCellValue("ҳудуди");


        cell = row1.getCell(11);
        cell.setCellValue("Давлат харидларида фаолиятини бошлаган санаси");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 11, 11));


        cell = row1.getCell(12);
        cell.setCellValue("Телефон рақами");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 12, 12));


        cell = row1.getCell(13);
        cell.setCellValue("E-mail манзили");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 13, 13));


        List<SecondUserDebate> all = secondUserDebateRepository.findAll();

        XSSFFont userFont = ((XSSFWorkbook) workbook).createFont();
        userFont.setFontName("Arial");
        userFont.setFontHeightInPoints((short) 11);
        userFont.setColor(IndexedColors.BLACK1.getIndex());


        CellStyle userStyle = workbook.createCellStyle();
        userStyle.setWrapText(true);
        userStyle.setFont(userFont);
        userStyle.setAlignment(CENTER);
        userStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 3; i < all.size() + 3; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 14; j++) {
                Cell userCell = row.createCell(j);
                userCell.setCellStyle(userStyle);
            }
        }

        for (int i = 3; i < all.size() + 3; i++) {
            SecondUserDebate debate = all.get(i - 3);
            Row row = sheet.getRow(i);
            row.getCell(0).setCellValue(i - 2);
            String name = debate.getName() == null ? "" : debate.getName();
            String surname = debate.getSurname() == null ? "" : debate.getSurname();
            String fatherName = debate.getFatherName() == null ? "" : debate.getFatherName();
            row.getCell(1).setCellValue(surname + " " + name + " " + fatherName);
            row.getCell(2).setCellValue(debate.getSubjectName());
            row.getCell(3).setCellValue(debate.getRegion());
            row.getCell(4).setCellValue(debate.getDistrict());
            row.getCell(5).setCellValue(debate.getStir().toString());
            row.getCell(6).setCellValue(debate.getServiceType());
            row.getCell(7).setCellValue(debate.getProductName());
            row.getCell(8).setCellValue(debate.getOperatorName());
            row.getCell(9).setCellValue(debate.getRegisteredYear().toString());
            row.getCell(10).setCellValue(debate.getArea());
            row.getCell(11).setCellValue(debate.getActivityYear().toString());
            row.getCell(12).setCellValue(debate.getPhoneNumber());
            row.getCell(13).setCellValue(debate.getEmail());
        }

        try {
            workbook.write(bos);
        } catch (IOException e) {
            log.warn("----> WORK_BOOK ----> " + e.getMessage());
        }
        return bos.toByteArray();
    }

    public void getUrl(String url) {
        secondUserDebateRepository.deleteAll();
        try {
            InputStream is = new URL(url).openStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);

            var last = sheet.getLastRowNum();
            for (int i = 1; i <= last; i++) {
                try {
                    var row = sheet.getRow(i);
                    String name = String.valueOf(row.getCell(1));
                    String surname = String.valueOf(row.getCell(2));
                    String fatherName = row.getCell(3) == null ? "" : String.valueOf(row.getCell(3));
                    String phone = String.valueOf(row.getCell(4));
                    String email = String.valueOf(row.getCell(5));
                    String subjectName = String.valueOf(row.getCell(6));
                    String region = String.valueOf(row.getCell(7));
                    String dist = String.valueOf(row.getCell(8));
                    long stir = (long) Double.parseDouble(String.valueOf(row.getCell(9)));
                    String type = String.valueOf(row.getCell(10));
                    String productName = String.valueOf(row.getCell(11));
                    int activityYear = (int) Double.parseDouble(String.valueOf(row.getCell(12)));
                    String operatorName = String.valueOf(row.getCell(13));
                    int registrationYear = (int) Double.parseDouble((String.valueOf(row.getCell(14))));
                    String address = String.valueOf(row.getCell(15));

                    SecondUserDebate userDebate = new SecondUserDebate();
                    userDebate.setName(name);
                    userDebate.setSurname(surname);
                    userDebate.setFatherName(fatherName);
                    userDebate.setPhoneNumber(phone);
                    userDebate.setEmail(email);
                    userDebate.setSubjectName(subjectName);
                    userDebate.setRegion(region);
                    userDebate.setDistrict(dist);
                    userDebate.setStir(stir);
                    userDebate.setServiceType(type);
                    userDebate.setProductName(productName);
                    userDebate.setActivityYear(activityYear);
                    userDebate.setOperatorName(operatorName);
                    userDebate.setRegisteredYear(registrationYear);
                    userDebate.setArea(address);
                    Optional<SecondUserDebate> debate = secondUserDebateRepository.findByStir(userDebate.getStir());
                    debate.ifPresent(secondUserDebate -> userDebate.setId(secondUserDebate.getId()));
                    secondUserDebateRepository.save(userDebate);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
