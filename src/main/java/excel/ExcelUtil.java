package excel;

import org.apache.poi.hssf.usermodel.*;

public class ExcelUtil {

    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String[] title,String[][] values,HSSFWorkbook wb){
        //创建一个HSSFWorkbook，对应一个excel文件
        if(wb==null){
            wb=new HSSFWorkbook();
        }
        //创建一个sheet，对应excel中的sheet
        HSSFSheet sheet =wb.createSheet(sheetName);

        //添加表头第0行
        HSSFRow row=sheet.createRow(0);

        //创建单元格，设置表头居中
        HSSFCellStyle cellStyle=wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居中

        //声明列对象
        HSSFCell cell=null;

        //设置标题
        for(int i=0;i<title.length;i++){
            cell=row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(cellStyle);
        }

        //填充值
        for(int i=0;i<values.length;i++){
            row=sheet.createRow(i+1);
            for(int j=0;j<values[i].length;j++){
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;

    }
}
