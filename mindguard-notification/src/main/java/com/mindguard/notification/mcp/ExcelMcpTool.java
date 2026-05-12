package com.mindguard.notification.mcp;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ExcelMcpTool {

    public byte[] generateReport(List<Map<String, Object>> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("心理评估报告");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("用户ID");
            header.createCell(1).setCellValue("风险等级");
            header.createCell(2).setCellValue("分析结果");
            header.createCell(3).setCellValue("关键词");
            header.createCell(4).setCellValue("评估时间");

            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> record = data.get(i);
                row.createCell(0).setCellValue(String.valueOf(record.getOrDefault("userId", "")));
                row.createCell(1).setCellValue(String.valueOf(record.getOrDefault("riskLevel", "")));
                row.createCell(2).setCellValue(String.valueOf(record.getOrDefault("analysisText", "")));
                row.createCell(3).setCellValue(String.valueOf(record.getOrDefault("keywords", "")));
                row.createCell(4).setCellValue(String.valueOf(record.getOrDefault("createdAt", "")));
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}
