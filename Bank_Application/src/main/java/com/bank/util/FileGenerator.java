package com.bank.util;

import com.bank.dao.UserDao.Transaction;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.List;

public class FileGenerator {
    public static File generateTransactionPDF(List<Transaction> transactions, long accountNo) throws Exception {
        String fileName = "TransactionHistory_" + accountNo + ".pdf";
        File file = new File(fileName);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Transaction History for Account: " + accountNo));
        Table table = new Table(new float[]{2, 2, 3});
        table.addHeaderCell("Type");
        table.addHeaderCell("Amount");
        table.addHeaderCell("Date");

        for (Transaction transaction : transactions) {
            table.addCell(transaction.getType());
            table.addCell(String.format("₹%.2f", transaction.getAmount()));
            table.addCell(transaction.getTimestamp().toString());
        }

        document.add(table);
        document.close();
        return file;
    }

    public static File generateTransactionCSV(List<Transaction> transactions, long accountNo) throws Exception {
        String fileName = "TransactionHistory_" + accountNo + ".csv";
        File file = new File(fileName);

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            String[] header = {"Type", "Amount", "Date"};
            writer.writeNext(header);

            for (Transaction transaction : transactions) {
                String[] row = {
                    transaction.getType(),
                    String.format("%.2f", transaction.getAmount()),
                    transaction.getTimestamp().toString()
                };
                writer.writeNext(row);
            }
        }
        return file;
    }
}
