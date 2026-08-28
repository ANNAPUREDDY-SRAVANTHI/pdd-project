const ExcelJS = require('exceljs');
const fs = require('fs');
const path = require('path');

async function generateExcelReport(results, folderName, fileName) {
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Test Results');

    sheet.columns = [
        { header: 'Test ID', key: 'id', width: 10 },
        { header: 'Test Name', key: 'name', width: 30 },
        { header: 'Status', key: 'status', width: 15 },
        { header: 'Duration (ms)', key: 'duration', width: 15 },
        { header: 'Error', key: 'error', width: 50 },
    ];

    results.forEach(result => {
        sheet.addRow(result);
    });

    const dirPath = path.join(__dirname, '..', '..', folderName);
    if (!fs.existsSync(dirPath)) {
        fs.mkdirSync(dirPath, { recursive: true });
    }

    const filePath = path.join(dirPath, fileName);
    await workbook.xlsx.writeFile(filePath);
    console.log(`Excel report saved to: ${filePath}`);
}

module.exports = { generateExcelReport };
