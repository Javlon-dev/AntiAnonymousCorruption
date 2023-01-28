package fintech.evolution.service;

import fintech.evolution.service.user.AbstractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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



        XSSFFont userFont = ((XSSFWorkbook) workbook).createFont();
        userFont.setFontName("Arial");
        userFont.setFontHeightInPoints((short) 11);
        userFont.setColor(IndexedColors.BLACK1.getIndex());


        CellStyle userStyle = workbook.createCellStyle();
        userStyle.setWrapText(true);
        userStyle.setFont(userFont);
        userStyle.setAlignment(CENTER);
        userStyle.setVerticalAlignment(VerticalAlignment.CENTER);



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



        XSSFFont userFont = ((XSSFWorkbook) workbook).createFont();
        userFont.setFontName("Arial");
        userFont.setFontHeightInPoints((short) 11);
        userFont.setColor(IndexedColors.BLACK1.getIndex());


        CellStyle userStyle = workbook.createCellStyle();
        userStyle.setWrapText(true);
        userStyle.setFont(userFont);
        userStyle.setAlignment(CENTER);
        userStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return null;
    }


}
